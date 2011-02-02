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

import java.util.Properties;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import ch.elca.el4j.tests.aspects.util.InvocationMonitor;

/**
 * Wrapped transaction interceptor to count invocation of it.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class ObservedTransactionInterceptor extends TransactionInterceptor {
	/**
	 * Generated serialVersionUID.
	 */
	private static final long serialVersionUID = 3763830156550688091L;

	/**
	 * @see super{@link TransactionInterceptor#TransactionInterceptor()}
	 */
	public ObservedTransactionInterceptor() {
		super();
	}

	/**
	 * @see {@link TransactionInterceptor#TransactionInterceptor(PlatformTransactionManager, Properties)}
	 */
	public ObservedTransactionInterceptor(PlatformTransactionManager ptm,
		Properties attributes) {
		super(ptm, attributes);
	}
	
	/**
	 * @see {@link TransactionInterceptor#TransactionInterceptor(
	 * 			PlatformTransactionManager, TransactionAttributeSource)}
	 */
	public ObservedTransactionInterceptor(PlatformTransactionManager ptm,
		TransactionAttributeSource tas) {
		super(ptm, tas);
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		InvocationMonitor.incrementCounter(ObservedTransactionInterceptor.class);
		return super.invoke(invocation);
	}
}
