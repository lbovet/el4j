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

package ch.elca.el4j.demos.remoting.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.hessian.server.HessianSkeleton;

import ch.elca.el4j.demos.remoting.Calculator;
import ch.elca.el4j.demos.remoting.CalculatorException;
import ch.elca.el4j.demos.remoting.ComplexNumber;

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
 * @author Rashid Waraich (RWA)
 */
public class CalculatorImpl implements Calculator {

    /**
     * Constructor added due to exception logging flood with jdk logging
     * and Hessian. Further, default constructor inserted for Aegis (xfire), as
     * it requires always a no-argument constructor also.
     */
    public CalculatorImpl() {
        String loggerName = HessianSkeleton.class.getName();
        Logger.getLogger(loggerName).setLevel(Level.SEVERE);
    }
    
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
    public int countNumberOfUppercaseLetters(String text) {
        if (text == null) {
            return 0;
        }
 
        int numberOfUppercaseLetters = 0;
        char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 'A' && c[i] <= 'B') {
                numberOfUppercaseLetters++;
            }
        }
        return numberOfUppercaseLetters;
    }
    
    /**
     * {@inheritDoc}
     */
    public ComplexNumber add(ComplexNumber cn1, ComplexNumber cn2) {
        ComplexNumber result = new ComplexNumber(0, 0);
        result.setReal(cn1.getReal() + cn2.getReal());
        result.setImag(cn1.getImag() + cn2.getImag());
        return result;
    }
}
