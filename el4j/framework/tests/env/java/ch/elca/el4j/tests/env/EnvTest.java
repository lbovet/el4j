/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.env;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * This class test the environment support.
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
public class EnvTest extends TestCase {

    /** The configuration location. */
    public static final String CONFIG = "classpath*:mandatory/*";
    
    /** The test bean's name. */
    public static final String CONTAINER_BEAN = "servletContainer";

    /** The highest transport protocol port number. */ 
    private static final int MAX_PORT = 1 << 16 - 1;
    
    /** The application context. */
    private ApplicationContext m_appContext;
    
    /** The container instance. */
    private ServletContainer m_container;
    
    /**
     * Default constructor.
     */
    public EnvTest() {
        m_appContext = new ClassPathXmlApplicationContext(CONFIG);
        
        m_container = (ServletContainer) m_appContext.getBean(CONTAINER_BEAN);
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
