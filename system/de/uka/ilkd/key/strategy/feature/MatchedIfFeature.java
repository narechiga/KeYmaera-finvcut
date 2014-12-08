// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.strategy.feature;

import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.TacletApp;

/**
 * Binary features that returns zero iff the if-formulas of a Taclet are
 * instantiated or the Taclet does not have any if-formulas.
 */
public class MatchedIfFeature extends BinaryTacletAppFeature {
    
    public static final Feature INSTANCE = new MatchedIfFeature (); 

    private MatchedIfFeature () {}
    
    protected boolean filter ( TacletApp app, PosInOccurrence pos, Goal goal ) {
        return app.ifInstsComplete();
    }

}
