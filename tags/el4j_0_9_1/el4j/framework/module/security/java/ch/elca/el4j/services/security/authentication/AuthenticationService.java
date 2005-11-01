/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.security.authentication;

import net.sf.acegisecurity.Authentication;

/**
 * AuthenticationService interface. Provides methods to log in and out. If you
 * are already logged in, you have to log out first and then log in again.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 * @author Andreas Pfenninger (APR)
 * @author Christoph Schwitter (CSC)
 */
public interface AuthenticationService {

    /**
     * Authenticate using the given Authentication object.
     * @param auth The Authentication object.
     */
    public void authenticate(Authentication auth);

    /**
     * After a login method has been called, or the user is authenticated in any
     * other way, the authentication data object as constructed during the
     * authentication process can be accessed using this method.
     * 
     * @return The authentication data object representing the user.
     */
    public Authentication getAuthenticationData();

    /**
     * After the user has logged in, the user's name can be accessed using this
     * method.
     * 
     * @return The name of the logged in user.
     */
    public String getUserName();

}