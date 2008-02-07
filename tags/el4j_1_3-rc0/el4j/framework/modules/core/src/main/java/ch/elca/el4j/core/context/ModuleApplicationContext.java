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

package ch.elca.el4j.core.context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * <p>
 * Class that loads the ApplicationContext given an array of config files and an
 * array of exclusion config files. The files can be declared via classpath or
 * filepath.
 * </p>
 * <p>
 * Classpath files can be declared by the following two patterns:
 * "ch/elca/.../x.xml" or by "classpath:ch/elca/.../x.xml".
 * </p>
 * <p>
 * Filepath files have to be declared in the following way:
 * "file:C:/folder/.../x.xml".
 * </p>
 * <p>
 * Classpath and filepath files can be declared via ant-style pattern. For
 * instance "classpath:ch/elca/el4j/*.xml" will be resolved into all xml files
 * in the specified classpath. If there is more than one file having the same
 * classpath in two different jars, then you have to change classpath:folder/...
 * into classpath*:folder/... Otherwise, only the file(s) from the first jar is
 * loaded.
 * </p>
 * 
 * Additional features: 
 * <ul>
 * <li>
 *    PropertyPlaceholder/ PropertyOverride configurers (actually all 
 *    {@link BeanFactoryPostProcessor}) can override properties of 
 *    {@link BeanFactoryPostProcessor}s that come later in the order
 *    (see {@link Ordered} for more details).
 * </li>
 * <li>
 *    Exclusion lists (config files to explicitly exclude from the
 *    configuration)
 * </li>
 * <li>
 *    One can define (constructor argument) whether we allow bean-definitions
 *    to silently overwrite earlier configuration settings.
 * </li>
 * <li>
 *    The order of underlying resources is better conserved than with pure
 *    spring
 * </li>
 * <li>
 *    More configuration info is available in JMX (only when the jmx module is
 *    active)
 * </li>
 * </ul>  
 * 
 * @see ModuleWebApplicationContext 
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" ); </script>
 * 
 * @author Raphael Boog (RBO)
 * @author Andreas Bur (ABU)
 * @author Martin Zeltner (MZE)
 */
public class ModuleApplicationContext extends AbstractXmlApplicationContext {
    /**
     * The common debugging logger for EL4J. 
     */
    public static final String EL4J_DEBUGGING_LOGGER = "el4j.debugging";
    
    /** 
     * This logger is used to print out some global debugging info.
     * Consult it for info what is going on.
     */
    protected static Log s_el4jLogger
        = LogFactory.getLog(EL4J_DEBUGGING_LOGGER);
    
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
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], boolean)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                allowBeanDefinitionOverriding);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations, new String[] {},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            String exclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                new String[] {exclusiveConfigLocation},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String exclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations,
                new String[] {exclusiveConfigLocation},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                exclusiveConfigLocations, allowBeanDefinitionOverriding, null);
    }

    /**
     * <ul>
     * <li>Merge with outer resources is set to <code>true</code>.</li>
     * </ul>
     * 
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding, ApplicationContext parent) {
        this(inclusiveConfigLocations, exclusiveConfigLocations,
                allowBeanDefinitionOverriding, parent, true); 
    }
    
    /**
     * <ul>
     * <li>Most specific resource last is set to <code>false</code>.</li>
     * <li>Most specific bean definition counts is set to 
     * <code>true</code>.</li>
     * </ul>
     * 
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(
     *      String[], String[], boolean, ApplicationContext, boolean, boolean,
     *      boolean)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
        String[] exclusiveConfigLocations,
        boolean allowBeanDefinitionOverriding, ApplicationContext parent,
        boolean mergeWithOuterResources) {
        this(inclusiveConfigLocations, exclusiveConfigLocations,
                allowBeanDefinitionOverriding, parent, mergeWithOuterResources,
                false, true); 
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
     * @param parent
     *            the parent context
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
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding, ApplicationContext parent,
            boolean mergeWithOuterResources,
            boolean mostSpecificResourceLast,
            boolean mostSpecificBeanDefinitionCounts) {
        super(parent);
        m_inclusiveConfigLocations = inclusiveConfigLocations;
        m_exclusiveConfigLocations = exclusiveConfigLocations;
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
        m_mergeWithOuterResources = mergeWithOuterResources;
        m_mostSpecificResourceLast = mostSpecificResourceLast;
        m_mostSpecificBeanDefinitionCounts = mostSpecificBeanDefinitionCounts;
        
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

        ModuleApplicationContextUtils utils 
            = new ModuleApplicationContextUtils(this);
        utils.setReverseConfigLocationResourceArray(
            isMostSpecificResourceLast() 
                != isMostSpecificBeanDefinitionCounts());
        
        m_configLocations = utils.calculateInputFiles(inclusiveConfigLocations,
                exclusiveConfigLocations, allowBeanDefinitionOverriding);
        
        additionalLoggingOutput(allowBeanDefinitionOverriding,
            mergeWithOuterResources, mostSpecificResourceLast,
            mostSpecificBeanDefinitionCounts);
        
        refresh();
    }

    /**
     * Constructor to create a module application context by using the given 
     * configurations.
     * 
     * @param config Is the used configuration.
     */
    public ModuleApplicationContext(
        ModuleApplicationContextConfiguration config) {
        this(config.getInclusiveConfigLocations(),
            config.getExclusiveConfigLocations(),
            config.isAllowBeanDefinitionOverriding(),
            config.getParent(),
            config.isMergeWithOuterResources(),
            config.isMostSpecificResourceLast(),
            config.isMostSpecificBeanDefinitionCounts());
    }
    
    /**
     * Log some interesting values.
     *  
     * Not nice: Code duplication between the 2 ModuleApplicationContext
     *           classes!
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

        s_el4jLogger.info(
            "Starting up ModuleApplicationContext. configLocations :"
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

            for (String configLocation : m_configLocations) {
                Resource res = getResource(configLocation);
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(res
                        .getInputStream()));
                    StringBuffer buf = new StringBuffer();
                    while (reader.ready()) {
                        buf.append(reader.readLine());
                        buf.append("\n");
                    }
                    s_el4jLogger.debug("Content of " + configLocation + " : "
                        + buf.toString() + "\n---");
                } catch (IOException e) {
                    // deliberately ignore exception
                    s_el4jLogger.debug(
                        "Error during printing of config location "
                            + configLocation, e);
                }               
            }
        }
    }
    
    /**
     * @return Returns the ConfigLocations.
     */
    @Override
    public String[] getConfigLocations() {
        return m_configLocations;
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
        ListResourcePatternResolverDecorator patternResolver 
            = new ListResourcePatternResolverDecorator(
                new ManifestOrderedConfigLocationProvider());
        patternResolver.setMostSpecificResourceLast(
            isMostSpecificResourceLast());
        patternResolver.setMergeWithOuterResources(
            isMergeWithOuterResources());
        m_patternResolver = patternResolver;
        return m_patternResolver; 
    }
    
    /**
     * Not just method {@link BeanFactoryPostProcessor#postProcessBeanFactory(
     * ConfigurableListableBeanFactory)} is invoked ordered but also the
     * creation of the factory post processor beans!
     * 
     * {@inheritDoc}
     */
    protected void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory) {
        ModuleApplicationContextUtils ctxUtil 
            = new ModuleApplicationContextUtils(this);
        ctxUtil.invokeBeanFactoryPostProcessorsStrictlyOrdered(beanFactory);
    }
}
