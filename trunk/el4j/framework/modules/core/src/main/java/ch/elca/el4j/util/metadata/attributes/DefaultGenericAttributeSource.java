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
package ch.elca.el4j.util.metadata.attributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.metadata.Attributes;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.util.metadata.MetaDataCollectorAware;
import ch.elca.el4j.util.metadata.Target;
import ch.elca.el4j.util.metadata.AbstractGenericMetaDataCollector.collectionTarget;
import ch.elca.el4j.util.metadata.annotations.DefaultGenericAnnotationCollector;


/**
 * <p>
 * The class <code>DefaultGenericAttributeSource</code> provides methods to get 
 * meta data from a specific target (e. g.  method). It is possible to define, 
 * which types of meta data will be searched. By default will all meta 
 * data found.</p>
 * 
 * <p>
 * The class chaches the request. If also the metaData of a target is called 
 * twice, the second time will be the cached results returned.</p>
 * 
 * <p>The searching of metaData considers inheritence. The class can be 
 * configured, to define how deeply metaData from higher levels will be 
 * inherited. As usual, a mataData on an lower level will overwride the same 
 * meta Data on a higher level
 * {@link #addNewMetaDataBasedOnHierarchie(Collection, Collection)}</p>
 * 
 * 
 * TODO Use old default collection 
 * This class is ... TODO ADH
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Häfeli (ADH)
 * @deprecated use {@link AbstractGenericMetaDataSource}.
 */
@Deprecated
public class DefaultGenericAttributeSource 
    extends DefaultGenericAttributeCollector implements GenericAttributeSource {

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(Method targetMethod, Class targetClass) {

        Target targetObject = new Target(targetMethod, targetClass);
        
        Collection c = new ArrayList();
        c = computeMethodMetaData(c, targetObject);
        Object[] attributes = c.toArray();
        if (attributes != null && attributes.length > 0) {
            return attributes[0];
        } else {
            return null;
        }
    
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes() {
        /* In the old implementation DefaultGenericAttributeSource contained
         * a collector object which implemented the Interface
         * Attributes. In the new implementation, the AttributeSource and 
         * the AttributeCollector are merged to one class 
         * (DefaultGenericAttributeCollector). Therefore the collector
         * is now the DefaultGenericAttributeSource itself.
         */
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean matches(Method method, Class targetClass) {
        return (getAttribute(method, targetClass) != null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setInterceptingAttributes(List interceptingAttributes) {
        setInterceptingMetaData(interceptingAttributes); 
    }

    /**
     * {@inheritDoc}
     */
    public List getInterceptingAttributes() {
        return m_interceptingMetaData;
    }





    


}