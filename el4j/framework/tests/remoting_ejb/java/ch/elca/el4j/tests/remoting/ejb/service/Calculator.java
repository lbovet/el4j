/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.tests.remoting.ejb.service;

/**
 * Example class used to test the EJB integration.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public interface Calculator {
    
    /**
     * Calculates the power of <code>x</code> and <code>y</code>.
     * 
     * @param x
     *      The base;
     *      
     * @param y
     *      The exponent.
     *      
     * @return Returns <code>x</code> powered by <code>y</code>.
     */
    public int pow(double x, double y);
    
    /**
     * Throws an Exception.
     * 
     * <p><b>Note</b>: EJB 2.1 spec says that "an application exception class
     * must be a subclass (direct or indirect) of java.lang.Exception". JBoss
     * also accepts java.lang.Exception whereas WebLogic does not.
     * 
     * @throws Exception
     *      The test Exception.
     */
    public void throwException() throws Exception;
    
    /**
     * Throws a runtime exception that is wrapped on server and unwrapped on
     * client side transparently.
     */
    public void throwRTException();
    
    /**
     * Throws a runtime exception that is not available on client side.
     * 
     * @see ch.elca.el4j.tests.remoting.ejb.service.impl.CalculatorImpl.FooRTException
     *      for details about how to drive this test.
     */
    public void throwFooRtException();
    
    /**
     * Throws a valid application exception.
     * 
     * @throws IllegalAccessException
     *      The test exception.
     */
    public void throwIllegalAccessException() throws IllegalAccessException;
}
