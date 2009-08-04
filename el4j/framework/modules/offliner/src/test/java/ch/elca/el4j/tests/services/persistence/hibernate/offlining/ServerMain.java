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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import java.io.IOException;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Main class for offlining server.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public final class ServerMain implements Killable {

	/** Default constructor. Used so we can export the kill-hook. */
	public ServerMain() { }
	
	/**
	 * Shut down the server after all tests are complete.
	 */
	public void kill() {
		System.err.println("Server killed.");
		System.exit(0);
	}
	
	/**
	 * Start the server.
	 * @param args Unused.
	 */
	// Checkstyle: UncommentedMain off
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ApplicationContext ctx;
		
		String[] remoteConfig = new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml",
			"classpath:Hibernate.xml",
			"classpath:common.xml",
			"classpath:remote.xml"
		};
		
		ctx = new ModuleApplicationContext(remoteConfig, true);
		
		System.err.println("Server running.");
		// Wait for user to end.
		while (true) {
			try {
				int i = System.in.read();
				if (i == 'x') {
					System.err.println("Server shutdown.");
					System.exit(0);
				}
			} catch (IOException e) {
				System.err.println("IO exception.");
				System.exit(1);
			}
		}
	}
	// CheckStyle: UncommentedMain off
}
