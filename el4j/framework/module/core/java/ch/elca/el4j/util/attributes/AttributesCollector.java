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
package ch.elca.el4j.util.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.metadata.Attributes;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class collects attributes from different classes. To explain which
 * attributes will be taken you have to go through the following example:
 * 
 * <pre>
 * &#47;**
 *  * &#64;&#64;AttributeClassX()
 *  *&#47;
 * public interface X {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodX()
 *      *&#47;
 *     public int getValue();
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassY()
 *  *&#47;
 * public interface Y {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodY()
 *      *&#47;
 *     public int getValue();
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassA()
 *  *&#47;
 * public class A implements X {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodA()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassB()
 *  *&#47;
 * public class B extends A implements Y {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodB()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassC()
 *  *&#47;
 * public class C extends B {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodC()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * </pre>
 * 
 * If you would like to get attributes from class <code>C</code> you will get
 * the following:
 * <ul>
 * <li>AttributeClassC</li>
 * <li>AttributeClassY</li>
 * <li>AttributeClassX</li>
 * </ul>
 * 
 * Analog, if you would like to get attributes from method
 * <code>getValue()</code> of class <code>C</code> you will get the
 * following:
 * <ul>
 * <li>AttributeMethodC</li>
 * <li>AttributeMethodY</li>
 * <li>AttributeMethodX</li>
 * </ul>
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
public class AttributesCollector implements Attributes, InitializingBean {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(AttributesCollector.class);

    /**
     * Inner object to get attributes from classes.
     */
    private Attributes m_attributes;

    /**
     * @return Returns the attributes.
     */
    public Attributes getAttributes() {
        return m_attributes;
    }

    /**
     * @param attributes
     *            Is the object to get the attributes from classes.
     */
    public void setAttributes(Attributes attributes) {
        m_attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Class targetClass) {
        Set attributeSet = new HashSet();
        List interfaceList = getAllInterfaces(new LinkedList(), targetClass);

        attributeSet.addAll(m_attributes.getAttributes(targetClass));
        Iterator it = interfaceList.iterator();
        while (it.hasNext()) {
            Class c = (Class) it.next();
            attributeSet.addAll(m_attributes.getAttributes(c));
        }

        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Class targetClass, Class filter) {
        Set attributeSet = new HashSet();
        List interfaceList = getAllInterfaces(new LinkedList(), targetClass);

        attributeSet.addAll(m_attributes.getAttributes(targetClass, filter));
        Iterator it = interfaceList.iterator();
        while (it.hasNext()) {
            Class c = (Class) it.next();
            attributeSet.addAll(m_attributes.getAttributes(c, filter));
        }

        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod) {
        Set attributeSet = new HashSet();
        List interfaceList = getAllInterfaces(new LinkedList(), targetMethod
                .getDeclaringClass());

        attributeSet.addAll(m_attributes.getAttributes(targetMethod));
        Iterator it = interfaceList.iterator();
        while (it.hasNext()) {
            Class c = (Class) it.next();
            try {
                Method m = c.getDeclaredMethod(targetMethod.getName(),
                        targetMethod.getParameterTypes());
                attributeSet.addAll(m_attributes.getAttributes(m));
            } catch (SecurityException e) {
                // Do nothing. Security problem with current interface.
            } catch (NoSuchMethodException e) {
                // Do nothing. Method does not exist in current interface.
            }
        }

        return attributeSet;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod, Class filter) {
        Set attributeSet = new HashSet();
        List interfaceList = getAllInterfaces(new LinkedList(), targetMethod
                .getDeclaringClass());

        attributeSet.addAll(m_attributes.getAttributes(targetMethod, filter));
        Iterator it = interfaceList.iterator();
        while (it.hasNext()) {
            Class c = (Class) it.next();
            try {
                Method m = c.getDeclaredMethod(targetMethod.getName(),
                        targetMethod.getParameterTypes());
                attributeSet.addAll(m_attributes.getAttributes(m, filter));
            } catch (SecurityException e) {
                // Do nothing. Security problem with current interface.
            } catch (NoSuchMethodException e) {
                // Do nothing. Method does not exist in current interface.
            }
        }

        return attributeSet;
    }

    /**
     * This method is used to get all interfaces, which are implemented by the
     * given class and all its super classes.
     * 
     * @param list
     *            Is the list with all interfaces.
     * @param clazz
     *            Is the class, which has to be analyzed.
     * @return Returns the list with all interfaces.
     */
    private List getAllInterfaces(List list, Class clazz) {
        if (clazz != null && clazz != Object.class) {
            Class[] classes = clazz.getInterfaces();
            for (int i = 0; i < classes.length; i++) {
                list.add(classes[i]);
            }
            getAllInterfaces(list, clazz.getSuperclass());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField) {
        return m_attributes.getAttributes(targetField);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField, Class filter) {
        return m_attributes.getAttributes(targetField, filter);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_attributes == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty("attributes",
                    this);
        }
    }
}