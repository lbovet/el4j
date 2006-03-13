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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import ch.elca.el4j.services.exceptionhandler.InappropriateHandlerException;
import ch.elca.el4j.services.exceptionhandler.handler.AbstractReconfigureExceptionHandler;

/**
 * This exception handler reconfigures the A bean, exchanging the concrete
 * {@link ch.elca.el4j.tests.services.exceptionhandler.Adder} implementation.
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
public class ReconfigureExceptionHandler extends
        AbstractReconfigureExceptionHandler {

    /** The replacement object. */
    private C m_c;
    
    /**
     * Sets the replacement object.
     * 
     * @param c
     *      The instance of C that is used as the replacement.
     */
    public void setC(C c) {
        m_c = c;
    }

    /**
     * {@inheritDoc}
     */
    protected void reconfigure(Throwable t, MethodInvocation invocation,
            Log logger) {
        
        if (!"add".equals(invocation.getMethod().getName())) {
            throw new InappropriateHandlerException();
        }
        
        ((A) invocation.getThis()).setAdder(m_c);
    }
}
