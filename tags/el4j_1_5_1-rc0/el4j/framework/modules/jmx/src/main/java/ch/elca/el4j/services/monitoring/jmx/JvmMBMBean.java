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
 * The interface of the proxy class for the JVM.
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
public interface JvmMBMBean {

	/**
	 * Returns the system properties of this JVM in a String array.
	 *
	 * @return The system properties of this JVM
	 */
	String[] getSystemProperties();

	/**
	 * Returns all the Application Context proxies in this JVM.
	 *
	 * @return The Application Context proxies in this JVM.
	 */
	ObjectName[] getApplicationContexts();
	
	/**
	 * Get the classpath of this jvm. If the Jvm MBean is URL-loaded,
	 * converts all urls to strings; otherwise returns the system classpath.
	 *
	 * @return The classpath.
	 */
	String[] getClassPath();
	
	/**
	 * Searches for duplicated classes on the classpath.
	 *
	 * @return A report of any duplicated classes found.
	 */
	String findDuplicateClasses();
	
	/**
	 * An HTML table is created, which lists all Threads and
	 * their property values.
	 * @return A table with all threads
	 */
	public String showThreadTable();
}
