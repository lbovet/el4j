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

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * This class extends Springs <code>DispatcherServlet</code>. It inherits the
 * its complete behaviour but replaces the web application context with a
 * {@link ch.elca.el4j.web.context.ModuleWebApplicationContext} which 
 * preserves the order of module dependencies when resources are resolved.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ModuleDispatcherServlet extends DispatcherServlet {

    /**
     * The configuration locations that are included to initialize the
     * application context.
     */
    private String m_inclusiveLocations;
    
    /**
     * The configuration locations that are excluded during the application
     * context's initialization.
     */
    private String m_exclusiveLocations;
    
    /** Holds whether overriding of already defined beans is allowed. */
    private boolean m_allowBeanDefinitionOverriding = false;

    /**
     * Whether to merge the configuration locations defined in the manifest
     * files have to be merged with files looked up in the file system.
     */
    private boolean m_mergeWithOuterResources = false;
    
    /**
     * {@inheritDoc}
     */
    protected WebApplicationContext createWebApplicationContext(
            WebApplicationContext parent) throws BeansException {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Servlet with name '" + getServletName()
                    + "' will try to create custom WebApplicationContext "
                    + "context of class '" + getContextClass().getName() + "'"
                    + ", using parent context [" + parent + "]");
        }

        String[] incl = toStringArray(getInclusiveLocations());
        String[] excl = toStringArray(getExclusiveLocations());
        
        ModuleWebApplicationContext wac
            = new ModuleWebApplicationContext(incl, excl,
                    isAllowBeanDefinitionOverriding(),
                    getServletContext(),
                    m_mergeWithOuterResources);
        
        wac.setParent(parent);
        wac.setNamespace(getNamespace());
        wac.refresh();
        return wac;
    }

    /**
     * Sets whether it's allowed to override bean definitions, as it may happen
     * by using the same bean name in different configuration files, which
     * are loaded sequentially.
     *  
     * @param allowBeanDefinitionOverriding
     *      Whether it's allowed to override bean definitions.
     */
    public void setAllowBeanDefinitionOverriding(
            boolean allowBeanDefinitionOverriding) {
        
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    /**
     * Sets the list of configuration files that have to be excluded from
     * the application context's configuration.
     * 
     * @param exclusiveLocations
     *      Configuration locations to exclude.
     */
    public void setExclusiveLocations(String exclusiveLocations) {
        m_exclusiveLocations = exclusiveLocations;
    }

    /**
     * Sets the list of configuration files that have to be included when the
     * application context is initialized.
     * 
     * @param inclusiveLocations
     *      Configuration locations to include.
     */
    public void setInclusiveLocations(String inclusiveLocations) {
        m_inclusiveLocations = inclusiveLocations;
    }
    
    /**
     * @return Returns whether it's allowed to override already exisiting
     *      bean definitions.
     */
    public boolean isAllowBeanDefinitionOverriding() {
        return m_allowBeanDefinitionOverriding;
    }

    /**
     * @return Returns the list of configuration locations that are excluded
     *      when the application context is initialized.
     */
    public String getExclusiveLocations() {
        return m_exclusiveLocations;
    }

    /**
     * @return Returns the list of configuration locations that are used to
     *      initialize the application context.
     */
    public String getInclusiveLocations() {
        String result;
        if (m_inclusiveLocations == null) {
            result = super.getContextConfigLocation();
        } else {
            result = m_inclusiveLocations;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getContextConfigLocation() {
        return m_inclusiveLocations;
    }

    /**
     * @return Returns <code>true</code> if the resource pattern resolver merges
     *      items found in the manifest configuration section with items found
     *      in the file system. <code>false</code> otherwise.
     */
    public boolean isMergeWithOuterResources() {
        return m_mergeWithOuterResources;
    }

    /**
     * Sets whether configuration locations retrieved by the pattern resolver
     * have to be merged with resources looked up in the file system.
     * 
     * @param mergeWithOuterResources
     *      <code>true</code> for merging configuration locations provided
     *      through the manifest file with resources looked up in the file
     *      system. <code>false</code> otherwise.
     */
    public void setMergeWithOuterResources(boolean mergeWithOuterResources) {
        m_mergeWithOuterResources = mergeWithOuterResources;
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
