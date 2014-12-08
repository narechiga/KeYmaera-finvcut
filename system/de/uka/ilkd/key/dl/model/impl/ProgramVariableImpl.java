/***************************************************************************
 *   Copyright (C) 2007 by Jan David Quesel                                *
 *   quesel@informatik.uni-oldenburg.de                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
/* Generated by Together */

package de.uka.ilkd.key.dl.model.impl;

import java.util.Map;
import java.util.WeakHashMap;

import de.uka.ilkd.key.dl.model.ProgramVariable;
import de.uka.ilkd.key.logic.Name;

/**
 * Implementation of {@link ProgramVariable}. Weak hashing of the variables is
 * done to assert that there is only one instance of this object per name.
 * 
 * @author jdq
 * @since 13.02.2007
 * 
 */
public class ProgramVariableImpl extends VariableImpl implements
        ProgramVariable {
    private static Map<Name, ProgramVariable> instances = new WeakHashMap<Name, ProgramVariable>();

    /**
     * Creates a new ProgramVariable or returns a cached one with the given
     * name. This method ensures that there is only one program variable object
     * for one function name at a time.
     * 
     * @param name
     *            the name of the program variable
     * @return the new or cached program variable
     */
    public static ProgramVariable getProgramVariable(String name) {
        return getProgramVariable(new Name(name), true);
    }

    /**
     * Creates a new ProgramVariable or returns a cached one with the given
     * name. This method ensures that there is only one program variable object
     * for one function name at a time.
     * 
     * @param name
     *            the name of the program variable
     * @return the new or cached program variable
     */
    public static ProgramVariable getProgramVariable(String name, boolean create) {
        return getProgramVariable(new Name(name), create);
    }

    /**
     * Creates a new ProgramVariable or returns a cached one with the given
     * name. This method ensures that there is only one program variable object
     * for one function name at a time.
     * 
     * @param name
     *            the name of the program variable
     * @return the new or cached program variable
     */
    public static ProgramVariable getProgramVariable(Name name, boolean create) {
        ProgramVariable result = instances.get(name);
        if (result == null) {
            if (create) {
                result = new ProgramVariableImpl(name);
                instances.put(name, result);
            } else {
                throw new IllegalStateException("Programvariable " + name
                        + " was used, but not declared");
            }
        }
        return result;
    }

    /**
     * Creates a new program variable with a given name
     * 
     * @param name
     *            the name to use
     */
    protected ProgramVariableImpl(Name name) {
        super(name);
    }
    
    /* (non-Javadoc)
     * @see de.uka.ilkd.key.dl.model.impl.VariableImpl#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ProgramVariable) {
            return getElementName().toString().equals(((ProgramVariable) obj).getElementName().toString());
        } else if(obj instanceof de.uka.ilkd.key.logic.op.ProgramVariable) {
            return getElementName().toString().equals(((de.uka.ilkd.key.logic.op.ProgramVariable) obj).name().toString());
        }
        return super.equals(obj);
    }

}
