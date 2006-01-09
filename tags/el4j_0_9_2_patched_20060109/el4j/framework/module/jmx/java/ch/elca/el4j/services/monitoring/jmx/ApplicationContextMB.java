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

package ch.elca.el4j.services.monitoring.jmx;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * The proxy class for an ApplicationContext.
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
public class ApplicationContextMB implements ApplicationContextMBMBean {

    /**
     * The domain of the Application Context proxy as it will be registered at
     * the MBean Server.
     */
    public static final String APPLICATION_CONTEXT_DOMAIN 
        = "ApplicationContext";

    /**
     * The message to be printed in case the used Application Context is not the
     * ModuleApplicationContext.
     */
    public static final String NOT_MODULE_APPLICATION_CONTEXT = "This context "
        + "was not loaded via the "
        + "'ch.elca.el4j.core.context.ModuleApplicationContext'. This is "
        + "the reason why the configuration locations cannot be displayed.";

    /**
     * The counter on the number of Application Contexts.
     */
    private static int s_counter = 1;

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(ApplicationContextMB.class);

    /**
     * The reference to the real Application Context.
     */
    protected ApplicationContext m_applicationContext;

    /**
     * The MBean Server to which this Application Context is registered.
     */
    protected MBeanServer m_mBeanServer;

    /**
     * The Bean Factory belonging to this Application Context.
     */
    protected BeanFactory m_beanFactory;

    /**
     * The instance counter of this object.
     */
    private int m_instanceCounter;

    /**
     * An array of spring beans registered to this Application Context.
     */
    private SpringBeanMB[] m_springBeanMB;

    /**
     * The object name of this Application Context.
     */
    private ObjectName m_objectName;

    /**
     * The proxy JVM in which this Application Context is running.
     */
    private JvmMB m_jvmmb;

    /**
     * Constructor.
     * 
     * @param applicationContext
     *            The Application Context to which this class is a proxy.
     * @param beanFactory
     *            The Bean Factory belonging to this Application Context.
     * @param mBeanServer
     *            The MBean Server to which this Application Context is
     *            registered.
     * @param jvmmb
     *            The proxy JVM in which this Application Context is running.
     */
    public ApplicationContextMB(ApplicationContext applicationContext,
        BeanFactory beanFactory, MBeanServer mBeanServer, JvmMB jvmmb) {

        this.m_applicationContext = applicationContext;
        this.m_beanFactory = beanFactory;
        this.m_mBeanServer = mBeanServer;
        this.m_jvmmb = jvmmb;

    }

    /**
     * Init method. Sets up this ApplicationContextMB.
     * 
     * @throws BaseException
     *             in case the initialization failed
     */
    public void init() throws BaseException {

        String[] beanDefinitionNames = m_applicationContext
            .getBeanDefinitionNames();

        // Set the instanceCounter.
        setInstanceCounter();

        // Set the object name of this object.
        setObjectName();

        int cnt = m_applicationContext.getBeanDefinitionCount();

        m_springBeanMB = new SpringBeanMB[cnt];

        // Create the proxies of the SpringBeans loaded by this Application
        // Context.
        for (int i = 0; i < cnt; i++) {
            m_springBeanMB[i] = new SpringBeanMB(beanDefinitionNames[i], this,
                m_applicationContext, m_beanFactory, m_mBeanServer);
            m_springBeanMB[i].init();
        }

        // Register the Application Context at the MBean Server.
        registerApplicationContext();

        // Add this Application Context proxy to the JVM proxy.
        registerAtJvmMB();

    }

    /**
     * Register this ApplicationContext at the MBean Server.
     * 
     * @throws BaseException
     *             in case the registration at the MBean Server failed
     */
    protected void registerApplicationContext() throws BaseException {

        Reject.ifNull(getObjectName(), "The object name of the "
                + "ApplicationContextMB '" + this.toString()
                + "' should not be null.");

        try {
            m_mBeanServer.registerMBean(this, getObjectName());
        } catch (InstanceAlreadyExistsException e) {
            String message = "The MBean is already under the control"
                + " of the MBean server.";
            s_logger.error(message);
            throw new BaseException(message, e);
        } catch (MBeanRegistrationException e) {
            String message = "The MBean will not be registered.";
            s_logger.error(message);
            throw new BaseException(message, e);
        } catch (NotCompliantMBeanException e) {
            String message = "This object is not a JMX compliant MBean.";
            s_logger.error(message);
            throw new BaseException(message, e);
        }
    }

    /**
     * Register this ApplicationContext at the JVM MBean.
     *  
     */
    protected void registerAtJvmMB() {
        m_jvmmb.addApplicationContext(this);
    }

    /**
     * Set the objectName of this ApplicationContext.
     *  
     */
    protected void setObjectName() {

        // valid JMX key properties must not contain any of these chars
        // :",=*?  (JMX specs v1.2, p110)
        String dispName = getName().replaceAll("[:\\\",=*?]", " ");
        
        // Create the object name String.
        String name = APPLICATION_CONTEXT_DOMAIN + ":name="
            + getInstanceCounter() + " - " + dispName;

        try {
            m_objectName = new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The string passed as a parameter does not have"
                    + " the right format.", e);
        }
    }

    /**
     * Returns the object name of the ApplicationContext.
     * 
     * @return objectName The object name of this Application Context proxy.
     */
    public ObjectName getObjectName() {
        return m_objectName;
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
     * The getter method for the instanceCounter member.
     * 
     * @return The instanceCounter member
     */
    public int getInstanceCounter() {
        return m_instanceCounter;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return m_applicationContext.getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectName[] getSpringBeansMB() {

        List springBeansList = new ArrayList();

        for (int i = 0; i < m_springBeanMB.length; i++) {
            if (m_mBeanServer.isRegistered(m_springBeanMB[i].getObjectName())) {
                springBeansList.add(m_springBeanMB[i].getObjectName());
            }
        }

        return (ObjectName[]) springBeansList
            .toArray(new ObjectName[springBeansList.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectName getJvmMB() {
        return m_jvmmb.getObjectName();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getResolvedConfigLocations() {
        String[] result = null;
        if (m_applicationContext instanceof ModuleApplicationContext) {
            ModuleApplicationContext lac 
                = (ModuleApplicationContext) m_applicationContext;
            result = lac.getConfigLocations();
        } else {
            result = new String[] {};
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getInclusiveConfigLocations() {
        String[] result = null;
        if (m_applicationContext instanceof ModuleApplicationContext) {
            ModuleApplicationContext lac 
                = (ModuleApplicationContext) m_applicationContext;
            result = lac.getInclusiveConfigLocations();
        } else {
            result = new String[] {NOT_MODULE_APPLICATION_CONTEXT};
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getExclusiveConfigLocations() {
        String[] result = null;
        if (m_applicationContext instanceof ModuleApplicationContext) {
            ModuleApplicationContext lac 
                = (ModuleApplicationContext) m_applicationContext;
            result = lac.getExclusiveConfigLocations();
        } else {
            result = new String[] {NOT_MODULE_APPLICATION_CONTEXT};
        }
        return result;
    }

    /**
     * Setter for the springBeanMB member.
     * 
     * @param springBeanMB
     *            The value to set
     */
    protected void setSpringBeanMB(SpringBeanMB[] springBeanMB) {
        this.m_springBeanMB = springBeanMB;
    }

    /**
     * Getter for the springBeanMB member.
     * 
     * @return The value of the springBeanMB member
     */
    public SpringBeanMB[] getSpringBeanMB() {
        return m_springBeanMB;
    }
}