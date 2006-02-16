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

package ch.elca.el4j.services.exceptionhandler.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.RetryException;

/**
 * This class allows to specify a number of exception handlers that are
 * called in sequence until the first succeeds, i.e. it doesn't throw an
 * exception. In this case, it returns its result. If all exception handler
 * fail, the last caught exception is thrown.
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
public class SequenceExceptionHandler extends AbstractExceptionHandler {

    /** The exception handlers. */
    private ExceptionHandler[] m_exceptionHandlers;
    
    /**
     * Sets the exception handlers.
     * 
     * @param exceptionHandlers
     *      The exception handlers to set.
     */
    public void setExceptionHandlers(
            ExceptionHandler[] exceptionHandlers) {
        m_exceptionHandlers = exceptionHandlers;
    }

    /**
     * {@inheritDoc}
     */
    protected Object handleException(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation, Log logger) throws Throwable {
        
        Throwable lastThrowable = t;
        
        for (int i = 0; i < m_exceptionHandlers.length; i++) {
            try {
                return m_exceptionHandlers[i].handleException(
                        lastThrowable, exceptionInvoker, invocation);
                
            } catch (RetryException re) {
                throw re;
            } catch (Throwable lt) {
                lastThrowable = lt;
                
                if (logger.isDebugEnabled()) {
                    StringBuffer buffer = new StringBuffer("Handler [");
                    buffer.append(m_exceptionHandlers[i].getClass().getName());
                    buffer.append("] failed.");
                    if (i < m_exceptionHandlers.length) {
                        buffer.append(" Trying next one.");
                    }
                    logger.debug(buffer.toString(), lt);
                }
            }
        }

        throw lastThrowable;
    }
}
