// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.util;




public class CollKeYRecoderExcHandler extends KeYRecoderExcHandler{

    public void reportException(Throwable e){
	try {
            super.reportException(e);
        } catch(Throwable t) {
            // we collect more than one error
        }
    }
    	  
}
