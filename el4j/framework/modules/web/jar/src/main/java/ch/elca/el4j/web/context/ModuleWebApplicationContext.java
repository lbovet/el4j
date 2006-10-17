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

package ch.elca.el4j.web.context;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.support.XmlWebApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContextUtils;
import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * This web application context behaves exactly the same way as Spring's
 * {@link org.springframework.web.context.support.XmlWebApplicationContext} but
 * uses a {@link org.springframework.core.io.support.ResourcePatternResolver}
 * that preserves the order defined by the EL4Ant's module hierarchy. Further,
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
 * @see ch.elca.el4j.core.context.ModuleApplicationContext
 */
public class ModuleWebApplicationContext extends XmlWebApplicationContext {
    
    /**
     * Inclusive configuration locations.
     */
    private String[] m_inclusiveConfigLocations;
    
    /**
     * Exclusive configuration locations.
     */
    private String[] m_exclusiveConfigLocations;
    
    /**
     * Indicates if bean definition overriding is enabled.
     */
    private boolean m_allowBeanDefinitionOverriding = false;
    
    /** The resource pattern resolver. */
    private ListResourcePatternResolverDecorator m_patternResolver;
    
    /**
     * Creates a new instance and computes the set of configuration locations
     * by removing the exclusive locations from the inclusive ones.
     * 
     * @param inclusiveConfigLocations
     *      The configuration locations to include.
     * @param exclusiveConfigLocations
     *      The configuration locations to exclude.
     * @param allowBeanDefinitionOverriding
     *      Whether it's allowed to override already existing bean definitions.
     * @param context
     *      The Servlet context which this web application context is running
     *      in.
     * @param mergeWithOuterResources
     *      A boolean which defines if the resources retrieved by the 
     *      configuration files section of the manifest files should be merged
     *      with resources found by searching in the file system.
     */
    public ModuleWebApplicationContext(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding, ServletContext context,
            boolean mergeWithOuterResources) {
        
        setServletContext(context);
        
        m_inclusiveConfigLocations = inclusiveConfigLocations;
        m_exclusiveConfigLocations = exclusiveConfigLocations;
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
        
        /* HACK overrides the pattern resolver of the
         *      AbastractApplicationContext to perform a customized
         *      initialization.
         */
        m_patternResolver = (ListResourcePatternResolverDecorator)
            getResourcePatternResolver();
        m_patternResolver.setMergeWithOuterResources(mergeWithOuterResources);

        if (m_inclusiveConfigLocations != null
                && m_inclusiveConfigLocations.length > 0) {
            ModuleApplicationContextUtils utils 
                = new ModuleApplicationContextUtils(this);
            
            String[] configs = utils.calculateInputFiles(
                    inclusiveConfigLocations,
                    exclusiveConfigLocations,
                    allowBeanDefinitionOverriding);
            
            setConfigLocations(configs);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return m_patternResolver.getResources(locationPattern);
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
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ListResourcePatternResolverDecorator(
                new ManifestOrderedConfigLocationProvider(),
                super.getResourcePatternResolver(),
                new AntPathMatcher());
    }
    
    /**
     * @return Returns <code>true</code> if overriding of already exisiting
     *      bean definitions is allowed. <code>false</code> otherwise.
     */
    public boolean isAllowBeanDefinitionOverriding() {
        return m_allowBeanDefinitionOverriding;
    }

    /**
     * @return Returns the list of configuration file locations that has to
     *      be excluded when the application context is initialized.
     */
    public String[] getExclusiveConfigLocations() {
        return m_exclusiveConfigLocations;
    }

    /**
     * @return Returns the list of configuration file locations that are
     *      included when the application context is initizlized.
     */
    public String[] getInclusiveConfigLocations() {
        return m_inclusiveConfigLocations;
    }
}
