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

package ch.elca.el4j.services.exceptionhandler;

import org.aopalliance.intercept.MethodInvocation;

import ch.elca.el4j.services.exceptionhandler.handler.ExceptionHandler;

/**
 * The exception configuration is used to determine, whether a exception handler
 * is able to handle a given exception that was thrown in a given method
 * invocation.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public interface ExceptionConfiguration {

	/**
	 * Requests whether this exception configuration is able to handle the
	 * given exception that was thrown in the given method invocation.
	 *
	 * @param t
	 *      The exception to handle.
	 *
	 * @param invocation
	 *      The invocation in which the exception was thrown.
	 *
	 * @return Returns <code>true</code> if this exception configuratin is able
	 *      to handle the exception, <code>false</code> otherwise.
	 */
	public boolean handlesExceptions(Throwable t, MethodInvocation invocation);
	
	/**
	 * @return Returns the exception handler.
	 */
	public ExceptionHandler getExceptionHandler();
}
