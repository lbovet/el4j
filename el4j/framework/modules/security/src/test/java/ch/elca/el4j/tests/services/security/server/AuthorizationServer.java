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
package ch.elca.el4j.tests.services.security.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

// Checkstyle: UncommentedMain off

/**
 * The server part for <code>AuthorizationTestDistributed</code>.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Pfenninger (APR)
 */
public class AuthorizationServer {

	/**
	 * Logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(AuthorizationServer.class);

	/** The application context. */
	private static ConfigurableApplicationContext s_appContext;

	/**
		 * Hide constructor.
		 *
		 */
	protected AuthorizationServer() {
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            Command line parameters.
	 */
	public static void main(String[] args) {
		s_appContext = new ModuleApplicationContext(args, false);
		String[] str = s_appContext.getBeanDefinitionNames();
		for (int i = 0; i < str.length; i++) {
			s_logger.info(str[i]);
		}
	}
	
	/**
	 * Returns the Spring Application Context for this Authorization Server.
	 * 
	 * @return The ApplicationContext for this authorization.
	 */
	public static ConfigurableApplicationContext getApplicationContext() {
		
		return s_appContext;
	}
	
	/**
	 * Close the application context after the AuthorizationServer has been
	 * used.
	 */
	public static void close() {
		if (s_appContext != null) {
			s_appContext.close();
		}
	}
}
// Checkstyle: UncommentedMain on
