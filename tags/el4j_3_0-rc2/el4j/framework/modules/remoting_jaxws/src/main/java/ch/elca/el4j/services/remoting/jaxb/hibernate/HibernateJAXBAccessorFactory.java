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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.bind.AccessorFactory;
import com.sun.xml.bind.AccessorFactoryImpl;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;

/**
 * A JAXB Accessor factory that uses {@link HibernateJAXBAccessor} if possible.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author clemenb (see https://forum.hibernate.org/viewtopic.php?f=1&t=998896)
 */
public class HibernateJAXBAccessorFactory implements AccessorFactory {

	/**
	 * The delegate accessor factory.
	 */
	private final AccessorFactory delegate;

	/**
	 * The method to call for checking if value is uninitialized.
	 */
	private Method hibernateInitializationCheck;

	/**
	 * Create a new AccessorFactory that never throws LazyInitializationException during (un)marshaling.
	 */
	public HibernateJAXBAccessorFactory() {
		this(AccessorFactoryImpl.getInstance());
	}

	/**
	 * @param delegate    the delegate accessor factory
	 */
	@SuppressWarnings("unchecked")
	public HibernateJAXBAccessorFactory(AccessorFactory delegate) {
		this.delegate = delegate;
		try {
			Class hibernate = Class.forName("org.hibernate.Hibernate");
			hibernateInitializationCheck = hibernate.getMethod("isInitialized", Object.class);
			Logger logger = LoggerFactory.getLogger(HibernateJAXBAccessorFactory.class);
			logger.info("Detected Hibernate: Enabled "
				+ "hiding of uninitialized lazy objects and collections during XML marshalling.");
		} catch (ClassNotFoundException e) {
			hibernateInitializationCheck = null;
			Logger logger = LoggerFactory.getLogger(HibernateJAXBAccessorFactory.class);
			logger.info("Hibernate was not detected: Disabled "
				+ "hiding of uninitialized lazy objects and collections during XML marshalling.");
		} catch (Exception e) {
			hibernateInitializationCheck = null;
			Logger logger = LoggerFactory.getLogger(HibernateJAXBAccessorFactory.class);
			logger.warn("Detected Hibernate, but failed "
				+ "to enable hiding of uninitialized lazy objects and collections during XML marshalling.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Accessor createFieldAccessor(Class bean, Field field, boolean readOnly) throws JAXBException {
		Accessor accessor = delegate.createFieldAccessor(bean, field, readOnly);

		if (hibernateInitializationCheck == null) {
			return accessor;
		} else {
			return new HibernateJAXBAccessor(accessor, hibernateInitializationCheck);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Accessor createPropertyAccessor(Class bean, Method getter, Method setter) throws JAXBException {
		Accessor accessor = delegate.createPropertyAccessor(bean, getter, setter);

		if (hibernateInitializationCheck == null) {
			return accessor;
		} else {
			return new HibernateJAXBAccessor(accessor, hibernateInitializationCheck);
		}
	}
}