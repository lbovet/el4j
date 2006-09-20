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

package ch.elca.el4j.core.context;

import java.io.IOException;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * Class that loads the ApplicationContext given an array of config files and an
 * array of exclusion config files. The files can be declared via classpath or
 * filepath.
 * 
 * <p>
 * Classpath files can be declared by the following two patterns:
 * "ch/elca/.../x.xml" or by "classpath:ch/elca/.../x.xml".
 * 
 * <p>
 * Filepath files have to be declared in the following way:
 * "file:C:/folder/.../x.xml".
 * 
 * <p>
 * Classpath and filepath files can be declared via ant-style pattern. For
 * instance "classpath:ch/elca/el4j/*.xml" will be resolved into all xml files
 * in the specified classpath. If there is more than one file having the same
 * classpath in two different jars, then you have to change classpath:folder/...
 * into classpath*:folder/... Otherwise, only the file(s) from the first jar is
 * loaded.
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" ); </script>
 * 
 * @author Raphael Boog (RBO)
 * @author Andreas Bur (ABU)
 */
public class ModuleApplicationContext extends AbstractXmlApplicationContext {

    /**
     * Inclusive config locations.
     */
    private String[] m_inclusiveConfigLocations;

    /**
     * Exclusive config locations.
     */
    private String[] m_exclusiveConfigLocations;

    /**
     * Config locations.
     */
    private String[] m_configLocations;

    /**
     * Indicates if bean definition overriding is enabled.
     */
    private boolean m_allowBeanDefinitionOverriding = false;
    
    /** The resource pattern resolver. */
    private ListResourcePatternResolverDecorator m_patternResolver;
    
    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                allowBeanDefinitionOverriding);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations, new String[] {},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            String exclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                new String[] {exclusiveConfigLocation},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String exclusiveConfigLocation,
            boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations,
                new String[] {exclusiveConfigLocation},
                allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String inclusiveConfigLocation,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
                exclusiveConfigLocations, allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.core.context.ModuleApplicationContext#ModuleApplicationContext(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding, ApplicationContext parent) {
        this(inclusiveConfigLocations, exclusiveConfigLocations,
                allowBeanDefinitionOverriding, parent, true); 
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
     *            configuration files section of the manifest files should
     *            be merged with resources found by searching in the file
     *            system.
     */
    public ModuleApplicationContext(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding, ApplicationContext parent,
            boolean mergeWithOuterResources) {
        super(parent);
        m_inclusiveConfigLocations = inclusiveConfigLocations;
        m_exclusiveConfigLocations = exclusiveConfigLocations;
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
        
        if (mergeWithOuterResources) {
            /* HACK overrides the pattern resolver of the
             *      AbstractApplicationContext to perform a customized
             *      initialization.
             */
            m_patternResolver = (ListResourcePatternResolverDecorator)
                    getResourcePatternResolver();
            m_patternResolver.setMergeWithOuterResources(true);
        }
        
        ModuleApplicationContextUtils utils 
            = new ModuleApplicationContextUtils(this);
        
        m_configLocations = utils.calculateInputFiles(inclusiveConfigLocations,
                exclusiveConfigLocations, allowBeanDefinitionOverriding);
        
        refresh();

    }
    
    /**
     * @return Returns the ConfigLocations.
     */
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
     * {@inheritDoc}
     */
    public Resource[] getResources(String locationPattern) throws IOException {
        
        /* HACK The AbstractApplicationContext caches the resource pattern
         *      resolver in a private field. Defining a resource pattern
         *      resolver in this class allows configuring the resolver.
         */
        if (m_patternResolver == null) {
            return super.getResources(locationPattern);
        } else {
            return m_patternResolver.getResources(locationPattern);
        }
    }
    
    /**
     * Override method createBeanFactory() in class
     * AbstractRefreshableApplicationContext. The property
     * m_allowBeanDefinitionOverriding can be set and is handed over to the
     * DefaultListableBeanFactory which creates the BeanFactory.
     * 
     * @return the DefaultListableBeanFactory
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(
                getInternalParentBeanFactory());
        dlbf.setAllowBeanDefinitionOverriding(m_allowBeanDefinitionOverriding);
        return dlbf;
    }

    /**
     * {@inheritDoc}
     */
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ListResourcePatternResolverDecorator(
                new ManifestOrderedConfigLocationProvider());
    }
}