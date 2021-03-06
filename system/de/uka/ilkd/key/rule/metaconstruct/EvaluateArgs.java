// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//


package de.uka.ilkd.key.rule.metaconstruct;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uka.ilkd.key.collection.ImmutableArray;
import de.uka.ilkd.key.java.*;
import de.uka.ilkd.key.java.abstraction.KeYJavaType;
import de.uka.ilkd.key.java.declaration.LocalVariableDeclaration;
import de.uka.ilkd.key.java.declaration.VariableSpecification;
import de.uka.ilkd.key.java.expression.operator.CopyAssignment;
import de.uka.ilkd.key.java.reference.*;
import de.uka.ilkd.key.logic.VariableNamer;
import de.uka.ilkd.key.logic.op.LocationVariable;
import de.uka.ilkd.key.logic.op.ProgramVariable;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

public class EvaluateArgs extends ProgramMetaConstruct{

    /** creates a typeof ProgramMetaConstruct 
     * @param pe the instance of expression contained by 
     * the meta construct 
     */
    public EvaluateArgs(ProgramElement pe) {
	super("#evaluate-arguments", pe); 
    }

    public static ProgramVariable evaluate(Expression e, 
                                           List<? super LocalVariableDeclaration> l, 
                                           Services services, 
                                           ExecutionContext ec) {

	final VariableNamer varNamer = services.getVariableNamer();
	final KeYJavaType t      = e.getKeYJavaType(services, ec);
	final ProgramVariable pv = 
	    new LocationVariable(VariableNamer.parseName
				(varNamer.
				 getSuggestiveNameProposalForSchemaVariable(e)), t);
	
	l.add(new LocalVariableDeclaration(new TypeRef(t), 
					   new VariableSpecification(pv, e, t)));
	return pv;
    }


    /** performs the program transformation needed for symbolic
     * program execution 
     * @param services the Services with all necessary information 
     * about the java programs
     * @param svInst the instantiations esp. of the inner and outer label 
     * @return the transformed program
     */
    public ProgramElement symbolicExecution(ProgramElement pe,
					    Services services,
					    SVInstantiations svInst) {

	final ExecutionContext ec = svInst.getExecutionContext();

	MethodReference mr = 
	    (MethodReference) (pe instanceof CopyAssignment ? 
			       ((CopyAssignment)pe).getChildAt(1) : pe);
	
	List<Statement> evalstat = new LinkedList<Statement>();

	final ReferencePrefix newCalled;	
	final ReferencePrefix invocationTarget = mr.getReferencePrefix();

	if (invocationTarget instanceof Expression && 
	    !(invocationTarget instanceof ThisReference)) {
	    newCalled = evaluate
		((Expression)invocationTarget, evalstat, services, ec);
	} else {
	    newCalled = mr.getReferencePrefix();
	}
	
	ImmutableArray<Expression> args = mr.getArguments();
	Expression[] newArgs = new Expression[args.size()];
	for (int i=0; i<args.size(); i++) { 
	    newArgs[i]=evaluate(args.get(i), evalstat, services, ec);
	}

	Statement[] res = new Statement[1+evalstat.size()];
	final Iterator<Statement> it = evalstat.iterator();

	for (int i=0; i<evalstat.size(); i++) {
	    res[i] = it.next();
	}

	final MethodReference resMR = new MethodReference
	    (new ImmutableArray<Expression>(newArgs), mr.getMethodName(), newCalled);

	if (pe instanceof CopyAssignment) {
	    res[res.length-1] = new CopyAssignment
		(((CopyAssignment)pe).getExpressionAt(0), resMR);
	} else {
	    res[res.length-1] = resMR;
	}

	return new StatementBlock(res);
    }

}

	
