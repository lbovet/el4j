/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.services.exceptionhandler;

/**
 * Interface for testing purposes.
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
public interface A extends Adder {

    /**
     * Sets the number of retries.
     * 
     * @param retries
     *      Number of retries.
     */
    public abstract void setRetries(int retries);

    /**
     * Sets the adder to delegate calls to.
     * 
     * @param adder
     *      The adder to set.
     */
    public abstract void setAdder(Adder adder);

    /**
     * Divides the two integers.
     * 
     * @param a
     *      Dividend.
     *      
     * @param b
     *      Divisor.
     *      
     * @return Returns a / b.
     */
    public abstract int div(int a, int b);

    /**
     * Throws an application level exception.
     * 
     * @throws ApplicationException
     *      The sample exception.
     */
    public abstract void throwException() throws ApplicationException;

    /**
     * Throws a runtime exception.
     */
    public abstract void throwRTException();

    /**
     * Concatenates the two Strings.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return Returns the concatenation of the two strings.
     */
    public abstract String concat(String a, String b);

    /**
     * {@inheritDoc}
     */
    public abstract int add(int a, int b);

    /**
     * Subtracts the given two numbers.
     * 
     * @param a
     *      The first number.
     *      
     * @param b
     *      The second number.
     *      
     * @return Returns a - b.
     */
    public abstract int sub(int a, int b);

}