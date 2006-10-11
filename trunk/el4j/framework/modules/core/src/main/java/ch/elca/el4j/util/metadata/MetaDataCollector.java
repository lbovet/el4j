/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.metadata.Attributes;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * <p>
 * Depending to the used kind of metadata, it can be defined on a field, on a 
 * method, on a class, and on a package. The used kinds of metadata are the 
 * <b>Commons Attributes</b> and the <b>Java 5 Annotations</b>. Both can handle
 * metadata defined on a field, on a method, and on a class.
 * </p>
 * 
 * <p>
 * This class collects metadata from different classes. The examples below 
 * show you how the metadata is collected.
 * </p> 
 * 
 * <pre>
 * public interface X {
 *     public int getValue();
 * }
 * 
 * public interface Y {
 *     public int getValue();
 * }
 * 
 * public class A implements X {
 *     public int getValue() {...}
 * }
 * 
 * public class B extends A implements Y {
 *     public int getValue() {...}
 * }
 * 
 * public class C extends B {
 *     public int getValue() {...}
 * }
 * </pre>
 * 
 * <p>
 * If metadata for method <code>getValue()</code> of Class <code>C</code> is
 * requested, the result will be a collection of metadata from class 
 * <code>C</code> and interfaces <code>Y</code> and <code>X</code>. Metadata
 * defined in class <code>A</code> and <code>B</code> will be omitted.
 * </p>
 * 
 * <p>
 * The same behavior can be found with metadata on class level. The collection
 * of metadata will also only contain metadata defined on class <code>C</code> 
 * and interfaces <code>Y</code> and <code>X</code>.
 * </p>
 * 
 * <p>
 * <b>Always the most specific implementation and all interfaces will be 
 * inspected.</b>
 * </p>
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public class MetaDataCollector implements Attributes, InitializingBean {
    /**
     * Inner object to fetch metadata.
     */
    private Attributes m_metaDataDelegator;

    /**
     * @return Returns the metaDataDelegator.
     */
    public Attributes getMetaDataDelegator() {
        return m_metaDataDelegator;
    }

    /**
     * @param metaDataDelegator Is the metaDataDelegator to set.
     */
    public void setMetaDataDelegator(Attributes metaDataDelegator) {
        m_metaDataDelegator = metaDataDelegator;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Collection getAttributes(Class targetClass) {
        Set attributeSet = new LinkedHashSet();
        List<Class> interfaceList 
            = getAllInterfaces(new ArrayList<Class>(), targetClass);
        Attributes metaDataDelegator = getMetaDataDelegator();

        attributeSet.addAll(metaDataDelegator.getAttributes(targetClass));
        for (Class iface : interfaceList) {
            attributeSet.addAll(metaDataDelegator.getAttributes(iface));
        }
        
        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Collection getAttributes(Class targetClass, Class filter) {
        Set attributeSet = new LinkedHashSet();
        List<Class> interfaceList 
            = getAllInterfaces(new ArrayList<Class>(), targetClass);
        Attributes metaDataDelegator = getMetaDataDelegator();

        attributeSet.addAll(
            metaDataDelegator.getAttributes(targetClass, filter));
        for (Class iface : interfaceList) {
            attributeSet.addAll(
                metaDataDelegator.getAttributes(iface, filter));
        }

        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Collection getAttributes(Method targetMethod) {
        Set attributeSet = new LinkedHashSet();
        List<Class> interfaceList = getAllInterfaces(
            new ArrayList<Class>(), targetMethod.getDeclaringClass());
        Attributes metaDataDelegator = getMetaDataDelegator();

        attributeSet.addAll(metaDataDelegator.getAttributes(targetMethod));
        for (Class iface : interfaceList) {
            try {
                Method m = iface.getDeclaredMethod(targetMethod.getName(),
                        targetMethod.getParameterTypes());
                attributeSet.addAll(metaDataDelegator.getAttributes(m));
            } catch (NoSuchMethodException e) {
                // Do nothing. Method does not exist in current interface.
            }
        }

        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Collection getAttributes(Method targetMethod, Class filter) {
        Set attributeSet = new LinkedHashSet();
        List<Class> interfaceList = getAllInterfaces(
            new ArrayList<Class>(), targetMethod.getDeclaringClass());
        Attributes metaDataDelegator = getMetaDataDelegator();

        attributeSet.addAll(
            metaDataDelegator.getAttributes(targetMethod, filter));
        for (Class iface : interfaceList) {
            try {
                Method m = iface.getDeclaredMethod(targetMethod.getName(),
                        targetMethod.getParameterTypes());
                attributeSet.addAll(
                    metaDataDelegator.getAttributes(m, filter));
            } catch (NoSuchMethodException e) {
                // Do nothing. Method does not exist in current interface.
            }
        }

        return attributeSet;
    }

    /**
     * Method to get all interfaces, that are implemented by the
     * given class and all its super classes.
     * 
     * @param list
     *            Is the list with all interfaces.
     * @param clazz
     *            Is the class, that must be analyzed.
     * @return Returns the list with all interfaces.
     */
    private List<Class> getAllInterfaces(List<Class> list, Class clazz) {
        if (clazz != null && clazz != Object.class) {
            Class[] classes = clazz.getInterfaces();
            for (Class c : classes) {
                list.add(c);
            }
            getAllInterfaces(list, clazz.getSuperclass());
        }
        return list;
    }

    /**
     * Directly calls the metadata delegator.
     * 
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField) {
        return getMetaDataDelegator().getAttributes(targetField);
    }

    /**
     * Directly calls the metadata delegator.
     * 
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField, Class filter) {
        return getMetaDataDelegator().getAttributes(targetField, filter);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (getMetaDataDelegator() == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                "metaDataDelegator", this);
        }
    }
}