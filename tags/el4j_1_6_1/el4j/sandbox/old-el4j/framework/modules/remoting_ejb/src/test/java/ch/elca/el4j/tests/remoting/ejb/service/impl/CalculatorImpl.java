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

package ch.elca.el4j.tests.remoting.ejb.service.impl;

import java.io.Serializable;

import ch.elca.el4j.tests.remoting.ejb.service.Calculator;

/**
 * Calculator implementation used for testing.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class CalculatorImpl implements Calculator, Serializable {

    /**
     * {@inheritDoc}
     */
    public int pow(double x, double y) {
        return (int) Math.pow(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void throwException() throws Exception {
        throw new Exception("A test exception.");
    }

    /**
     * {@inheritDoc}
     */
    public void throwRTException() {
        throw new NullPointerException("A RTException");
    }

    /**
     * {@inheritDoc}
     */
    public void throwFooRtException() {
        throw new FooRTException("A FooRTException");
    }

    /**
     * {@inheritDoc}
     */
    public void throwIllegalAccessException() throws IllegalAccessException {
        throw new IllegalAccessException("An IllegalAccessException");
    }
    
    
    /**
     * This exception is used to demonstrate the behaviour of a runtime
     * exception that is available on server side only. It gets wrapped on
     * server and unwrapped on client side transparently. The Exception thrown
     * on client side will be a {@link java.rmi.RemoteException}.
     */
    public static class FooRTException extends RuntimeException {

        /**
         * Creates a new instance.
         * 
         * @param message
         *      The exception's message.
         */
        public FooRTException(String message) {
            super(message);
        }
    }
}
