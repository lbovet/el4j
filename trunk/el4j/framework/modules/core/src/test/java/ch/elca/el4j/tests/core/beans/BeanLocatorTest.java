/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.tests.core.beans;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.beans.BeanLocator;
import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This test tests the <code>BeanLocator</code> class.
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
public class BeanLocatorTest extends TestCase {
    
    /** The application context. */
    final ApplicationContext m_appContext;

    /**
     * Default constructor.
     */
    public BeanLocatorTest() {
        m_appContext = new ModuleApplicationContext(
            "classpath:scenarios/core/beans/*.xml", false);
    }

    /**
     * This test locates classes of specific types.
     */
    public void testLocateClassesOfSpecificTypes() {
        BeanLocator beanLocator = (BeanLocator) m_appContext
                .getBean("locateClassesOfSpecificTypes");
        Map beans = beanLocator.getBeans();
        String[] expectedBeans = new String[] {"classB1", "classB2",
            "classB3", "fourthB", "classC", "secondC", "thirdC", "classW1",
            "classW2", "thirdW"};
        checkIfAllExpectedBeansAreInMap(beans, expectedBeans);
    }

    /**
     * This test locates classes of specific types and excludes some beans.
     */
    public void testLocateClassesOfSpecificTypesPlusExcludeList() {
        BeanLocator beanLocator = (BeanLocator) m_appContext
                .getBean("locateClassesOfSpecificTypesPlusExcludeList");
        Map beans = beanLocator.getBeans();
        String[] expectedBeans = new String[] {"fourthB", "secondC", "thirdC",
            "thirdW"};
        checkIfAllExpectedBeansAreInMap(beans, expectedBeans);
    }

    /**
     * This test locates classes of specific types and includes some beans.
     */
    public void testLocateClassesOfSpecificTypesPlusIncludeList() {
        BeanLocator beanLocator = (BeanLocator) m_appContext
                .getBean("locateClassesOfSpecificTypesPlusIncludeList");
        Map beans = beanLocator.getBeans();
        String[] expectedBeans = new String[] {"classC", "secondC", "thirdC",
            "thirdW"};
        checkIfAllExpectedBeansAreInMap(beans, expectedBeans);
    }

    /**
     * This test locates classes of specific types and includes and excludes
     * some beans.
     */
    public void testLocateClassesOfSpecificTypesPlusIncludeAndExcludeList() {
        BeanLocator beanLocator = (BeanLocator) m_appContext.getBean(
                "locateClassesOfSpecificTypesPlusIncludeAndExcludeList");
        Map beans = beanLocator.getBeans();
        String[] expectedBeans = new String[] {"classB2", "classB3", "classC",
            "secondC", "thirdC", "firstV", "classW2"};

        checkIfAllExpectedBeansAreInMap(beans, expectedBeans);
    }

    /**
     * This method checks if a map contains the right number of beans and if the
     * expected beans are present in the given map.
     * 
     * @param beans
     *            Are the beans found by <code>BeanLocator</code>
     * @param expectedBeans
     *            Are names of the expected beans.
     */
    private void checkIfAllExpectedBeansAreInMap(Map beans,
            String[] expectedBeans) {
        int expectedNumberOfBeans = (expectedBeans != null
                ? expectedBeans.length : 0);
        assertEquals("There is not the expected number of beans.",
                expectedNumberOfBeans, beans.size());

        for (int i = 0; i < expectedNumberOfBeans; i++) {
            String beanName = expectedBeans[i];
            assertTrue("Bean with name '" + beanName
                    + "' is not present in map.", beans.containsKey(beanName));
        }
    }
}