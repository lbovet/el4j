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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.runner;

import org.apache.log4j.Logger;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Main class to start server for tests during the maven build. 
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
public final class TestStarter {
	
	/** The logger. */
	private static Logger s_log = Logger.getLogger(TestStarter.class);
	
	/**
	 * The daemon thread. It keeps the server alive until shutdown() is called.
	 */
	private static DaemonThread s_daemon;

	/**
	 * The application context.
	 */
	private static ModuleApplicationContext s_ctx;
	
	/**
	 * Do not use.
	 */
	private TestStarter() {
		
	}
	
	/**
	 * Main method.
	 * @param args The following arguments:
	 * argv[0] = ${el4j.project.tools} The path to the tools folder (for derby).
	 * argv[1] = db port
	 */
	// Checkstyle: UncommentedMain off
	public static void main(String[] args) {
	// Checkstyle: UncommentedMain on
		
		// Create the main context.
		String[] config = new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml",
			"classpath:Hibernate.xml",
			"classpath:common.xml",
			"classpath:remote.xml",
			"classpath:testserver.xml"
		};

		s_ctx = new ModuleApplicationContext(config, true);
		
		// Start up a daemon thread.
		s_daemon = new DaemonThread();
		s_daemon.start();
	}
	
	/**
	 * Shutdown the test server. (Joining a daemon terminates the last non-daemon thread.)
	 */
	public static void shutdown() {
		s_log.info("Stopping test server.");
		try {
			s_ctx.close();
			s_daemon.end();
			s_daemon.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			// Server failed to stop.
			throw new RuntimeException("Failed to stop test server.", e);
		}
	}
	
	/**
	 * The daemon thread.
	 */
	static class DaemonThread extends Thread {

		/**
		 * Flag to terminate the loop.
		 */
		private volatile boolean m_run = true;
		
		/**
		 * Terminate by setting the flag.
		 */
		public void end() {
			m_run = false;
		}
		
		/**
		 * Mark this as a daemon thread.
		 */
		{
			setDaemon(true);
		}
		
		/** Keep the application alive. */
		@Override public void run() {
			s_log.info("Test server running.");
			while (m_run) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					s_log.warn("Interrupt");
				}
			}
		}
	}
}
