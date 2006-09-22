/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol.loadbalancing.policy;

import java.util.Calendar ;
import java.util.Random ;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.remoting.AbstractRemotingProtocol ;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.NoProtocolAvailableException ;

/**
 * 
 * Chooses the protocol according to a random policy. Protocols for whom a
 * failure has been notified, are removed.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public class RandomPolicy extends AbstractPolicy {

    @Override
    public AbstractRemotingProtocol getNextProtocol() 
        throws NoProtocolAvailableException {
        if ((m_protocols == null) || (m_protocols.length == 0)) {
            throw new NoProtocolAvailableException("No protocol defined") ;
        } // if 
        AbstractRemotingProtocol protocol = 
            m_protocols[m_random.nextInt(m_protocols.length)];
        s_logger.debug("Returning next protocol: " + protocol);
        return protocol;
    } // getNextProtocol()

    public void notifyFailure(AbstractRemotingProtocol protocol) {
        s_logger.debug("Removing protocol: " + protocol);
        removeProtocol(protocol) ;
    } // notifyFailure()
    
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(RandomPolicy.class);

    private Random m_random = new Random(Calendar.getInstance().getTimeInMillis()) ;
} // Class RandomPolicy
