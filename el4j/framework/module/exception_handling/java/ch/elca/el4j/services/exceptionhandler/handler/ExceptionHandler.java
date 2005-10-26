/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;

/**
 * An exception handler is a expert for handling some kind of exceptions.
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
public interface ExceptionHandler {

    /**
     * Handles the given exception that occurred in the invocation of the
     * given {@link MethodInvocation}.
     * 
     * @param t
     *      The throwable to handle.
     *      
     * @param exceptionInvoker
     *      The exception invoker that called this exception handler.
     * 
     * @param invocation
     *      The invocation which has resulted in the given throwable.
     *      
     * @return Returns an object that is treated as the original invocation's
     *      return value.
     * 
     * @throws ch.elca.el4j.services.exceptionhandler.RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Any exception thrown by the handler.
     */
    public Object handleException(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation) throws Throwable;
}
