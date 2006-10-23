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
package ch.elca.el4j.tests.core.io.support3;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.core.io.support.helper.Employee;

import junit.framework.TestCase;

/**
 * Tests the bean definition overriding behavior of the module application
 * context.
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
public class ModuleApplicationContextBeanOverridingTest extends TestCase {
    /**
     * Config location prefix.
     */
    public static final String CONFIG_LOCATION_PREFIX 
        = "classpath*:scenarios/core/io/support3/";
    
    /**
     * Name of the tested bean.
     */
    public static final String BEAN_NAME = "employee";
    
    /**
     * Tests bean overriding by using a config location with a wildcard and
     * not including outer resources.
     */
    public void testConfigLocationWithWildcardWithoutOuterResources() {
        boolean mergeWithOuterResources = false;
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {CONFIG_LOCATION_PREFIX + "*.xml"}, 
            null, true, null, mergeWithOuterResources);
        Employee e = (Employee) appContext.getBean("employee");
        assertNotNull(e);
        assertEquals("Martin", e.getPrename());
        assertEquals("Zeltner", e.getLastname());
    }
    
    /**
     * Tests bean overriding by using a config location with a wildcard and
     * including outer resources. The outer resources are the most specific.
     */
    public void 
    testConfigLocationWithWildcardWithOuterResourcesAsMostSpecific() {
        boolean mergeWithOuterResources = true;
        boolean mostSpecificResourceLast = true;
        boolean mostSpecificBeanDefinitionCounts = true;
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {CONFIG_LOCATION_PREFIX + "*.xml"}, 
            null, true, null, mergeWithOuterResources, mostSpecificResourceLast,
            mostSpecificBeanDefinitionCounts);
        Employee e = (Employee) appContext.getBean("employee");
        assertNotNull(e);
        assertEquals("Philipp", e.getPrename());
        assertEquals("Oser", e.getLastname());
    }

    /**
     * Tests bean overriding by using a config location with a wildcard but
     * without outer resources. The least specific resources are the most
     * important ones.
     */
    public void 
    testConfigLocationWithWildcardWithoutOuterResourcesAndLeastSpecifics() {
        boolean mergeWithOuterResources = false;
        boolean mostSpecificResourceLast = true;
        boolean mostSpecificBeanDefinitionCounts = false;
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {CONFIG_LOCATION_PREFIX + "*.xml"}, 
            null, true, null, mergeWithOuterResources, mostSpecificResourceLast,
            mostSpecificBeanDefinitionCounts);
        Employee e = (Employee) appContext.getBean("employee");
        assertNotNull(e);
        assertEquals("Alex", e.getPrename());
        assertEquals("Mathey", e.getLastname());
    }

    /**
     * Tests bean overriding by using a config location with a wildcard and
     * including outer resources. The outer resources are the least specific.
     */
    public void 
    testConfigLocationWithWildcardWithOuterResourcesAsLeastSpecific() {
        boolean mergeWithOuterResources = true;
        boolean mostSpecificResourceLast = true;
        boolean mostSpecificBeanDefinitionCounts = false;
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {CONFIG_LOCATION_PREFIX + "*.xml"}, 
            null, true, null, mergeWithOuterResources, mostSpecificResourceLast,
            mostSpecificBeanDefinitionCounts);
        Employee e = (Employee) appContext.getBean("employee");
        assertNotNull(e);
        assertEquals("Alex", e.getPrename());
        assertEquals("Mathey", e.getLastname());
    }

    /**
     * Tests bean overriding by using a config location with a wildcard and
     * including outer resources.
     */
    public void testStrictConfigLocationOrder() {
        boolean mergeWithOuterResources = true;
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {
                CONFIG_LOCATION_PREFIX
                    + "bean-definition-overriding-test-3.xml",
                CONFIG_LOCATION_PREFIX
                    + "bean-definition-overriding-test-2.xml",
                CONFIG_LOCATION_PREFIX
                    + "bean-definition-overriding-test-1.xml"
            }, null, true, null, mergeWithOuterResources);
        Employee e = (Employee) appContext.getBean("employee");
        assertNotNull(e);
        assertEquals("Alex", e.getPrename());
        assertEquals("Mathey", e.getLastname());
    }
}
