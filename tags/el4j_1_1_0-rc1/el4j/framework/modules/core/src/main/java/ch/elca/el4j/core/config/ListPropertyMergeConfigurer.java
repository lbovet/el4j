/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Adds an entry to a list property of a Spring bean. A file, e.g.
 * "listmerge.properties", whose values are of type
 * "beanName"."propertyName"="value 1", "value 2", ... can be referenced in the
 * bean definition of this class under the property name "location". The defined
 * values will be added to the list with name "propertyName" of the bean with
 * name "beanName".
 * 
 * <p>
 * The postProcessBeanFactory method from abstract class
 * PropertyResourceConfigurer is overridden in order to prevent the properties
 * from being overridden. Another possibility would be to suggest a refactoring
 * of this method in abstract class PropertyResourceConfigurer.
 * 
 * <p>
 * TODO This class handles only lists. Extensions could also manage Maps. A
 * possible pattern for defining map entries in a properties file is (tbd):
 * "bean_name.property_name = key1=value1, key2=value2".
 * 
 * <script type="text/javascript">printFileStatus
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$",
 *  "$Author$" );
 * </script>
 * 
 * @author Raphael Boog (RBO)
 * @author Martin Zeltner (MZE)
 */
public class ListPropertyMergeConfigurer extends PropertyOverrideConfigurer {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(ListPropertyMergeConfigurer.class);

    /**
     * Properties.
     */
    private Properties m_properties;

    /**
     * Resource locations.
     */
    private Resource[] m_locations;

    /**
     * File encoding.
     */
    private String m_fileEncoding;

    /**
     * Persister of properties.
     */
    private PropertiesPersister m_propertiesPersister 
        = new DefaultPropertiesPersister();

    /**
     * Ignore if a resource could not be found.
     */
    private boolean m_ignoreResourceNotFound = false;
    
    /**
     * Marks if new items should be pre- or appended to the old values.
     */
    private boolean m_insertNewItemsAfter = true;

    /**
     * {@inheritDoc}
     */
    public final void setProperties(Properties properties) {
        m_properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public final void setLocation(Resource location) {
        m_locations = new Resource[] {location};
        super.setLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    public final void setLocations(Resource[] locations) {
        super.setLocations(locations);
        m_locations = locations;
    }

    /**
     * {@inheritDoc}
     */
    public final void setFileEncoding(String encoding) {
        super.setFileEncoding(encoding);
        m_fileEncoding = encoding;
    }

    /**
     * {@inheritDoc}
     */
    public final void setPropertiesPersister(
        PropertiesPersister propertiesPersister) {
        setPropertiesPersister(propertiesPersister);
        m_propertiesPersister = propertiesPersister;
    }

    /**
     * {@inheritDoc}
     */
    public final void setIgnoreResourceNotFound(
        boolean ignoreResourceNotFound) {
        setIgnoreResourceNotFound(ignoreResourceNotFound);
        m_ignoreResourceNotFound = ignoreResourceNotFound;
    }

    /**
     * @return Returns the fileEncoding.
     */
    public final String getFileEncoding() {
        return m_fileEncoding;
    }

    /**
     * @return Returns the ignoreResourceNotFound.
     */
    public final boolean isIgnoreResourceNotFound() {
        return m_ignoreResourceNotFound;
    }

    /**
     * @return Returns the locations.
     */
    public final Resource[] getLocations() {
        return m_locations;
    }

    /**
     * @return Returns the properties.
     */
    public final Properties getProperties() {
        return m_properties;
    }

    /**
     * @return Returns the propertiesPersister.
     */
    public final PropertiesPersister getPropertiesPersister() {
        return m_propertiesPersister;
    }

    /**
     * Marks if new items should be pre- or appended to the old values.
     * 
     * @return Returns the insertNewItemsAfter.
     */
    public final boolean isInsertNewItemsAfter() {
        return m_insertNewItemsAfter;
    }

    /**
     * Marks if new items should be pre- or appended to the old values.
     * 
     * @param insertNewItemsAfter The insertNewItemsAfter to set.
     */
    public final void setInsertNewItemsAfter(boolean insertNewItemsAfter) {
        m_insertNewItemsAfter = insertNewItemsAfter;
    }

    /**
     * Marks if new items should be pre- or appended to the old values.
     * Is the opposite of method <code>isInsertNewItemsAfter</code>.
     * 
     * @return Returns the insertNewItemsBefore.
     * @see #isInsertNewItemsAfter()
     */
    public final boolean isInsertNewItemsBefore() {
        return !isInsertNewItemsAfter();
    }

    /**
     * Marks if new items should be pre- or appended to the old values.
     * Is the opposite of method <code>setInsertNewItemsAfter</code>.
     * 
     * @param insertNewItemsBefore The insertNewItemsBefore to set.
     * @see #setInsertNewItemsAfter(boolean)
     */
    public final void setInsertNewItemsBefore(boolean insertNewItemsBefore) {
        setInsertNewItemsAfter(!insertNewItemsBefore);
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(
        ConfigurableListableBeanFactory beanFactory) {
        Properties mergedProps = new Properties();

        addDefaultProperties(mergedProps);

        Resource[] locations = getLocations();
        if (locations != null) {
            for (int i = 0; i < locations.length; i++) {
                Resource location = locations[i];
                loadProperties(mergedProps, location);
                processProperties(beanFactory, mergedProps);
                mergedProps = new Properties();
            }
        } else {
            processProperties(beanFactory, mergedProps);
        }
    }

    /**
     * Adds default properties to the properties container.
     *  
     * @param mergedProps Is the properties container to write into.
     */
    protected void addDefaultProperties(Properties mergedProps) {
        Properties defaultProperties = getProperties();
        if (defaultProperties != null) {
            // use propertyNames enumeration to also catch default properties
            Enumeration en = defaultProperties.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                mergedProps.setProperty(
                    key, defaultProperties.getProperty(key));
            }
        }
    }

    /**
     * Loads the properties form the given location.
     * 
     * @param mergedProps Is the properties container to write into.
     * @param location Is the resource location to read properties from.
     */
    protected void loadProperties(Properties mergedProps, Resource location) {
        if (logger.isInfoEnabled()) {
            logger.info("Loading properties file from " + location);
        }
        InputStream is = null;
        try {
            is = location.getInputStream();
            if (location.getFilename().endsWith(XML_FILE_EXTENSION)) {
                getPropertiesPersister().loadFromXml(mergedProps, is);
            } else {
                String fileEncoding = getFileEncoding();
                if (fileEncoding != null) {
                    getPropertiesPersister().load(mergedProps, 
                        new InputStreamReader(is, fileEncoding));
                } else {
                    getPropertiesPersister().load(mergedProps, is);
                }
            }
        } catch (IOException ex) {
            String msg = "Could not load properties from " + location;
            if (isIgnoreResourceNotFound()) {
                if (s_logger.isWarnEnabled()) {
                    s_logger.warn(msg + ": " + ex.getMessage());
                }
            } else {
                CoreNotificationHelper.notifyMisconfiguration(msg, ex);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (s_logger.isWarnEnabled()) {
                        s_logger.warn("Inputstream could not be "
                            + "properly closed.", e);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void applyPropertyValue(ConfigurableListableBeanFactory factory,
        String beanName, String property, String value) {
        /**
         * Read the existing property value.
         */
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl();
        beanWrapper.registerCustomEditor(String[].class, new
        StringArrayPropertyEditor());
        BeanDefinition beanDefinition = factory.getBeanDefinition(beanName);
        MutablePropertyValues mutablePropertyValues = beanDefinition
            .getPropertyValues();
        PropertyValue propertyValue = mutablePropertyValues
            .getPropertyValue(property);

        /**
         * Fill the old values from property into list.
         */
        List oldValueList = new ArrayList();
        if (propertyValue != null) {
            Object valueObject = propertyValue.getValue();
            if (valueObject instanceof Collection) {
                oldValueList.addAll((Collection) valueObject);
            } else {
                
                 String[] oldValues = (String[]) beanWrapper.convertIfNecessary(
                    valueObject, String[].class);
                 
                /*String[] oldValues = (String[]) beanWrapper
                    .doTypeConversionIfNecessary(valueObject, String[].class);*/
                for (int i = 0; i < oldValues.length; i++) {
                    String oldValue = oldValues[i];
                    oldValueList.add(oldValue != null ? oldValue.trim()
                        : oldValue);
                }
            }
        }
        
        /**
         * Fill the new values into list.
         */
        List newValueList = new ArrayList();
        String[] newValues = (String[]) beanWrapper.convertIfNecessary(value,
            String[].class);
        /*
         * String[] newValues = (String[]) beanWrapper
         * .doTypeConversionIfNecessary(value, String[].class);
         */
        for (int i = 0; i < newValues.length; i++) {
            String newValue = newValues[i];
            newValueList.add(
                newValue != null ? newValue.trim() : newValue);
        }
        
        /**
         * Mix the old and new values and set the new property value.
         */
        List mixedValueList = new ArrayList();
        if (isInsertNewItemsAfter()) {
            mixedValueList.addAll(oldValueList);
            mixedValueList.addAll(newValueList);
        } else {
            mixedValueList.addAll(newValueList);
            mixedValueList.addAll(oldValueList);
        }
        mutablePropertyValues.addPropertyValue(property, mixedValueList);
    }
}