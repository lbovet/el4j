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
package ch.elca.el4j.services.monitoring.jmx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.OptionConverter;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * The logging proxy class, for setting logging properties via JMX. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/module/jmx/java/ch/elca/el4j/services/monitoring/jmx/SpringBeanMBMBean.java
 * $", "$Revision$", "$Date: 2006-03-13 14:15:43 +0100 (Mo, 13 Mrz 2006)
 * $", "$Author$" );</script>
 * 
 * @author Rashid Waraich (RWA)
 */
public class Log4jConfig implements Log4jConfigMBean {
    /**
     * The domain of the Logger bean proxy as it will be registered at the MBean
     * Server.
     */
    public static final String JVM_DOMAIN = "JVM";

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
     * Contains all appenders, which are currently loaded.
     */
    private HashMap m_appendersPool;

    /**
     * All logging level changes are stored here.
     */
    private HashMap m_loggingLevelCache = new HashMap();

    /**
     * True, if the RootLevel was changed through the setRootLoggerLevel method.
     */
    private boolean m_hasRootLevelChanged = false;

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
    public Log4jConfig(String name, ApplicationContextMB acMB,
        ApplicationContext ac, BeanFactory beanFactory, MBeanServer mBeanServer) {

        this.m_name = name;
        this.m_applicationContextMB = acMB;
        this.m_applicationContext = ac;
        this.m_mBeanServer = mBeanServer;
        this.m_beanFactory = beanFactory;
        this.m_class = this.getClass();

        StaticApplicationContext appContext = new StaticApplicationContext(
            m_applicationContext);

        // register the Log4jConfig bean with the spring appContext
        appContext.getBeanFactory().registerSingleton(m_name, this);

        // read-out the appenders list from the DeafultLog4jJmxLoader bean.
        setAppendersPool(((Log4jJmxLoader) appContext
            .getBean("DefaultLog4jJmxLoader")).getAppenders());
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws BaseException {
        // Set the object name of this object.
        setObjectName();

        // Register the Spring Bean at the MBean Server.
        registerSpringBean();
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
     * Getter method for the objectName member variable.
     * 
     * @return The objectName
     */
    public ObjectName getObjectName() {
        return m_objectName;
    }

    /**
     * Sets the objectName to of this object.
     */
    public void setObjectName() {

        String name = JVM_DOMAIN + ":name=" + getName();

        try {
            m_objectName = new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            CoreNotificationHelper
                .notifyMisconfiguration("The string passed as a parameter does not have"
                    + " the right format.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param appenders
     *            The appeenders list.
     */
    public void setAppendersPool(HashMap appenders) {
        this.m_appendersPool = appenders;
    }

    /**
     * {@inheritDoc}
     */
    public void addAppender(String category, String appenderName) {
        LogManager.getLogger(category).addAppender(
            (Appender) m_appendersPool.get(appenderName));
    }

    /**
     * {@inheritDoc}
     */
    public void removeAppender(String category, String appenderName) {
        LogManager.getLogger(category).removeAppender(
            (Appender) m_appendersPool.get(appenderName));
    }

    /**
     * {@inheritDoc}
     */
    public String[] getAvailableAppendersList() {
        String[] result = null;
        Set appKeySet = m_appendersPool.keySet();
        Iterator i = appKeySet.iterator();
        String appKey;
        LinkedList queue = new LinkedList();
        while (i.hasNext()) {
            appKey = (String) i.next();
            queue.add("appenderName=" + appKey + "; appenderObject="
                + m_appendersPool.get(appKey).toString());
        }

        Object[] array = queue.toArray();
        result = new String[array.length];
        for (int j = 0; j < array.length; j++) {
            result[j] = array[j].toString();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void changeLogLevel(String category, String level) {
        LogManager.getLogger(category).setLevel(Level.toLevel(level));
        m_loggingLevelCache.put(category, level);
    }

    /**
     * {@inheritDoc}
     */
    public Level showLogLevel(String category) {
        return LogManager.getLogger(category).getLevel();
    }

    /**
     * {@inheritDoc}
     */
    public Appender[] showAppenders(String category) {
        Appender[] result = null;
        Enumeration enumerator = LogManager.getLogger(category)
            .getAllAppenders();

        LinkedList queue = new LinkedList();

        while (enumerator.hasMoreElements()) {
            queue.add(enumerator.nextElement());
        }

        Object[] array = queue.toArray();
        if (array.length > 0) {
            result = new Appender[array.length];
            for (int j = 0; j < array.length; j++) {
                result[j] = (Appender) array[j];
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRootLoggerLevel(String level) {
        LogManager.getRootLogger().setLevel(Level.toLevel(level));
        m_hasRootLevelChanged = true;
    }

    /**
     * {@inheritDoc}
     */
    public String getRootLoggerLevel() {
        return LogManager.getRootLogger().getLevel().toString();
    }

    /**
     * {@inheritDoc}
     */
    public String showLogLevelCache() {
        String result = "";

        Iterator iter = m_loggingLevelCache.keySet().iterator();
        String category;
        while (iter.hasNext()) {
            category = (String) iter.next();
            result = result.concat(JmxHtmlFormatter.getXmlLog4jConfigString(
                category, (String) m_loggingLevelCache.get(category)));
        }

        if (m_hasRootLevelChanged) {
            result = result.concat(JmxHtmlFormatter
                .getXMLLog4jRootTag(getRootLoggerLevel()));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getInitialConfigurationPath() {
        String defaultConfigurationFile = "log4j.properties";
        String defaultXMLConfigurationFile = "log4j.xml";
        String defaultConfigurationKey = "log4j.configuration";
        String defaultInitOverrideKey = "log4j.defaultInitOverride";
        URL url = null;

        // Search for the properties file log4j.properties in the CLASSPATH.
        String override = OptionConverter.getSystemProperty(
            defaultInitOverrideKey, null);

        // if there is no default init override, then get the resource
        // specified by the user or the default config file.
        if (override == null || "false".equalsIgnoreCase(override)) {
            String configurationOptionStr = OptionConverter.getSystemProperty(
                defaultConfigurationKey, null);

            // if the user has not specified the log4j.configuration
            // property, we search first for the file "log4j.xml" and then
            // "log4j.properties"
            if (configurationOptionStr == null) {
                url = Loader.getResource(defaultXMLConfigurationFile);
                if (url == null) {
                    url = Loader.getResource(defaultConfigurationFile);
                }
            } else {
                try {
                    url = new URL(configurationOptionStr);
                } catch (MalformedURLException ex) {
                    // so, resource is not a URL:
                    // attempt to get the resource from the class path
                    url = Loader.getResource(configurationOptionStr);
                }
            }
        }
        
        if (url != null) {
            return url.getPath();
        } else {
            return null;
        }
    }
}
