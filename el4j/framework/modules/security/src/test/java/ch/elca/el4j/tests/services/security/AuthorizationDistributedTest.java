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
package ch.elca.el4j.tests.services.security;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.security.authentication.AuthenticationService;
import ch.elca.el4j.services.security.encryption.RSACipher;
import ch.elca.el4j.tests.services.security.provider.ExtendedTestingAuthenticationProvider;
import ch.elca.el4j.tests.services.security.sample.SampleService;
import ch.elca.el4j.tests.services.security.server.AuthorizationServer;

// Checkstyle: EmptyBlock off
// Checkstyle: MagicNumber off

/**
 * Tests various logins and authorization in a distributed environment. <br>
 * <ul>
 * <li>first runs AuthorizationServer (server part, no server case)</li>
 * <li>then runs AuthorizationTestDistributed as unit server</li>
 * </ul>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 */
public class AuthorizationDistributedTest {
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory
		.getLogger(AuthorizationDistributedTest.class);
	
	/**
	 * Method access role.
	 */
	private static final String METHOD_ACCESS_ROLE = "ROLE_PERMISSION_ADDONE";

	/**
	 * Server config locations.
	 */
	private String[] m_configLocationsServer = new String[] {
		"classpath*:mandatory/*.xml",
		"classpath:optional/security-attributes.xml",
		"classpath:scenarios/services/sampleService.xml",
		"classpath:scenarios/server/applicationContextTest.xml",
		"classpath:scenarios/securityscope/distributed-security-scope-server.xml",
		"classpath:optional/rmi-protocol-config.xml",
		"classpath:scenarios/services/serviceExporter.xml"};

	/**
	 * Client config locations.
	 */
	private String[] m_configLocationsClient = new String[] {
		"classpath*:mandatory/*.xml",
		"classpath:scenarios/services/serviceProxy.xml",
		"classpath:scenarios/securityscope/distributed-security-scope-client.xml",
		"classpath:optional/rmi-protocol-config.xml"};

	/**
	 * Application context.
	 */
	private ConfigurableApplicationContext m_ac;

	
	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() {
		s_logger.debug("Starting server.");
		AuthorizationServer.main(m_configLocationsServer);
		s_logger.debug("Server started. Loading client context.");

		m_ac = new ModuleApplicationContext(m_configLocationsClient, false);
		s_logger.debug("Client context loaded.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@After
	public void tearDown() {
		AuthorizationServer.close();
		m_ac.close();
	}
	
	/**
	 * Test tries to execute the target method without authentication.
	 *
	 * @throws Exception
	 *             If something.
	 */
	@Test
	public void testMethodCallWithoutLogin() throws Exception {
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
	@Test
	public void testCorrectAuthorization() throws Exception {
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
	@Test
	public void testCorrectAuthorizationAfterLogoutNoAccess() throws Exception {
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
	@Test
	public void testFailedAuthorization() throws Exception {
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
	@Test
	public void testFailedAuthentication() throws Exception {
		try {
			createSecureContext("Different username", "than password", "ROLE_TELLER");
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
	 * Returns the authentication provider of the authorization server.
	 * 
	 * @return The ExtendedTestingAuthenticationProvider of the server.
	 */
	private ExtendedTestingAuthenticationProvider getAuthenticationProvider() {
		
		return (ExtendedTestingAuthenticationProvider)
			AuthorizationServer.getApplicationContext().
			getBean("extendedTestingAuthenticationProvider");
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
		
		String publicKey = getAuthenticationProvider().getPublicKey();
		RSACipher rsaCipher = new RSACipher(publicKey);
		String encryptedCredential = rsaCipher.encrypt(credential);
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_TELLER"));
		authorities.add(new GrantedAuthorityImpl(role));
		
		Authentication auth = new TestingAuthenticationToken(principal,
			encryptedCredential, authorities);

		getAuthenticationService().authenticate(auth);
	}

	/**
	 * Delete the secure context, i.e. logging out the user.
	 *
	 * @param principal Is the principal.
	 * @param credential is the credential.
	 */
	private void destroySecureContext(String principal, String credential) {
		
		String publicKey = getAuthenticationProvider().getPublicKey();
		RSACipher rsaCipher = new RSACipher(publicKey);
		String encryptedCredential = rsaCipher.encrypt(credential);
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		Authentication auth = new TestingAuthenticationToken(principal,
			encryptedCredential, authorities);
		
		getAuthenticationService().authenticate(auth);
	}
}
//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
