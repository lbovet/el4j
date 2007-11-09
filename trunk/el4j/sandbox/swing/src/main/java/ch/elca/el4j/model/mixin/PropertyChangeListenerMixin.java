package ch.elca.el4j.model.mixin;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.validation.ValidationCapability;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;
import com.silvermindsoftware.hitch.validation.HibernateValidationCapability;

/**
 * Mixin to enable javaBeans event support and validation in ordinary POJOs.
 * 
 * To see each fired property change, set the log4j level for this class to
 * debug
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
    private static Log s_logger = LogFactory
            .getLog(PropertyChangeListenerMixin.class);
    
    /**
     * The support for property change notification.
     */
    private PropertyChangeSupport m_changeSupport;
    
    /**
     * Getter-to-setter/setter-to-getter method cache.
     */
    private Map<Method, Method> m_methodCache = new HashMap<Method, Method>();

    /**
     * The stored properties (a map containing the setter-method and its value).
     */
    private Map<Method, Object> m_backup = new HashMap<Method, Object>();
    
    /**
     * Hibernate class validator.
     */
    private ClassValidator m_classValidator;
    

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
     * @return the same object wrapped with a spring proxy that has the
     *         {@link PropertyChangeListenerMixin} as {@link Advisor}
     */
    public static <T> T addPropertyChangeMixin(T object) {
        ProxyFactory pc = new ProxyFactory(object);
        IntroductionAdvisor ii = new DefaultIntroductionAdvisor(
                new PropertyChangeListenerMixin());
        pc.setProxyTargetClass(true);
        pc.setExposeProxy(true);
        pc.addAdvisor(0, ii);
        object = (T) pc.getProxy();
        return object;
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
    protected void firePropertyChange(String key, Object oldValue,
            Object newValue) {
        s_logger.debug("Fire " + key + ": " + oldValue + " -> " + newValue);
        m_changeSupport.firePropertyChange(key, oldValue, newValue);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // initialize PropertyChangeSupport here
        if (m_changeSupport == null) {
            m_changeSupport = new PropertyChangeSupport(
                    AopContext.currentProxy());
        }
        if (m_classValidator == null) {
            m_classValidator = new ClassValidator(
                    invocation.getThis().getClass());
        }
        

        if (invocation.getMethod().getName().startsWith("set")) {
            if (invocation.getArguments().length == 1) {

                // invoke the corresponding get method to see
                // if the value has actually changed
                Method getter = getGetter(invocation.getMethod());

                if (getter != null) {
                    // modification check is unimportant
                    // for write only methods
                    Object oldVal = getter.invoke(invocation.getThis());
                    Object result = super.invoke(invocation);

                    String fieldName = invocation.getMethod().getName()
                            .substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase()
                            + fieldName.substring(1);

                    firePropertyChange(fieldName, oldVal, getter
                            .invoke(invocation.getThis()));
                    return result;
                }
                throw new IllegalArgumentException(
                        "Mixin Interceptor is not able to find getter Method");
            } else {
                throw new IllegalArgumentException(
                        "Too many arguments for Interceptor");
            }
        } else if (invocation.getMethod().getName().startsWith("get")
                && List.class.isAssignableFrom(invocation.getMethod()
                        .getReturnType())) {

            List result = (List) super.invoke(invocation);
            if (!(result instanceof ObservableList)) {
                // replace list by ObservableList
                result = ObservableCollections.observableList(result);
                Method setter = getSetter(invocation.getMethod());
                if (setter != null) {
                    setter.invoke(invocation.getThis(), result);
                }
            }
            return result;
        } else {
            return super.invoke(invocation);
        }
    }
    
    /**
     * @param setter
     *            the setter method
     * @return the corresponding getter method
     */
    private Method getGetter(Method setter) {
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
            // must be write only
            getter = null;
        }
        return getter;
    }

    /**
     * @param getter
     *            the getter method
     * @return the corresponding setter method
     */
    private Method getSetter(Method getter) {
        Method setter = null;

        // attempt cache retrieval.
        setter = (Method) m_methodCache.get(getter);

        if (setter != null) {
            return setter;
        }

        String setterName = getter.getName().replaceFirst("get", "set");
        try {
            setter = getter.getDeclaringClass().getMethod(setterName,
                    getter.getReturnType());

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
    
    /** {@inheritDoc} */
    public void save() {
        try {
            m_backup.clear();

            BeanInfo info = Introspector.getBeanInfo(
                    AopContext.currentProxy().getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method r = pd.getReadMethod();
                Method w = pd.getWriteMethod();
                if (r != null && w != null) {
                    m_backup.put(pd.getWriteMethod(), r.invoke(
                            AopContext.currentProxy()));
                }
            }
        } catch (Exception e) {
            m_backup.clear();
        }
    }

    /** {@inheritDoc} */
    public void restore() {
        for (Method method : m_backup.keySet()) {
            try {
                method.invoke(AopContext.currentProxy(), m_backup.get(method));
            } catch (Exception e) {
                s_logger.warn("Could not restore property with setter "
                        + method.getName());
            }
        }
    }

    /** {@inheritDoc} */
    public ClassValidator<?> getClassValidator() {
        return m_classValidator;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean isValid() {
        InvalidValue[] validationMessages = m_classValidator
            .getInvalidValues(AopContext.currentProxy());
        return validationMessages.length == 0;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean isValid(String property) {
        InvalidValue[] validationMessages = m_classValidator.getInvalidValues(
            AopContext.currentProxy(), property);
        return validationMessages.length == 0;
    }
}
