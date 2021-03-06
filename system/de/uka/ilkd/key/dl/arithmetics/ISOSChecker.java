/***************************************************************************
 *   Copyright (C) 2008 by Jan-David Quesel                                *
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
package de.uka.ilkd.key.dl.arithmetics;

import java.rmi.RemoteException;
import java.util.Set;

import de.uka.ilkd.key.dl.arithmetics.impl.SumOfSquaresChecker.PolynomialClassification;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;

/**
 * @author jdq
 * TODO Documentation since Jun 6, 2008
 */
public interface ISOSChecker extends IMathSolver {

	/**
	 * @param terms
	 * TODO documentation since Jun 6, 2008
	 * @param services 
	 * @throws RemoteException 
	 */
	boolean testForTautology(Set<Term> ante, Set<Term> succ, Services services) throws RemoteException;
}
