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
package ch.elca.el4j.web.context;

import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * This class extends Spring's <code>ContextLoader</code>. It inherits its
 * complete behaviour but replaces the web application context with a
 * {@link ch.elca.el4j.web.context.ModuleWebApplicationContext} which can be
 * configured with inclusive configuration locations, exclusive configuration
 * locations, and booleans indicating whether bean overriding is allowed and
 * whether the resources retrieved by the configuration files section of the
 * manifest files should be merged with resources found by searching in the file
 * system.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see org.springframework.web.context.ContextLoader
 * @see ModuleContextLoaderListener
 * @author Alex Mathey (AMA)
 */
public class ModuleContextLoader extends ContextLoader {

    
    /**
     * Name of servlet context parameter that can specify the inclusive 
     * configuration locations for the application context.
     */
    public static final String INCLUSIVE_LOCATIONS_PARAM = "inclusiveLocations";
    
    /**
     * Name of servlet context parameter that can specify the exclusive 
     * configuration locations for the application context.
     */
    public static final String EXCLUSIVE_LOCATIONS_PARAM = "exclusiveLocations";
    
    /**
     * Name of servlet context parameter indicating whether bean definition
     * overriding is allowed in the application context.
     */
    public static final String BEAN_OVERRIDING_PARAM
        = "overrideBeanDefinitions";
    
    /**
     * Name of servlet context parameter indicating whether the resources
     * retrieved by the configuration files section of the manifest files should
     * be merged with resources found by searching in the file system.
     */
    public static final String MERGE_RESOURCES_PARAM = "mergeResources";
    
    /**
     * {@inheritDoc}
     */
    protected WebApplicationContext createWebApplicationContext(
        ServletContext servletContext, ApplicationContext parent)
        throws BeansException {

        Class contextClass = determineContextClass(servletContext);
        if (!ConfigurableWebApplicationContext.class
            .isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Custom context class ["
                + contextClass.getName()
                + "] is not of type ConfigurableWebApplicationContext");
        }

        ConfigurableWebApplicationContext wac;

        if (contextClass == ModuleWebApplicationContext.class) {

            String[] inclusiveConfigLocations = getInclusiveConfigLocations(
                servletContext);
            String[] exclusiveConfigLocations = getExclusiveConfigLocations(
                servletContext);
            boolean beanOverriding = isBeanDefinitionOverridingAllowed(
                servletContext);
            boolean mergeResources = isMergingWithOuterResourcesAllowed(
                servletContext);

            wac = new ModuleWebApplicationContext(inclusiveConfigLocations,
                exclusiveConfigLocations, beanOverriding, servletContext,
                mergeResources);

        } else {
            wac = (ConfigurableWebApplicationContext) BeanUtils
                .instantiateClass(contextClass);

            wac.setServletContext(servletContext);
            String configLocation = servletContext
                .getInitParameter(CONFIG_LOCATION_PARAM);
            if (configLocation != null) {
                wac.setConfigLocations(toStringArray(configLocation));
            }
        }
        wac.setParent(parent);
        wac.refresh();
        return wac;
    }

    /**
     * {@inheritDoc}
     */
    protected Class determineContextClass(ServletContext servletContext)
        throws ApplicationContextException {
        String contextClassName = servletContext
            .getInitParameter(CONTEXT_CLASS_PARAM);
        if (contextClassName != null) {
            try {
                return ClassUtils.forName(contextClassName);
            } catch (ClassNotFoundException ex) {
                throw new ApplicationContextException(
                    "Failed to load custom context class [" + contextClassName
                        + "]", ex);
            }
        } else {
            contextClassName
                = "ch.elca.el4j.web.context.ModuleWebApplicationContext";
            try {
                return ClassUtils.forName(contextClassName);
            } catch (ClassNotFoundException ex) {
                throw new ApplicationContextException(
                    "Failed to load default context class [" + contextClassName
                        + "]", ex);
            }
        }
    }
    
    /**
     * Returns the inclusive configuration locations.
     * 
     * @param servletContext
     *            current servlet context
     * @return array containing the inclusive configuration locations
     */
    protected String[] getInclusiveConfigLocations(ServletContext
        servletContext) {
        String inclusiveConfigLocations = servletContext
            .getInitParameter(INCLUSIVE_LOCATIONS_PARAM);
        if (inclusiveConfigLocations == null) {
            inclusiveConfigLocations = servletContext
                .getInitParameter(CONFIG_LOCATION_PARAM);
            if (inclusiveConfigLocations == null) {
                return null;
            }
        }
        return toStringArray(inclusiveConfigLocations);
    }
    
    /**
     * Returns the exclusive configuration locations.
     * 
     * @param servletContext
     *            current servlet context
     * @return array containing the inclusive configuration locations
     */
    protected String[] getExclusiveConfigLocations(ServletContext 
        servletContext) {
        String exclusiveConfigLocations = servletContext.
            getInitParameter(EXCLUSIVE_LOCATIONS_PARAM);
        if (exclusiveConfigLocations == null) {
            return null;
        }
        return toStringArray(exclusiveConfigLocations);
    }
    
    /**
     * Returns whether bean definition overriding is allowed.
     * @param servletContext current servlet context
     * @return boolean indicating whether bean overriding is allowed
     */
    protected boolean isBeanDefinitionOverridingAllowed(ServletContext 
        servletContext) {
        boolean override = false;
        String beanOverriding = servletContext.getInitParameter(
            BEAN_OVERRIDING_PARAM);
        if (beanOverriding != null) {
            if (beanOverriding.equals("true")) {
                override = true;
            }
        }
        return override;
    }
    
    /**
     * Returns whether the resources retrieved by the configuration files
     * section of the manifest files should be merged ith resources found by
     * searching in the file system.
     * @param servletContext current servlet context
     * @return boolean indicating whether resource merging is allowed
     */
    protected boolean isMergingWithOuterResourcesAllowed(ServletContext
        servletContext) {
        boolean merge = true;
        String mergeResources = servletContext.getInitParameter(
            MERGE_RESOURCES_PARAM);
        if (mergeResources != null) {
            if (mergeResources.equals("false")) {
                merge = false;
            }
        }
        return merge;
    }
    
    /**
     * Transforms a string of tokens into a string array. The tokens have to
     * be separated by {@link
     * ConfigurableWebApplicationContext#CONFIG_LOCATION_DELIMITERS}.
     * 
     * @param str
     *      The string to parse.
     *      
     * @return Returns the given string's token as array.
     */
    private String[] toStringArray(String str) {
        String[] result = new String[0];
        if (str != null) {
            result =  StringUtils.tokenizeToStringArray(str, 
                ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS);
        }
        return result;
    }
}
