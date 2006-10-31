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

import ch.elca.el4j.services.remoting.AbstractRemotingProtocol ;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.NoProtocolAvailableException;


/**
 * 
 * This class defines the following load-balancing property: The protocols are
 * returned in a round-robin way, but the currently used protocol only changes
 * if a failure has been notified about it. However, no protocol is removed 
 * from the list of protocols, and failed protocols will eventually be returned
 * again by {@link nextProtocol}. 
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
public class RedirectUponFailurePolicy extends AbstractPolicy {

    /** {@inheritDoc} */
    public AbstractRemotingProtocol getNextProtocol() throws NoProtocolAvailableException {
        if ((m_protocols == null) || (m_protocols.length == 0)) {
            throw new NoProtocolAvailableException("No protocol defined");
        } // if
        return m_protocols[m_currentIndex];
    } // getNextProtocol()

    /** {@inheritDoc} */
    public void notifyFailure(AbstractRemotingProtocol protocol) {
        if (m_protocols != null) {
            int index = findIndex(protocol);
            if (index == m_currentIndex) {
                m_currentIndex = (m_currentIndex + 1) % m_protocols.length;
            } // if
        } // if
    } // notifyFailure()

    /** Index of the currently used protocol */
    private int m_currentIndex = 0;

    /**
     * @param protocol
     *            Protocol information to be found
     * @return index, -1 otherwise
     */
    private int findIndex(AbstractRemotingProtocol protocol) {
        int index = -1;
        for (int i = 0; (index < 0) && (i < m_protocols.length); i += 1) {
            if (protocol == m_protocols[i]) {
                index = i;
            } // if
        } // for i
        return index;
    } // findIndex()

} // CLASS RedirectUponFailure
