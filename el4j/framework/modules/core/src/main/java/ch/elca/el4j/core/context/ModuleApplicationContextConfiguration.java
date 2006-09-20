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
 * 
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
 */
public class ModuleApplicationContextConfiguration {

    /**
     * Inclusive config locations.
     */
    private String[] m_inclusiveConfigLocations;

    /**
     * Exclusive config locations.
     */
    private String[] m_exclusiveConfigLocations;

    /**
     * Indicates if bean definition overriding is enabled.
     */
    private boolean m_allowBeanDefinitionOverriding = false;

    /**
     * The parent application context.
     */
    private ApplicationContext m_parent;

    /**
     * Defines if the resources retrieved by the configuration files section of
     * the manifest files should be merged with resources found by searching in
     * the file system.
     */
    private boolean m_mergeWithOuterResources;

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String inclusiveConfigLocation, boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
            allowBeanDefinitionOverriding);
    }

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String[] inclusiveConfigLocations, boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations, new String[] {},
            allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String inclusiveConfigLocation, String exclusiveConfigLocation,
        boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation},
            new String[] {exclusiveConfigLocation},
            allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String[] inclusiveConfigLocations, String exclusiveConfigLocation,
        boolean allowBeanDefinitionOverriding) {
        this(inclusiveConfigLocations, new String[] {exclusiveConfigLocation},
            allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String inclusiveConfigLocation, String[] exclusiveConfigLocations,
        boolean allowBeanDefinitionOverriding) {
        this(new String[] {inclusiveConfigLocation}, exclusiveConfigLocations,
            allowBeanDefinitionOverriding, null);
    }

    /**
     * @see ch.elca.el4j.services.gui.richclient.ApplicationLauncher.ModuleApplicationContextConfiguration#ModuleApplicationContextConfiguration(String[],
     *      String[], boolean, ApplicationContext, boolean)
     */
    public ModuleApplicationContextConfiguration(
        String[] inclusiveConfigLocations, String[] exclusiveConfigLocations,
        boolean allowBeanDefinitionOverriding, ApplicationContext parent) {
        this(inclusiveConfigLocations, exclusiveConfigLocations,
            allowBeanDefinitionOverriding, parent, true);
    }

    /**
     * Creates a new ModuleApplicationContextConfiguration.
     * 
     * @param inclusiveConfigLocations
     *            Array of file paths which are included in the application
     *            context
     * @param exclusiveConfigLocations
     *            Array of file paths which are excluded from the application
     *            context
     * @param allowBeanOverriding
     *            A boolean which defines if overriding of bean definitions is
     *            allowed
     * @param parent
     *            The parent application context
     * @param mergeWithOuterResources
     *            A boolean which defines if the resources retrieved by the
     *            configuration files section of the manifest files should be
     *            merged with resources found by searching in the file system.
     */
    public ModuleApplicationContextConfiguration(
        String[] inclusiveConfigLocations, String[] exclusiveConfigLocations,
        boolean allowBeanOverriding, ApplicationContext parent,
        boolean mergeWithOuterResources) {
        m_inclusiveConfigLocations = inclusiveConfigLocations;
        m_exclusiveConfigLocations = exclusiveConfigLocations;
        m_allowBeanDefinitionOverriding = allowBeanOverriding;
        m_parent = parent;
        m_mergeWithOuterResources = mergeWithOuterResources;
    }

    /**
     * @return Returns the allowBeanDefinitionOverriding property.
     */
    public boolean isAllowBeanDefinitionOverriding() {
        return m_allowBeanDefinitionOverriding;
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
     * @return Returns the whether outer resources should be merged.
     */
    public boolean isMergeWithOuterResources() {
        return m_mergeWithOuterResources;
    }

    /**
     * @return Returns the parent application context.
     */
    public ApplicationContext getParent() {
        return m_parent;
    }

}
