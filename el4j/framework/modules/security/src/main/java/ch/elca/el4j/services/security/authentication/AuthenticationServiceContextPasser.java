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

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * The ImplicitContextPasser for the AuthenticationService. It handles the
 * passing of the AuthenticationData and stores it in a ThreadLocal.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Raphael Boog (RBO) 
 * @author Andreas Pfenninger (APR)
 */
public class AuthenticationServiceContextPasser extends
    AbstractImplicitContextPasser {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(AuthenticationServiceContextPasser.class);

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
            return;
        }
        Authentication ad = (Authentication) context;
        SecurityContextHolder.getContext().setAuthentication(ad);
    }

}
