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
package ch.elca.el4j.tests.services.remoting.loadbalancing.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Defines a dummy business object whose sole purpose is the illustration of the
 * idempotent invocation functionality.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Pleisch (SPL)
 */
public class BusinessObjectImpl implements BusinessObject, ApplicationContextAware {

	/**
	 * {@inheritDoc}
	*/
	public String call(String toto) {
		s_logger.debug("Method call called with arg=" + toto);
		if (toto.equals(BusinessObject.COMMIT_SUICIDE+m_serverName)) {
			s_logger.debug(m_serverName + " is committing suicide.") ;
			System.exit(0);
		} // if
		return m_serverName ;
	} // call()

 
	public void setServerName(String serverName) {
		m_serverName = serverName ;
	} // setServerName()
	
	// ////////////////// From ApplicationContextAware //////////////////
	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		m_applicationContext = applicationContext;
	} // setApplicationContext()

	
	private String m_serverName ;
	private ApplicationContext m_applicationContext ;

	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory
		.getLogger(BusinessObjectImpl.class);

	
}
