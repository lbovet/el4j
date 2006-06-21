/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * The proxy class for the JVM. Each MBean Server contains exactly one JVM
 * MBean.
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
public class JvmMB implements JvmMBMBean {

    /**
     * The domain of the JVM proxy as it will be registered at the MBean Server.
     */
    public static final String JVM_DOMAIN = "JVM-monitor";

    /**
     * The counter on the number of JVMs.
     */
    private static int s_counter = 1;

    /**
     * A list of Application Context proxies which are running in this JVM
     * proxy.
     */
    protected List m_applicationContextMB = new ArrayList();

    /**
     * The object name of this object.
     */
    private ObjectName m_objectName;

    /**
     * The instance counter of this object.
     */
    private int m_instanceCounter;

    /**
     * Constructor which calls the setObjectName() method.
     *  
     */
    public JvmMB() {

        // Set the instanceCounter.
        setInstanceCounter();

        // Set the object name of this object.
        setObjectName();

    }

    /**
     * {@inheritDoc}
     */
    public String[] getSystemProperties() {
        Properties systemProperties = System.getProperties();

        Set keySet = systemProperties.keySet();
        String[] result = new String[keySet.size()];
        Iterator iter = keySet.iterator();
        int j = 0;

        while (iter.hasNext()) {
            Object key = iter.next();
            result[j] = (String) key + " = " + systemProperties.get(key);
            j++;
        }

        return result;

    }

    /**
     * {@inheritDoc}
     */
    public ObjectName[] getApplicationContexts() {

        ObjectName[] result = new ObjectName[m_applicationContextMB.size()];

        for (int i = 0; i < result.length; i++) {
            ApplicationContextMB acLocal 
                = (ApplicationContextMB) m_applicationContextMB.get(i);
            result[i] = acLocal.getObjectName();

        }
        return result;
    }

    /**
     * Add an Application Context proxy to the list of Application Context
     * proxies in this JVM proxy.
     * 
     * @param appCont
     *            The Application Context proxy to add
     */
    public void addApplicationContext(ApplicationContextMB appCont) {
        this.m_applicationContextMB.add(appCont);
    }

    /**
     * Sets the object name of this JVM proxy.
     *  
     */
    public void setObjectName() {

        String name = JVM_DOMAIN + ":name=root " + getInstanceCounter();

        try {
            m_objectName = new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The string passed as a parameter does not have"
                    + " the right format.", e);
        }
    }

    /**
     * The getter method for the object name of this JVM proxy.
     * 
     * @return The object name of this JVM proxy
     */
    public ObjectName getObjectName() {
        return m_objectName;
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
}