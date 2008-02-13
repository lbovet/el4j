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

package ch.elca.el4j.tests.env;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class test the environment support.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class EnvTest extends TestCase {
    /** The highest transport protocol port number. */ 
    // Checkstyle: MagicNumber off
    private static final int MAX_PORT = 1 << 16 - 1;
    // Checkstyle: MagicNumber on
    
    /** The application context. */
    private final ApplicationContext m_appContext;
    
    /** The container instance. */
    private final ServletContainer m_container;
    
    /**
     * Default constructor.
     */
    public EnvTest() {
        m_appContext = new ModuleApplicationContext(new String[] {
            "classpath*:mandatory/*.xml", 
            "classpath:scenarios/envtest/environment.xml"}, 
            false);
        
        m_container 
            = (ServletContainer) m_appContext.getBean("servletContainer");
    }

    /**
     * Checks whether the provided values are in the expected range.
     *
     */
    public void testConformance() {
        assertNotNull("Container name not set.", m_container.getContainer());
        assertTrue("Container's port out of range.",
                m_container.getPort() > 0 
                && m_container.getPort() <= MAX_PORT);
    }
}
