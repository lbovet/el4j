/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.services.remoting.loadbalancing.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.tests.services.remoting.loadbalancing.common.BusinessObject;
import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Defines the server used to test the idempotent invocation module. Launches a
 * business object whose unique purpose is to illustrate the behavior of the
 * module. <script type="text/javascript">printFileStatus ("$URL$",
 * "$Revision$", "$Date$", "$Author$" );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public class LbTestServer2 {

	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(LbTestServer2.class);

	/** {@inheritDoc} */
	public static void main(String args[]) {

		ApplicationContext applicationContext = new ModuleApplicationContext(
			new String[] {"classpath*:mandatory/*.xml",
				"classpath:loadbalancing/server/startup2.xml",
				"classpath:loadbalancing/remoting/multiplermi-protocol-config.xml"},
			(String[]) null,
			false,
			(ApplicationContext) null);

		s_logger.debug("Starting up ....");

		BusinessObject obj = (BusinessObject)applicationContext.getBean("rmiTestObjImpl2");

		int iterations = 1;
		while (iterations < LbServerConstants.NBR_ITERATIONS) {

			try {
				// 100s
				Thread.sleep(LbServerConstants.SLEEPING_TIME);
			} catch (Exception e) {
				System.err.println("Problem:");
				e.printStackTrace();
				System.exit(-1);
			}

			iterations += 1;
			s_logger.debug("Looping around, iteration: " + iterations);

		} // while

		s_logger.debug("Done.");
		System.exit(0);
	} // main()
}
