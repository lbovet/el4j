package ch.elca.el4j.applications.refdbseam;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


/**
 * 
 * This class is provided by the Seam template. It should be used when starting
 * to work on authentication.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
@Name("authenticator")
public class Authenticator {
    /**
     * The used logger.
     */
    @Logger 
    private Log m_log;
    
    /**
     * The identity of the user trying to login.
     */
    @In 
    private Identity m_identity;
    
    /**
     * Try to authenticate the user.
     * @return Whether the authentication as successful
     */
    public boolean authenticate() {
        m_log.info("authenticating #0", m_identity.getUsername());
        //write your authentication logic here,
        //return true if the authentication was
        //successful, false otherwise
        m_identity.addRole("admin");
        return true;
    }
}
