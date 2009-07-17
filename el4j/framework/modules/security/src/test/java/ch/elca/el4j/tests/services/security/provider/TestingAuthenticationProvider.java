package ch.elca.el4j.tests.services.security.provider;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.AuthenticationProvider;

import ch.elca.el4j.tests.services.security.TestingAuthenticationToken;

/**
 * 
 * This class is an implementation of the AthenticationProvider for testing purposes.
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
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
