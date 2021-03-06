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

/**
 * This interceptor implements a safety facade. It allows to catch any
 * exceptions thrown by the proxied bean and to handle them by exception
 * handlers.
 *
 * <p/>Don't setup this interceptor directly as long as you don't need access to
 * it. Instead use the {@link
 * ch.elca.el4j.services.exceptionhandler.SafetyFacadeFactoryBean}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.services.exceptionhandler.SafetyFacadeFactoryBean
 */
public class SafetyFacadeInterceptor
	extends AbstractExceptionHandlerInterceptor {
	
	/** The exception configurations. */
	ExceptionConfiguration[] m_exceptionConfigurations;
	
	/**
	 * Default constructor. Configures the interceptor to handle only those
	 * exceptions that are <b>not</b> defined in the signature (excluding
	 * unchecked exceptions, which are handled always).
	 */
	public SafetyFacadeInterceptor() {
		super();
		// change this behaviour in the SafetyFacadeFactoryBean too
		// (to be done manually since Java doesn't support multi inheritance).
		setForwardSignatureExceptions(true);
		setHandleRTSignatureExceptions(true);
	}
	
	/**
	 * Sets the exception configurations.
	 *
	 * @param exceptionConfigurations
	 *      The exception configurations to set.
	 */
	public void setExceptionConfigurations(
			ExceptionConfiguration[] exceptionConfigurations) {
		m_exceptionConfigurations = exceptionConfigurations;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object handleException(Throwable t, MethodInvocation invocation)
		throws Throwable {
		
		return doHandleException(t, invocation, m_exceptionConfigurations);
	}
}
