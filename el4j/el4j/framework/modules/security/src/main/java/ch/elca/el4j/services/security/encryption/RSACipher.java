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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

//Checkstyle: MagicNumber off

/**
 * Lightweight support for basic asymmetric encryption (RSA algorithm).
 *
 * <p>Adapted from RSA.java, an RSA Encryption Implementation.
 * Copyright (C) 2003 Eugene Luzgin, eugene@luzgin.com
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 * @author Laurent Bovet (LBO)
 */
public class RSACipher implements Serializable  {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	/**	 */
	private final BigInteger m_e = BigInteger.valueOf(17);

	/**	 */
	private static final int m_chunkSize = 8;

	/**  */
	private static final String m_cipherDelimiter = "l";
	
	/**	 */
	private BigInteger m_pubKey = BigInteger.ZERO;

	/**	 */
	private BigInteger m_priKey = BigInteger.ZERO;

	/**
	 * Creates a new cipher for encryption and decryption. It generates a new key pair.
	 * 
	 * @param keyLength
	 *            The length of the key. That the algorithm does not work with all values, typical key lengths are 512,
	 *            1024, 2048, 3072 and 4096. Note that longer keys are more secure but also slower (several minutes).
	 *            Keys of length < 1024 are considered unsecure, 1024 is the minimum but still not perfectly secure.
	 */
	public RSACipher(int keyLength) {
		genKeyPair(keyLength);
	}

	/**
	 * Creates a new cipher for encryption and decryption.
	 * 
	 * @param publicKey The public key of the key pair.
	 */
	public RSACipher(String publicKey) {
		m_pubKey = new BigInteger(publicKey);
	}

	/**
	 * Returns the public key of this cipher.
	 * 
	 * @return The public key of this cipher.
	 */
	public String getPublicKey() {
		return m_pubKey.toString();
	}

	/**
	 * Encrypts a given text, by using the public key of the cipher.
	 * 
	 * @param text The string to encrypt.
	 * @return An encrypted string.
	 */
	public String encrypt(String text) {

		BigInteger[] biArr = padding(text);
		StringBuffer cipher = new StringBuffer();
		for (int i = 0; i < biArr.length; i++) {
			BigInteger bi = biArr[i];
			BigInteger c = bi.modPow(m_e, m_pubKey);
			if (cipher.length() == 0) {
				cipher.append(c.toString());
			} else {
				cipher.append(m_cipherDelimiter);
				cipher.append(c.toString());
			}
		}
		return cipher.toString();
	}

	/**
	 * Decripts a given message, by using the private key of this cipher.
	 * 
	 * @param encryptedMessage The message to decrypt.
	 * @return The decrypted message.
	 */
	public String decrypt(String encryptedMessage) {
		if (m_pubKey.compareTo(BigInteger.ZERO) == 0) {
			throw new IllegalStateException(
				"ERROR: public and private keys are not generated!");
		}
		String[] cipherChunks = encryptedMessage.split(m_cipherDelimiter);
		String[] txtcodeChunks = new String[cipherChunks.length];
		for (int i = 0; i < cipherChunks.length; i++) {
			BigInteger bi = new BigInteger(cipherChunks[i]);
			BigInteger tc = bi.modPow(m_priKey, m_pubKey);
			txtcodeChunks[i] = tc.toString();
		}
		String text = unpadding(txtcodeChunks);
		return text;
	}

	/**
	 * Generates a new public-private key pair.
	 * 
	 * @param klen The length of the keys.
	 */
	private void genKeyPair(int klen) {
		BigInteger p = getPrime(klen);
		BigInteger q = getPrime(klen);
		BigInteger one = BigInteger.ONE;
		BigInteger p1 = p.subtract(one);
		BigInteger q1 = q.subtract(one);
		BigInteger p1q1 = p1.multiply(q1);
		BigInteger d = BigInteger.ZERO;
		BigInteger x = BigInteger.ONE;
		boolean success = true;
		while (d.compareTo(BigInteger.ZERO) == 0) {
			BigInteger xp1q1 = x.multiply(p1q1);
			BigInteger xp1q1a1 = xp1q1.add(one);
			BigInteger xp1q1a1re = xp1q1a1.remainder(m_e);
			if (xp1q1a1re.compareTo(BigInteger.ZERO) == 0) {
				d = xp1q1a1.divide(m_e);
			}
			x = x.add(one);
			if (x.compareTo(BigInteger.valueOf(10)) == 1) {
				success = false;
				break;
			}
		}
		if (success) {
			m_pubKey = p.multiply(q);
			m_priKey = d;
		} else {
			genKeyPair(klen);
		}
	}

	/**
	 * 
	 * @param klen
	 * @return
	 */
	private BigInteger getPrime(int klen) {
		Random rdm = new Random();
		return BigInteger.probablePrime(klen, rdm);
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	private BigInteger[] padding(String text) {
		
		int textLen = text.length();
		int biArrLen = (int) ((textLen / m_chunkSize)
			+ (((textLen % m_chunkSize) == 0) ? 0 : 1) + 1);
		BigInteger[] biArr = new BigInteger[biArrLen];
		int biArrCounter = 0;
		int chunkCounter = 0;
		String chunkStr = "";
		// generate first random row:
		String firstRow = "";
		while (firstRow.length() < 65) {
			int rm = (int) (Math.random() * 100000000);
			String rmStr = String.valueOf(rm);
			firstRow = firstRow.concat(rmStr);
		}
		firstRow = firstRow.substring(0, 65);
		biArr[biArrCounter] = new BigInteger(firstRow);
		biArrCounter++;
		// then fill the rest rows by actual code:
		for (int i = 0; i < textLen; i++) {
			char ch = text.charAt(i);
			int code = (int) ch;
			String codeStr = String.valueOf(code);
			int codeStrLen = codeStr.length();
			chunkCounter = i % m_chunkSize;
			// format a single character code
			for (int j = 0; j < (m_chunkSize - codeStrLen); j++) {
				codeStr = "0".concat(codeStr);
			}
			// build a single chunk from 8 character codes
			if (chunkCounter == 0) {
				chunkStr = "";
				chunkStr = "1".concat(codeStr);
			} else if (chunkCounter < m_chunkSize - 1) {
				chunkStr = chunkStr.concat(codeStr);
			} else if (chunkCounter == m_chunkSize - 1) {
				chunkStr = chunkStr.concat(codeStr);
				biArr[biArrCounter] = new BigInteger(chunkStr);
				biArrCounter++;
			}
		}
		if (chunkCounter != m_chunkSize - 1) {
			for (int k = chunkCounter; k < m_chunkSize; k++) {
				for (int m = 0; m < m_chunkSize; m++) {
					chunkStr = chunkStr.concat("0");
				}
			}
			biArr[biArrCounter] = new BigInteger(chunkStr);
		}
		return biArr;
	}

	/**
	 * 
	 * @param txtcode
	 * @return
	 */
	private String unpadding(String[] txtcode) {
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < txtcode.length; i++) {
			String codeChunk = txtcode[i].substring(1);
			String codeStr = "";
			int j = 0;
			while (j + m_chunkSize <= codeChunk.length()) {
				codeStr = codeChunk.substring(j, j + m_chunkSize);
				BigInteger biCode = new BigInteger(codeStr);
				int code = biCode.intValue();
				if (code == 0) {
					break;
				}
				char[] ch = new char[1];
				ch[0] = (char) code;
				buf.append(ch);
				j = j + m_chunkSize;
			}
		}
		return buf.toString();
	}
}

//Checkstyle: MagicNumber on