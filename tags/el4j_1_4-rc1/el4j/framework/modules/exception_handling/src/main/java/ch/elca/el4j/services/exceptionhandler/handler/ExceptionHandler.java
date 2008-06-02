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

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.RetryException;

/**
 * An exception handler is a expert for handling some kind of exceptions.
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
     * @throws RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Any exception thrown by the handler.
     */
    public Object handleException(Throwable t,
        AbstractExceptionHandlerInterceptor exceptionInvoker,
        MethodInvocation invocation) throws RetryException, Throwable;
}
