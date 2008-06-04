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

package ch.elca.el4j.services.exceptionhandler.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.SafetyFacadeInterceptor;

/**
 * Retry exception handlers force the {@link
 * ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor}
 * to rerun the complete invocation. They signal a retry with the
 * {@link ch.elca.el4j.services.exceptionhandler.RetryException} which contains
 * the expected number of retries. The exception handler interceptor however is
 * free to choose another number of retries (e.g. if different retry exception
 * handler are used).
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
public abstract class AbstractRetryExceptionHandler
    extends AbstractExceptionHandler {

    /** The default number of retries. */
    private static final int DEFAULT_RETRIES = 5;
    
    /** Number of retires. Default is <code>5</code>. */
    private int m_retries = DEFAULT_RETRIES;
    
    /**
     * Sets the number of retires. Default is <code>5</code>.
     * 
     * @param retries
     *      The number of retries to set.
     */
    public void setRetries(int retries) {
        m_retries = retries;
    }
    
    /**
     * @return Returns the number of expected retires.
     */
    protected int getRetries() {
        return m_retries;
    }

    /**
     * {@inheritDoc}
     */
    public Object handleException(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation, Log logger) throws Throwable {
        
        if (SafetyFacadeInterceptor.getRetries() == -1
                || SafetyFacadeInterceptor.getRetries() > 0) {
            return retry(t, exceptionInvoker, invocation, logger);
            
        } else {
            throw t;
        }
    }

    /**
     * Creates a new retry exception. Subclasses may also do some other tasks
     * (e.g. waiting). This method is called only if the number of retires is
     * not already exceeded.
     * 
     * @param t
     *      The exception thrown in the method invocation.
     *      
     * @param exceptionInvoker
     *      The exception invoker that called this exception handler.
     * 
     * @param invocation
     *      The original method invocation.
     * @param logger
     *      The logger to be used by subclasses.
     *      
     * @return Returns an object that is treated as the original invocation's
     *      return value.
     *      
     * @throws Throwable
     *      Whenever something goes wrong.
     */
    protected abstract Object retry(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation, Log logger) throws Throwable;
}
