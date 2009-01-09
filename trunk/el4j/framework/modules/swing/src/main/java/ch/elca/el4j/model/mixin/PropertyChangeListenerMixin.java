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
package ch.elca.el4j.model.mixin;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableMap;
import org.jdesktop.swingbinding.validation.ValidationCapability;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;
import com.silvermindsoftware.hitch.validation.HibernateValidationCapability;

import ch.elca.el4j.util.codingsupport.AopHelper;


/**
 * Mixin to enable javaBeans event support and validation in ordinary POJOs.
 *
 * To see each fired property change, set the log4j level for this class to
 * debug
 * 
 * It also contains a simple implementation of the {@link SaveRestoreCapability}. All Java bean properties of
 * primitive or immutable (see IMMUTABLE_CLASSES) type can be saved and restored.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class PropertyChangeListenerMixin extends
		DelegatingIntroductionInterceptor implements
		PropertyChangeListenerCapability, SaveRestoreCapability,
		ValidationCapability, HibernateValidationCapability {
	
	/**
	 * The logger.
	 */
	private static final Log s_logger = LogFactory.getLog(PropertyChangeListenerMixin.class);
	
	/**
	 * A (incomplete) list of immutable classes.
	 */
	private static final Set<Class<?>> IMMUTABLE_CLASSES;
	
	static {
		IMMUTABLE_CLASSES = new HashSet<Class<?>>();
		IMMUTABLE_CLASSES.add(Integer.class);
		IMMUTABLE_CLASSES.add(String.class);
		IMMUTABLE_CLASSES.add(Float.class);
		IMMUTABLE_CLASSES.add(Double.class);
		IMMUTABLE_CLASSES.add(Byte.class);
		IMMUTABLE_CLASSES.add(Long.class);
		IMMUTABLE_CLASSES.add(Short.class);
		IMMUTABLE_CLASSES.add(Boolean.class);
		IMMUTABLE_CLASSES.add(Character.class);
	}
	/**
	 * Should bean property be overwritten by proxied property to speed up following accesses?
	 * This field is intended to be overwritten by subclasses.
	 */
	protected boolean m_writeBack = true;
	
	/**
	 * The support for property change notification.
	 */
	protected PropertyChangeSupport m_changeSupport;
	
	/**
	 * Getter-to-setter/setter-to-getter method cache.
	 */
	protected Map<Method, Method> m_methodCache = new HashMap<Method, Method>();

	/**
	 * The stored properties (a map containing the setter-method and its value).
	 */
	protected Map<Method, Object> m_backup = new HashMap<Method, Object>();
	
	/**
	 * Hibernate class validator.
	 */
	@SuppressWarnings("unchecked")
	protected ClassValidator m_classValidator;
	

	/**
	 * The constructor.
	 */
	public PropertyChangeListenerMixin() {
		// initialize later when reference to the model is known
		m_changeSupport = null;
		m_classValidator = null;
	}
	
	/**
	 * Wrap an object with the change tracking mixin.
	 *
	 * @param <T>      the object class
	 * @param object   the object to be wrapped
	 * @return         the same object wrapped with a spring proxy that has the
	 *                 {@link PropertyChangeListenerMixin} as {@link Advisor}
	 */
	public static <T> T addPropertyChangeMixin(T object) {
		if (!AopHelper.isProxiedBy(object, PropertyChangeListenerMixin.class)) {
			if (object instanceof List) {
				return (T) addPropertyChangeMixin((List) object);
			} else {
				return AopHelper.addAdvice(object, new PropertyChangeListenerMixin());
			}
		} else {
			return object;
		}
	}
	
	/**
	 * Wrap a list with the change tracking mixin.
	 *
	 * @param <T>      the object class
	 * @param list     the list to be wrapped
	 * @return         the corresponding {@link ObservableList}
	 */
	public static <T> List<T> addPropertyChangeMixin(List<T> list) {
		if (!AopHelper.isProxiedBy(list, PropertyChangeListenerMixin.class)) {
			if (list instanceof ObservableList) {
				return list;
			} else {
				// replace List by ObservableList
				return ObservableCollections.observableList(list);
			}
		} else {
			return list;
		}
	}
	
	/**
	 * Wrap a map with the change tracking mixin.
	 *
	 * @param <K>      the key class of the map
	 * @param <V>      the value class of the map
	 * @param map      the map to be wrapped
	 * @return         the corresponding {@link ObservableMap}
	 */
	public static <K, V> Map<K, V> addPropertyChangeMixin(Map<K, V> map) {
		if (map instanceof ObservableMap) {
			return map;
		} else {
			// replace Map by ObservableMap
			return ObservableCollections.observableMap(map);
		}
	}

	/** {@inheritDoc} */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		m_changeSupport.addPropertyChangeListener(l);
	}

	/** {@inheritDoc} */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		m_changeSupport.removePropertyChangeListener(l);
	}

	/** {@inheritDoc} */
	public void addPropertyChangeListener(String key,
		PropertyChangeListener l) {
		
		m_changeSupport.addPropertyChangeListener(key, l);
	}

	/** {@inheritDoc} */
	public void removePropertyChangeListener(String key,
		PropertyChangeListener l) {
		
		m_changeSupport.removePropertyChangeListener(key, l);
	}

	/** {@inheritDoc} */
	protected void firePropertyChange(String key, Object oldValue, Object newValue) {
		s_logger.debug("Fire " + key + ": " + oldValue + " -> " + newValue);
		// Do not fire a property change when old and new value are null
		// This case is not handled in the propertyChangeSupport we call
		if (oldValue == null && newValue == null) {
			return;
		}
		m_changeSupport.firePropertyChange(key, oldValue, newValue);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// initialize PropertyChangeSupport here
		if (m_changeSupport == null) {
			m_changeSupport = new PropertyChangeSupport(AopContext.currentProxy());
		}
		if (m_classValidator == null) {
			m_classValidator = new ClassValidator(invocation.getThis().getClass());
		}
		
		String methodName = invocation.getMethod().getName();
		
		// Only intercept java bean setters and getters (setXyz(xyz) and getXyz())
		if (methodName.startsWith("set") && !methodName.equals("set")
			&& invocation.getArguments().length == 1) {

			// invoke the corresponding get method to see
			// if the value has actually changed
			Method getter = getGetter(invocation.getMethod());

			if (getter != null) {
				// modification check is unimportant
				// for write only methods
				Object oldVal = getter.invoke(invocation.getThis());
				Object result = super.invoke(invocation);

				String fieldName = methodName.substring("set".length());
				fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

				firePropertyChange(fieldName, oldVal, getter.invoke(invocation.getThis()));
				return result;
			} else {
				throw new IllegalArgumentException(
					"Mixin Interceptor is not able to find getter Method");
			}
		} else if (((methodName.startsWith("get") && !methodName.equals("get"))
			|| methodName.startsWith("is")) && invocation.getArguments().length == 0) {
			
			Object result = super.invoke(invocation);
			
			Object wrappedResult = applyMixinToResult(result);
			
			// reassign value if modified
			if (m_writeBack && wrappedResult != result) {
				Method setter = getSetter(invocation.getMethod());
				if (setter != null) {
					setter.invoke(invocation.getThis(), wrappedResult);
				}
			}
			return wrappedResult;
		} else {
			return super.invoke(invocation);
		}
	}

	/**
	 * Add property change listeners to result of method invocation. 
	 * 
	 * @param object    the result of the method invocation
	 * @return          the wrapped result
	 */
	protected Object applyMixinToResult(Object object) {
		if (object == null) {
			return null;
		}
		Object result = object;
		
		if (List.class.isAssignableFrom(object.getClass())) {
			if (!(object instanceof ObservableList)) {
				// replace List by ObservableList
				result = ObservableCollections.observableList((List) object);
			}
		} else if (Map.class.isAssignableFrom(object.getClass())) {
			if (!(object instanceof ObservableMap)) {
				// replace Map by ObservableMap
				result = ObservableCollections.observableMap((Map) object);
			}
		}
		
		return result;
	}
	
	/**
	 * @param setter    the setter method
	 * @return          the corresponding getter method
	 */
	protected Method getGetter(Method setter) {
		Method getter = null;

		// attempt cache retrieval.
		getter = (Method) m_methodCache.get(setter);

		if (getter != null) {
			return getter;
		}

		String getterName = setter.getName().replaceFirst("set", "get");
		try {
			getter = setter.getDeclaringClass().getMethod(getterName);

			// cache getter
			synchronized (m_methodCache) {
				m_methodCache.put(setter, getter);
			}
		} catch (NoSuchMethodException ex) {
			// try boolean getter method
			try {
				getterName = setter.getName().replaceFirst("set", "is");
				getter = setter.getDeclaringClass().getMethod(getterName);
				// cache getter
				synchronized (m_methodCache) {
					m_methodCache.put(setter, getter);
				}
			} catch (NoSuchMethodException e) {
				// must be write only
				getter = null;
			}
			
		}
		return getter;
	}

	/**
	 * @param getter    the getter method
	 * @return          the corresponding setter method
	 */
	protected Method getSetter(Method getter) {
		Method setter = null;

		// attempt cache retrieval.
		setter = (Method) m_methodCache.get(getter);

		if (setter != null) {
			return setter;
		}
		String setterName;
		if (getter.getName().startsWith("is")) {
			setterName = getter.getName().replaceFirst("is", "set");
		} else {
			setterName = getter.getName().replaceFirst("get", "set");
		}
		
		try {
			setter = getter.getDeclaringClass().getMethod(setterName, getter.getReturnType());

			// cache setter
			synchronized (m_methodCache) {
				m_methodCache.put(getter, setter);
			}
		} catch (NoSuchMethodException ex) {
			// must be write only
			setter = null;
		}
		return setter;
	}
	
	/**
	 * {@inheritDoc}
	 * Attention: Only Java bean properties of primitive or immutable (see IMMUTABLE_CLASSES) type
	 * can be saved and restored.
	 */
	public void save() {
		try {
			m_backup.clear();

			BeanInfo info = Introspector.getBeanInfo(AopContext.currentProxy().getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method r = pd.getReadMethod();
				Method w = pd.getWriteMethod();
				if (r != null && w != null && (r.getReturnType().isPrimitive()
					|| IMMUTABLE_CLASSES.contains(r.getReturnType()))) {
					
					m_backup.put(pd.getWriteMethod(), r.invoke(AopContext.currentProxy()));
				}
			}
		} catch (Exception e) {
			m_backup.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 * Attention: Only Java bean properties of primitive or immutable (see IMMUTABLE_CLASSES) type
	 * can be saved and restored.
	 */
	public void restore() {
		for (Method method : m_backup.keySet()) {
			try {
				method.invoke(AopContext.currentProxy(), m_backup.get(method));
			} catch (Exception e) {
				s_logger.warn("Could not restore property with setter " + method.getName());
			}
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public ClassValidator getClassValidator() {
		return m_classValidator;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public boolean isValid() {
		InvalidValue[] validationMessages = m_classValidator.getInvalidValues(AopContext.currentProxy());
		return validationMessages.length == 0;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public boolean isValid(String property) {
		InvalidValue[] validationMessages = m_classValidator.getInvalidValues(AopContext.currentProxy(), property);
		return validationMessages.length == 0;
	}
}
