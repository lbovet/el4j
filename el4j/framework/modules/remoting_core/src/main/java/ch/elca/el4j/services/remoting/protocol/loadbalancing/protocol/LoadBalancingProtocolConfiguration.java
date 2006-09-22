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
package ch.elca.el4j.services.remoting.protocol.loadbalancing.protocol;

import java.util.Map;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol ;
import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.AbstractPolicy;

/**
 * 
 * Defines the configuration parameters for {@link LoadBalancingProtocol}
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
public class LoadBalancingProtocolConfiguration implements
    ProtocolSpecificConfiguration {

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(getProtocols(),
            "protocols", this);
        if (getProtocols().length == 0) {
            CoreNotificationHelper.notifyMisconfiguration("At least one " +
                    "protocol needs to be defined.") ;
        } // if
   } // afterPropertiesSet()
    
    /**
     * Setter method
     * 
     * @param protocols
     *            Available protocols
     */
    public void setProtocols(AbstractRemotingProtocol[] protocols) {
        m_protocols = protocols;
    } // setProtocols()

    /** 
     * 
     * @return Defined protocols, or arguments to protocols
     */
    public AbstractRemotingProtocol[] getProtocols() {
        return m_protocols ;
    } // getProtocols()
    
    /**
     * Installs the policy used to select the protocols.
     * 
     * @param policy
     */
    public void setPolicy(AbstractPolicy policy) {
        m_policy = policy;
    } // setPolicy()

    /**
     * 
     * @return Policy map
     */
    public AbstractPolicy getPolicy() {
        return m_policy ;
    } // getPolicy()

    
    protected AbstractRemotingProtocol[] m_protocols ;
    protected AbstractPolicy m_policy ;
    
} // Class LoadBalancingProtocolConfiguration
