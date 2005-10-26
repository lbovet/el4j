/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * The proxy class for a bean loaded in the ApplicationContext.
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
public class SpringBeanMB implements SpringBeanMBMBean {

    /**
     * The domain of the Spring Bean proxy as it will be registered at the MBean
     * Server.
     */
    public static final String SPRING_BEAN_DOMAIN = "SpringBean";

    /**
     * The name of the domain of the ObjectName.
     */
    public static final String MBEAN_DOMAIN = "MBean";

    /**
     * The name of the key of the ObjectName.
     */
    public static final String MBEAN_KEY = "name";

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(SpringBeanMB.class);

    /**
     * The Application Context where this Spring Bean is registered at.
     */
    protected ApplicationContext m_applicationContext;

    /**
     * The Application Context proxy for the real Application Context.
     */
    protected ApplicationContextMB m_applicationContextMB;

    /**
     * The class name of this class.
     */
    protected Class m_class;

    /**
     * The Bean Factory belonging to the referenced Application Context.
     */
    protected BeanFactory m_beanFactory;

    /**
     * The MBean Server where this Spring Bean is registered at.
     */
    private MBeanServer m_mBeanServer;

    /**
     * The bean definition name of this Spring Bean.
     */
    private String m_name;

    /**
     * The object name of this Spring Bean proxy.
     */
    private ObjectName m_objectName;

    /**
     * Constructor.
     * 
     * @param name
     *            The bean definition name of this Spring Bean
     * @param acMB
     *            The Application Context proxy
     * @param ac
     *            The real Application Context
     * @param beanFactory
     *            The Bean Factory belonging to this Application Context
     * @param mBeanServer
     *            The MBean Server where this Spring Bean is registered at
     */
    public SpringBeanMB(String name, ApplicationContextMB acMB,
        ApplicationContext ac, BeanFactory beanFactory,
        MBeanServer mBeanServer) {

        this.m_name = name;
        this.m_applicationContextMB = acMB;
        this.m_applicationContext = ac;
        this.m_mBeanServer = mBeanServer;
        this.m_beanFactory = beanFactory;
        this.m_class = ac.getType(name);

    }

    /**
     * Init method. Sets up this ApplicationContextMB.
     * 
     * @throws BaseException
     *             in case the initialization failed
     */
    public void init() throws BaseException {

        // Set the object name of this object.
        setObjectName();

        // Register the Spring Bean at the MBean Server.
        registerSpringBean();

    }

    /**
     * Sets the objectName to of this object.
     */
    public void setObjectName() {

        String name = SPRING_BEAN_DOMAIN
            + m_applicationContextMB.getInstanceCounter() + ":name="
            + getName();

        try {
            m_objectName = new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The string passed as a parameter does not have"
                    + " the right format.");
        }
    }

    /**
     * Getter method for the objectName member variable.
     * 
     * @return The objectName
     */
    public ObjectName getObjectName() {
        return m_objectName;
    }

    /**
     * Register this SpringBean at the MBean Server.
     * 
     * @throws BaseException
     *             in case the registration at the MBean Server failed
     */
    protected void registerSpringBean() throws BaseException {

        if (getObjectName() == null) {
            String message = "The object name of the SpringBeanMB '"
                + this.toString() + "' should not be null.";
            s_logger.error(message);
            throw new BaseRTException(message, (Object[]) null);
        } else {
            try {
                m_mBeanServer.registerMBean(this, getObjectName());
            } catch (InstanceAlreadyExistsException e) {
                String message = "The MBean is already under the "
                    + "control of the MBean server.";
                s_logger.error(message);
                throw new BaseException(message, e);
            } catch (MBeanRegistrationException e) {
                String message = "The MBean will not be registered.";
                s_logger.error(message);
                throw new BaseException(message, e);
            } catch (NotCompliantMBeanException e) {
                String message = "This object is not a JMX compliant"
                    + " MBean.";
                s_logger.error(message);
                throw new BaseException(message, e);
            }
        }
    }

    /**
     * Getter method for the mBeanServer member variable.
     * 
     * @return The MBean Server
     */
    public MBeanServer getMBeanServer() {
        return m_mBeanServer;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return m_name;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getConfiguration() {

        DefaultListableBeanFactory dlbf 
            = (DefaultListableBeanFactory) m_beanFactory;
        BeanDefinition bd = dlbf.getBeanDefinition(getName());

        // Extract the property values from the BeanDefinition object.
        MutablePropertyValues mpv = bd.getPropertyValues();

        PropertyValue[] pv = mpv.getPropertyValues();
        String[] result = new String[pv.length];

        for (int i = 0; i < pv.length; i++) {
            result[i] = pv[i].getName() + " = " + pv[i].getValue().toString();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectName getApplicationContextMB() {
        return m_applicationContextMB.getObjectName();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectName[] getRegisteredMBean() {

        Loader loader = new Loader();

        return loader.getObjectNames(getMBeanServer(), MBEAN_DOMAIN, MBEAN_KEY,
            getName());
    }

    /**
     * {@inheritDoc}
     */
    public String getClassName() {
        return m_class.toString();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getIsSingleton() {
        return m_applicationContext.isSingleton(getName());
    }

}