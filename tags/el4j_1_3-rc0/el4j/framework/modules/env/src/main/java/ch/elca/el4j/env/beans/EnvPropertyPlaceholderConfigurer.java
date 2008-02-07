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
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Specific property placeholder configurer for the EL4J environment.
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
public class EnvPropertyPlaceholderConfigurer
    extends PropertyPlaceholderConfigurer implements ApplicationContextAware {
    
    /**
     * The correct env location.
     */
    public static final String ENV_PLACEHOLDER_PROPERTIES_LOCATION
        = "classpath:env-placeholder.properties";
    
    /**
     * The deprecated env location.
     */
    public static final String OLD_ENV_PROPERTIES_LOCATION
        = "classpath:env/env.properties";

    /** 
     * This logger is used to print out some global debugging info.
     * Consult it for info what is going on.
     */
    protected static Log s_el4jLogger
        = LogFactory.getLog(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);
    
    /**
     * Is the used application context.
     */
    protected ApplicationContext m_applicationContext;
    
    /**
     * Sets the location of the env placeholder properties file. A fallback to
     * the old env location is possible.
     * 
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Resource oldEnvLocation
            = m_applicationContext.getResource(OLD_ENV_PROPERTIES_LOCATION);
        Resource envLocation
            = m_applicationContext.getResource(
                ENV_PLACEHOLDER_PROPERTIES_LOCATION);
        
        String oldEnvLocationUrl = "no url";
        if (oldEnvLocation.exists()) {
            try {
                oldEnvLocationUrl = oldEnvLocation.getURL().toString();
            } catch (IOException e) {
                oldEnvLocationUrl = "unknown url";
            }
        }
        
        String envLocationUrl = "no url";
        if (envLocation.exists()) {
            try {
                envLocationUrl = envLocation.getURL().toString();
            } catch (IOException e) {
                envLocationUrl = "unknown url";
            }
        }
        
        if (oldEnvLocation.exists() && envLocation.exists()) {
            CoreNotificationHelper.notifyMisconfiguration(
                "There are two environment configuration files on the "
                    + "classpath. Please remove the deprecated one. "
                    + "Correct location: '"
                    + envLocationUrl
                    + "'; deprecated location '"
                    + oldEnvLocationUrl + "'.");
        }
        
        if (envLocation.exists()) {
            s_el4jLogger.debug("The used env placeholder properties file is '"
                + envLocationUrl + "'.");
            super.setLocation(envLocation);
        } else if (oldEnvLocation.exists()) {
            s_el4jLogger.warn(
                "DEPRECATED: The used env placeholder properties file '"
                    + oldEnvLocationUrl
                    + "' is deprecated. Please use the new loaction '"
                    + ENV_PLACEHOLDER_PROPERTIES_LOCATION + "'.");
            super.setLocation(oldEnvLocation);
        } else {
            s_el4jLogger.warn(
                "No env placeholder properties file could be found. The "
                    + "correct location for this file is '"
                    + ENV_PLACEHOLDER_PROPERTIES_LOCATION + "'.");
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
