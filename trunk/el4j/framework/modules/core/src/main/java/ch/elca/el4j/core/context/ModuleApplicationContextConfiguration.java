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
package ch.elca.el4j.core.context;

import org.springframework.context.ApplicationContext;

/**
 * This class describes the configuration of a ModuleApplicationContext. It can
 * be used to specify how a ModuleApplicationContext should be configured
 * without actually creating the application context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public class ModuleApplicationContextConfiguration {
    /**
     * @see #setInclusiveConfigLocations(String[])
     */
    private String[] m_inclusiveConfigLocations = new String[0];

    /**
     * @see #setExclusiveConfigLocations(String[])
     */
    private String[] m_exclusiveConfigLocations = new String[0];

    /**
     * @see #isAllowBeanDefinitionOverriding()
     */
    private boolean m_allowBeanDefinitionOverriding = false;
    
    /**
     * @see #isMergeWithOuterResources()
     */
    private boolean m_mergeWithOuterResources = true;
    
    /**
     * @see #isMostSpecificResourceLast()
     */
    private boolean m_mostSpecificResourceLast = false;
    
    /**
     * @see #isMostSpecificBeanDefinitionCounts()
     */
    private boolean m_mostSpecificBeanDefinitionCounts = true;

    /**
     * @see #setParent(ApplicationContext)
     */
    private ApplicationContext m_parent = null;

    /**
     * @return Returns the allowBeanDefinitionOverriding.
     */
    public final boolean isAllowBeanDefinitionOverriding() {
        return m_allowBeanDefinitionOverriding;
    }

    /**
     * Indicates if bean definition overriding is enabled.
     * The default is set to <code>false</code>.
     * 
     * @param allowBeanDefinitionOverriding
     *            Is the allowBeanDefinitionOverriding to set.
     */
    public final void setAllowBeanDefinitionOverriding(
        boolean allowBeanDefinitionOverriding) {
        m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    /**
     * @return Returns the exclusiveConfigLocations.
     */
    public final String[] getExclusiveConfigLocations() {
        return m_exclusiveConfigLocations;
    }

    /**
     * Exclusive config locations.
     * The default is an empty string array.
     * 
     * @param exclusiveConfigLocations
     *            Is the exclusiveConfigLocations to set.
     */
    public final void setExclusiveConfigLocations(
        String[] exclusiveConfigLocations) {
        m_exclusiveConfigLocations = exclusiveConfigLocations;
    }

    /**
     * @return Returns the inclusiveConfigLocations.
     */
    public final String[] getInclusiveConfigLocations() {
        return m_inclusiveConfigLocations;
    }

    /**
     * Inclusive config locations.
     * The default is an empty string array.
     * 
     * @param inclusiveConfigLocations
     *            Is the inclusiveConfigLocations to set.
     */
    public final void setInclusiveConfigLocations(
        String[] inclusiveConfigLocations) {
        m_inclusiveConfigLocations = inclusiveConfigLocations;
    }

    /**
     * @return Returns the mergeWithOuterResources.
     */
    public final boolean isMergeWithOuterResources() {
        return m_mergeWithOuterResources;
    }

    /**
     * Indicates if unordered/unknown resources should be used.
     * The default is set to <code>true</code>.
     * 
     * @param mergeWithOuterResources
     *            Is the mergeWithOuterResources to set.
     */
    public final void setMergeWithOuterResources(
        boolean mergeWithOuterResources) {
        m_mergeWithOuterResources = mergeWithOuterResources;
    }

    /**
     * @return Returns the mostSpecificBeanDefinitionCounts.
     */
    public final boolean isMostSpecificBeanDefinitionCounts() {
        return m_mostSpecificBeanDefinitionCounts;
    }

    /**
     * Indicates if the most specific bean definition counts.
     * The default is set to <code>true</code>.
     * 
     * @param mostSpecificBeanDefinitionCounts
     *            Is the mostSpecificBeanDefinitionCounts to set.
     */
    public final void setMostSpecificBeanDefinitionCounts(
        boolean mostSpecificBeanDefinitionCounts) {
        m_mostSpecificBeanDefinitionCounts = mostSpecificBeanDefinitionCounts;
    }

    /**
     * @return Returns the mostSpecificResourceLast.
     */
    public final boolean isMostSpecificResourceLast() {
        return m_mostSpecificResourceLast;
    }

    /**
     * Indicates if the most specific resource should be the last resource
     * in the fetched resource array. If its value is set to <code>true</code>
     * and only one resource is requested the least specific resource will be
     * returned. The default is set to <code>false</code>.
     * 
     * @param mostSpecificResourceLast
     *            Is the mostSpecificResourceLast to set.
     */
    public final void setMostSpecificResourceLast(
        boolean mostSpecificResourceLast) {
        m_mostSpecificResourceLast = mostSpecificResourceLast;
    }

    /**
     * @return Returns the parent.
     */
    public final ApplicationContext getParent() {
        return m_parent;
    }

    /**
     * The parent application context.
     * The default is set to <code>null</code>.
     * 
     * @param parent Is the parent to set.
     */
    public final void setParent(ApplicationContext parent) {
        m_parent = parent;
    }
}
