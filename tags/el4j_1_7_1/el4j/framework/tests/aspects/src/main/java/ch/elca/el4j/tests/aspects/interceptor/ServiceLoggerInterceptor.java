/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.aspects.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;

import ch.elca.el4j.tests.aspects.util.InvocationMonitor;

/**
 * Logging interceptor for services.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @author Reynald Borer (RBR)
 */
public class ServiceLoggerInterceptor extends AbstractTraceInterceptor {
	/**
	 * Generated serial version uid.
	 */
	private static final long serialVersionUID = -2635477201989637313L;

	/**
	 * Is the invoked interception method.
	 * 
	 * @param pjp Is the interception point to proceed further.
	 * @return Returns the result of the proceeded method.
	 * @throws Throwable In case of an error.
	 */
	protected Object test(ProceedingJoinPoint pjp) throws Throwable {
		InvocationMonitor.incrementCounter(ServiceLoggerInterceptor.class);
		Object result = pjp.proceed();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
		return null;
	}
}
