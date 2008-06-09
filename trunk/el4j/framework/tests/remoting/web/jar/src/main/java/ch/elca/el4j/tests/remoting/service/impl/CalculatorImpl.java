/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.remoting.service.impl;

import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorOperation;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.service.SpecialCalculatorException;


/**
 * This class is the implementation of the calculator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorImpl implements Calculator {
	/**
	 * {@inheritDoc}
	 */
	public double getArea(double a, double b) {
		return a * b;
	}

	/**
	 * {@inheritDoc}
	 */
	public void throwMeAnException() throws CalculatorException {
		throw new CalculatorException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void throwMeASpecialException(String action)
		throws SpecialCalculatorException {
		throw new SpecialCalculatorException(action);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countNumberOfUppercaseLetters(String text) {
		if (text == null) {
			return 0;
		}
 
		int numberOfUppercaseLetters = 0;
		char[] c = text.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] >= 'A' && c[i] <= 'Z') {
				numberOfUppercaseLetters++;
			}
		}
		return numberOfUppercaseLetters;
	}

	/**
	 * {@inheritDoc}
	 */
	public CalculatorValueObject echoValueObject(
		CalculatorValueObject valueObject) {
		return valueObject;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double calculate(double a, double b, CalculatorOperation operation) {
		double result;
		if (operation == CalculatorOperation.ADDITION) {
			result = a + b;
		} else if (operation == CalculatorOperation.SUBTRACTION) {
			result = a - b;
		} else if (operation == CalculatorOperation.MULTIPLICATION) {
			result = a * b;
		} else if (operation == CalculatorOperation.DIVISION) {
			result = a / b;
		} else {
			throw new IllegalArgumentException(
				"Unknown calculator operation: " + operation);
		}
		return result;
	}
}
