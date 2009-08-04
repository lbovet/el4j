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

package ch.elca.el4j.tests.services.security.provider;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;

import ch.elca.el4j.services.security.encryption.RSACipher;

/**
 * Provider for testing reasons. This class throws a BadCredentialsException in
 * case the username is not equal to the password.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 */
public class ExtendedTestingAuthenticationProvider extends
	TestingAuthenticationProvider {

	/**	 */
	private RSACipher m_cipher;
	
	/**	Length of the RSA key pair. */
	private static final int m_keyLength = 256;
	
	/**
	 * Default constructor in which the cipher will be initialized.
	 */
	public ExtendedTestingAuthenticationProvider() {
		
		m_cipher = new RSACipher(m_keyLength);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException {

		if (authentication == null) {
			return null;
		}
		
		String encryptedCredential = authentication.getCredentials().toString();
		String decryptedCredential = m_cipher.decrypt(encryptedCredential);
		
		if (authentication.getPrincipal().toString().equals(
			decryptedCredential)) {
			return authentication;
		} else {
			throw new BadCredentialsException(
				"Authentication Failed with Principal "
					+ authentication.getPrincipal().toString()
					+ " and Credential "
					+ authentication.getCredentials().toString() + ".");
		}
	}
	
	/**
	 * Obtain the public key to encrypt the password to avoid passing it in
	 * clear over the network.
	 * 
	 * @return The public key suitable to encrypt the password with
	 */
	public String getPublicKey() {
		
		return m_cipher.getPublicKey();
	}
}