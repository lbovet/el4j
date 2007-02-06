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

import javax.management.ObjectName;

/**
 * The interface of the proxy class for all the beans loaded in the
 * ApplicationContext. <script type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/module/jmx/java/ch/elca/el4j/services/monitoring/jmx/SpringBeanMBMBean.java
 * $", "$Revision$", "$Date: 2006-03-13 14:15:43 +0100 (Mo, 13 Mrz 2006)
 * $", "$Author$" );</script>
 * 
 * @author Raphael Boog (RBO)
 * @author Rashid Waraich (RWA)
 */
public interface SpringBeanMBMBean {

    /**
     * Getter method for the name member variable.
     * 
     * @return Returns the name.
     */
    String getName();

    /**
     * Returns the configuration parameter as they have been set in the
     * configuration files loaded by the Application Context.
     * 
     * @return Returns the Configuration
     */
    String[] getConfiguration();

    /**
     * Returns the object name of the Application Context proxy.
     * 
     * @return Returns the applicationContextMB.
     */
    ObjectName getApplicationContextMB();

    /**
     * Returns the corresponding MBeans which are registered at the MBean Server
     * via MBeanExporter.
     * 
     * @return Returns the registered MBeans
     */
    ObjectName[] getRegisteredMBean();

    /**
     * Returns the class name of the referenced bean.
     * 
     * @return Returns the className.
     */
    String getClassName();

    /**
     * Returns whether the referenced bean is a Singleton or a Prototype.
     * 
     * @return Returns if the referenced bean is a singleton
     */
    boolean getIsSingleton();

    /**
     * Returns the interceptor class names of the referenced bean.
     * 
     * @return Returns the interceptor class names of the referenced bean.
     */
    String[] getInterceptors();

    /**
     * Returns whether the referenced bean is proxied or not.
     * 
     * @return Returns whether the referenced bean is proxied or not.
     */
    boolean getIsProxied();
}