package ch.elca.el4j.gui.model.mixin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.springframework.aop.Advisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ch.elca.el4j.demos.gui.model.Person;

import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;
import com.silvermindsoftware.hitch.validation.ValidationCapability;

/**
 * Mixin to enable javaBeans event support and validation in ordinary POJOs
 * 
 *  To see each fired property change, 
 *   set the log4j level for this class to debug
 * 
 * @author SWI
 */
public class PropertyChangeListenerMixin extends DelegatingIntroductionInterceptor
    implements PropertyChangeListenerCapability, ValidationCapability {

    private static Log s_logger = LogFactory.getLog(PropertyChangeListenerMixin.class);
	
	/**
	 *  Wrap an object with the change tracking mixin.
	 * @param object
	 * @return the same object wrapped with a spring proxy that has the {@link PropertyChangeListenerMixin} 
	 *   as {@link Advisor}
	 */
	public static Object addPropertyChangeMixin(Object object) {
		ProxyFactory pc = new ProxyFactory (object);
        IntroductionAdvisor ii = new DefaultIntroductionAdvisor(new PropertyChangeListenerMixin());
		pc.setProxyTargetClass(true);
		pc.setExposeProxy(true);
        pc.addAdvisor(0, ii);
        object = (Person) pc.getProxy();
		return object;
	}
    
    private PropertyChangeSupport changeSupport;
    private Map<Method, Method> methodCache = new HashMap<Method, Method>();
    
    private ClassValidator classValidator;
    

	public PropertyChangeListenerMixin() {
        // initialize later when reference to the model is known
        changeSupport = null;
        classValidator = null;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String key, PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(key, l);
    }

    public void removePropertyChangeListener(String key, PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(key, l);
    }

    protected void firePropertyChange(String key, Object oldValue, Object newValue)
    {
        s_logger.debug("Fire " + key + ": " + oldValue + " -> " + newValue);
        changeSupport.firePropertyChange(key, oldValue, newValue);
    }

    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // initialize PropertyChangeSupport here
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(AopContext.currentProxy());
        }
        if (classValidator == null) {
            classValidator = new ClassValidator(invocation.getThis().getClass());
        }
        
        // TODO delete sysout
        //System.out.println(invocation.getMethod().getName() + " called!");
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
                    
                    String fieldName = invocation.getMethod().getName().substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                    
                    firePropertyChange(fieldName, oldVal, getter.invoke(invocation.getThis()));
                    return result;
                }
                throw new IllegalArgumentException("Mixin Interceptor is not able to find getter Method");
            } else {
                throw new IllegalArgumentException("Too many arguments for Interceptor");
            }
        } else if (invocation.getMethod().getName().startsWith("get")
                && List.class.isAssignableFrom(invocation.getMethod().getReturnType())) {
            
            /* ATTENTION Beans Binding doesn't work properly with lists/tables */
            List result = (List)super.invoke(invocation);
            if (!(result instanceof EventList)) {
                // replace list by glazedList
                result = GlazedLists.eventList(result);
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
     * @param setter    the setter method
     * @return          the corresponding getter method
     */
    private Method getGetter(Method setter) {
        Method getter = null;

        // attempt cache retrieval.
        getter = (Method) methodCache.get(setter);

        if (getter != null) {
            return getter;
        }

        String getterName = setter.getName().replaceFirst("set", "get");
        try {
            getter = setter.getDeclaringClass().getMethod(getterName);

            // cache getter
            synchronized (methodCache) {
                methodCache.put(setter, getter);
            }
        } catch (NoSuchMethodException ex) {
            // must be write only
            getter = null;
        }
        return getter;
    }
    
    /**
     * @param getter    the getter method
     * @return          the corresponding setter method
     */
    private Method getSetter(Method getter) {
        Method setter = null;

        // attempt cache retrieval.
        setter = (Method) methodCache.get(getter);

        if (setter != null) {
            return setter;
        }

        String setterName = getter.getName().replaceFirst("get", "set");
        try {
            setter = getter.getDeclaringClass().getMethod(
                    setterName, getter.getReturnType());

            // cache setter
            synchronized (methodCache) {
                methodCache.put(getter, setter);
            }
        } catch (NoSuchMethodException ex) {
            // must be write only
            setter = null;
        }
        return setter;
    }
    
    public ClassValidator getClassValidator() {
        return classValidator;
    }
}
