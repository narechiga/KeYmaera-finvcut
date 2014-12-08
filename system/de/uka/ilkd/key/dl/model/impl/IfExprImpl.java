/***************************************************************************
 *   Copyright (C) 2012 by Jan David Quesel                                *
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

import de.uka.ilkd.key.dl.model.DLProgramElement;
import de.uka.ilkd.key.dl.model.Expression;
import de.uka.ilkd.key.dl.model.Formula;
import de.uka.ilkd.key.dl.model.FreeFunction;
import de.uka.ilkd.key.dl.model.Function;
import de.uka.ilkd.key.dl.model.FunctionTerm;
import de.uka.ilkd.key.dl.model.IfExpr;
import de.uka.ilkd.key.java.PrettyPrinter;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.reference.ExecutionContext;

import java.io.IOException;

/**
 * Implementation of {@link IfExpr}
 * 
 * @author jdq
 * @since 06.08.2012
 */
public class IfExprImpl extends DLNonTerminalProgramElementImpl implements
        IfExpr {

    /**
     * Creates a new IfExpr with the given function and the given
     * Parameters.
     */
    public IfExprImpl(Formula f, Expression thenExpr, Expression elseExpr) {
        addChild(f);
        addChild(thenExpr);
        addChild(elseExpr);
    }

    /**
     * @see de.uka.ilkd.key.dl.model.impl.DLNonTerminalProgramElementImpl#prettyPrint(de.uka.ilkd.key.java.PrettyPrinter)
     *      prettyPrint
     */
    public void prettyPrint(PrettyPrinter arg0) throws IOException {
        arg0.printDLIf(this);
    }

    /**
     * @see de.uka.ilkd.key.dl.model.impl.DLNonTerminalProgramElementImpl#toString()
     *      toString
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("if (");
        result.append(getChildAt(0) + ") then ");
        result.append(getChildAt(1) + " else ");
        result.append(getChildAt(2) + " fi ");
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uka.ilkd.key.java.ReuseableProgramElement#reuseSignature(de.uka.ilkd.key.java.Services,
     *      de.uka.ilkd.key.java.reference.ExecutionContext)
     */
    public String reuseSignature(Services services, ExecutionContext ec) {
        StringBuilder result = new StringBuilder();
        result.append("if (");
        for (int i = 0; i < getChildCount(); i++) {
            result.append(((DLProgramElement) getChildAt(i)).reuseSignature(
                    services, ec)
                    + ", ");
        }
        result.append(")");
        return result.toString();
    }
}