// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//This file is part of KeY - Integrated Deductive Software Design
//Copyright (C) 2001-2005 Universitaet Karlsruhe, Germany
//Universitaet Koblenz-Landau, Germany
//Chalmers University of Technology, Sweden

//The KeY system is protected by the GNU General Public License. 
//See LICENSE.TXT for details.



package de.uka.ilkd.key.java;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import recoder.java.declaration.TypeDeclaration;
import recoder.list.generic.ASTArrayList;
import recoder.list.generic.ASTList;
import de.uka.ilkd.key.java.recoderext.ImplicitIdentifier;
import de.uka.ilkd.key.java.recoderext.SchemaCrossReferenceServiceConfiguration;
import de.uka.ilkd.key.java.recoderext.SchemaJavaProgramFactory;
import de.uka.ilkd.key.logic.Namespace;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.util.Debug;
import de.uka.ilkd.key.util.KeYRecoderExcHandler;

public class SchemaRecoder2KeY extends Recoder2KeY implements SchemaJavaReader {

    /** the namespace containing the program schema variables allowed here */
    protected Namespace svns;

    /** caches access to methods for reflection */
    private final static HashMap schemaCt2meth = new HashMap(400);

    /** caches constructor access for reflection */
    private final static HashMap recClass2schemakeyClassCons = new HashMap(400);

    // could this be the servConf of the super class?
    private static SchemaCrossReferenceServiceConfiguration schemaServConf = new SchemaCrossReferenceServiceConfiguration(
            new KeYRecoderExcHandler());

    public SchemaRecoder2KeY(Services services, NamespaceSet nss) {
        super(services, nss);
    }

    protected Recoder2KeYConverter makeConverter() {
        return new SchemaRecoder2KeYConverter(this);
    }

    /**
     * returns the hashmap of a concrete RecodeR class to the constructor of its
     * corresponding KeY class. Speeds up reflection. Attention must be
     * overwritten by subclasses!
     */
    protected HashMap getKeYClassConstructorCache() {
        return recClass2schemakeyClassCons;
    }

    protected HashMap getMethodCache() {
        return schemaCt2meth;
    }

    public void setSVNamespace(Namespace svns) {
        this.svns = svns;
    }

    /**
     * creates an empty RECODER compilation unit
     * 
     * @return the recoder.java.CompilationUnit
     */
    public Context createEmptyContext() {
        return new Context(schemaServConf, new recoder.java.CompilationUnit(),
                schemaServConf.getProgramFactory().createClassDeclaration(null,
                        new ImplicitIdentifier("<KeYSpecialParsing>"), null,
                        null, null));	
    }

    /**
     * wraps a RECODER ClassDeclaration in a compilation unit
     * 
     * @param classDecl
     *            the recoder.java.ClassDeclaration to wrap
     * @param context
     *            the Context containing the recoder.java.CompilationUnit where the class is wrapped
     * @return the enclosing recoder.java.CompilationUnit
     */
    protected recoder.java.CompilationUnit embedClass(
            recoder.java.declaration.ClassDeclaration classDecl, Context context) {

        recoder.java.CompilationUnit cUnit = context
        .getCompilationUnitContext();

        // add class to compilation unit
        ASTList<TypeDeclaration> typeDecls = cUnit.getDeclarations();

        if (typeDecls == null) {
            typeDecls = new ASTArrayList<TypeDeclaration>(0);
        } else {
            typeDecls = typeDecls.deepClone();
        }
        typeDecls.add(classDecl);

        recoder.java.CompilationUnit compUnitContext = (recoder.java.CompilationUnit) cUnit
        .deepClone();

        compUnitContext.setDeclarations(typeDecls);
        compUnitContext.makeParentRoleValid();
        schemaServConf.getChangeHistory().attached(compUnitContext);
        schemaServConf.getChangeHistory().updateModel();
        return compUnitContext;
    }

    /**
     * parses a given JavaBlock using the context to determine the right
     * references and returns a statement block of recoder.
     * 
     * @param block
     *            a String describing a java block
     * @param context
     *            recoder.java.CompilationUnit in which the block has to be
     *            interpreted
     * @return the parsed and resolved recoder statement block
     */
    protected recoder.java.StatementBlock recoderBlock(String block,
            Context context) {
        recoder.java.StatementBlock bl = null;

        SchemaJavaProgramFactory factory = (SchemaJavaProgramFactory) schemaServConf
        .getProgramFactory();
        factory.setSVNamespace(svns);
        try {
            bl = factory.parseStatementBlock(new StringReader(block));
        } catch (recoder.ParserException e) {
            Debug.out("readSchemaJavaBlock(Reader,CompilationUnit)"
                    + " caused the " + "exception:\n", e);
            Debug.out(e);
            throw new ConvertException("Parsing: \n **** BEGIN ****\n " + block
                    + "\n **** END ****\n failed. Thrown Exception:"
                    + e.toString());
        } catch (IOException ioe) {
            Debug.out("readSchemaJavaBlock(Reader,CompilationUnit)"
                    + " caused the IO exception:\n", ioe);
            Debug.out(ioe);
            throw new ConvertException(
                    "IO Error when parsing: \n **** BEGIN ****\n " + block
                    + "\n **** END ****\n failed. Thrown IOException:"
                    + ioe.toString());
        }

        embedClass(embedMethod(embedBlock(bl), context), context);

        return bl;
    }

    /**
     * there is no need to parse special classes in this case, so
     * this is empty
     * @see de.uka.ilkd.key.java.Recoder2KeY#parseSpecialClasses()
     */
    public void parseSpecialClasses() {
    }
}
