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
package org.springframework.transaction.interceptor;

/**
 * Class to represent the transaction attribute source as a pointcut. This is used in aop spring xml config.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class El4jTransactionAttributeSourcePointcut extends TransactionAttributeSourcePointcut {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The pointcut object, transaction attribute source.
	 */
	private final TransactionAttributeSource tas;

	/**
	 * Constructor, where the transaction interceptor is passed to support old transaction def style.
	 * @param ti Is the transaction interceptor, where the transaction attribute source should be in there.
	 */
	public El4jTransactionAttributeSourcePointcut(TransactionInterceptor ti) {
		this.tas = ti != null ? ti.getTransactionAttributeSource() : null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TransactionAttributeSource getTransactionAttributeSource() {
		return tas;
	}
}
