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
import org.springframework.security.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;

/**
 * The ImplicitContextPasser for the AuthenticationService. It handles the
 * passing of the AuthenticationData and stores it in a ThreadLocal.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 * @author Andreas Pfenninger (APR)
 * @author Dominik Zindel (DZI)
 */
public class AuthenticationServiceContextPasser extends
	AbstractImplicitContextPasser {

	/*
	 * The retry interceptor requires that, for implicit context passing, an
	 * InheritableThreadLocal be used and not (as the acegi-default value) a
	 * ThreadLocal. To achieve this, we set the corresponding strategy.
	 */
	static {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}
	
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger = LoggerFactory
		.getLogger(AuthenticationServiceContextPasser.class);

	/**
	 * This method is called by the client that makes a remote invocation to
	 * collect the implicitly passed context and add it to the request. The
	 * authentication data is fetched from the SecurityContextHolder.
	 *
	 * @return The authentication data for the currently logged in user
	 * @see ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser
	 */
	public Object getImplicitlyPassedContext() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * This method is called by the server that receives a remote invocation
	 * to push the context to the service. The authentication data is stored in
	 * the SecurityContextHolder.
	 *
	 * @param context
	 *            The received implicit context for this passer.
	 * @throws ImplicitContextPassingRTException
	 * @see ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser
	 */
	public void pushImplicitlyPassedContext(Object context) {
		if (context == null) {
			s_logger.warn("Authentication == null");
			SecurityContextHolder.getContext().setAuthentication(null);
			return;
		}
		Authentication auth = (Authentication) context;
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
