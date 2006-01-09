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

import javax.management.ObjectName;

/**
 * The interface of the proxy class for all the beans loaded in the
 * ApplicationContext.
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
}