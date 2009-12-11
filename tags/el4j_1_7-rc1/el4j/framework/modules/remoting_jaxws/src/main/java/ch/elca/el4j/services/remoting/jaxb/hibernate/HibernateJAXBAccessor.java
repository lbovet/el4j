/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.jaxb.hibernate;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;

/**
 * A JAXB Accessor that replaces uninitialized values by <code>null</code> to avoid LazyInitializationException.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @param <B> the bean type
 * @param <V> the value type
 *
 * @author clemenb (see https://forum.hibernate.org/viewtopic.php?f=1&t=998896)
 */
public class HibernateJAXBAccessor<B, V> extends Accessor<B, V> {

	/**
	 * The delegate accessor.
	 */
	private Accessor<B, V> delegate;
	
	/**
	 * The method to call for checking if value is uninitialized.
	 */
	private final Method hibernateInitializationCheck;

	/**
	 * @param delegate                        the delegate accessor
	 * @param hibernateInitializationCheck    the method to call for checking if value is uninitialized
	 */
	protected HibernateJAXBAccessor(Accessor<B, V> delegate, Method hibernateInitializationCheck) {
		super(delegate.getValueType());
		if (delegate == null) {
			throw new IllegalArgumentException("delegate must not be null");
		} else if (hibernateInitializationCheck == null) {
			throw new IllegalArgumentException("hibernateInitializationCheck must not be null");
		}

		this.delegate = delegate;
		this.hibernateInitializationCheck = hibernateInitializationCheck;
	}

	@Override
	public Accessor<B, V> optimize(JAXBContextImpl context) {
		delegate = delegate.optimize(context);
		return this;
	}

	@Override
	public V get(B bean) throws AccessorException {
		return hideLazy(delegate.get(bean));
	}

	@Override
	public void set(B bean, V value) throws AccessorException {
		delegate.set(bean, value);
	}

	/**
	 * @param value    the value to check
	 * @return         the value if it is initialized, otherwise <code>null</code> 
	 */
	protected V hideLazy(V value) {
		try {
			boolean isInitialized = (Boolean) hibernateInitializationCheck.invoke(null, new Object[] {value});
			if (isInitialized) {
				return value;
			} else {
				return null;
			}
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(HibernateJAXBAccessor.class);
			logger.error("Failed to determine state of Hibernate object or collection, assuming " + value
				+ " is initialized", e);
			return null;
		}
	}
}