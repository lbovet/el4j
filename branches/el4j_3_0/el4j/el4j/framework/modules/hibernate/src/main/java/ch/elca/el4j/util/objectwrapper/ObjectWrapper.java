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
package ch.elca.el4j.util.objectwrapper;

import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;

/**
 * Main class of the object wrapper package. The purpose of this package is to treat objects of different classes
 * as if they implemented exactly the utility interfaces we want to use. This prevents us from having to include the
 * interfaces in every domain class.
 * <p>
 * For example, Keyed is an interface indicating an object has a key. But domain classes may have keys without 
 * implementing the keyed interface. As long as we have an algorithm to get/set keys on an object, 
 * we can pretend it does actually implement Keyed.
 * <p>
 * All methods and the interfaces they create throw ObjectWrapperRTException if an object does not have the interface we
 * want and we cannot emulate it either. As it is a RuntimeException it is not
 * always declared; getting such an exception is under most circumstances non-recoverable anyway. 
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class ObjectWrapper {

	/**
	 * All known wrappers, created as map Wrappable->Implementation.
	 */
	private Map<Class<? extends Wrappable>, AbstractWrapper> m_wrappables;
	
	/**
	 * Set up the apsects object. 
	 */
	public ObjectWrapper() {
		m_wrappables = new HashMap<Class<? extends Wrappable>, AbstractWrapper>();
	}
	
	/**
	 * Get the wrappables.
	 * @return The wrappables.
	 */
	public Map<Class<? extends Wrappable>, AbstractWrapper> getWrappables() {
		return m_wrappables;
	}

	/**
	 * Setter for wrappabless.
	 * @param wrappables The new wrappables to set.
	 */
	public void setWrappables(Map<Class<? extends Wrappable>, AbstractWrapper> wrappables) {
		m_wrappables = wrappables;
	}

	/**
	 * Register a wrappable.
	 * @param wrappableClass The wrappable class to register.
	 * @param implementation The implementation.
	 */
	public void addWrappable(Class<? extends Wrappable> wrappableClass, AbstractWrapper implementation) {
		m_wrappables.put(wrappableClass, implementation);
	}
	
	/**
	 * Cast an object to a wrapper.
	 * @param <T> The type parameter. (This eliminates a cast of the return value.)
	 * @param wrappable The wrappable to cast the object to.
	 * @param object the object.
	 * @return The wrapped object.
	 */
	public <T extends Wrappable> T wrap(Class<T> wrappable, Object object) {
		// If the object in question already implements this interface, just return it.
		
		if (wrappable.isAssignableFrom(object.getClass())) {
			// Safe because we've just checked it against the class object.
			@SuppressWarnings("unchecked")
			T cast = (T) object;
			return cast;
		}
		
		
		AbstractWrapper impl = null;
		for (Class<? extends Wrappable> candidate : m_wrappables.keySet()) {
			if (candidate == wrappable) {
				impl = m_wrappables.get(candidate);
			}
		}
		if (impl == null) {
			throw new IllegalArgumentException("Wrappable " + wrappable + " is not registered.");
		}

		try {
			impl = (AbstractWrapper) impl.clone();
		} catch (CloneNotSupportedException e) {
			throw new ObjectWrapperRTException("Clone on wrappable failed.", e);
		}
		
		impl.setTarget(object);
		impl.setWrapper(this);
		try {
			impl.create();
		} catch (ObjectWrapperRTException ex) {
			// Provide a standard message and chain the exception.
			throw new ObjectWrapperRTException("Failed to create wrappable " + wrappable 
				+ " for object of class " + object.getClass(), ex);
		}
		
		// An exception here is an error in the set-up of the APSECTS mapping.
		// As long as that is ok this cast is safe.
		@SuppressWarnings("unchecked")
		T result = (T) impl;
		return result;
	}
	
	/**
	 * Check if a wrappable is registered. Note that even if it is not, an object might
	 * implement the interface already in which case wrap() will succeed on it.
	 * To be sure of this, use wrappablePresent(wrappableClass, objectClass).
	 * @param cls The wrappable class.
	 * @return <code>true</code> if the wrappable exists.
	 */
	public boolean wrappablePresent(Class<? extends Wrappable> cls) {
		return m_wrappables.containsKey(cls);
	}
	
	/**
	 * Check if a call to wrap() has a chance of creating a wrapper on an object class.
	 * This is the case if a) the object implements the interface or b) the wrappable is registered.
	 * Note that even if this returns true, the wrapper implementation may throw an expection
	 * in create(). This can happen if the wrapper is present but does not apply to this class.
	 * @param wrappableClass The wrappable class. 
	 * @param objectClass The object class.
	 * @return <code>false</code> if there is definitely no way to create the wrapper for this
	 * class, <code>true</code> if it can be tried (and will only fail if the implementation does). 
	 */
	public boolean wrappablePresent(Class<? extends Wrappable> wrappableClass, Class<?> objectClass) {
		return wrappableClass.isAssignableFrom(objectClass) || wrappablePresent(wrappableClass);
	}
}
