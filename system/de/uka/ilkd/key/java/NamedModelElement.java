// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//


package de.uka.ilkd.key.java;

/** 
 * A model element that carries a name.
 */
public interface NamedModelElement extends ModelElement {

    /** 
     * Return the name of the model element.
     *
     * @return the name of the model element.
     */
    String getName();

}
