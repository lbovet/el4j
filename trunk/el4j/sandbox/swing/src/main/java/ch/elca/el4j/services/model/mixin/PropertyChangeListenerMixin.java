package ch.elca.el4j.services.model.mixin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.ClassValidator;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;
import com.silvermindsoftware.hitch.validation.ValidationCapability;

/**
 * Mixin to enable javaBeans event support and validation in ordinary POJOs
 * 
 * @author SWI
 */
public class PropertyChangeListenerMixin extends DelegatingIntroductionInterceptor
    implements PropertyChangeListenerCapability, ValidationCapability {
    
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
        //System.out.println("Fire " + key + ": " + oldValue + " -> " + newValue);
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
                throw new IllegalArgumentException("To much arguments for Interceptor");
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
