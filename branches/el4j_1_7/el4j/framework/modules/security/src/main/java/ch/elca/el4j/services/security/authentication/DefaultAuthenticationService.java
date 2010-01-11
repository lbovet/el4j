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
package ch.elca.el4j.services.security.authentication;


import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Default implementation of <code>AuthenticationService</code> used for
 * logging in and out. <br>
 * <br>
 * Simple usage example with a given BeanFactory: <br>
 * <br>
 * AuthenticationService as = (AuthenticationService)
 * beanFactory.getBean("authenticationService"); <br>
 * as.login(loginContext, callbackHandler); <br>
 * <br>
 * Both parameters are optional, if they are included in the applicationContext
 * configuration. The bean may contain two properties :
 * <ul>
 * <li>defaultCallbackHandler, which is the default callback handler to be used
 * for the callbacks of the PAM.
 * <li>defaultLoginContext, which defines the default login context to be used
 * for the login.
 * </ul>
 * <br>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 * @author Andreas Pfenninger (APR)
 * @author Christoph Schwitter (CSC)
 */
public class DefaultAuthenticationService implements AuthenticationService,
	InitializingBean {
	

	/** The LoginService to be used for the login. */
	private AuthenticationManager m_authenticationManager;

	/**
	 * Return the authentication data that is stored for this thread. Used by
	 * the AuthenticationServiceContextPasser.
	 *
	 * @return The authentication data, may be null.
	 */
	public Authentication getAuthenticationData() {

		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Convenience method to set the authentication data.
	 *
	 * @param authenticationData
	 *            The authentication data to be stored in the ThreadLocal.
	 */
	private void setAuthenticationData(Authentication authenticationData) {
		SecurityContextHolder.getContext().setAuthentication(authenticationData);
	}

	/**
	 * Sets the authenticationManager to be used for the authentication.
	 *
	 * @param am
	 *            The AuthenticationManager to be used for the authentication.
	 */
	public void setAuthenticationManager(AuthenticationManager am) {
		m_authenticationManager = am;
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception {
		CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
				m_authenticationManager, "authenticationManager", this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void authenticate(Authentication auth) {
		Authentication authResult = m_authenticationManager.authenticate(auth);
		setAuthenticationData(authResult);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserName() {
		if (getAuthenticationData() != null) {
			Object obj = getAuthenticationData().getPrincipal();
			if (obj instanceof String) {
				return (String) obj;
			}
		}
		return null;
	}
}
