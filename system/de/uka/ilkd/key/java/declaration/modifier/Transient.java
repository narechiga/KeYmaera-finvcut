// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//


package de.uka.ilkd.key.java.declaration.modifier;

import de.uka.ilkd.key.java.declaration.Modifier;

/**
 *  Transient.
 *  @author <TT>AutoDoc</TT>
 */

public class Transient extends Modifier {

    /**
     *      Transient.
     */

    public Transient() {}

    /**
     *      Transient.
     * @param children the children of this AST element as KeY classes.
     *  May contain: Comments
     */

    public Transient(de.uka.ilkd.key.util.ExtList children) {
	super(children);
    }
    
    /**
     *      Get symbol.
     *      @return the string.
     */

    protected String getSymbol() {
        return "transient";
    }
}
