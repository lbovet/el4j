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
 * Abstract class that defines the policy of the protocol selection.
 * Protocol comparison must be done using "=="!
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
public abstract class AbstractPolicy {
    
    /**
     * This method is called before any other method is called. It passes the
     * protocol information.
     * @param protocols Array of currently defined protocols.
     */
    public void setProtocols(AbstractRemotingProtocol[] protocols) {
        m_protocols = protocols ;
    } // setProtocols()
    
    /**
     * This method is called if the use of one of the protocols has failed.
     * Protocol comparison must be done using "=="!
     * @param protocol The protocol that has lead to a failure. 
     */
    public void notifyFailure(AbstractRemotingProtocol protocol) {
        // Do nothing
    } // notifyFailure()
    
    /** 
     * @return Number of available protocols
     */
    public int getProtocolCount() {
        return m_protocols.length ;
    } // getProtocolCount()
    
    /**
     * Note that protocol comparison must be done using "=="!
     * 
     * @return The next protocol to be used. 
     * @throws NoProtocolAvailableException If no protocol is available any more
     */
    public abstract AbstractRemotingProtocol getNextProtocol() 
       throws NoProtocolAvailableException;
    
    
    /**
     * Removes protocol 'pi' from {@link #m_protocols}. Does nothing if 'pi'
     * does not exist.
     * @param pi Protocol to be removed.
     */
    protected void removeProtocol(AbstractRemotingProtocol protocol) {
       int index = -1 ;
       for (int i = 0; i < m_protocols.length; i += 1) {
           if (m_protocols[i] == protocol) {
               index = i;
               break ;
           } // if
       } // for i
        if (index >= 0) {
            // Element found
            AbstractRemotingProtocol[] tmp = new AbstractRemotingProtocol[m_protocols.length - 1];
            for (int k = 0; k < index; k += 1) {
               tmp[k] = m_protocols[k] ; 
            } // for k
            for (int j = index + 1; j < m_protocols.length; j += 1) {
                tmp[j-1] = m_protocols[j] ;
            } // for j
            m_protocols = tmp ;
        } // if
    } // removeProtocol()
    
    /** 
     * Array of protocols, each represented by an array of Strings, using
     * a [protocolTag, arg1, arg2, ...] representation.
    */
    protected AbstractRemotingProtocol[] m_protocols ;
} // CLASS AbstractPolicy
