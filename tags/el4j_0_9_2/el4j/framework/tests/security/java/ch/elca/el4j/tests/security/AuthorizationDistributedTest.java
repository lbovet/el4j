/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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
package ch.elca.el4j.tests.security;

import junit.framework.TestCase;
import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationCredentialsNotFoundException;
import net.sf.acegisecurity.BadCredentialsException;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.providers.TestingAuthenticationToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.services.security.authentication.AuthenticationService;
import ch.elca.el4j.tests.security.sample.SampleService;
import ch.elca.el4j.tests.security.server.AuthorizationServer;

/**
 * Tests various logins and authorization in a distributed environment. <br>
 * <ul>
 * <li>first runs AuthorizationServer (server part, no server case)</li>
 * <li>then runs AuthorizationTestDistributed as unit server</li>
 * </ul>
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class AuthorizationDistributedTest extends TestCase {
    private static Log s_logger = LogFactory
        .getLog(AuthorizationDistributedTest.class);

    private static String METHOD_ACCESS_ROLE = "ROLE_PERMISSION_ADDONE";

    private String[] m_configLocationsServer = new String[] {
        "classpath:optional/security-attributes.xml",
        "classpath:services/sampleService.xml",
        "classpath:server/applicationContextTest.xml",
        "classpath:scenarios/securityscope/distributed-security"
            + "-scope-server.xml",
        "classpath:optional/rmi-protocol-config.xml",
        "classpath:services/serviceExporter.xml"};

    private String[] m_configLocationsClient = new String[] {
        "classpath:services/serviceProxy.xml",
        "classpath:scenarios/securityscope/"
            + "distributed-security-scope-client.xml",
        "classpath:optional/rmi-protocol-config.xml"};

    private ApplicationContext m_ac;

    /**
     * Test tries to execute the target method without authentication.
     * 
     * @throws Exception
     */
    public void testMethodCallWithoutLogin() throws Exception {

        s_logger.debug("Starting server.");
        AuthorizationServer.main(m_configLocationsServer);
        s_logger.debug("Server started. Loading client context.");

        m_ac = new ClassPathXmlApplicationContext(m_configLocationsClient);
        s_logger.debug("Client context loaded.");

        try {
            int result = getSampleService().addOne(1234);
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
     * @throws Exception
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
     * @throws Exception
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
     * @throws Exception
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
     * @throws Exception
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
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Additional methods
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    private AuthenticationService getAuthenticationService() {
        return (AuthenticationService) m_ac.getBean("authenticationService");
    }

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
     */
    private void destroySecureContext(String principal, String credential) {
        Authentication auth = new TestingAuthenticationToken(principal,
            credential, new GrantedAuthority[]{});
        
        getAuthenticationService().authenticate(auth);
    }

}