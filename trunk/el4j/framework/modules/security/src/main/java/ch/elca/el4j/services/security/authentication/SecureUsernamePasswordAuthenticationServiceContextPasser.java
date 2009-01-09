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
package ch.elca.el4j.services.security.authentication;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.security.encryption.AESCipher;

/**
 * This ContextPasser en/decrypts credentials using an AES-128 cipher such that no plain text passwords
 * are sent over the network.
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
public class SecureUsernamePasswordAuthenticationServiceContextPasser extends
	AuthenticationServiceContextPasser {
	
	/**
	 * Private logger of this class.
	 */
	private static Log s_logger = LogFactory.getLog(SecureUsernamePasswordAuthenticationServiceContextPasser.class);
	
	/**
	 * The AES cipher.
	 */
	private AESCipher m_cipher;

	/**
	 * @param key Is the key to set.
	 */
	public void setKey(String key) {
		m_cipher = new AESCipher(key);
	}
	
	/** {@inheritDoc} */
	@Override
	public Object getImplicitlyPassedContext() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				auth.getPrincipal(), m_cipher.encrypt((String) auth.getCredentials()), auth.getAuthorities());
			
			return result;
		} else {
			return auth;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void pushImplicitlyPassedContext(Object context) {
		if (context == null) {
			s_logger.warn("Authentication == null");
			return;
		}
		
		Authentication auth = (Authentication) context;
		if (auth instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				auth.getPrincipal(), m_cipher.decrypt((String) auth.getCredentials()), auth.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(result);
		} else {
			//throw new IllegalArgumentException("Context has to be of type UsernamePasswordAuthenticationToken");
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
	}
	
	
}
