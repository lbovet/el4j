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
package ch.elca.el4j.util.socketstatistics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Delegator for the use of SocketImpl using reflection.
 *
 * Inspired by http://www.javaspecialists.eu/archive/Issue168.html
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class ReflectiveDelegator {

	/**
	 * Source class of delegation.
	 */
	private final Object m_source;

	/**
	 * Target calls of delegation.
	 */
	private final Object m_delegate;

	/**
	 * Superclass for delegation-calls.
	 */
	private final Class m_superclass;

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            source class for reflection
	 * @param superclass
	 *            class used for delegation
	 * @param delegate
	 *            reference to class for delegated method calls
	 */
	public ReflectiveDelegator(Object source, Class superclass, Object delegate) {
		this.m_source = source;
		this.m_superclass = superclass;
		this.m_delegate = delegate;
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            source class for reflection
	 * @param superclass
	 *            class used for delegation
	 * @param delegateClassName
	 *            name of the class for delegated method calls
	 */
	public ReflectiveDelegator(Object source, Class superclass, String delegateClassName) {
		try {
			this.m_source = source;
			this.m_superclass = superclass;
			Class implCl = Class.forName(delegateClassName);
			Constructor delegateConstructor = implCl.getDeclaredConstructor();
			delegateConstructor.setAccessible(true);
			this.m_delegate = delegateConstructor.newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new DelegationException("Could not make delegate object", e);
		}
	}

	/**
	 * Method for invocation on delegated class.
	 * 
	 * @param <T>
	 *            generic return type
	 * @param args
	 *            arguments to be passed to the original method
	 * @return return value of original method
	 */
	public final <T> T invoke(Object... args) {
		try {
			String methodName = extractMethodName();
			Method method = findMethod(methodName, args);
			@SuppressWarnings("unchecked")
			T t = (T) invoke0(method, args);
			return t;
		} catch (NoSuchMethodException e) {
			throw new DelegationException(e);
		}
	}

	/**
	 * Internal implementation for invocation on delegated class.
	 * 
	 * @param method
	 *            method to be called
	 * @param args
	 *            arguments to be passed to the called method
	 * @return return value of original method
	 */
	private Object invoke0(Method method, Object[] args) {
		try {
			writeFields(m_superclass, m_source, m_delegate);
			method.setAccessible(true);
			Object result = method.invoke(m_delegate, args);
			writeFields(m_superclass, m_delegate, m_source);
			return result;
		} catch (RuntimeException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new DelegationException(e.getCause());
		} catch (Exception e) {
			throw new DelegationException(e);
		}
	}

	/**
	 * @param clazz
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	private void writeFields(Class clazz, Object from, Object to) throws Exception {
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			field.set(to, field.get(from));
		}
	}

	/**
	 * Returns name of calling method using stacktrace.
	 * 
	 * @return name of the calling method
	 */
	private String extractMethodName() {
		Throwable t = new Throwable();
		String methodName = t.getStackTrace()[2].getMethodName();
		return methodName;
	}

	/**
	 * Search for method with matching name and signature.
	 * 
	 * @param methodName
	 *            name of the method to find for delegated call
	 * @param args
	 *            list of arguments the found method has to take
	 * @return found method
	 * @throws NoSuchMethodException
	 *             thrown if no matching method found for delegation
	 */
	private Method findMethod(String methodName, Object[] args) throws NoSuchMethodException {
		Class<?> clazz = m_superclass;
		if (args.length == 0) {
			return clazz.getDeclaredMethod(methodName);
		}
		Method match = null;
		// search inside declared class methods
		next: for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				Class<?>[] classes = method.getParameterTypes();
				if (classes.length == args.length) {
					for (int i = 0; i < classes.length; i++) {
						Class<?> argType = classes[i];
						argType = convertPrimitiveClass(argType);
						if (!argType.isInstance(args[i])) {
							continue next;
						}
					}
					if (match == null) {
						match = method;
					} else {
						throw new DelegationException("Duplicate matches");
					}
				}
			}
		}
		if (match != null) {
			return match;
		}

		// if no fitting method found, also search inside inherited methods
		next: for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				Class<?>[] classes = method.getParameterTypes();
				if (classes.length == args.length) {
					for (int i = 0; i < classes.length; i++) {
						Class<?> argType = classes[i];
						argType = convertPrimitiveClass(argType);
						if (!argType.isInstance(args[i])) {
							continue next;
						}
					}
					if (match == null) {
						match = method;
					} else {
						throw new DelegationException("Duplicate matches");
					}
				}
			}
		}
		if (match != null) {
			return match;
		}

		// no matching method found
		throw new DelegationException("Could not find method: " + methodName);
	}

	/**
	 * Converts primitives to corresponding java class definitions.
	 * 
	 * @param primitive
	 *            primitive for conversion
	 * @return corresponding java class
	 */
	private Class<?> convertPrimitiveClass(Class<?> primitive) {
		if (primitive.isPrimitive()) {
			if (primitive == int.class) {
				return Integer.class;
			}
			if (primitive == boolean.class) {
				return Boolean.class;
			}
			if (primitive == float.class) {
				return Float.class;
			}
			if (primitive == long.class) {
				return Long.class;
			}
			if (primitive == double.class) {
				return Double.class;
			}
			if (primitive == short.class) {
				return Short.class;
			}
			if (primitive == byte.class) {
				return Byte.class;
			}
			if (primitive == char.class) {
				return Character.class;
			}
		}
		return primitive;
	}

	/**
	 * Method for explicit delegation of method call.
	 * 
	 * @param methodName
	 *            name of method to be called
	 * @param parameters
	 *            parameters to be passed to called method
	 * @return return value of called method
	 */
	public DelegatorMethodFinder delegateTo(String methodName, Class<?>... parameters) {
		return new DelegatorMethodFinder(methodName, parameters);
	}

	/**
	 * Internal class for explicitly defined method calls using delegateTo.
	 */
	public class DelegatorMethodFinder {
		/**
		 * Method to be used for call.
		 */
		private final Method method;

		/**
		 * Constructor.
		 * 
		 * @param methodName
		 *            name of the method to find for delegated call
		 * @param parameterTypes
		 *            signature of method to be called
		 */
		public DelegatorMethodFinder(String methodName, Class<?>... parameterTypes) {
			try {
				method = m_superclass.getDeclaredMethod(methodName, parameterTypes);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new DelegationException(e);
			}
		}

		/**
		 * Method for invocation on delegated class.
		 * 
		 * @param <T>
		 *            generic return type
		 * @param parameters
		 *            arguments to be passed to the original method
		 * @return return value of original method
		 */
		public <T> T invoke(Object... parameters) {
			@SuppressWarnings("unchecked")
			T t = (T) ReflectiveDelegator.this.invoke0(method, parameters);
			return t;
		}
	}
}
