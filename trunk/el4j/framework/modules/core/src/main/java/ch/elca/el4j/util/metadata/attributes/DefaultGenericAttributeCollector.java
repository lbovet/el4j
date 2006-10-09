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

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.metadata.Attributes;
import org.springframework.metadata.commons.CommonsAttributes;

import ch.elca.el4j.util.metadata.AbstractGenericMetaDataCollector;


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
 */
public class DefaultGenericAttributeCollector extends AbstractGenericMetaDataCollector {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(DefaultGenericAttributeCollector.class);
    
    /**
     * Inner object to get attributes from classes.
     */
    private Attributes m_attributes;
    
    public DefaultGenericAttributeCollector() {
        m_attributes =  new CommonsAttributes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getClassName(Object object) {
        return object.getClass().getName();
    }

    /** 
     * {@inheritDoc}
     */
    protected Collection clean(Collection metaData, ElementType elementType) {
        //TODO Documentation
        return metaData;
    } 
    
    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Class targetClass) {
        Collection attributes = m_attributes.getAttributes(targetClass);
        return getList(attributes);
    }

    /**
     * {@inheritDoc}
     * 
     * TODO Return null if empty
     */
    public Collection getAttributes(Class targetClass, Class filter) {
        Collection attributes = m_attributes.getAttributes(targetClass, filter);
        return getList(attributes);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod) {
        Collection attributes = m_attributes.getAttributes(targetMethod);
        return getList(attributes);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod, Class filter) {
        Collection attributes = m_attributes.getAttributes(targetMethod, filter);
        return getList(attributes);
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
     * Returns an <code>ArrayList</code> with the values of the specified
     * Collection. 
     * 
     * @param array
     *            Array with the values to add to the array list.
     * @return ArrayList containing the values of the specified array.
     */
    private Collection getList(Collection attributes) {
        Collection alist = new ArrayList();
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
            alist.add(iter.next());
        }
        return alist;
    }
}