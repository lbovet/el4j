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
package ch.elca.el4j.tests.security;

import junit.framework.TestCase;
import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.AuthenticationCredentialsNotFoundException;
import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.context.Context;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContext;
import net.sf.acegisecurity.context.security.SecureContextImpl;
import net.sf.acegisecurity.providers.TestingAuthenticationToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.tests.security.sample.SampleService;

/**
 * Tests various logins and authorization in a local environment.
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
public class AuthorizationLocalTest extends TestCase {
    
    /** The static logger. */
    private static Log s_logger = LogFactory
        .getLog(AuthorizationLocalTest.class);

    private static String METHOD_ACCESS_ROLE = "ROLE_PERMISSION_ADDONE";

    private String[] m_applicationContext = new String[] {
        "classpath:optional/security-attributes.xml",
        "classpath:server/applicationContextTest.xml",
        "classpath:services/sampleService.xml"};

    private ApplicationContext m_ac;

    /**
     * Test tries to execute the target method without authentication.
     * 
     * @throws Exception
     */
    public void testMethodCallWithoutLogin() throws Exception {

        s_logger.debug("Loading Application Context.");
        m_ac = new ClassPathXmlApplicationContext(m_applicationContext);
        s_logger.debug("Application Context loaded.");

        try {
            int result = getSampleService().addOne(1234);
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
     * @throws Exception
     */
    public void testCorrectAuthorization() throws Exception {

        s_logger.debug("Loading Application Context.");
        m_ac = new ClassPathXmlApplicationContext(m_applicationContext);
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
     * @throws Exception
     */
    public void testCorrectAuthorizationAfterLogoutNoAccess() throws Exception {

        s_logger.debug("Loading Application Context.");
        m_ac = new ClassPathXmlApplicationContext(m_applicationContext);
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
     * @throws Exception
     */
    public void testFailedAuthorization() throws Exception {

        s_logger.debug("Loading Application Context.");
        m_ac = new ClassPathXmlApplicationContext(m_applicationContext);
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
     * @throws Exception
     */
    public void testFailedAuthentication() throws Exception {

        s_logger.debug("Loading Application Context.");
        m_ac = new ClassPathXmlApplicationContext(m_applicationContext);
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
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Additional methods
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    private AuthenticationManager getAuthenticationManager() {
        return (AuthenticationManager) m_ac.getBean("authenticationManager");
    }

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

        Context cxt = ContextHolder.getContext();
        if (cxt == null) {
            cxt = new SecureContextImpl();
            ContextHolder.setContext(cxt);
        }
        SecureContext sc;
        if (cxt instanceof SecureContext) {
            sc = (SecureContext) cxt;
            sc.setAuthentication(auth);
        } else {
            throw new BaseRTException(
                "The context is not of type 'SecureContext'.", (Object[]) null);
        }
    }

    /**
     * Delete the secure context, i.e. logging out the user.
     */
    private static void destroySecureContext() {
        ContextHolder.setContext(new SecureContextImpl());
    }

}