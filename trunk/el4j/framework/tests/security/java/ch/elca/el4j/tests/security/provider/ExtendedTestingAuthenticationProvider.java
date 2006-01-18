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

package ch.elca.el4j.tests.security.provider;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.BadCredentialsException;
import net.sf.acegisecurity.providers.TestingAuthenticationProvider;

/**
 * Provider for testing reasons. This class throws a BadCredentialsException in
 * case the username is not equal to the password.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class ExtendedTestingAuthenticationProvider extends
    TestingAuthenticationProvider {

    /**
     * {@inheritDoc}
     */
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {

        if (authentication == null) {
            return null;
        }
        
        if (authentication.getPrincipal().toString().equals(
            authentication.getCredentials().toString())) {
            return authentication;
        } else {
            throw new BadCredentialsException(
                "Authentication Failed with Principal "
                    + authentication.getPrincipal().toString()
                    + " and Credential "
                    + authentication.getCredentials().toString() + ".");
        }
    }
}