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

/**
 * This exception is thrown if the context of a context exception handler has
 * not been set appropriately.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.services.exceptionhandler.ContextExceptionHandlerInterceptor
 */
public class MissingContextException extends RuntimeException {

	/**
	 * Creates a new instance.
	 *
	 * @param message
	 *      The exception's message.
	 *
	 * @param cause
	 *      The exception should have been handled by the exception handling
	 *      framework.
	 */
	public MissingContextException(String message, Throwable cause) {
		super(message, cause);
	}
}
