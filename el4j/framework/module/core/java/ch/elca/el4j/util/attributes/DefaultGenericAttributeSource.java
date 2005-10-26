/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.util.attributes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.metadata.Attributes;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * The default implementation of the GenericAttributeSource interface.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class DefaultGenericAttributeSource implements GenericAttributeSource {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(DefaultGenericAttributeSource.class);

    /**
     * Canonical value held in m_cache to indicate no attribute was found for
     * this method, and we don't need to look again.
     */
    private static final Object NULL_ATTRIBUTE = new Object();

    /**
     * The attributes.
     */
    private Attributes m_attributes;

    /**
     *  
     */
    private List m_interceptingAttributes;

    /**
     * Cache of Attributes, keyed by Method and target class.
     */
    private Map m_cache = new HashMap();

    /**
     * Return all attributes for this method.
     * 
     * @param m
     *            method to retrieve attributes for
     * @return all attributes associated with this method. May return null.
     *  
     */
    protected Collection findAllAttributes(Method m) {
        return m_attributes.getAttributes(m);
    }

    /**
     * Return all attributes for this class.
     * 
     * @param clazz
     *            class to retrieve attributes for
     * @return all attributes associated with this class. May return null.
     */
    protected Collection findAllAttributes(Class clazz) {
        return m_attributes.getAttributes(clazz);
    }

    /**
     * Calculates the cache key of a certain method in a certain class.
     * 
     * @param method
     *            method for the current invocation.
     * @param targetClass
     *            targetClass for this invocation. May be null.
     * @return The cache key
     */
    protected Object getCacheKey(Method method, Class targetClass) {
        // Class may be null, method can't
        // Must not produce same key for overloaded methods
        // Must produce same key for different instances of the same method

        // TODO this works fine, but could consider making it faster in future:
        // Method.toString() is relatively (although not disastrously) slow
        return targetClass + "" + method;
    }

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(Method method, Class targetClass) {
        // First, see if we have a cached value
        Object cacheKey = getCacheKey(method, targetClass);
        Object cached = m_cache.get(cacheKey);

        if (cached != null) {
            // Value will either be canonical value indicating there is no
            // attribute or an actual attribute
            if (cached == NULL_ATTRIBUTE) {
                return null;
            } else {
                return cached;
            }
        } else {
            // We need to work it out
            Object att = computeAttribute(method, targetClass);
            // Put it in the cache
            if (att == null) {
                m_cache.put(cacheKey, NULL_ATTRIBUTE);
            } else {
                m_cache.put(cacheKey, att);
            }
            return att;
        }
    }

    /**
     * 
     * Same return as getAttribute method, but doesn't cache the result.
     * getAttribute is a caching decorator for this method.
     * 
     * @param method
     *            The method at which the attributes are collected.
     * @param targetClass
     *            The class at which the attributes are collected.
     * @return The attribute which was found at the method or the class and also
     *         is an interceptingAttribute.
     */
    protected Object computeAttribute(Method method, Class targetClass) {
        // The method may be on an interface, but we need attributes from the
        // target class.
        // The AopUtils class provides a convenience method for this. If the
        // target class is null, the method will be unchanged.
        Method specificMethod = AopUtils.getMostSpecificMethod(method,
            targetClass);

        // First try is the method in the target class
        Object att = findAttribute(findAllAttributes(specificMethod));

        if (att != null) {
            return att;
        }

        // Second try is the attribute on the target class
        att = findAttribute(findAllAttributes(specificMethod
            .getDeclaringClass()));
        if (att != null) {
            return att;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method
            att = findAttribute(findAllAttributes(method));
            if (att != null) {
                return att;
            }
            // Last fallback is the class of the original method
            return findAttribute(findAllAttributes(method.getDeclaringClass()));
        }
        return null;
    }

    /**
     * Return the specified attribute, given this set of attributes attached to
     * a method or class. Protected rather than private as subclasses may want
     * to customize how this is done: for example, returning a
     * TransactionAttribute affected by the values of other attributes. Return
     * null if the specified attribute is not defined at this method or class.
     * 
     * @param atts
     *            attributes attached to a method or class. May be null, in
     *            which case a null Attribute will be returned.
     * @return Attribute configured attribute, or null if none was found
     */
    protected Object findAttribute(Collection atts) {

        Reject.ifEmpty(getInterceptingAttributes(),
                "There is no attribute defined which will be intercepted.");

        if (atts == null) {
            return null;
        }

        Object attribute = null;
        // Check whether there is an Attribute which was defined via the
        // interceptingAttributes property.
        for (Iterator itr = atts.iterator(); itr.hasNext()
            && attribute == null;) {
            Object att = itr.next();

            for (Iterator intatts = getInterceptingAttributes().iterator();
                intatts.hasNext() && attribute == null;) {
                String className = (String) intatts.next();

                try {
                    Class cl = Class.forName(className);
                    if (cl.isInstance(att)) {
                        attribute = att;
                    }
                } catch (ClassNotFoundException e) {
                    String message = "The class '" + className
                        + "' does not exist.";
                    s_logger.error(message);
                    throw new BaseRTException(message, e);
                }
            }
        }
        return attribute;
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes() {
        return m_attributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributes(Attributes attributes) {
        m_attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public List getInterceptingAttributes() {
        return m_interceptingAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setInterceptingAttributes(List interceptedAttributes) {
        m_interceptingAttributes = interceptedAttributes;
    }
}