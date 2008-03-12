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

package ch.elca.el4j.util.encryption;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

/**
 *  En- / decrypts strings with a key derived from a user-supplied password
 *  with java's PBEStringEncryption (DES, MD5 password hash).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class PBEncryptor {

    private Cipher m_cipher;
    private SecretKey m_key;

    private String ENCODING = "UTF-8";
    private String ALGORITHM = "PBEWithMD5AndDES";

    private int SALTSIZE = 8;
    private int ENCITERATIONS = 10;

    /**
     * Constructor. As this lives in external we don't initialize with the
     * internal password here.
     * @throws EncryptionException
     */
    public PBEncryptor() throws EncryptionException {
        try {
            deriveKey("This is a fairly long phrase used to encrypt.");
            m_cipher = Cipher.getInstance(ALGORITHM);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }

    }

    /**
     * Hashes the password and converts it to a suitable format for the cipher.
     * 
     * @param password
     *            The password to generate a key from.
     * Sets a valid key derived from the password.
     * @throws EncryptionException
     */
    public void deriveKey(String password) throws EncryptionException {

        String salt = "ELCAEL4J";
        int itCount = 100;

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt
                .getBytes(), itCount);
            m_key = factory.generateSecret(spec);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    private byte[] generateSalt() throws EncryptionException {
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
        byte[] salt = new byte[SALTSIZE];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Encrypts a string with the currently set password.
     * 
     * @param plain String.
     * @return The encrypted string.
     * @throws EncryptionException
     */
    public String encrypt(String plain) throws EncryptionException {
        byte[] salt = generateSalt();
        PBEParameterSpec spec = new PBEParameterSpec(salt, ENCITERATIONS);

        try {
            m_cipher.init(Cipher.ENCRYPT_MODE, m_key, spec);
            byte[] plainBytes = plain.getBytes(ENCODING);
            byte[] encBytes = m_cipher.doFinal(plainBytes);
            return new String(Base64.encodeBase64(ArrayUtils.addAll(salt,
                encBytes)));
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Decrypts a string with the currently set password.
     * 
     * @param enc String.
     * @return The decrypted string.
     * @throws EncryptionException
     */
    public String decrypt(String enc) throws EncryptionException {
        byte[] plainBytes;
        byte[] encBytes = Base64.decodeBase64(enc.getBytes());
        byte[] salt = ArrayUtils.subarray(encBytes, 0, SALTSIZE);
        encBytes = ArrayUtils.subarray(encBytes, SALTSIZE, encBytes.length);

        PBEParameterSpec spec = new PBEParameterSpec(salt, ENCITERATIONS);

        try {
            m_cipher.init(Cipher.DECRYPT_MODE, m_key, spec);
            plainBytes = m_cipher.doFinal(encBytes);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < plainBytes.length; i++) {
            stringBuffer.append((char) plainBytes[i]);
        }
        return stringBuffer.toString();
    }

}