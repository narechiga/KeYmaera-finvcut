// This file is part of KeY - Integrated Deductive Software Design
// Copyright (C) 2001-2009 Universitaet Karlsruhe, Germany
//                         Universitaet Koblenz-Landau, Germany
//                         Chalmers University of Technology, Sweden
//
// The KeY system is protected by the GNU General Public License. 
// See LICENSE.TXT for details.
//
//

package de.uka.ilkd.key.gui.nodeviews;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import de.uka.ilkd.key.collection.ImmutableList;
import de.uka.ilkd.key.gui.KeYMediator;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.gui.MethodCallInfo;
import de.uka.ilkd.key.gui.configuration.Config;
import de.uka.ilkd.key.gui.configuration.ConfigChangeAdapter;
import de.uka.ilkd.key.gui.configuration.ConfigChangeListener;
import de.uka.ilkd.key.gui.notification.events.GeneralFailureEvent;
import de.uka.ilkd.key.gui.syntaxhighlighting.HighlightSyntax;
import de.uka.ilkd.key.gui.syntaxhighlighting.TextToHtml;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.PosInTerm;
import de.uka.ilkd.key.pp.ConstraintSequentPrintFilter;
import de.uka.ilkd.key.pp.InitialPositionTable;
import de.uka.ilkd.key.pp.LogicPrinter;
import de.uka.ilkd.key.pp.LogicPrinterHTML;
import de.uka.ilkd.key.pp.ProgramPrinter;
import de.uka.ilkd.key.pp.Range;
import de.uka.ilkd.key.pp.SequentPrintFilter;
import de.uka.ilkd.key.proof.Node;
import de.uka.ilkd.key.rule.IfFormulaInstSeq;
import de.uka.ilkd.key.rule.IfFormulaInstantiation;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.Taclet;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.export.html.HTMLFileTaclet;
import de.uka.ilkd.key.rule.inst.GenericSortInstantiations;
import de.uka.ilkd.key.util.Debug;


public class NonGoalInfoView extends JEditorPane {
    	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1381305416290810149L;
	private LogicPrinter printer;	 
    private SequentPrintFilter filter;
    private InitialPositionTable posTable;
    private ConfigChangeListener configChangeListener = new ConfigChangeAdapter(this);
    
    public NonGoalInfoView (Node node, KeYMediator mediator) {
    	super("text/html","");
    	setEditable(false);
        if(MethodCallInfo.MethodCallCounterOn){
            MethodCallInfo.Global.incForClass(this.getClass().toString(), MethodCallInfo.constructor);
            MethodCallInfo.Local.incForClass(this.getClass().toString(), MethodCallInfo.constructor);
        }

	filter = new ConstraintSequentPrintFilter 
	    ( node.sequent (), 
	      mediator.getUserConstraint ().getConstraint () );
	printer = new LogicPrinterHTML
	    (new ProgramPrinter(null), 
	     mediator.getNotationInfo(),
	     mediator.getServices(), false);
	printer.printSequent (null, filter);
	String s = printer.toString();
	s = HighlightSyntax.highlight(s);
        posTable = printer.getPositionTable();
        printer=null;
	RuleApp app = node.getAppliedRuleApp();
        s += TextToHtml.changeHtmlSpecialCharacters("\nNode Nr "+node.serialNr()+"\n");
        
	if ( app != null ) {
	    s = s + TextToHtml.changeHtmlSpecialCharacters("\n \nUpcoming rule application: \n");
	    if (app.rule() instanceof Taclet) {
		LogicPrinter tacPrinter = new LogicPrinter 
		    (new ProgramPrinter(null),	                     
		     mediator.getNotationInfo(),
		     mediator.getServices(),
		     true);	 
		tacPrinter.printTaclet((Taclet)(app.rule()));	 
		s += TextToHtml.changeHtmlSpecialCharacters(tacPrinter.toString());
	    } else {
	    	s = s + TextToHtml.changeHtmlSpecialCharacters(app.rule().toString());
	    }

	    if ( app instanceof TacletApp ) {
		TacletApp tapp = (TacletApp)app;
		if ( tapp.instantiations ().getGenericSortInstantiations () !=
		     GenericSortInstantiations.EMPTY_INSTANTIATIONS ) {
		    s = s + TextToHtml.changeHtmlSpecialCharacters("\n\nWith sorts:\n");
		    s = s +
		    TextToHtml.changeHtmlSpecialCharacters(tapp.instantiations().getGenericSortInstantiations().toString());
		}

		StringBuffer sb = new StringBuffer("\n\n");
                HTMLFileTaclet.writeTacletSchemaVariablesHelper(
		    sb,tapp.taclet());
		s = s + TextToHtml.changeHtmlSpecialCharacters(sb.toString());
	    }
            
            s = s + TextToHtml.changeHtmlSpecialCharacters("\n\nApplication justified by: ");
            s = s + TextToHtml.changeHtmlSpecialCharacters(mediator.getSelectedProof().env().getJustifInfo()
                                .getJustification(app, mediator.getServices())+"\n");
            
	}

/* Removed for the book version
        s = s + "\nActive statement from:\n"+
           node.getNodeInfo().getExecStatementParentClass()+":"+
           node.getNodeInfo().getExecStatementPosition()+"\n";
*/
           
        if (node.getReuseSource() != null) {
            s += TextToHtml.changeHtmlSpecialCharacters("\n"+node.getReuseSource().scoringInfo());
        }

	Config.DEFAULT.addConfigChangeListener(configChangeListener);
	s = s.replaceAll("\n", "<br/>");
	updateUI();
	setText(s);

	if (app!=null) {	 
	    highlightRuleAppPosition(app);	 
	} else {	 
	    // no rule app	 
             setCaretPosition(0);	 
	}
	
	setEditable(false);
    }
    
    public void addNotify() {
        super.addNotify();
        Config.DEFAULT.addConfigChangeListener(configChangeListener);
    }
    
    public void removeNotify(){
        super.removeNotify();
        unregisterListener();
        if(MethodCallInfo.MethodCallCounterOn){
            MethodCallInfo.Local.incForClass(this.getClass().toString(), "removeNotify()");
        }
    }

    public void unregisterListener(){
        if(configChangeListener!=null){
            Config.DEFAULT.removeConfigChangeListener(configChangeListener);            
        }
    }
    
    protected void finalize(){
        try{
            unregisterListener();
            if(MethodCallInfo.MethodCallCounterOn){
                MethodCallInfo.Global.incForClass(this.getClass().toString(), MethodCallInfo.finalize);
                MethodCallInfo.Local.incForClass(this.getClass().toString(), MethodCallInfo.finalize);
            }
        } catch (Throwable e) {
            Main.getInstance().notify(new GeneralFailureEvent(e.getMessage()));
        }finally{
                try {
                    super.finalize();
                } catch (Throwable e) {
                    Main.getInstance().notify(new GeneralFailureEvent(e.getMessage()));
                }
        }
    }

    static final Highlighter.HighlightPainter RULEAPP_HIGHLIGHTER =	 
	new DefaultHighlighter	 
	.DefaultHighlightPainter(new Color(0.5f,1.0f,0.5f));	 
 	 
    static final Highlighter.HighlightPainter IF_FORMULA_HIGHLIGHTER =	 
	new DefaultHighlighter	 
	.DefaultHighlightPainter(new Color(0.8f,1.0f,0.8f));	 
 	 
 	 
    private void highlightRuleAppPosition(RuleApp app) {	 
	try {	 
	    setHighlighter ( new DefaultHighlighter () );	 

	    // Set the find highlight first and then the if highlights
	    // This seems to make cause the find one to be painted 
	    // over the if one.
 	 
	    final Range r;
	    if ( app.posInOccurrence()==null ) {
	        // rule does not have a find-part
	        r = null;
	    } else {
	        r = highlightPos ( app.posInOccurrence (), RULEAPP_HIGHLIGHTER );
	    }

	    if ( app instanceof TacletApp ) {	 
		highlightIfFormulas ( (TacletApp)app );	 
	    }	 

	    if ( r != null ) makeRangeVisible ( r );	 
	} catch(BadLocationException badLocation) {	 
	    System.err.println("NonGoalInfoView tried to "	 
			       +"highlight an area "	 
			       +"that does not exist.");	 
	    System.err.println("Exception:"+badLocation);	 
	}	 
    }	 
 	 
    /**	 
     * Ensure that the given range is visible	 
     */	 
    private void makeRangeVisible (final Range r) {	 
	setCaretPosition ( r.start () );	 
	final Runnable safeScroller = new Runnable () {	 
		public void run () {	 
		    try {	 
			final TextUI ui = getUI ();	 
			final NonGoalInfoView t = NonGoalInfoView.this;	 
			final Rectangle rect = ui.modelToView ( t, r.start () );
			if(rect == null) {
			    return;
			}
			rect.add ( ui.modelToView ( t, r.end () ) );	 
 	 
			for ( int i = 4; i >= 0; --i ) {	 
			    final Rectangle rect2 = new Rectangle ( rect );	 
			    final int border = i * 30;	 
			    rect2.add ( rect.getMinX () - border,	 
					rect.getMinY () - border );	 
			    rect2.add ( rect.getMaxX () + border,	 
					rect.getMaxY () + border );	 
			    scrollRectToVisible ( rect2 );	 
			}	 
		    } catch ( BadLocationException badLocation ) {	 
			System.err.println("NonGoalInfoView tried to "	 
					   +"make an area visible "	 
					   +"that does not exist.");	 
			System.err.println("Exception:"+badLocation);	 
		    }	 
		}	 
	    };	 
	SwingUtilities.invokeLater ( safeScroller );	 
    }	 
 	 
    /**	 
     * @param tapp The taclet app for which the if formulae	 
     * should be highlighted.	 
     * @throws BadLocationException	 
     */	 
    private void highlightIfFormulas (TacletApp tapp)	 
	throws BadLocationException {	 
	final ImmutableList<IfFormulaInstantiation> ifs = tapp.ifFormulaInstantiations ();	 
	if ( ifs == null ) return;
        for (final IfFormulaInstantiation inst2 : ifs) {
            if (!(inst2 instanceof IfFormulaInstSeq)) continue;
            final IfFormulaInstSeq inst = (IfFormulaInstSeq) inst2;
            final PosInOccurrence pos =
                    new PosInOccurrence(inst.getConstrainedFormula(),
                            PosInTerm.TOP_LEVEL,
                            inst.inAntec());
            final Range r = highlightPos(pos, IF_FORMULA_HIGHLIGHTER);
            makeRangeVisible(r);
        }
    }	 
 	 
    /**	 
     * @param pos   the PosInOccurrence that should be highlighted.	 
     * @param light the painter for the highlight.	 
     * @return the range of characters that was highlighted.	 
     * @throws BadLocationException	 
     */	 
    private Range highlightPos (PosInOccurrence pos,	 
				HighlightPainter light)	 
	throws BadLocationException {	 
	ImmutableList<Integer> path = posTable.pathForPosition (pos, filter);	 
	Range r = posTable.rangeForPath(path);	 
	getHighlighter().addHighlight(r.start(), r.end(), light);	 
	return r;
    }

    public void updateUI() {
	super.updateUI();
	Font myFont = UIManager.getFont(Config.KEY_FONT_INNER_NODE_VIEW);
	if (myFont != null) {
	    putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  // Allow font to changed in JEditorPane when set to "text/html"
	    setFont(myFont);
	} else {
	    Debug.out("KEY-INNER_NODE_FONT not available, use standard font.");
	}
    }
}
