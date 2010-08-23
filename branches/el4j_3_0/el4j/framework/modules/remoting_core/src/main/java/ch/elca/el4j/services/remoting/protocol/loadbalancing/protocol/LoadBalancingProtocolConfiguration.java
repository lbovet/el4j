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
package ch.elca.el4j.services.remoting.protocol.loadbalancing.protocol;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.AbstractPolicy;

/**
 *
 * Defines the configuration parameters for {@link LoadBalancingProtocol}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Pleisch (SPL)
 */
public class LoadBalancingProtocolConfiguration implements
	ProtocolSpecificConfiguration {

	protected AbstractRemotingProtocol[] m_protocols;
	
	protected AbstractPolicy m_policy;
	
	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception {
		CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(getProtocols(),
			"protocols", this);
		if (getProtocols().length == 0) {
			CoreNotificationHelper.notifyMisconfiguration("At least one "
				+ "protocol needs to be defined.");
		}
	}
	
	/**
	 * Setter method.
	 *
	 * @param protocols
	 *            Available protocols
	 */
	public void setProtocols(AbstractRemotingProtocol[] protocols) {
		m_protocols = protocols;
	}

	/**
	 *
	 * @return Defined protocols, or arguments to protocols
	 */
	public AbstractRemotingProtocol[] getProtocols() {
		return m_protocols;
	}
	
	/**
	 * Installs the policy used to select the protocols.
	 *
	 * @param policy The policy to install
	 */
	public void setPolicy(AbstractPolicy policy) {
		m_policy = policy;
	}

	/**
	 *
	 * @return Policy map
	 */
	public AbstractPolicy getPolicy() {
		return m_policy;
	}
}
