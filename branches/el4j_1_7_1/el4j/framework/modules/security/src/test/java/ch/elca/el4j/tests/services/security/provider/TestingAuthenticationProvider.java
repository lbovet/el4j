package ch.elca.el4j.tests.services.security.provider;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.AuthenticationProvider;

import ch.elca.el4j.tests.services.security.TestingAuthenticationToken;

/**
 * 
 * This class is an implementation of the AthenticationProvider for testing purposes.
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */

public class TestingAuthenticationProvider implements AuthenticationProvider {
	
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authentication;
	}

	public boolean supports(Class authentication) {
		if (TestingAuthenticationToken.class.isAssignableFrom(authentication)) {
			return true;
		} else {
			return false;
		}
	}
}
