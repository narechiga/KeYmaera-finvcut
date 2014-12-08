// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//


package de.uka.ilkd.key.logic.op.oclop;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.logic.sort.oclsort.CollectionSort;
import de.uka.ilkd.key.logic.sort.oclsort.OclSort;

/**
 * Represents the OCL operation: any()
 */
public class OclAny extends OclCollOpBound {
       
    public OclAny() {
	super(new Name("$any"), OclSort.BOOLEAN, OclSort.OCLANY);
    }

    public Sort sort(Term[] subTerm) {
	helpSort(subTerm);
	return ((CollectionSort)subTerm[0].sort()).getElemSort();
    }
  
    public String toString() {
	return (name()+":"+OclSort.SEQUENCE_OF_OCLANY);
    }
    
    public String proofToString() {
       String s = OclSort.SEQUENCE_OF_OCLANY+" "+name();
       s+="(" + OclSort.COLLECTION_OF_OCLANY + "," + OclSort.REAL;
       s+=");\n";
       return s;
    }
}