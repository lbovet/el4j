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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.metadata.Attributes;
import org.springframework.util.Assert;

/**
 * The default implementation of the GenericAttributeSource interface.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 * @author Martin Zeltner (MZE)
 */
public class DefaultGenericMetaDataSource implements GenericMetaDataSource {
    /**
     * Canonical value held in cache to indicate that no metadata was found for
     * a method. So we don't need to look for metadata again.
     */
    protected static final Collection NULL_METADATA = Collections.EMPTY_LIST;
    
    /**
     * Cache of Attributes, keyed by Method and target class.
     */
    protected final Map<String, Collection> m_cache 
        = Collections.synchronizedMap(new HashMap<String, Collection>());

    /**
     * Used to delegate metadata requests.
     */
    private Attributes m_metaDataDelegator;

    /**
     * Are the metadata types this source is made for.
     */
    private List<Class> m_interceptingMetaData;

    /**
     * Calculates the cache key of a certain method in a certain class.
     * 
     * @param method
     *            method for the current invocation.
     * @param targetClass
     *            targetClass for this invocation. May be null.
     * @return Returns the cache key string.
     */
    protected String getCacheKey(Method method, Class targetClass) {
        // Class may be null, method can't
        // Must not produce same key for overloaded methods
        // Must produce same key for different instances of the same method
        return targetClass + ";" + method;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getMetaData(Method method, Class targetClass) {
        // First, see if we have a cached value
        String cacheKey = getCacheKey(method, targetClass);
        Collection cachedMetaData = m_cache.get(cacheKey);

        if (cachedMetaData != null) {
            // Value will either be canonical value indicating there is no
            // metadata or an actual metadata
            return cachedMetaData == NULL_METADATA ? null : cachedMetaData;
        } else {
            // We need to work it out
            Collection metaData = computeMetaData(method, targetClass);
            // Put it in the cache
            m_cache.put(cacheKey, metaData == null ? NULL_METADATA : metaData);
            // Return the metadata collection
            return metaData;
        }
    }

    /**
     * Same as method {@link #getMetaData(Method, Class)} but without caching
     * of the result.
     * 
     * @param method
     *            Is the method for the current invocation. Must not be null.
     * @param targetClass
     *            target class for this invocation. May be null.
     * @return Returns a collection of the matching meta data for the given
     *         method and targetClass.
     */
    @SuppressWarnings("unchecked")
    protected Collection computeMetaData(Method method, Class targetClass) {
        Assert.notNull(method);
        
        // Helps to find the bridge method. This is needed if the given method
        // is currently on a proxy.
        Method bridgeMethod = BridgeMethodResolver.findBridgedMethod(method);

        // Collect the metadata from method.
        Collection metaDataOnMethod = new ArrayList();
        
        // Try the bridge method.
        metaDataOnMethod.addAll(
            filterMetaData(findAllAttributes(bridgeMethod)));
        
        if (metaDataOnMethod.isEmpty() && bridgeMethod.equals(method)) {
            // Fallback is to look at the original method
            metaDataOnMethod.addAll(filterMetaData(findAllAttributes(method)));
        }
        
        // Collect the metadata from class.
        Collection metaDataOnClass = new ArrayList();
        
        // Try as first the given target class
        if (targetClass != null) {
            metaDataOnClass.addAll(
                filterMetaData(findAllAttributes(bridgeMethod)));
        }
        
        if (metaDataOnClass.isEmpty()) {
            // Try as second the declaring class of found bridge method.
            metaDataOnClass.addAll(
                filterMetaData(
                    findAllAttributes(bridgeMethod.getDeclaringClass())));
        }

        if (metaDataOnClass.isEmpty() && bridgeMethod != method) {
            // Last fallback is the class of the original method
            metaDataOnClass.addAll(filterMetaData(findAllAttributes(
                method.getDeclaringClass())));
        }
        
        Collection result = new ArrayList();
        result.addAll(metaDataOnMethod);
        result.addAll(metaDataOnClass);
        return result.isEmpty() ? null : result;
    }

    /**
     * @param m Is the method to retrieve metadata for.
     * @return Returns all found metadata associated with the given method or
     *         <code>null</code> if no metadata could be found.
     */
    protected Collection findAllAttributes(Method m) {
        return getMetaDataDelegator().getAttributes(m);
    }

    /**
     * @param clazz Is the class to retrieve metadata for.
     * @return Returns all found metadata associated with the given class or
     *         <code>null</code> if no metadata could be found.
     */
    protected Collection findAllAttributes(Class clazz) {
        return getMetaDataDelegator().getAttributes(clazz);
    }

    /**
     * @param metaData Are the metadata to filter.
     * @return Returns the filtered collection of metadata or <code>null</code>
     *         if the returned collection would be empty. Only intercepting
     *         metadata will endure the filter process.
     */
    @SuppressWarnings("unchecked")
    protected Collection filterMetaData(Collection metaData) {
        List<Class> interceptingMetaData = getInterceptingMetaData();
        Assert.notEmpty(interceptingMetaData,
                "There is no metadata defined to be used for interception.");

        if (metaData == null) {
            return null;
        }
        
        List filteredMetaData = new ArrayList();
        for (Class metaDataClass : interceptingMetaData) {
            for (Object metaDataObject : metaData) {
                if (metaDataClass.isInstance(metaDataObject)) {
                    filteredMetaData.add(metaDataObject);
                }
            }
        }
        return filteredMetaData.isEmpty() ? null : filteredMetaData;
    }

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
    public List<Class> getInterceptingMetaData() {
        return m_interceptingMetaData;
    }

    /**
     * {@inheritDoc}
     */
    public void setInterceptingMetaData(List<Class> interceptedAttributes) {
        m_interceptingMetaData = interceptedAttributes;
    }
}