/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.security;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import ch.elca.el4j.services.security.encryption.AESCipher;
import ch.elca.el4j.services.security.encryption.RSACipher;


/**
 * 
 * This class tests the encryption and decryption using RSA.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Dominik Zindel (DZI)
 */
public class RSAEncryptionTest {

	/**
	 * The cipher used for en- and decryption.
	 */
	private RSACipher m_cipher;
	
	/**
	 * The generated key.
	 */
	private String m_key;
	
	/**
	 * The text that will be en- and decrypted.
	 */
	private String m_text = "myTopSecretPassword";
	
	/**
	 * Generates a cipher with a key.
	 */
	@Before
	public void setUp() throws Exception {
		m_cipher = new RSACipher(512);
	}
	
	/**
	 * Encrypts and then decrypts a text and checks if the result of the decryption
	 * equals the input of the encryption (= the original text).
	 */
	@Test
	public void testEnDecryption() {
		String encrypted = m_cipher.encrypt(m_text);
		String decrypted = m_cipher.decrypt(encrypted);
		assertEquals(m_text, decrypted);
	}
	
	/**
	 * Encrypts a text, modifies the encrypted text, decrypts it and
	 * checks if the result is really different from the original input.
	 */
	@Test
	public void testDiffEnDecryption() {
		String encrypted = (m_cipher.encrypt(m_text)).substring(1) + "9";
		String decrypted = m_cipher.decrypt(encrypted);
		assertFalse(m_text.equals(decrypted));
	}
	
}
