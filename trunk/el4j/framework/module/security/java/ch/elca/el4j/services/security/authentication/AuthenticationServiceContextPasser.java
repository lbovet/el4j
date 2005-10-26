/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.services.security.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContext;
import net.sf.acegisecurity.context.security.SecureContextImpl;

/**
 * The ImplicitContextPasser for the AuthenticationService. It handles the
 * passing of the AuthenticationData and stores it in a ThreadLocal.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
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

    /** The authentication service where the autentication data is stored. */
    private DefaultAuthenticationService m_authenticationService;

    /**
     * Sets the authentication service to get the authentication data.
     * 
     * @param authenticationService
     *            The authentication service to get the authentication data.
     */
    public void setAuthenticationService(
        DefaultAuthenticationService authenticationService) {
        m_authenticationService = authenticationService;
    }

    /**
     * Get the secure context.
     * 
     * @return the secure context
     */
    public SecureContext getSecureContext() {
        if (ContextHolder.getContext() == null) {
            ContextHolder.setContext(new SecureContextImpl());
        }
        return (SecureContext) ContextHolder.getContext();
    }

    /**
     * {@inheritDoc}
     */
    public Authentication getAuthenticationData() {
        Authentication ad = (Authentication) getSecureContext()
            .getAuthentication();
        return ad;
    }

    /**
     * {@inheritDoc}
     */
    public void setAuthenticationData(Authentication authenticationData) {
        getSecureContext().setAuthentication(authenticationData);
    }

    /**
     * {@inheritDoc}
     */
    public void removeAuthenticationData() {
        getSecureContext().setAuthentication(null);
    }

    /**
     * This method is called by the stub that makes a remote invocation to
     * collect the implicitly passed context and add it to the request. The
     * authentication data is fetched from the authentication service.
     * 
     * @return The authentication data for the currently logged in user
     * @see ch.elca.springframework.contextpassing.AbstractImplicitContextPasser
     */
    public Object getImplicitlyPassedContext() {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_authenticationService, "authenticationService", this);
        Authentication ad = m_authenticationService.getAuthenticationData();
        return ad;
    }

    /**
     * This method is called by the skeleton that receives a remote invocation
     * to push the context to the service. The authentication data is stored in
     * a ThreadLocal variable.
     * 
     * @param context
     *            The received implicit context for this passer.
     * @throws ImplicitContextPassingRTException
     * @see ch.elca.springframework.contextpassing.AbstractImplicitContextPasser
     */
    public void pushImplicitlyPassedContext(Object context) {
        if (context == null) {
            return;
        }
        Authentication ad = (Authentication) context;
        if (ad == null) {
            s_logger.warn("ad == null");
            return;
        }

        getSecureContext().setAuthentication(ad);
    }

}