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

package ch.elca.el4j.services.monitoring.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class creates a HtmlAdapter for an MBeanServer.
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
public class HtmlAdapterFactoryBean implements FactoryBean, InitializingBean {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(HtmlAdapterFactoryBean.class);

    /**
     * The counter on the number of created HtmlAdaptorServers.
     */
    private static int s_counter = 1;

    /**
     * The default port.
     */
    private static final int DEFAULT_PORT = 9092;

    /**
     * The instance counter of this object.
     */
    private int m_instanceCounter = 1;

    /**
     * The HtmlAdaptorServer which is created by this FactoryBean.
     */
    private HtmlAdaptorServer m_htmlAdaptorServer;

    /**
     * The MBean Server this HtmlAdaptorServer is registered at.
     */
    private MBeanServer m_server;

    /**
     * The port member.
     */
    private int m_port = DEFAULT_PORT;

    /**
     * The name of the ObjectName of the HtmlAdaptorServer.
     */
    private String m_name = null;

    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        return m_htmlAdaptorServer;
    }

    /**
     * {@inheritDoc}
     */
    public Class getObjectType() {
        return HtmlAdaptorServer.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Creating an HtmlAdaptorServer and register it at the MBean Server.
     * 
     * @throws Exception
     *             In case the object name is invalid or the HtmlAdaptorServer
     *             could not be registered at the MBean Server.
     */
    public void afterPropertiesSet() throws Exception {

        m_htmlAdaptorServer = new HtmlAdaptorServer();
        m_htmlAdaptorServer.setPort(m_port);

        setInstanceCounter();

        if (getName() == null) {
            String name = "HtmlAdapter:name=HtmlAdapter" + getInstanceCounter();
            setName(name);
        }

        ObjectName objectName = new ObjectName(getName());

        if (getServer() == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "No MBean Server was defined.");
        }

        getServer().registerMBean(getHtmlAdaptorServer(), objectName);

        getHtmlAdaptorServer().start();
    }

    /**
     * The getter method for the instanceCounter member.
     * 
     * @return The instanceCounter member
     */
    public int getInstanceCounter() {
        return m_instanceCounter;
    }

    /**
     * Save the class variable s_counter to an instance member and increment the
     * class variable by 1.
     *  
     */
    public void setInstanceCounter() {

        synchronized (getClass()) {
            m_instanceCounter = s_counter;
            s_counter++;
        }

    }

    /**
     * The getter method for the port member.
     * 
     * @return The port of the HtmlAdaptorServer.
     */
    public int getPort() {
        return m_port;
    }

    /**
     * The setter method for the port member.
     * 
     * @param port
     *            The port to set.
     */
    public void setPort(int port) {
        this.m_port = port;
    }

    /**
     * The getter method for the MBean Server.
     * 
     * @return The MBean Server this HtmlAdaptorServer is registered at.
     */
    public MBeanServer getServer() {
        return m_server;
    }

    /**
     * The setter method for the MBean Server.
     * 
     * @param mBeanServer
     *            The MBean Server to set.
     */
    public void setServer(MBeanServer mBeanServer) {
        this.m_server = mBeanServer;
    }

    /**
     * The getter method for the name of the ObjectName.
     * 
     * @return The name of the ObjectName.
     */
    public String getName() {
        return m_name;
    }

    /**
     * The setter method for the name of the ObjectName.
     * 
     * @param name
     *            The name of the ObjectName to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * The getter method for the HtmlAdaptorServer.
     * 
     * @return The HtmlAdaptorServer.
     */
    public HtmlAdaptorServer getHtmlAdaptorServer() {
        return m_htmlAdaptorServer;
    }
}