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
package ch.elca.el4j.services.persistence.hibernate.offlining.test.runner;

import org.apache.log4j.Logger;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Main class to stop test server after tests have run.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public final class TestStopper {

	/** The logger. */
	private static final Logger s_log = Logger.getLogger(TestStopper.class);
	
	/**
	 * Don't use.
	 */
	private TestStopper() {
		
	}
	
	/**
	 * @param args unused.
	 */
	//Checkstyle: UncommentedMain off
	public static void main(String[] args) {
	//Checkstyle: UncommentedMain on
		String[] config = new String[] {
			"classpath*:mandatory/*.xml",
			"classpath:testclient.xml"
		};
		
		ModuleApplicationContext ctx = new ModuleApplicationContext(config, true);
		TestController controller = (TestController) ctx.getBean("testController");
		s_log.info("Stopping test server.");
		controller.shutdown();
		ctx.close();
		// System.exit(0);
	}

}
