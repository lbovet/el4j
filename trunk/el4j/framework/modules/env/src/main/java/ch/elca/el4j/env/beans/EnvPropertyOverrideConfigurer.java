/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.env.beans;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Specific property override configurer for the EL4J environment.
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
public class EnvPropertyOverrideConfigurer
    extends PropertyOverrideConfigurer implements ApplicationContextAware {
    
    /** 
     * This logger is used to print out some global debugging info.
     * Consult it for info what is going on.
     */
    protected static Log s_el4jLogger
        = LogFactory.getLog(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);
    
    /**
     * The env bean property location.
     */
    public static final String ENV_BEAN_PROPERTY_PROPERTIES_LOCATION
        = "classpath:env-bean-property.properties";
    
    /**
     * Is the used application context.
     */
    protected ApplicationContext m_applicationContext;
    
    /**
     * Sets the location of the env bean property properties file.
     * 
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Resource envBeanPropertyLocation
            = m_applicationContext.getResource(
                ENV_BEAN_PROPERTY_PROPERTIES_LOCATION);
        
        String envBeanPropertyLocationUrl = "no url";
        if (envBeanPropertyLocation.exists()) {
            try {
                envBeanPropertyLocationUrl
                    = envBeanPropertyLocation.getURL().toString();
            } catch (IOException e) {
                envBeanPropertyLocationUrl = "unknown url";
            }
        }
        
        if (envBeanPropertyLocation.exists()) {
            s_el4jLogger.debug("The used env bean property properties file is '"
                + envBeanPropertyLocationUrl + "'.");
            super.setLocation(envBeanPropertyLocation);
        } else {
            s_el4jLogger.warn(
                "No env bean property properties file could be found. The "
                    + "correct location for this file is '"
                    + ENV_BEAN_PROPERTY_PROPERTIES_LOCATION + "'.");
        }
        
        super.postProcessBeanFactory(beanFactory);
    }
    
    /**
     * NOT ALLOWED TO USE!
     * 
     * {@inheritDoc}
     */
    @Override
    public void setLocation(Resource location) {
        CoreNotificationHelper.notifyMisconfiguration(
            "It is not allowed to set the location manually!");
    }
    
    /**
     * NOT ALLOWED TO USE!
     * 
     * {@inheritDoc}
     */
    @Override
    public void setLocations(Resource[] locations) {
        CoreNotificationHelper.notifyMisconfiguration(
            "It is not allowed to set the locations manually!");
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        m_applicationContext = applicationContext;
    }

}
