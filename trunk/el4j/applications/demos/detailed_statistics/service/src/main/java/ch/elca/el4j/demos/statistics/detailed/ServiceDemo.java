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
package ch.elca.el4j.demos.statistics.detailed;

import ch.elca.el4j.core.context.ModuleApplicationContext;

//Checkstyle: UncommentedMain off
//Checkstyle: UseLogger off
/**
 * This class is the service class for the detailed statistics demo.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Fabian Reichlin (FRE)
 * @author David Stefan (DST)
 */
public final class ServiceDemo {

	/**
	 * Locations of configuration files.
	 */
	private static final String[] CONFIG_LOCATIONS = {
		"scenarios/demo-rmi-server-config.xml",
		"scenarios/demo-rmi-protocol-config.xml",
		"scenarios/common-detailedStatistics.xml",
		"scenarios/server-detailedStatistics.xml",
		"classpath*:mandatory/*.xml"};
	
	/**
	 * Private dummy constructor.
	 */
	private ServiceDemo() {
		
	}
	
	/**
	 * Prepares the application context such that a client can access the
	 * service's methods through RMI.
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {

		ModuleApplicationContext context = new ModuleApplicationContext(CONFIG_LOCATIONS, true);
		
		System.out.println();
		System.out.println("JMX console can be started in browser with "
			+ "URL http://localhost:9092");
		System.out.println();
		System.out.println("Waiting on client...");
		
		context.registerShutdownHook();
	}
}
