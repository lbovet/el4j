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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Entry point for the JMX package. If this bean is defined in an Application
 * Context, then it will set up the whole JMX world by creating the proxies and
 * setting the corresponding references.
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
public class Loader implements ApplicationContextAware, InitializingBean {

    /**
     * A map containing the MBean Server as key and the JvmMB as value.
     */
    protected static Map s_jvmMBs = new HashMap();

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(Loader.class);

    /**
     * The Application Context proxy for the Application Context containing this
     * loader.
     */
    protected ApplicationContextMB m_acMB;

    /**
     * The JVM proxy containing this Application Context.
     */
    protected JvmMB m_jvmmb;

    /**
     * The MBean Server.
     */
    private MBeanServer m_server;

    /**
     * The Application Context having instantiated this loader.
     */
    private ApplicationContext m_applicationContext;

    /**
     * Returns the MBean Server of this ApplicationContext.
     * 
     * @return Returns the MBean Server.
     */
    public MBeanServer getServer() {
        return m_server;
    }

    /**
     * Sets the MBean Server of this ApplicationContext.
     * 
     * @param server
     *            The MBean Server to set.
     */
    public void setServer(MBeanServer server) {
        this.m_server = server;
    }

    /**
     * Takes the JvmMB Bean from the MBean Server or creates a new JvmMB if no
     * such bean is registered at the MBean Server. The JvmMBs have to be stored
     * in a static map for later Application Contexts referring to the same
     * MBeanServer.
     * 
     * @throws BaseException
     *             in case the registration at the MBean Server failed
     */
    protected void setJvmMB() throws BaseException {

        // Since we access the static member s_jvmMBs, this block is
        // synchronized.
        synchronized (getClass()) {

            // Get all the object names of this MBean Server in the JVM_DOMAIN.
            ObjectName[] jvms = getObjectNames(this.m_server, JvmMB.JVM_DOMAIN,
                null, null);

            // In case there is no MBean in the JVM_DOMAIN, we create one and
            // register it at the MBean Server.
            if (jvms.length == 0) {
                m_jvmmb = new JvmMB();
                s_jvmMBs.put(m_server, m_jvmmb);
                try {
                    m_server.registerMBean(m_jvmmb, m_jvmmb.getObjectName());
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
            } else if (jvms.length == 1) {
                // In case there is one MBean in the JVM_DOMAIN, we make
                // reference to it in this loader.
                m_jvmmb = (JvmMB) s_jvmMBs.get(m_server);
                if (m_jvmmb == null) {
                    CoreNotificationHelper.notifyMisconfiguration(
                            "Error in Mapping: No JVM defined on this"
                            + " MBean Server.");
                }
            } else {
                // In case there is more than one JVM proxy registered at this
                // MBean Server, we throw a BaseRTException.
                String message = "There is more than 1 JVM defined on this"
                    + " MBean Server.";
                s_logger.error(message);
                throw new BaseRTException(message, (Object[]) null);
            }
        }
    }

    /**
     * Returns all object names of the beans in the domain "domain" and where
     * "keyProperty"="name" which are registered on the MBeanServer server.
     * 
     * @param server
     *            the MBean Server
     * @param domain
     *            the domain of the wanted beans
     * @param keyProperty
     *            the key property (e.g. name) as registered on the MBean Server
     * @param value
     *            the value of the key property
     * @return an array of object names satisfying the constraints
     */
    public ObjectName[] getObjectNames(MBeanServer server, String domain,
        String keyProperty, String value) {

        // Get all MBeans at this MBean Server.
        Set mBeansSet = server.queryMBeans(null, null);

        Reject.ifNull(mBeansSet, "The 'queryMBeans(ObjectName, QueryExp)' "
                + "method on the MBeanServer returned null.");

        ArrayList relatedBeans = new ArrayList();

        Iterator it = mBeansSet.iterator();
        while (it.hasNext()) {
            ObjectInstance objectInstance = (ObjectInstance) it.next();
            ObjectName objectName = objectInstance.getObjectName();

            // Check whether this object name contains the requested domain and
            // the requested value of the requested key property.
            if (objectName.getDomain().equals(domain)) {
                if (keyProperty != null) {
                    if (objectName.getKeyProperty(keyProperty).equals(value)) {
                        relatedBeans.add(objectName);
                    }
                } else {
                    relatedBeans.add(objectName);
                }
            }
        }

        return (ObjectName[]) relatedBeans.toArray(new ObjectName[relatedBeans
            .size()]);
    }

    /**
     * The ApplicationContext is set via ApplicationContextAware.
     * 
     * @param applicationContext
     *            The Application Context delivered by the
     *            ApplicationContextAware
     */
    public void setApplicationContext(ApplicationContext applicationContext) {

        this.m_applicationContext = applicationContext;

    }

    /**
     * Getter method for the Application Context.
     * 
     * @return The Application Context containing this loader object
     */
    public ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }

    /**
     * Creates the "Real World" image of the given Application Context by
     * setting up the corresponding JMX objects.
     * 
     * @throws BaseException
     *             Rethrows the BaseException thrown by setJvmMB() and
     *             m_acMB.init()
     */
    public void afterPropertiesSet() throws BaseException {

        // Set the JVM MBean to this Application Context.
        setJvmMB();

        // Create the Application Context proxy.
        m_acMB = new ApplicationContextMB(m_applicationContext,
            ((AbstractApplicationContext) m_applicationContext)
                .getBeanFactory(), m_server, m_jvmmb);

        // Initialize the Application Context proxy.
        m_acMB.init();

    }
}