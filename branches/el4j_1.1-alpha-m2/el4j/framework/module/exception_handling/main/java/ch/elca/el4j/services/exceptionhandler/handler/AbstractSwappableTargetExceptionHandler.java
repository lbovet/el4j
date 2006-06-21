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
import org.springframework.aop.target.HotSwappableTargetSource;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.InappropriateHandlerException;
import ch.elca.el4j.services.exceptionhandler.RetryException;

/**
 * This class simplifies the implementation of exception handlers that use
 * another implementation with the same interface to fulfil the callers
 * invocation.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.services.exceptionhandler.handler.AbstractReconfigureExceptionHandler
 */
public abstract class AbstractSwappableTargetExceptionHandler extends
        AbstractRetryExceptionHandler {

    /** The TargetSource to change the target on. */
    private HotSwappableTargetSource m_swapper;
    
    /**
     * Sets the TargetSource that is used in the proxy, where the target has to
     * be swapped.
     * 
     * @param swapper
     *      The TargetSource to set.
     */
    public void setSwapper(HotSwappableTargetSource swapper) {
        m_swapper = swapper;
    }

    /**
     * {@inheritDoc}
     */
    protected Object retry(Throwable t,
        AbstractExceptionHandlerInterceptor exceptionInvoker,
        MethodInvocation invocation, Log logger) 
        throws InappropriateHandlerException, RetryException {
        
        try {
            Object newTarget = getNewTarget(
                    m_swapper.getTarget(), t, invocation, logger);
            m_swapper.swap(newTarget);
            
        } catch (InappropriateHandlerException ihe) {
            throw ihe;
        } catch (Throwable nt) {
            throw new RetryException(getRetries());
        }
        
        throw new RetryException(getRetries(), m_swapper);
    }

    /**
     * Determines a new target to be used by the proxy.
     * 
     * @param current
     *      The current target which the proxy is working on.
     * 
     * @param t
     *      The exception that caused this strategy. Subclasses may distinguish
     *      between different exception types.
     *      
     * @param invocation
     *      The original invocation that failed.
     *      
     * @param logger
     *      The logger.
     *      
     * @return Returns an alternative bean that implements the same interface
     *      as the original one.
     *      
     * @throws RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Whenever something goes wrong.
     */
    protected abstract Object getNewTarget(Object current, Throwable t,
        MethodInvocation invocation, Log logger) 
        throws RetryException, Throwable;
}
