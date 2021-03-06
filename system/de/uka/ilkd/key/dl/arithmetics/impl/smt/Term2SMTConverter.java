/*******************************************************************************
 * Copyright (c) 2012 Jan-David Quesel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Jan-David Quesel - initial API and implementation
 *     Nikos Arechiga - Hijacked to use dReal instead of z3, added support
 *     			for several of the neat extra functions
 ******************************************************************************/
package de.uka.ilkd.key.dl.arithmetics.impl.smt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import orbital.math.Arithmetic;

import de.uka.ilkd.key.dl.arithmetics.exceptions.UnableToConvertInputException;
import de.uka.ilkd.key.dl.arithmetics.impl.orbital.OrbitalSimplifier;
import de.uka.ilkd.key.dl.arithmetics.impl.orbital.PolynomTool;
import de.uka.ilkd.key.dl.arithmetics.impl.orbital.PolynomTool.BigFraction;
import de.uka.ilkd.key.dl.formulatools.Prenex;
import de.uka.ilkd.key.logic.NamespaceSet;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.LogicVariable;
import de.uka.ilkd.key.logic.op.Metavariable;
import de.uka.ilkd.key.logic.op.Op;
import de.uka.ilkd.key.logic.op.QuantifiableVariable;
import de.uka.ilkd.key.logic.op.Quantifier;

/**
 * Converts a term to a SMTInput for SMT LIB programs 
 *  
 * @author jdq
 */
public class Term2SMTConverter {

	static final String USCOREESCAPE = "uscore";
	static final String DOLLARESCAPE = "dollar";
	static final String MODREPLACEMENT = "moduscorereplaceuscorement";
	private SMTInput input = new SMTInput(); // Result
	private ArrayList<String> existingVars = new ArrayList<String>(); // List of

	// existing
	// Variables

	/**
	 * Standardconstructor.
	 */
	public Term2SMTConverter() {
	}

	/**
	 * Function to start to convert a given term.
	 * 
	 * @param form
	 *            Term to convert
	 * @param variables
	 * @return QepCadInput-Instance of the given term.
	 */
	public static SMTInput convert(Term form,
			List<QuantifiableVariable> variables, NamespaceSet nss) {
		Term2SMTConverter converter = new Term2SMTConverter();
		if(Options.INSTANCE.isPrenexForm()) {
			form = Prenex.transform(form, nss)._1;
		}
		if(Options.INSTANCE.isElimExistentialQuantifierPrefix()) {
			form = elimForQuan(form);
		}
		return converter.convertImpl(form, variables);
	}

	/**
	 * Eliminates a universal quantifier prefix.
	 * 
	 * @param form
	 * @return
	 */
	private static Term elimForQuan(Term form) {
		if(form.op() == Op.ALL) {
			return elimForQuan(form.sub(0));
		}
		return form;
	}

	/**
	 * Implementation of the convert-algorithm
	 * 
	 * @param variables
	 */
	private SMTInput convertImpl(Term form,
			List<QuantifiableVariable> variables) {

		// Getting the string-representation
		// String formula = "(" + convert2String( form ) + ")";
		String formula = convert2String(form, null, false);

		// extracts additional information for qepcad
		List<String> freeVarlist = new ArrayList<String>(existingVars);

		// the first parameter is changed by the function
		this.input.setVariableList(variableDeclarations2String(getVariableList(freeVarlist, variables)));

		if (!formula.startsWith("(")) {
			formula = "( " + formula + " )";
		}

		this.input.setFormula(formula);
		return this.input;
	}

	private String convert2String(Term form, NamespaceSet nss,
			boolean eliminateFractions) {
		if (form.op() == Op.FALSE) {
			return "false";
		} else if (form.op() == Op.TRUE) {
			return "true";
		} else if (form.op().name().toString().equals("equals")) {
			if (eliminateFractions) {
				return convert2String(PolynomTool
						.eliminateFractionsFromInequality(form, nss), nss,
						false);
			}
			return "(= " + convert2String(form.sub(0), nss, true) + " "
					+ convert2String(form.sub(1), nss, true) + " )";
		} else if (form.op() instanceof Function) {
			Function f = (Function) form.op();
			if (f.name().toString().equals("gt")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(> " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " )";
			} else if (f.name().toString().equals("geq")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(>= " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " )";
			} else if (f.name().toString().equals("equals")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(= " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " )";
				// 2x EQUALS ?
			} else if (f.name().toString().equals("neq")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(not (= " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " ))";
			} else if (f.name().toString().equals("leq")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(<= " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " )";
			} else if (f.name().toString().equals("lt")) {
				if (eliminateFractions) {
					return convert2String(PolynomTool
							.eliminateFractionsFromInequality(form, nss), nss,
							false);
				}
				return "(< " + convert2String(form.sub(0), nss, true) + " "
						+ convert2String(form.sub(1), nss, true) + " )";
			} else if (f.name().toString().equals("add")) {
				return "(+ "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (f.name().toString().equals("sub")) {
				return "(- "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (f.name().toString().equals("neg")) {
				return "(- "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";
			} else if (f.name().toString().equals("mul")) {
				return "(* "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (f.name().toString().equals("div")) {
				return "(/ "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (f.name().toString().equals("Exp")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(exp "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("Sin")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(sin "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("Cos")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(cos "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("Tan")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(tan "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("Asin")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(asin "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";
						
			} else if (f.name().toString().equals("Acos")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(acos "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("Atan")) { // An effort to be able to use fancy dReal capabilities ! (Nikos on 10-13-2014)
				return "(atan "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";

			} else if (f.name().toString().equals("exp")) { // Hijacked by Nikos, to use fancy dReal capabilities!
									// Forget about being limited to integer exponents, that's no fun
				return "(^ "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
//				// FIXME this function only works for integer exponentials
//				int exp;
//                try {
//    				Arithmetic e = OrbitalSimplifier.translateArithmetic(form.sub(1));
//    				if(e instanceof orbital.math.Integer) {
//    				    exp = ((orbital.math.Integer) e).intValue();
//    				} else {
//    				    throw new IllegalArgumentException("Cannot handle exponent " + form.sub(1));
//    				}
//                } catch (Exception e1) {
//                    if(e1 instanceof InterruptedException) {
//                        Thread.currentThread().interrupt();
//                    }
//				    throw new IllegalArgumentException("Cannot handle exponent " + form.sub(1));
//                }
//				if(exp == 0) {
//					return "1";
//				}
//				boolean negative = false;
//				if(exp < 0) {
//					exp = -exp;
//					negative = true;
//				}
//				String s = convert2String(form.sub(0), nss, eliminateFractions);
//				String res = "(*";
//				for(int i = 0; i < exp; i++) {
//					res += " " + s;
//				}
//				res += ")";
//				if(negative) {
//					return ("(/ 1. " + res + ")");
//				}
//				return res;
			} else {
				String[] args = new String[form.arity()];
				for (int i = 0; i < args.length; i++) {
					args[i] = convert2String(form.sub(i), nss,
							eliminateFractions);
				}

				//Jyo
				return form.op().name().toString();
				// try {
				// 	String numberAsString = form.op().name().toString();
				// 	BigFraction frac = PolynomTool
				// 			.convertStringToFraction(numberAsString);
				// 	if (frac.getDenominator().equals(BigInteger.ONE)) {
				// 		return printNumber(frac.getNumerator());
				// 	} else {
				// 		return "(/ " + printNumber(frac.getNumerator()) + " "
				// 				+ printNumber(frac.getDenominator()) + ")";
				// 	}
				// } catch (NumberFormatException nfe) {
				// 	String name = form.op().name().toString();
				// 	if (name.contains("_")) {
				// 		name = name.replaceAll("_", USCOREESCAPE);

				// 	}
				// 	if (name.contains("$")) {
				// 		name = name.replaceAll("\\$", DOLLARESCAPE);
				// 	}
				// 	if (name.equals("mod")) {
				// 		name = MODREPLACEMENT;
				// 	}
				// 	addExistingVariable(name);
				// 	if (args.length == 0) {
				// 		return name;
				// 	}
				// 	return "(" + name + " " + array2StringBlanks(args) + ")";
				// }
			}
		} else if (form.op() instanceof LogicVariable
				|| form.op() instanceof de.uka.ilkd.key.logic.op.ProgramVariable
				|| form.op() instanceof Metavariable) {
			String name = form.op().name().toString();
			if (name.contains("_")) {
				name = name.replaceAll("_", USCOREESCAPE);
			}
			if (name.contains("$")) {
				name = name.replaceAll("\\$", DOLLARESCAPE);
			}
			if (name.equals("mod")) {
				name = MODREPLACEMENT;
			}
			addExistingVariable(name);
			return name;
		} else if (form.op() instanceof Junctor) {
			if (form.op() == Junctor.AND) {
				return "(and "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (form.op() == Junctor.OR) {
				return "(or "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (form.op() == Junctor.IMP) {
				return "(or "
						+ "(not " + convert2String(form.sub(0), nss, eliminateFractions) + ")"
						+ " "
						+ convert2String(form.sub(1), nss, eliminateFractions)
						+ ")";
			} else if (form.op() == Junctor.NOT) {
				return "(not "
						+ convert2String(form.sub(0), nss, eliminateFractions)
						+ ")";
			}
        } else if (form.op() == Op.EQV) {
            return "(equiv "
                    + convert2String(form.sub(0), nss, eliminateFractions)
                    + " "
                    + convert2String(form.sub(1), nss, eliminateFractions)
                    + ")";
		} else if (form.op() instanceof Quantifier) {

			int varsNum = form.varsBoundHere(0).size();
			String[] vars = new String[varsNum];
			for (int i = 0; i < varsNum; i++) {
				String name = form.varsBoundHere(0).get(i)
						.name().toString();
				if (name.contains("_")) {
					name = name.replaceAll("_", USCOREESCAPE);
				}
				if (name.contains("$")) {
					name = name.replaceAll("\\$", DOLLARESCAPE);
				}
				if (name.equals("mod")) {
					name = MODREPLACEMENT;
				}
				vars[i] = name;
//				addExistingVariable(name);
			}

			if (form.op() == Quantifier.ALL) {
				return "(forall " + array2String(vars) + " "
						+ convert2String(form.sub(0), nss, eliminateFractions) + ")";
			} else if (form.op() == Quantifier.EX) {
				return "(exists " + array2String(vars) + " "
						+ convert2String(form.sub(0), nss, eliminateFractions) + ")";
			}
		}
		throw new IllegalArgumentException("Could not convert Term: " + form
				+ "Operator was: " + form.op());
	}

	private String convertNumber(Term form) {
   		//return form.op().name().toString();
 		String numberAsString = form.op().name().toString();
 		BigFraction frac = PolynomTool
 				.convertStringToFraction(numberAsString);
 		if (frac.getDenominator().equals(BigInteger.ONE)) {
 			return printNumber(frac.getNumerator());
 		} else {
 			return "(/ " + printNumber(frac.getNumerator())
 					+ printNumber(frac.getDenominator()) + ")";
 		}
	}

	private String printNumber(BigInteger b) {
		if(b.compareTo(BigInteger.ZERO) < 0) {
			return "(- " + b.abs().toString() + ")"; // got rid of decimal point. it was silly, since it converts everything to fractions anyway
		} else {
			return b.toString();// got rid of decimal point. it was silly, since it converts everything to fractions anyway
		}
	}


	// Converts an array of Strings in
	// one string. The elements are seperated by
	// ','
	private String array2String(String[] args) {
		if (args == null)
			return "";

		String result = "(";
		for (int i = 0; i < args.length; i++) {
			result += "(" + args[i] + " Real)";
			if (i != args.length - 1)
				result += " ";
		}

		return result + ")";
	}
	
	private String array2StringBlanks(String[] args) {
		if (args == null)
			return "";

		String result = "";
		for (int i = 0; i < args.length; i++) {
			result += args[i];
			if (i != args.length - 1)
				result += " ";
		}

		return result;
	}
	
	// Converts an array of Strings in
	// one string. The elements are seperated by
	// ','
	private String variableDeclarations2String(String[] args) {
		if (args == null)
			return "";

		String result = "";
		for (int i = 0; i < args.length; i++) {
			result += "(declare-fun " + args[i] + " () Real)";
			if (i != args.length - 1)
				result += "\n";
		}

		return result;
	}

	// Inserts a new variable in the list of existing variables,
	// if is not in the list
	private void addExistingVariable(String varName) {
		if (!existingVars.contains(varName)) {
			this.existingVars.add(varName);
		}

	}

	// Gets the variable-list, which is important for qepcad
	private String[] getVariableList(List<String> allVariables,
			List<QuantifiableVariable> quantifiedVars) {

		ArrayList<String> freeVars = new ArrayList<String>();
		List<String> quantified = new ArrayList<String>();

		for (QuantifiableVariable var : quantifiedVars) {
			String name = var.name().toString();
			if (name.contains("_")) {
				name = name.replaceAll("_", USCOREESCAPE);
			}
			if (name.contains("$")) {
				name = name.replaceAll("\\$", DOLLARESCAPE);
			}
			if (name.equals("mod")) {
				name = MODREPLACEMENT;
			}
			allVariables.remove(name);
			quantified.add(name);
		}
		Collections.reverse(quantified);

		for (String var : allVariables) {
			freeVars.add(var);
		}

		Set<String> vars = new HashSet<String>();

		vars.addAll(allVariables);
		vars.addAll(quantified);

		return vars.toArray(new String[vars.size()]);
	}
}
