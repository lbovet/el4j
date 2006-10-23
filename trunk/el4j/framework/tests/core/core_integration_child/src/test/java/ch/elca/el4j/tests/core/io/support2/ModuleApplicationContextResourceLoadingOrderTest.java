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
package ch.elca.el4j.tests.core.io.support2;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.core.io.support.helper.Employee;

import junit.framework.TestCase;

/**
 * Tests the order how resources are loaded by the module application context.
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
public class ModuleApplicationContextResourceLoadingOrderTest extends TestCase {
    /**
     * @see #getApplicationContext()
     */
    private ApplicationContext m_applicationContext = null;
    
    /**
     * @param mergeWithOuterSources
     *            If <code>true</code> unordered resources will be used too.
     * @param mostSpecificResourceLast
     *            If <code>false</code> most specific resources will be found
     *            as first.
     * @return Returns the application context used for testing.
     */
    protected ApplicationContext getApplicationContext(
        boolean mergeWithOuterSources,
        boolean mostSpecificResourceLast) {
        if (m_applicationContext == null) {
            m_applicationContext = new ModuleApplicationContext(new String[] {
                "classpath*:scenarios/core/io/support2/*.xml"
            }, null, false, null, mergeWithOuterSources, 
                mostSpecificResourceLast, true);
        }
        return m_applicationContext;
    }

    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files. Outer resources will be included.
     */
    public void testEmployee1ConfigOverridingWithOuterResources() {
        ApplicationContext appContext = getApplicationContext(true, false);
        Employee e1 = (Employee) appContext.getBean("employee1");
        assertNotNull(e1);
        assertEquals("Martin", e1.getPrename());
        assertEquals("Zeltner", e1.getLastname());
    }
    
    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files. Outer resources will be excluded.
     */
    public void testEmployee1ConfigOverridingWithoutOuterResources() {
        ApplicationContext appContext = getApplicationContext(false, false);
        Employee e1 = (Employee) appContext.getBean("employee1");
        assertNotNull(e1);
        assertEquals("Martin", e1.getPrename());
        assertEquals("Zeltner", e1.getLastname());
    }

    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files and directly on classpath. Outer resources
     * will be included.
     */
    public void testEmployee2ConfigOverridingWithOuterResources() {
        ApplicationContext appContext = getApplicationContext(true, false);
        Employee e2 = (Employee) appContext.getBean("employee2");
        assertNotNull(e2);
        assertEquals("Dominic", e2.getPrename());
        assertEquals("Ullmann", e2.getLastname());
    }
    
    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files and directly on classpath. Outer resources
     * will be excluded.
     */
    public void testEmployee2ConfigOverridingWithoutOuterResources() {
        ApplicationContext appContext = getApplicationContext(false, false);
        Employee e2 = (Employee) appContext.getBean("employee2");
        assertNotNull(e2);
        assertEquals("Philipp", e2.getPrename());
        assertEquals("Oser", e2.getLastname());
    }

    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files and directly on classpath. Outer resources
     * will be included. The least specific resource will be most important.
     */
    public void 
    testEmployee2ConfigOverridingWithOuterResourcesReverseResourceOrder() {
        ApplicationContext appContext = getApplicationContext(true, true);
        Employee e2 = (Employee) appContext.getBean("employee2");
        assertNotNull(e2);
        assertEquals("Marc", e2.getPrename());
        assertEquals("Lehmann", e2.getLastname());
    }
    
    /**
     * Tests the config overriding by application context when looked up
     * resources are in jar files and directly on classpath. Outer resources
     * will be excluded. The least specific resource will be most important.
     */
    public void 
    testEmployee2ConfigOverridingWithoutOuterResourcesReverseResourceOrder() {
        ApplicationContext appContext = getApplicationContext(false, true);
        Employee e2 = (Employee) appContext.getBean("employee2");
        assertNotNull(e2);
        assertEquals("Marc", e2.getPrename());
        assertEquals("Lehmann", e2.getLastname());
    }
}
