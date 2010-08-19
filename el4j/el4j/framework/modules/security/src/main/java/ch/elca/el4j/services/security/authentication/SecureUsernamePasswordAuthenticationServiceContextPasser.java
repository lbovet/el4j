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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.elca.el4j.services.security.encryption.AESCipher;

/**
 * This ContextPasser en/decrypts credentials using an AES-128 cipher such that no plain text passwords
 * are sent over the network.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SecureUsernamePasswordAuthenticationServiceContextPasser extends
	AuthenticationServiceContextPasser {
	
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(SecureUsernamePasswordAuthenticationServiceContextPasser.class);
	
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
			SecurityContextHolder.getContext().setAuthentication(null);
			return;
		}
		
		Authentication auth = (Authentication) context;
		if (auth instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				auth.getPrincipal(), m_cipher.decrypt((String) auth.getCredentials()));
			
			SecurityContextHolder.getContext().setAuthentication(result);
		} else {
			//throw new IllegalArgumentException("Context has to be of type UsernamePasswordAuthenticationToken");
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
	}
	
	
}
