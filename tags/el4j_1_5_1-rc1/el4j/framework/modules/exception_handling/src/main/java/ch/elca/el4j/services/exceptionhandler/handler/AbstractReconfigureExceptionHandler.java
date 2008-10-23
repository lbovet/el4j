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
import ch.elca.el4j.services.exceptionhandler.RetryException;

/**
 * This class simplifies the implementation of exception handlers that use
 * another implementation with the same semantic to fulfil the callers
 * invocation. This is achieved by reconfiguring the class that is advised by
 * this exception handler's interceptor.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.services.exceptionhandler.handler.AbstractSwappableTargetExceptionHandler
 */
public abstract class AbstractReconfigureExceptionHandler extends
		AbstractRetryExceptionHandler {

	/**
	 * {@inheritDoc}
	 */
	protected Object retry(Throwable t,
			AbstractExceptionHandlerInterceptor exceptionInvoker,
			MethodInvocation invocation, Log logger) throws Throwable {

		reconfigure(t, invocation, logger);
		throw new RetryException(getRetries());
	}

	/**
	 * Subclasses have to reconfigure the advised target.
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
	 */
	protected abstract void reconfigure(Throwable t,
			MethodInvocation invocation, Log logger);
}
