/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.demos.remoting;


/**
 * This interface is a calculator.
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
public interface Calculator {
    /**
     * This method calculates the area of a rectangle.
     * 
     * @param a
     *            Is the first side.
     * @param b
     *            Is the second side.
     * @return Returns the area of the triangle.
     */
    public double getArea(double a, double b);
    
    /**
     * This method throws an exception for test reason.
     * 
     * @throws CalculatorException will be thrown every time.
     */
    public void throwMeAnException() throws CalculatorException;
    
    /**
     * This method counts all uppercase letters of a text. 
     * 
     * @param text Is the object to analyze.
     * @return Returns the number of uppercase letters.
     */
    public int countNumberOfUppercaseLetters(String text);
}