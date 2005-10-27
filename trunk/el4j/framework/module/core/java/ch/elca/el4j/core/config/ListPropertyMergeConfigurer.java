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

package ch.elca.el4j.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

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
 * ("$Source$",
 *  "$Revision$",
 *  "$Date$",
 *  "$Author$" );
 * </script>
 * 
 * @author Raphael Boog (RBO)
 */
public class ListPropertyMergeConfigurer extends PropertyOverrideConfigurer {
    /**
     * Used logger.
     */
    private static Log s_logger = LogFactory
            .getLog(ListPropertyMergeConfigurer.class);

    /**
     * Execution order of property override.
     * Default is the same as non-ordered. 
     */
    private int m_order = Integer.MAX_VALUE;

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
     * {@inheritDoc}
     */
    public void setOrder(int order) {
        this.m_order = order;
    }

    /**
     * {@inheritDoc}
     */
    public int getOrder() {
        return m_order;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(Properties properties) {
        this.m_properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setLocation(Resource location) {
        this.m_locations = new Resource[] {location};
        super.setLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    public void setLocations(Resource[] locations) {
        this.m_locations = locations;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileEncoding(String encoding) {
        this.m_fileEncoding = encoding;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertiesPersister(
            PropertiesPersister propertiesPersister) {
        this.m_propertiesPersister = propertiesPersister;
    }

    /**
     * {@inheritDoc}
     */
    public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
        this.m_ignoreResourceNotFound = ignoreResourceNotFound;
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) {
        Properties mergedProps = new Properties();

        if (this.m_properties != null) {
            // use propertyNames enumeration to also catch default properties
            for (Enumeration en = this.m_properties.propertyNames(); en
                    .hasMoreElements();) {
                String key = (String) en.nextElement();
                mergedProps
                        .setProperty(key, this.m_properties.getProperty(key));
            }
        }

        if (this.m_locations != null) {
            for (int i = 0; i < this.m_locations.length; i++) {
                Resource location = this.m_locations[i];
                if (s_logger.isInfoEnabled()) {
                    s_logger.info("Loading properties from " + location + "");
                }
                try {
                    InputStream is = location.getInputStream();
                    try {
                        if (this.m_fileEncoding != null) {
                            this.m_propertiesPersister.load(mergedProps,
                                    new InputStreamReader(is,
                                            this.m_fileEncoding));
                        } else {
                            this.m_propertiesPersister.load(mergedProps, is);
                        }
                    } finally {
                        is.close();
                    }
                } catch (IOException ex) {
                    String msg = "Could not load properties from " + location;
                    if (this.m_ignoreResourceNotFound) {
                        if (s_logger.isWarnEnabled()) {
                            s_logger.warn(msg + ": " + ex.getMessage());
                        }
                    } else {
                        throw new BeanInitializationException(msg, ex);
                    }
                }
                processProperties(beanFactory, mergedProps);
                mergedProps = new Properties();
            }
        } else {
            processProperties(beanFactory, mergedProps);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void applyPropertyValue(ConfigurableListableBeanFactory factory,
            String beanName, String property, String value) {

        BeanWrapperImpl bw = new BeanWrapperImpl();
        BeanDefinition bd = factory.getBeanDefinition(beanName);
        MutablePropertyValues mpv = bd.getPropertyValues();
        PropertyValue pv = mpv.getPropertyValue(property);
        List valueList;

        if (pv != null) {
            if (!(pv.getValue() instanceof List)) {
                String[] oldValues = (String[]) bw.doTypeConversionIfNecessary(
                        pv.getValue(), String[].class);
                valueList = new ArrayList();
                for (int i = 0; i < oldValues.length; i++) {
                    valueList.add(oldValues[i].trim());
                }
            } else {
                valueList = (List) pv.getValue();
            }
        } else {
            valueList = new ArrayList();
        }
        String[] newValues = (String[]) bw.doTypeConversionIfNecessary(value,
                String[].class);
        for (int i = 0; i < newValues.length; i++) {
            valueList.add(newValues[i].trim());
        }
        mpv.addPropertyValue(property, valueList);
    }
}