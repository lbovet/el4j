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

package ch.elca.el4j.tests.remoting.service;


import javax.jws.WebService;

/**
 * This interface is a calculator.
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
@WebService
public interface CalculatorJaxws {
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
     * This method throws a special exception for test reason.
     * 
     * @param action Is the dynamic part of the thrown exception.
     * @throws SpecialCalculatorException will be thrown every time.
     */
    public void throwMeASpecialException(String action) 
        throws SpecialCalculatorException;

    /**
     * This method counts all uppercase letters of a text. 
     * 
     * @param text Is the object to analyze.
     * @return Returns the number of uppercase letters.
     */
    public int countNumberOfUppercaseLetters(String text);
    
    /**
     * This method does an echo of the given object.
     * 
     * @param o Is the object to echo.
     * @return Returns the received object.
     */
    public CalculatorValueObjectJaxws echoValueObjectJaxws(CalculatorValueObjectJaxws o);
}