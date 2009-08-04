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
//Checkstyle: MagicNumber off
/**
 * This class is the client class for the detailed statistics demo.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 * @author David Stefan (DST)
 */
public final class ClientDemo {

	/**
	 * Locations of configuration files.
	 */
	private static final String[] CONFIG_LOCATIONS = {
		"scenarios/demo-client-config.xml",
		"scenarios/demo-rmi-protocol-config.xml",
		"scenarios/client-detailedStatistics.xml",
		"scenarios/common-detailedStatistics.xml",
		"classpath*:mandatory/*.xml"};

	/**
	 * Name of interface and proxy bean resp.
	 */
	private static final String SERVICENAME = "printer";

	/**
	 * Private dummy constructor.
	 */
	private ClientDemo() {
		
	}
	
	/**
	 * Prepares the application context and calls the service's method
	 * through RMI.
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {

		ModuleApplicationContext appContext = new ModuleApplicationContext(
			CONFIG_LOCATIONS, true);
		DemoA printer = (DemoA) appContext.getBean(SERVICENAME);
		printer.computeA(10);
		
		appContext.close();
	}
}
