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
package ch.elca.el4j.services.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Base64;


/**
 * This class can be used to en/decrypt Strings using the AES algorithm.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class AESCipher {
	/**
	 * The prefix of encrypted messages.
	 */
	private static final String PREFIX = "{AES-128}";
	
	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(AESCipher.class);
	
	/**
	 * Cipher used for encryption.
	 */
	private Cipher m_cipherEncypt;
	
	/**
	 * Cipher used for decryption.
	 */
	private Cipher m_cipherDecypt;
	
	/**
	 * @param base64encodedKey    a 128 bit AES key (base64 encoded) to use for en/decryption
	 */
	public AESCipher(String base64encodedKey) {
		try {
			byte[] key = Base64.decodeBase64(base64encodedKey.getBytes());
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			
			m_cipherEncypt = Cipher.getInstance("AES");
			m_cipherEncypt.init(Cipher.ENCRYPT_MODE, skeySpec);
			
			m_cipherDecypt = Cipher.getInstance("AES");
			m_cipherDecypt.init(Cipher.DECRYPT_MODE, skeySpec);
		} catch (Exception e) {
			s_logger.error("Error creating AESCipher", e);
		}
	}
	
	/**
	 * @param plainText    plain text
	 * @return             encrypted text
	 */
	public String encrypt(String plainText) {
		try {
			return PREFIX + new String(Base64.encodeBase64(m_cipherEncypt.doFinal(plainText.getBytes())));
		} catch (Exception e) {
			s_logger.error("Error while encrypting text", e);
			return null;
		}
	}
	
	/**
	 * @param encryptedText    encrypted text
	 * @return                decrypted text
	 */
	public String decrypt(String encryptedText) {
		try {
			if (encryptedText.startsWith(PREFIX)) {
				return new String(m_cipherDecypt.doFinal(
						Base64.decodeBase64(encryptedText.substring(PREFIX.length()).getBytes())));
			} else {
				s_logger.error("Error while decrypting text: encyptedText does not start with " + PREFIX);
				return encryptedText;
			}
		} catch (Exception e) {
			s_logger.error("Error while decrypting text", e);
			return null;
		}
	}

	/**
	 * @param args          (no arguments expected)
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();

		System.out.println("Generated AES-128 key:" + Base64.encodeBase64(raw));
	}

}
