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

package ch.elca.el4j.services.exceptionhandler.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.RetryException;

/**
 * This class implements an exception handler that tries to call the target
 * several times after waiting a configurable delay.
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
public class RetryExceptionHandler extends AbstractRetryExceptionHandler {

    /**
     * Time to wait between two successive invocations in milli seconds. Default
     * is <code>0</code>.
     */
    private int m_sleepMillis = 0;
    
    /**
     * Sets the amount of time in milli seconds between two successive
     * invocations.
     * 
     * @param sleepMillis
     *      The time in milli seconds to wait.
     */
    public void setSleepMillis(int sleepMillis) {
        m_sleepMillis = sleepMillis;
    }

    /**
     * {@inheritDoc}
     */
    protected Object retry(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation, Log logger) throws Throwable {
        
        if (m_sleepMillis > 0) {
            Thread.sleep(m_sleepMillis);
        }
        
        throw new RetryException(getRetries());
    }
}
