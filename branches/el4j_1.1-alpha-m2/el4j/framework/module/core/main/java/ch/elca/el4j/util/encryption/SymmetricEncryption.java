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
package ch.elca.el4j.util.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Class to en- and decrypt data symetrically.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class SymmetricEncryption {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(SymmetricEncryption.class);

    /** DESede encryption scheme. */
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

    /** DES encryption scheme. */
    private static final String DES_ENCRYPTION_SCHEME = "DES";

    /** AES encryption scheme. */
    private static final String AES_ENCRYPTION_SCHEME = "AES";

    /** Default encryption key. */
    private static final String DEFAULT_ENCRYPTION_KEY 
        = "This is a fairly long phrase used to encrypt.";

    /** The encoding used for the data to be signed, default: UTF-8. */
    private String m_dataEncodingFormat = "UTF-8";

    /** The encryptionScheme, default: "AES". */
    private String m_encryptionScheme = AES_ENCRYPTION_SCHEME;

    /** The encryptionKey. */
    private String m_encryptionKey = DEFAULT_ENCRYPTION_KEY;

    /** m_keySpec. */
    private KeySpec m_keySpec;

    /** m_cipher. */
    private Cipher m_cipher;

    /**
     * @param dataEncodingFormat
     *            The dataEncodingFormat to set.
     */
    public void setDataEncodingFormat(String dataEncodingFormat) {
        m_dataEncodingFormat = dataEncodingFormat;
    }

    /**
     * @param encryptionKey
     *            The encryptionKey to set.
     */
    public void setEncryptionKey(String encryptionKey) {
        m_encryptionKey = encryptionKey;
    }

    /**
     * @param encryptionScheme
     *            The encryptionScheme to set.
     */
    public void setEncryptionScheme(String encryptionScheme) {
        m_encryptionScheme = encryptionScheme;
    }

    /**
     * Initializes the encryptor.
     * 
     * @throws EncryptionException if encryption could not be initialized.
     */
    public void init() throws EncryptionException {
        try {
            byte[] keyAsBytes = m_encryptionKey.getBytes(m_dataEncodingFormat);

            if (m_encryptionScheme.equals(DESEDE_ENCRYPTION_SCHEME)) {
                m_keySpec = new DESedeKeySpec(keyAsBytes);
            } else if (m_encryptionScheme.equals(DES_ENCRYPTION_SCHEME)) {
                m_keySpec = new DESKeySpec(keyAsBytes);
            } else if (m_encryptionScheme.equals(AES_ENCRYPTION_SCHEME)) {
                m_keySpec 
                    = new SecretKeySpec(keyAsBytes, AES_ENCRYPTION_SCHEME);
            } else {
                throw new IllegalArgumentException(
                    "Encryption scheme not supported: " + m_encryptionScheme);
            }

            m_cipher = Cipher.getInstance(m_encryptionScheme);

        } catch (InvalidKeyException e) {
            throw new EncryptionException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncryptionException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException(e);
        }

    }

    /**
     * Encrypts a String.
     * 
     * @param unencryptedString
     *            The String to encrypt.
     * @return The decrypted String.
     * @throws EncryptionException if there was an error.
     */
    public String encrypt(String unencryptedString) throws EncryptionException {
        try {
            byte[] cleartext = unencryptedString.getBytes(m_dataEncodingFormat);
            return encryptByteArrayToString(cleartext);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Decrypts a String.
     * 
     * @param encryptedString
     *            The String to decrypt.
     * @return The decrypted String.
     * @throws EncryptionException if there was an error.
     */
    public String decrypt(String encryptedString) throws EncryptionException {
        return bytes2String(decryptStringToByteArray(encryptedString));
    }

    /**
     * Method to convert a byte array to a string.
     * 
     * @param bytes To convert.
     * @return Returns generated string.
     */
    private String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

    /**
     * Encrypts a byte array into a String.
     * 
     * @param cleartext
     *            The byte array to encrypt.
     * @return The encrypted String.
     * @throws EncryptionException if there was an error.
     */
    public String encryptByteArrayToString(byte[] cleartext)
        throws EncryptionException {

        if (cleartext == null || cleartext.length == 0) {
            throw new EncryptionException("Unencrypted string was null or "
                + "empty");
        }

        try {
            m_cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] ciphertext = m_cipher.doFinal(cleartext);

            BASE64Encoder base64encoder = new BASE64Encoder();
            return base64encoder.encode(ciphertext);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Decrypts a String into a byte array.
     * 
     * @param encryptedString
     *            The String to decrypt.
     * @return The decrypted byte array.
     * @throws EncryptionException if there was an error.
     */
    public byte[] decryptStringToByteArray(String encryptedString)
        throws EncryptionException {

        if (encryptedString == null || encryptedString.trim().length() <= 0) {
            throw new EncryptionException("Unencrypted string was null or "
                + "empty");
        }

        try {
            m_cipher.init(Cipher.DECRYPT_MODE, getKey());
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] cleartext = base64decoder.decodeBuffer(encryptedString);
            byte[] ciphertext = m_cipher.doFinal(cleartext);

            return ciphertext;
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Returns the secret key.
     * 
     * @return The secret key
     * @throws EncryptionException if there was an error.
     */
    private SecretKey getKey() throws EncryptionException {

        SecretKey key;
        if (m_encryptionScheme.equals(AES_ENCRYPTION_SCHEME)) {
            // Get the secret key from the secret key specification
            key = (SecretKeySpec) m_keySpec;
        } else {
            // Generate the secret key via secret key factory
            try {
                SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance(m_encryptionScheme);
                key = keyFactory.generateSecret(m_keySpec);
            } catch (InvalidKeySpecException e) {
                throw new EncryptionException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException(e);
            }
        }

        return key;

    }
}