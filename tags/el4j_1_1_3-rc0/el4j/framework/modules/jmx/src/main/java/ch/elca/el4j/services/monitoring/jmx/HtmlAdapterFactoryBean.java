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

package ch.elca.el4j.services.monitoring.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class creates a HtmlAdapter for an MBeanServer.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class HtmlAdapterFactoryBean 
    implements FactoryBean, InitializingBean, DisposableBean {
    
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
     * Is the path to the used stylesheet. Default is set to 
     * <code>etc/jmx/stylesheet.css</code>.
     */
    private String m_stylesheetPath = "etc/jmx/stylesheet.css";

    /**
     * The port member.
     */
    private int m_port = DEFAULT_PORT;

    /**
     * The name of the ObjectName of the HtmlAdaptorServer.
     */
    private String m_name = null;
    
    /**
     * ObjectName of the html parser.
     */
    private String m_htmlParserName = null;

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
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getServer(), "server", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getStylesheetPath(), "stylesheetPath", this);

        /**
         * Increase instance counter.
         */
        setInstanceCounter();
        
        /**
         * Setup css html parser.
         */
        CssHtmlParser htmlParser = new CssHtmlParser(getStylesheetPath());
        if (!StringUtils.hasText(m_htmlParserName)) {
            m_htmlParserName 
                = "HtmlAdapter:name=HtmlParser" + getInstanceCounter();
        }
        ObjectName htmlParserObjectName = new ObjectName(m_htmlParserName);
        getServer().registerMBean(htmlParser, htmlParserObjectName);
        
        
        /**
         * Setup html adapter server.
         */
        m_htmlAdaptorServer = new HtmlAdaptorServer();
        m_htmlAdaptorServer.setPort(m_port);
        if (!StringUtils.hasText(m_name)) {
            m_name = "HtmlAdapter:name=HtmlAdapter" + getInstanceCounter();
        }
        ObjectName htmlAdapterServerObjectName = new ObjectName(getName());
        getServer().registerMBean(m_htmlAdaptorServer, 
            htmlAdapterServerObjectName);

        
        /**
         * Set css html parser in html adaptor server.
         */
        m_htmlAdaptorServer.setParser(htmlParserObjectName);
        m_htmlAdaptorServer.start();
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
     * @return Returns the htmlParserName.
     */
    public String getHtmlParserName() {
        return m_htmlParserName;
    }

    /**
     * @param htmlParserName The htmlParserName to set.
     */
    public void setHtmlParserName(String htmlParserName) {
        m_htmlParserName = htmlParserName;
    }

    /**
     * @return Returns the stylesheetPath.
     */
    public String getStylesheetPath() {
        return m_stylesheetPath;
    }

    /**
     * @param stylesheetPath The stylesheetPath to set.
     */
    public void setStylesheetPath(String stylesheetPath) {
        m_stylesheetPath = stylesheetPath;
    }

    /**
     * The getter method for the HtmlAdaptorServer.
     * 
     * @return The HtmlAdaptorServer.
     */
    public HtmlAdaptorServer getHtmlAdaptorServer() {
        return m_htmlAdaptorServer;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void destroy() throws Exception {
        // Try to stop the AdaptorServer
        if (m_htmlAdaptorServer != null) {
            m_htmlAdaptorServer.stop();
        }
    }
}