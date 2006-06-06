/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.security;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.services.security.authentication.AuthenticationService;
import ch.elca.el4j.tests.security.sample.SampleService;
import ch.elca.el4j.tests.security.server.AuthorizationServer;

import junit.framework.TestCase;

// Checkstyle: EmptyBlock off
// Checkstyle: MagicNumber off

/**
 * Tests various logins and authorization in a distributed environment. <br>
 * <ul>
 * <li>first runs AuthorizationServer (server part, no server case)</li>
 * <li>then runs AuthorizationTestDistributed as unit server</li>
 * </ul>
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class AuthorizationDistributedTest extends TestCase {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
        .getLog(AuthorizationDistributedTest.class);
    
    /**
     * Method access role.
     */
    private static final String METHOD_ACCESS_ROLE = "ROLE_PERMISSION_ADDONE";

    /**
     * Server config locations.
     */
    private String[] m_configLocationsServer = new String[] {
        "classpath:optional/security-attributes.xml",
        "classpath:services/sampleService.xml",
        "classpath:server/applicationContextTest.xml",
        "classpath:scenarios/securityscope/distributed-security"
            + "-scope-server.xml",
        "classpath:optional/rmi-protocol-config.xml",
        "classpath:services/serviceExporter.xml"};

    /**
     * Client config locations.
     */
    private String[] m_configLocationsClient = new String[] {
        "classpath:services/serviceProxy.xml",
        "classpath:scenarios/securityscope/"
            + "distributed-security-scope-client.xml",
        "classpath:optional/rmi-protocol-config.xml"};

    /**
     * Application context.
     */
    private ApplicationContext m_ac;

    /**
     * Test tries to execute the target method without authentication.
     * 
     * @throws Exception If something.
     */
    public void testMethodCallWithoutLogin() throws Exception {
        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        try {
            getSampleService().addOne(1234);
            fail("User should not be able to execute this method "
                    + "without login");
        } catch (AuthenticationCredentialsNotFoundException e) {
            // o.k.
        }
    }

    /**
     * Test does a correct authorization. Then it does a remote call to the
     * sample service.
     * 
     * @throws Exception If something.
     */
    public void testCorrectAuthorization() throws Exception {
        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        createSecureContext("server", "server", METHOD_ACCESS_ROLE);
        int result = getSampleService().addOne(1234);
        assertEquals(result, 1235);
    }
    
    /**
     * Test does a correct authorization. Then it does a remote call to the
     * sample service. Afterwards, it logs out, tries to call the method again
     * and fails.
     * 
     * @throws Exception If something.
     */
    public void testCorrectAuthorizationAfterLogoutNoAccess() throws Exception {
        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        createSecureContext("server", "server", METHOD_ACCESS_ROLE);
        int result = getSampleService().addOne(1234);
        assertEquals(result, 1235);

        destroySecureContext("server", "server");

        try {
            getSampleService().addOne(1234);
            fail("An AccessDeniedException should have been thrown.");
        } catch (AccessDeniedException e) {
            // ok.
        }
    }

    /**
     * Test does a correct login with the wrong role. Then it does a remote call
     * to the sample service. Since the required permission is not given, the
     * call should throw an exception.
     * 
     * @throws Exception If something.
     */
    public void testFailedAuthorization() throws Exception {
        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        createSecureContext("test4", "test4", "ROLE_NO_PERMISSION");

        try {
            getSampleService().addOne(1234);
            fail("An AccessDeniedException should have been thrown.");
        } catch (AccessDeniedException e) {
            // ok.
        }
    }

    /**
     * Test tries to authenticate with a wrong username/password combination. An
     * exception should be thrown.
     * 
     * @throws Exception If something.
     */
    public void testFailedAuthentication() throws Exception {
        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        try {
            createSecureContext("Different username", "than password", "");
            fail("User should not be able to authenticate since the password "
                    + "is not valid.");
        } catch (BadCredentialsException e) {
            // o.k.
        }
    }

    /**
     * @return Returns the authentication service.
     */
    private AuthenticationService getAuthenticationService() {
        return (AuthenticationService) m_ac.getBean("authenticationService");
    }

    /**
     * @return Returns the sample service.
     */
    private SampleService getSampleService() {
        return (SampleService) m_ac.getBean("sampleService");
    }

    /**
     * Create a secure context with a TestingAuthenticationToken, i.e. a token
     * where the user can define which roles it possesses.
     * 
     * @param principal
     *            The username
     * @param credential
     *            The password
     * @param role
     *            The role
     */
    private void createSecureContext(String principal, String credential,
        String role) {
        Authentication auth = new TestingAuthenticationToken(principal,
            credential, new GrantedAuthority[] {
                new GrantedAuthorityImpl("ROLE_TELLER"),
                new GrantedAuthorityImpl(role)});

        getAuthenticationService().authenticate(auth);
    }

    /**
     * Delete the secure context, i.e. logging out the user.
     * 
     * @param principal Is the principal.
     * @param credential is the credential.
     */
    private void destroySecureContext(String principal, String credential) {
        Authentication auth = new TestingAuthenticationToken(principal,
            credential, new GrantedAuthority[]{});
        
        getAuthenticationService().authenticate(auth);
    }
}
//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
