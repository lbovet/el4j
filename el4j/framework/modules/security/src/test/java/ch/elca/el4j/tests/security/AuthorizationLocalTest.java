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
import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.security.sample.SampleService;

import junit.framework.TestCase;

// Checkstyle: EmptyBlock off
// Checkstyle: MagicNumber off

/**
 * Tests various logins and authorization in a local environment.
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
public class AuthorizationLocalTest extends TestCase {
    
    /** The static logger. */
    private static Log s_logger = LogFactory
        .getLog(AuthorizationLocalTest.class);

    /**
     * Method access role.
     */
    private static final String METHOD_ACCESS_ROLE = "ROLE_PERMISSION_ADDONE";

    /**
     * Config locations.
     */
    private String[] m_configLocations = new String[] {
        "classpath:optional/security-attributes.xml",
        "classpath:scenarios/server/applicationContextTest.xml",
        "classpath:scenarios/services/sampleService.xml"};

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

        s_logger.debug("Loading Application Context.");
        m_ac = new ModuleApplicationContext(m_configLocations, false);
        s_logger.debug("Application Context loaded.");

        try {
            getSampleService().addOne(1234);
            fail("User should not be able to execute this method without "
                    + "login");
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

        s_logger.debug("Loading Application Context.");
        m_ac = new ModuleApplicationContext(m_configLocations, false);
        s_logger.debug("Application Context loaded.");

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

        s_logger.debug("Loading Application Context.");
        m_ac = new ModuleApplicationContext(m_configLocations, false);
        s_logger.debug("Application Context loaded.");

        createSecureContext("server", "server", METHOD_ACCESS_ROLE);
        int result = getSampleService().addOne(1234);
        assertEquals(result, 1235);

        destroySecureContext();

        try {
            getSampleService().addOne(1234);
            fail("An AccessDeniedException should have been thrown.");
        } catch (AuthenticationCredentialsNotFoundException e) {
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

        s_logger.debug("Loading Application Context.");
        m_ac = new ModuleApplicationContext(m_configLocations, false);
        s_logger.debug("Application Context loaded.");

        createSecureContext("server", "server", "WRONG_ROLE");

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

        s_logger.debug("Loading Application Context.");
        m_ac = new ModuleApplicationContext(m_configLocations, false);
        s_logger.debug("Application Context loaded.");

        createSecureContext("server", "wrong_credential", "SOME_ROLE");

        try {
            getSampleService().addOne(1234);
            fail("An AuthenticationException should have been thrown.");
        } catch (AuthenticationException e) {
            // o.k.
        }

    }

    /**
     * @return Returns the sample service.
     */
    private SampleService getSampleService() {
        return (SampleService) m_ac.getBean("sampleService");
    }

    /**
     * Create a secure context, i.e. login, with a TestingAuthenticationToken,
     * i.e. a token where the user can define which roles it possesses.
     * 
     * @param principal
     *            The username
     * @param credential
     *            The password
     * @param role
     *            The role
     */
    private static void createSecureContext(String principal,
        String credential, String role) {
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
            principal, credential, new GrantedAuthority[] {
                new GrantedAuthorityImpl("ROLE_TELLER"),
                new GrantedAuthorityImpl(role)});
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
    }

    /**
     * Delete the secure context, i.e. logging out the user.
     */
    private static void destroySecureContext() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }
}
//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
