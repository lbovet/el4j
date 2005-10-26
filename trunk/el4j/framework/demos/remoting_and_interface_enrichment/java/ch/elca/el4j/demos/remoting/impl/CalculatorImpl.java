/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.demos.remoting.impl;

import ch.elca.el4j.demos.remoting.Calculator;
import ch.elca.el4j.demos.remoting.CalculatorException;


/**
 * This class is the implementation of the calculator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
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
}
