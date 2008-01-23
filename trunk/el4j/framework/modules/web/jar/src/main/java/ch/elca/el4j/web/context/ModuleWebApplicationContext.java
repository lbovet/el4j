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

package ch.elca.el4j.web.context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextUtils;
import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * This web application context behaves exactly the same way as Spring's
 * {@link org.springframework.web.context.support.XmlWebApplicationContext} but
 * uses a {@link org.springframework.core.io.support.ResourcePatternResolver}
 * that preserves the order defined by the EL4J's module hierarchy. Further,
 * it allows to define configuration locations that have to be included and
 * those, that have to be excluded. This allows removing individual
 * configuration files that are included using using wildcard notation.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @author Martin Zeltner (MZE)
 * @see ch.elca.el4j.core.context.ModuleApplicationContext
 */
public class ModuleWebApplicationContext extends XmlWebApplicationContext {
    
    /**
     * This logger is used to print out some global debugging info. Consult it
     * for info what is going on.
     */
    protected static Log s_el4jLogger
        = LogFactory.getLog(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);

    /**
     * Inclusive config locations.
     */
    private final String[] m_inclusiveConfigLocations;

    /**
     * Exclusive config locations.
     */
    private final String[] m_exclusiveConfigLocations;

    /**
     * Config locations.
     */
    private final String[] m_configLocations;

    /**
     * Indicates if bean definition overriding is enabled.
     */
    private final boolean m_allowBeanDefinitionOverriding;
    
    /**
     * Indicates if unordered/unknown resource should be used.
     */
    private final boolean m_mergeWithOuterResources;
    
    /**
     * Indicates if the most specific resource should be the last resource
     * in the fetched resource array. If its value is set to <code>true</code>
     * and only one resource is requested the least specific resource will be
     * returned. Default is set to <code>false</code>.
     */
    private final boolean m_mostSpecificResourceLast;
    
    /**
     * Indicates if the most specific bean definition counts.
     */
    private final boolean m_mostSpecificBeanDefinitionCounts;
    
    /**
     * The resource pattern resolver.
     */
    private ListResourcePatternResolverDecorator m_patternResolver;
    
    /**
     * <ul>
     * <li>Most specific resource last is set to <code>false</code>.</li>
     * <li>Most specific bean definition counts is set to 
     * <code>true</code>.</li>
     * </ul>
     * 
     * @see #ModuleWebApplicationContext(String[], String[], boolean,
     *       ServletContext, boolean, boolean, boolean))
     */
    public ModuleWebApplicationContext(String[] inclusiveConfigLocations,
        String[] exclusiveConfigLocations,
        boolean allowBeanDefinitionOverriding, ServletContext servletContext,
        boolean mergeWithOuterResources) {
        this(inclusiveConfigLocations, exclusiveConfigLocations,
            allowBeanDefinitionOverriding, servletContext,
            mergeWithOuterResources, false, true);
    }

    /**
     * Create a new ModuleApplicationContext with the given parent, loading the
     * definitions from the given XML files in "inclusiveConfigLocations"
     * excluded the XML files defined in "exclusiveConfigLocations". If the
     * parameter "allowBeanDefinitionOverriding" is set to true then the
     * BeanFactory is allowed to override a bean if there is another one with
     * the same name.
     * 
     * @param inclusiveConfigLocations
     *            array of file paths
     * @param exclusiveConfigLocations
     *            array of file paths which are excluded
     * @param allowBeanDefinitionOverriding
     *            a boolean which defines if overriding of bean definitions is
     *            allowed
     * @param servletContext
     *            the servlet context where this application context is used
     * @param mergeWithOuterResources
     *            a boolean which defines if the resources retrieved by the
     *            configuration files section of the manifest files should be
     *            merged with resources found by searching in the file system.
     * @param mostSpecificResourceLast
     *            Indicates if the most specific resource should be the last
     *            resource in the fetched resource array. If its value is set to
     *            <code>true</code> and only one resource is requested the
     *            least specific resource will be returned.
     * @param mostSpecificBeanDefinitionCounts
     *            Indicates that the most specific bean definition is used.
     */
    public ModuleWebApplicationContext(String[] inclusiveConfigLocations,
        String[] exclusiveConfigLocations,
        boolean allowBeanDefinitionOverriding, ServletContext servletContext,
        boolean mergeWithOuterResources, boolean mostSpecificResourceLast,
        boolean mostSpecificBeanDefinitionCounts) {

        super();
        
        m_inclusiveConfigLocations = inclusiveConfigLocations;
        m_exclusiveConfigLocations = exclusiveConfigLocations;
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
        m_mergeWithOuterResources = mergeWithOuterResources;
        m_mostSpecificResourceLast = mostSpecificResourceLast;
        m_mostSpecificBeanDefinitionCounts = mostSpecificBeanDefinitionCounts;
        setServletContext(servletContext);
        
        /**
         * HACK: The pattern resolver is initialized by a super class
         * via method <code>getResourcePatternResolver</code>.
         */
        Assert.notNull(m_patternResolver);
        Assert.isInstanceOf(ListResourcePatternResolverDecorator.class, 
            m_patternResolver);
        ListResourcePatternResolverDecorator listResourcePatternResolver
            = (ListResourcePatternResolverDecorator) m_patternResolver;
        listResourcePatternResolver.setMostSpecificResourceLast(
            isMostSpecificResourceLast());
        listResourcePatternResolver.setMergeWithOuterResources(
            isMergeWithOuterResources());
        
        if (servletContext != null) {
            listResourcePatternResolver.setPatternResolver(
                new ServletContextResourcePatternResolver(servletContext));
        }

        ModuleApplicationContextUtils utils 
            = new ModuleApplicationContextUtils(this);
        utils.setReverseConfigLocationResourceArray(
            isMostSpecificResourceLast() 
                != isMostSpecificBeanDefinitionCounts());
        
        m_configLocations = utils.calculateInputFiles(inclusiveConfigLocations,
                exclusiveConfigLocations, allowBeanDefinitionOverriding);

        if (!ArrayUtils.isEmpty(m_configLocations)) {
            setConfigLocations(m_configLocations);
        }
        
        additionalLoggingOutput(allowBeanDefinitionOverriding,
            mergeWithOuterResources, mostSpecificResourceLast,
            mostSpecificBeanDefinitionCounts);
    }

    /**
     * Log some interesting values. Not nice: Code duplication between the 2
     * ModuleApplicationContext classes!
     * 
     * @param allowBeanDefinitionOverriding
     *            a boolean which defines if overriding of bean definitions is
     *            allowed
     * @param mergeWithOuterResources
     *            a boolean which defines if the resources retrieved by the
     *            configuration files section of the manifest files should be
     *            merged with resources found by searching in the file system.
     * @param mostSpecificResourceLast
     *            Indicates if the most specific resource should be the last
     *            resource in the fetched resource array. If its value is set to
     *            <code>true</code> and only one resource is requested the
     *            least specific resource will be returned.
     * @param mostSpecificBeanDefinitionCounts
     *            Indicates that the most specific bean definition is used.
     */
    private void additionalLoggingOutput(boolean allowBeanDefinitionOverriding,
        boolean mergeWithOuterResources, boolean mostSpecificResourceLast,
        boolean mostSpecificBeanDefinitionCounts) {

        s_el4jLogger
            .info("Starting up ModuleApplicationContext. configLocations :"
                + StringUtils.arrayToDelimitedString(m_configLocations, ", "));
        if (s_el4jLogger.isDebugEnabled()) {
            s_el4jLogger.debug("inclusiveLocation:"
                + StringUtils.arrayToDelimitedString(
                    m_inclusiveConfigLocations, ", "));
            s_el4jLogger.debug("exclusiveLocation:"
                + StringUtils.arrayToDelimitedString(
                    m_exclusiveConfigLocations, ", "));
            s_el4jLogger.debug("allowBeanDefinitionOverriding:"
                + allowBeanDefinitionOverriding);
            s_el4jLogger.debug("mergeWithOuterResources:"
                + mergeWithOuterResources);
            s_el4jLogger.debug("mostSpecificResourceLast:"
                + mostSpecificResourceLast);
            s_el4jLogger.debug("mostSpecificBeanDefinitionCounts:"
                + mostSpecificBeanDefinitionCounts);

            try {
                for (String configLocation : m_configLocations) {
                    Resource res = getResource(configLocation);
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(res.getInputStream()));
                    StringBuffer buf = new StringBuffer();
                    while (reader.ready()) {
                        buf.append(reader.readLine());
                        buf.append("\n");
                    }
                    s_el4jLogger.debug("Content of " + configLocation + " : "
                        + buf.toString() + "\n---");

                }
            } catch (Exception e) {
                // deliberately ignore exception
                s_el4jLogger.debug("Error during printing of config location "
                    + m_configLocations, e);
            }
        }
    }
    
    /**
     * @return Returns the exclusiveConfigLocations.
     */
    public String[] getExclusiveConfigLocations() {
        return m_exclusiveConfigLocations;
    }

    /**
     * @return Returns the inclusiveConfigLocations.
     */
    public String[] getInclusiveConfigLocations() {
        return m_inclusiveConfigLocations;
    }

    /**
     * @return Returns the allowBeanDefinitionOverriding.
     */
    public boolean isAllowBeanDefinitionOverriding() {
        return m_allowBeanDefinitionOverriding;
    }

    /**
     * @return Returns the mergeWithOuterResources.
     */
    public boolean isMergeWithOuterResources() {
        return m_mergeWithOuterResources;
    }

    /**
     * @return Returns the mostSpecificResourceLast.
     */
    public boolean isMostSpecificResourceLast() {
        return m_mostSpecificResourceLast;
    }

    /**
     * @return Returns the mostSpecificBeanDefinitionCounts.
     */
    public boolean isMostSpecificBeanDefinitionCounts() {
        return m_mostSpecificBeanDefinitionCounts;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource(String location) {
        return m_patternResolver.getResource(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return m_patternResolver.getResources(locationPattern);
    }
    
    /**
     * Override method createBeanFactory() in class
     * AbstractRefreshableApplicationContext. The property
     * m_allowBeanDefinitionOverriding can be set and is handed over to the
     * DefaultListableBeanFactory which creates the BeanFactory.
     * 
     * @return the DefaultListableBeanFactory
     */
    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(
                getInternalParentBeanFactory());
        dlbf.setAllowBeanDefinitionOverriding(
            isAllowBeanDefinitionOverriding());
        return dlbf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        ResourceLoader resourceLoader;
        ServletContext servletContext = getServletContext();
        if (servletContext == null) {
            resourceLoader = new DefaultResourceLoader(getClassLoader());
        } else {
            resourceLoader = new ServletContextResourceLoader(servletContext);
        }
        
        ListResourcePatternResolverDecorator patternResolver 
            = new ListResourcePatternResolverDecorator(
                new ManifestOrderedConfigLocationProvider(),
                new ServletContextResourcePatternResolver(
                    resourceLoader));
        patternResolver.setMostSpecificResourceLast(
            isMostSpecificResourceLast());
        patternResolver.setMergeWithOuterResources(
            isMergeWithOuterResources());
        m_patternResolver = patternResolver;
        return m_patternResolver; 
    }
}
