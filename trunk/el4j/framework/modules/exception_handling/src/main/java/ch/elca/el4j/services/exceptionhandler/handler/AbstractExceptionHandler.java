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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;

/**
 * This class provides a logging setup that allows to use the target's log
 * as well as the exception handler's.
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
public abstract class AbstractExceptionHandler implements ExceptionHandler {

	/** The logger. */
	private Logger m_logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Sets whether the dynamic logger which is the one of the target class.
	 *
	 * @param useDyanmicLog
	 *      If <code>true</code> the target class' logger will be used or
	 *      the one of the concrete exception handler otherwise.
	 */
	public void setUseDynamicLogger(boolean useDyanmicLog) {
		m_logger = useDyanmicLog ? null : LoggerFactory.getLogger(getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object handleException(Throwable t,
			AbstractExceptionHandlerInterceptor exceptionInvoker,
			MethodInvocation invocation) throws Throwable {
		
		return handleException(t, exceptionInvoker, invocation,
				getLoggerForInvocation(invocation));
	}

	/**
	 * Handles the given throwable thrown by the method invocation. This method
	 * is called with the appropriate logger.
	 *
	 * @param t
	 *      The exception thrown in the method invocation.
	 * @param exceptionInvoker
	 *      The exception invoker that called this exception handler.
	 * @param invocation
	 *      The original method invocation.
	 * @param logger
	 *      The logger to be used by subclasses.
	 * @return Returns an object that is treated as the original invocation's
	 *      return value.
	 * @throws Throwable
	 *      Any exception thrown by the handler.
	 */
	protected abstract Object handleException(Throwable t,
		AbstractExceptionHandlerInterceptor exceptionInvoker,
		MethodInvocation invocation, Logger logger) throws Throwable;

	/**
	 * Pretty-prints the invocation. This method is intended to be used for
	 * logging purposes. If the dynamic logger is used only the method's name
	 * is included.
	 *
	 * @param invocation
	 *      The called method invocation.
	 *
	 * @return Returns the method invocation, pretty-printed.
	 */
	protected String getInvocationDescription(MethodInvocation invocation) {
		StringBuffer buffer = new StringBuffer("method '");
		buffer.append(invocation.getMethod().getName());
		buffer.append("'");
		if (m_logger != null) {
			buffer.append(" of class [");
			buffer.append(invocation.getThis().getClass().getName());
			buffer.append("]");
		}
		return buffer.toString();
	}
	
	/**
	 * Determines the logger to use.
	 *
	 * @param invocation
	 *      The current method invocation.
	 *
	 * @return Returns this class' logger, if dynamic logging is disabled and
	 *      otherwise the method invocation target's one.
	 */
	private Logger getLoggerForInvocation(MethodInvocation invocation) {
		return (m_logger != null)
			? m_logger : LoggerFactory.getLogger(invocation.getThis().getClass());
	}
}
