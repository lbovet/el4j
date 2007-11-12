/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.remoting.protocol.jaxws;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;


/**
 * This class is used to wrap the by wsimport generated classes so that they
 * implement the original interface.
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
public class JaxwsInvoker implements java.lang.reflect.InvocationHandler {
    
    /**
     * The logger.
     */
    private static Logger s_logger = Logger.getLogger(JaxwsInvoker.class);
    
    /**
     * The proxied object (generated by wsimport).
     */
    private Object m_target;
    
    
    /**
     * The original interface.
     */
    private Class<?> m_interface;
    
    /**
     * Class mapping from original to corresponding generated class.
     */
    private Map<Class<?>, Class<?>> m_origToGenerated;
    
    /**
     * Class mapping from generated to corresponding original class.
     */
    private Map<Class<?>, Class<?>> m_generatedToOrig;
    
    /**
     * Map of objects that have already been converted.
     * This is used to prevent infinite recursion.
     */
    private Map<Object, Object> m_alreadyConverted;
    
    /**
     * The name of the package containing all generated classes.
     */
    private String m_genClsPackageName;

    /**
     * Constucts this invocation handler.
     * 
     * @param obj               the object to wrap
     * @param newInterface      the new interface it should implement
     * @param genPackageName    the package containing all generated classes
     */
    @SuppressWarnings("unchecked")
    public JaxwsInvoker(Object obj, Class newInterface, String genPackageName) {
        m_target = obj;
        m_interface = newInterface;
        m_genClsPackageName = genPackageName;
        
        m_origToGenerated = new HashMap<Class<?>, Class<?>>();
        m_generatedToOrig = new HashMap<Class<?>, Class<?>>();
        inspectUsedTypes(m_interface);
        
        m_alreadyConverted = new HashMap<Object, Object>();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method m, Object[] args)
        throws Throwable {
        
        m_alreadyConverted.clear();
        Object result = null;
        try {
            // convert arguments to generated
            Class[] argTypes = null;
            if (args != null) {
                convertParams(args);
                
                argTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    // preserve primitve types
                    if (m.getParameterTypes()[i].isPrimitive()) {
                        argTypes[i] = m.getParameterTypes()[i];
                    } else {
                        argTypes[i] = args[i].getClass();
                    }
                }
            }
            Method targetMethod = m_target.getClass().getMethod(
                m.getName(), argTypes);
            
            result = targetMethod.invoke(m_target, args);
            
            // convert result back to original
            result = convert(result, m_generatedToOrig);
        } catch (InvocationTargetException e) {
            convertException(e.getTargetException());
        }
        return result;
    }
    
    /**
     * Creates a new dynamic proxy for a generated class.
     * @param obj               the object to wrap
     * @param newInterface      the new interface it should implement
     * @param genPackageName    the package containing all generated classes
     * @return                  the generated proxy
     */
    @SuppressWarnings("unchecked")
    public static Object newInstance(Object obj, Class newInterface,
        String genPackageName) {
        
        return java.lang.reflect.Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[]{newInterface},
            new JaxwsInvoker(obj, newInterface, genPackageName));
    }
    
    /**
     * Goes through all used types and checks if a corresponding generated class
     * exists. All hits get stored into {@link #m_origToGenerated} and 
     * {@link #m_generatedToOrig}.
     * 
     * @param classToInspect    the class to inspect
     */
    @SuppressWarnings("unchecked")
    private void inspectUsedTypes(Class classToInspect) {
        Set<Class> classes = new HashSet<Class>();
        for (Method m : classToInspect.getMethods()) {
            // parameter types
            classes.addAll(Arrays.asList(m.getParameterTypes()));
            
            // exception types
            classes.addAll(Arrays.asList(m.getExceptionTypes()));
            
            // return type
            classes.add(m.getReturnType());
        }
        
        // find corresponding generated classes
        for (Class cls : classes) {
            Class gen = findGeneratedClass(cls);
            // class has already been inspected -> prevent infinite recursion
            if (!m_origToGenerated.containsKey(cls) && gen != null) {
                m_generatedToOrig.put(gen, cls);
                m_origToGenerated.put(cls, gen);
                inspectUsedTypes(cls);
            }
        }
    }
    
    /**
     * Find corresponding generated class.
     * 
     * @param type    the original class
     * @return        the generated class or <code>null</code> if none was found
     */
    @SuppressWarnings("unchecked")
    private Class findGeneratedClass(Class type) {
        if (!type.isPrimitive()) {
            // find corresponding generated class
            String typeName = type.getSimpleName();
            // Checkstyle: EmptyBlock off
            try {
                Class genClass = Class.forName(
                    m_genClsPackageName + "." + typeName);
                return genClass;
            } catch (ClassNotFoundException e) {
                // ignore class
            }
            // Checkstyle: EmptyBlock on
        }
        return null;
    }
    
    /**
     * Converts all parameters from original class to generated.
     * 
     * @param params    the array containing objects to convert
     */
    private void convertParams(Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                params[i] = convert(params[i], m_origToGenerated);
            }
        }
    }
    
    /**
     * Convert an object using the specified mapping.
     * 
     * @param obj        the object to convert
     * @param mapping    the conversion mapping (orig->gen / gen->orig)
     * @return           the converted object
     */
    @SuppressWarnings("unchecked")
    private Object convert(Object obj, Map<Class<?>, Class<?>> mapping) {
        Object result = obj;
        if (mapping.containsKey(obj.getClass())) {
            Class target = mapping.get(obj.getClass());
            if (target.isInterface()) {
                // create dynamic proxy
                result = newInstance(obj, target, m_genClsPackageName);
            } else {
                // create converted copy
                try {
                    // prevent infinite recursion
                    if (m_alreadyConverted.containsKey(obj)) {
                        result = m_alreadyConverted.get(obj);
                    } else {
                        Object tmp = target.newInstance();
                        m_alreadyConverted.put(obj, tmp);
                        copyPropertiesDeep(obj, tmp, mapping);
                        result = tmp;
                    }
                } catch (Exception e) {
                    s_logger.error("Could not convert " + obj
                        + " to " + target);
                }
            }
        }
        return result;
    }
    
    /**
     * Performs a deep copy based on bean properties. Additionaly a conversion
     * specified by the mapping is done.
     * 
     * @param source             the source object
     * @param target             the target object
     * @param mapping            the mapping for conversion
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    private void copyPropertiesDeep(Object source, Object target,
        Map<Class<?>, Class<?>> mapping) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        
        PropertyDescriptor[] targetPds;
        PropertyDescriptor[] sourcePds;
        try {
            targetPds = Introspector.getBeanInfo(target.getClass()).
                getPropertyDescriptors();
            sourcePds = Introspector.getBeanInfo(source.getClass()).
                getPropertyDescriptors();
        } catch (IntrospectionException e) {
            return;
        }
        
        for (PropertyDescriptor targetPd : targetPds) {
            boolean copyProperty = false;
            if (targetPd.getWriteMethod() != null) {
                copyProperty = true;
            }
            if (targetPd.getReadMethod() != null) {
                if (List.class.isAssignableFrom(
                    targetPd.getReadMethod().getReturnType())) {
                    copyProperty = true;
                }
                if (Set.class.isAssignableFrom(
                    targetPd.getReadMethod().getReturnType())) {
                    copyProperty = true;
                }
            }
            
            if (copyProperty) {
                PropertyDescriptor sourcePd = null;
                for (PropertyDescriptor propDesc : sourcePds) {
                    if (propDesc.getName().equals(targetPd.getName())) {
                        sourcePd = propDesc;
                        break;
                    }
                }
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source, new Object[0]);
                        Method writeMethod = targetPd.getWriteMethod();
                        
                        // determine target type for conversion
                        Class convertTarget = null;
                        if (writeMethod == null) {
                            // write List or Set
                            // the generated Collection has no setter method
                            // Use get to access the generated Collection
                            convertTarget = targetPd.getReadMethod().
                                getReturnType();
                        } else {
                            convertTarget = writeMethod.getParameterTypes()[0];
                        }
                        
                        // convert value if necessary
                        if (value != null) {
                            value = convert(value, mapping);
                            value = convertCollection(value, 
                                convertTarget);
                        }
                        
                        // write value
                        if (writeMethod == null) {
                            Collection collection = (Collection) targetPd
                                .getReadMethod().invoke(target);
                            if (collection != null && value != null) {
                                collection.addAll((Collection) value);
                            }
                        } else {
                            if (!Modifier.isPublic(writeMethod
                                .getDeclaringClass().getModifiers())) {
                                
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, new Object[] {value});
                        }
                        
                    } catch (Throwable ex) {
                        throw new FatalBeanException(
                            "Could not copy properties from source to target",
                            ex);
                    }
                }
            }
        }
    }
    
    /**
     * Converts from array to list and backwards.
     * 
     * @param src       the source object
     * @param target    the target class
     * @return          an object of type target
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private Object convertCollection(Object src, Class target)
        throws Exception {
        
        Object result = src;
        if (!target.isAssignableFrom(src.getClass())) {
            if (target.isArray() && src instanceof List) {
                // List -> Array
                Class componentType = target.getComponentType();
                List list = (List) src;
                Object tmp = Array.newInstance(componentType, list.size());
                // add values and convert recursively
                for (int i = 0; i < list.size(); i++) {
                    Array.set(tmp, i,
                        convertCollection(list.get(i), componentType));
                }
                result = target.cast(tmp);
            } else if (List.class.isAssignableFrom(target)
                && src.getClass().isArray()) {
                
                // Array -> List
                ArrayList tmp = new ArrayList(Array.getLength(src));
                Class componentType = src.getClass().getComponentType();
                // add values and convert recursively
                for (int i = 0; i < Array.getLength(src); i++) {
                    tmp.add(convertCollection(Array.get(src, i),
                        componentType));
                }
                result = target.cast(tmp);
            } else if (src instanceof Collection
                && List.class.isAssignableFrom(target)) {
                
                // Collection -> List
                Collection tmp = new ArrayList();
                tmp.addAll((Collection) src);
                result = tmp;
            } else if (src instanceof Collection
                && Set.class.isAssignableFrom(target)) {
                
                // Collection -> Set
                Collection tmp = new HashSet();
                tmp.addAll((Collection) src);
                result = tmp;
            } /*
                // Map is not yet supported
                else if (Map.class.isAssignableFrom(src.getClass())) {
                // Map -> generated Map with entries
                Map map = (Map) src;
                result = target.newInstance();
                
                if (map.size() > 0) {
                    Class keyClass = map.keySet().toArray()[0].getClass();
                    Class valueClass = map.values().toArray()[0].getClass();
                    
                    Method getEntryMethod
                        = target.getMethod("getEntry", new Class[0]);
                    List entries = (List) getEntryMethod.invoke(result);
                    
                    // there is only one inner class named 'Entry'
                    Class entryClass = target.getClasses()[0];
                    
                    // get setter methods for 'Entry'
                    Method setKeyMethod =
                        entryClass.getMethod("setKey",
                        new Class[]{keyClass});
                    Method setValueMethod =
                        entryClass.getMethod("setValue",
                        new Class[]{valueClass});
                    
                    for (Object key : map.keySet()) {
                        Object entry = entryClass.newInstance();
                        setKeyMethod.invoke(entry, key);
                        setValueMethod.invoke(entry, map.get(key));
                        entries.add(entry);
                    }
                }
                
            } else if (Map.class.isAssignableFrom(target)) {
                // generated Map with entries -> Map
                Method getEntryMethod
                    = src.getClass().getMethod("getEntry", new Class[0]);
                List list = (List) getEntryMethod.invoke(src);
                Map map = (Map) src.getClass().newInstance();
                if (list.size() > 0) {
                    Method getKeyMethod = list.get(0)
                        .getClass().getMethod("getKey", new Class[0]);
                    Method getValueMethod = list.get(0)
                        .getClass().getMethod("getValue", new Class[0]);
                    for (Object item : list) {
                        map.put(getKeyMethod.invoke(item),
                            getValueMethod.invoke(item));
                    }
                }
                result = map;
            }*/
        }
        return result;
    }
    
    /**
     * Converts a generated (by wsimport) exception to its
     * corresponding orignal.
     * 
     * @param exception    the generated exception
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    private void convertException(Throwable exception) throws Throwable {
        if (m_generatedToOrig.containsKey(exception.getClass())) {
            // exception is not generated by JAX-WS.
            throw exception;
        }
        
        // get the right exception by calling getFaultInfo()
        Method getFaultInfo = exception.getClass().getMethod(
            "getFaultInfo", new Class[] {});
        Object source = getFaultInfo.invoke(exception);
        
        Class exceptionClass = m_generatedToOrig.get(source.getClass());
        Throwable realException = (Throwable) exceptionClass.newInstance();
        
        // unfortunately, the cause is already set, so properties can be
        // copied only
        //realException.initCause(exception);
        BeanUtils.copyProperties(source, realException);
        realException.setStackTrace(exception.getStackTrace());
        
        throw realException;
    }
    

}