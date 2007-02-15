/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol.loadbalancing.policy;

import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.NoProtocolAvailableRTException;


/**
 * 
 * Chooses protocols according to a round robin policy.
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
public class RoundRobinPolicy extends AbstractPolicy {

    /** Index of the protocol currently in use. */
    private int m_currentIndex = -1;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRemotingProtocol getNextProtocol() 
        throws NoProtocolAvailableRTException {
        if ((m_protocols == null) || (m_protocols.length == 0)) {
            throw new NoProtocolAvailableRTException("No protocol defined");
        }
        m_currentIndex = (m_currentIndex + 1) % m_protocols.length;
        return m_protocols[m_currentIndex];
    }   
}
