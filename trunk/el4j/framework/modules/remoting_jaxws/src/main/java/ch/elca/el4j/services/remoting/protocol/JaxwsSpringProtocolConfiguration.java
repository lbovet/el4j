/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol;

import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;

/**
 * The JAX-WS protocol (Spring) configuration class.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class JaxwsSpringProtocolConfiguration implements ProtocolSpecificConfiguration {
	
	/**
	 * The URI of the namespace.
	 */
	private String m_namespaceUri;
	
	/**
	 * The service name.
	 */
	private String m_serviceName;
	
	/**
	 * The port name.
	 */
	private String m_portName;
	
	/** {@inheritDoc} */
	public void afterPropertiesSet() throws Exception { }

	/**
	 * @return the URI of the namespace
	 */
	public String getNamespaceUri() {
		return m_namespaceUri;
	}

	/**
	 * @param namespaceUri the URI of the namespace
	 */
	public void setNamespaceUri(String namespaceUri) {
		m_namespaceUri = namespaceUri;
	}

	/**
	 * @return the service name
	 */
	public String getServiceName() {
		return m_serviceName;
	}

	/**
	 * @param serviceName the service name
	 */
	public void setServiceName(String serviceName) {
		m_serviceName = serviceName;
	}

	/**
	 * @return the port name
	 */
	public String getPortName() {
		return m_portName;
	}

	/**
	 * @param portName the port name
	 */
	public void setPortName(String portName) {
		m_portName = portName;
	}

}
