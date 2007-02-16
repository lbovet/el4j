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
 * @author Rashid Waraich (RWA)
 */
public class JvmMB implements JvmMBMBean {

    /**
     * The domain of the JVM proxy as it will be registered at the MBean Server.
     */
    public static final String JVM_DOMAIN = "JVM";

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
    @SuppressWarnings("unchecked")
    public void addApplicationContext(ApplicationContextMB appCont) {
        this.m_applicationContextMB.add(appCont);
    }

    /**
     * Sets the object name of this JVM proxy.
     *  
     */
    public void setObjectName() {

        String name 
            = JVM_DOMAIN + ":name=jvmRootMonitor " + getInstanceCounter();

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
    
    /**
     * {@inheritDoc}
     */
    public String showThreadTable() {
        // Checkstyle: MagicNumber off
        Thread[] threads = getAllThreads();
        String[][] cells = new String[threads.length + 1][7];
        
        cells[0][0] = "Thread Id";
        cells[0][1] = "Name";
        cells[0][2] = "isDeamon";
        cells[0][3] = "State";
        cells[0][4] = "Thread Group";
        cells[0][5] = "Priority";
        cells[0][6] = "Stack Trace";
        
        for (int i = 0; i < threads.length; i++) {
            cells[i + 1][0] = Long.toString(threads[i].getId());
            cells[i + 1][1] = threads[i].getName();
            cells[i + 1][2] = Boolean.toString(threads[i].isDaemon());
            cells[i + 1][3] = threads[i].getState().toString();
            cells[i + 1][4] = threads[i].getThreadGroup().getName();
            cells[i + 1][5] = Integer.toString(threads[i].getPriority());
            cells[i + 1][6] 
                = stackTraceElementsToString(threads[i].getStackTrace());
        }
        // Checkstyle: MagicNumber on
        return JmxHtmlFormatter.getHtmlTable(cells);
    }
    
    
    /**
     * Converts an array of stacktrace elements to a string.
     * @param trace The stacktrace array to convert 
     * @return The resulting string
     */
    private String stackTraceElementsToString(StackTraceElement[] trace) {
        String result = "";
        
        for (int i = 0; i < trace.length; i++) {
            result = result.concat(trace[i].toString());
        }
        
        return result;
    }
    
    
    /**
     * @return An array of all current threads.
     */
    private Thread[] getAllThreads() {
        return Thread.getAllStackTraces().keySet().toArray(new Thread[1]);
    }
    
    
    
    
}