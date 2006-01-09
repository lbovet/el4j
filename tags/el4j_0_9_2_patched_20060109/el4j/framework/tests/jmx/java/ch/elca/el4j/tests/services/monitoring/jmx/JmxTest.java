/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.tests.services.monitoring.jmx;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * JUnit Test Class for the JMX package.
 * 
 * <p>
 * If one wants to observe the SpringBeans of this test environment, then the
 * commented part at the end of the second method should be uncommented. The
 * SpringBeans of both ApplicationContexts of the first test can be accessed by
 * entering <A HREF="http://localhost:9092/">http://localhost:9092/ </A> at your
 * web browser. The SpringBeans of the second test of the first
 * ApplicationContext can be accessed by <A
 * HREF="http://localhost:9093/">http://localhost:9093/ </A>. The ones of the
 * second ApplicationContext can be viewed by a management console which
 * supports JSR 160 (JMX Remoting) connections. One such console is MC4J which
 * can be found at <A
 * HREF="http://mc4j.sourceforge.net/">http://mc4j.sourceforge.net/ </A>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class JmxTest extends TestCase {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(JmxTest.class);

    /**
     * All xml files in the 'mandatory' folder.
     */
    String m_mandatoryFiles = "classpath*:mandatory/*.xml";

    /**
     * Configuration file number 1.
     */
    String m_fileName1 = "classpath:optional/beans1.xml";

    /**
     * Configuration file number 2.
     */
    String m_fileName2 = "classpath:optional/beans2.xml";

    /**
     * Configuration file number 3.
     */
    String m_fileName3 = "classpath:optional/beans3.xml";

    /**
     * Configuration file number 4.
     */
    String m_fileName4 = "classpath:optional/beans4.xml";

    /**
     * This test loads two ApplicationContexts where MBeans are registered at
     * the same MBean Server and checks if the MBeans are really registered at
     * the MBean Server.
     * 
     * @throws MalformedObjectNameException
     *             The format of the String does not correspond to a valid
     *             ObjectName
     */
    public void testRegisteredMBeanWithTwoAppCxtsAndOneJmxInstance()
        throws MalformedObjectNameException {

        ApplicationContext tac1 = new ClassPathXmlApplicationContext(
            new String[] {m_mandatoryFiles, m_fileName1});

        ApplicationContext tac2 = new ModuleApplicationContext(new String[] {
            m_mandatoryFiles, m_fileName2}, false);

        ObjectName on1 = new ObjectName("MBean:name=foo1");
        ObjectName on2 = new ObjectName("MBean:name=foo2");
        ObjectName on3 = new ObjectName("SpringBean1:name=mBeanExporter");
        ObjectName on4 = new ObjectName("SpringBean2:name=foo2");

        MBeanServer mBeanServer1 = (MBeanServer) tac1.getBean("mBeanServer");

        MBeanServer mBeanServer2 = (MBeanServer) tac2.getBean("mBeanServer");

        assertEquals("The two MBean Servers in the two Application Contexts "
            + "should be the same.", mBeanServer1, mBeanServer2);

        assertTrue("MBean with object name 'MBean:name=foo1' is not "
            + "registered at the MBean Server", mBeanServer1.isRegistered(on1));

        assertTrue("MBean with object name 'MBean:name=foo2' is not "
            + "registered at the MBean Server", mBeanServer1.isRegistered(on2));

        assertTrue("MBean with object name 'SpringBean1:name=mBeanExporter1' "
            + "is not registered at the MBean Server", mBeanServer1
            .isRegistered(on3));

        assertTrue("MBean with object name 'SpringBean2:name=foo2' is not "
            + "registered at the MBean Server", mBeanServer1.isRegistered(on4));

    }

    /**
     * This test loads two ApplicationContexts where MBeans are registered at
     * two different MBean Servers and checks if the MBeans are really
     * registered at the corresponding MBean Server. The only difference in
     * configuration to the test before is that the 'defaultDomain' property of
     * the MBean Server is overridden in both ApplicationContexts and that the
     * second ApplicationContext can be connected by a JSR 160 JMX connection
     * instead of html.
     * 
     * @throws MalformedObjectNameException
     *             The format of the string does not correspond to a valid
     *             ObjectName
     */
    public void testRegisteredMBeanWithTwoAppCxtsAndTwoMBeanServers()
        throws MalformedObjectNameException {

        ApplicationContext tac1 = new ClassPathXmlApplicationContext(
            new String[] {m_mandatoryFiles, m_fileName3});

        ApplicationContext tac2 = new ClassPathXmlApplicationContext(
            new String[] {m_mandatoryFiles, m_fileName4});

        ObjectName on1 = new ObjectName("MBean:name=foo3");
        ObjectName on2 = new ObjectName("MBean:name=foo4");
        ObjectName on3 = new ObjectName("SpringBean3:name=mBeanExporter");
        ObjectName on4 = new ObjectName("SpringBean4:name=foo4");

        MBeanServer mBeanServer1 = (MBeanServer) tac1.getBean("mBeanServer");

        MBeanServer mBeanServer2 = (MBeanServer) tac2.getBean("mBeanServer");

        assertTrue("MBean with object name 'MBean:name=foo3' is not "
            + "registered at the MBean Server 1", mBeanServer1
            .isRegistered(on1));

        assertTrue("MBean with object name 'MBean:name=foo4' is not "
            + "registered at the MBean Server 2", mBeanServer2
            .isRegistered(on2));

        assertTrue("MBean with object name 'SpringBean3:name=mBeanExporter3' "
            + "is not registered at the MBean Server 1", mBeanServer1
            .isRegistered(on3));

        assertTrue("MBean with object name 'SpringBean4:name=foo4' is not "
            + "registered at the MBean Server 2", mBeanServer2
            .isRegistered(on4));

        s_logger.info("see source file to run it in non-stopping mode.");
        /*
        System.out.println("Waiting forever...");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            String message = "Another thread has interrupted the current "
                + "thread. The interrupted status of the current thread "
                + "is cleared when this exception is thrown.";
            s_logger.error(message);
            throw new BaseRTException(message, e);
        }
        */
    }
}