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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This class implements all needed things for the JAX-WS protocol using Spring remoting.
 * Creating service exporters is not supported because of some restrictions
 * (see {@link #createExporterBean(RemotingServiceExporter, Class, Object)}.
 * 
 * This protocol should be used for clients (no servers) that communicate with self-written servers (i.e. Code is
 * available and should not be generated by wsimport). If the code of one side has to be generated, use {@link Jaxws}.
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
public class JaxwsSpring extends AbstractInetSocketAddressWebProtocol {
	
	/**
	 * {@inheritDoc}
	 */
	public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
			Class serviceInterfaceWithContext) {
		
		StaticApplicationContext appContext = new StaticApplicationContext(m_parentApplicationContext);
		registerChildApplicationContext(appContext);
		
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue("serviceInterface", serviceInterfaceWithContext);
		props.addPropertyValue("wsdlDocumentUrl", generateUrl(proxyBean));
		
		ProtocolSpecificConfiguration cfg = proxyBean.getProtocolSpecificConfiguration();
		if (cfg != null && cfg instanceof JaxwsSpringProtocolConfiguration) {
			JaxwsSpringProtocolConfiguration jaxWsConfig = (JaxwsSpringProtocolConfiguration) cfg;
			
			props.addPropertyValue("namespaceUri", jaxWsConfig.getNamespaceUri());
			props.addPropertyValue("serviceName", jaxWsConfig.getServiceName());
			props.addPropertyValue("portName", jaxWsConfig.getPortName());
		}
		
		adaptProxyServiceProperties(props);
		
		appContext.registerSingleton("jaxWsProxyBeanGen", getProxyObjectType(), props);
		appContext.refresh();
		
		return appContext.getBean("jaxWsProxyBeanGen");
	}

	/**
	 * Do not use exporter from {@link JaxwsSpring}, but {@link Jaxws}, because it requires JDK 6 and
	 * does not support implicit context passing. 
	 * {@inheritDoc}
	 */
	public Object createExporterBean(RemotingServiceExporter exporterBean,
			Class serviceInterfaceWithContext, Object serviceProxy) {
		/*StaticApplicationContext appContext = new StaticApplicationContext(m_parentApplicationContext);
		registerChildApplicationContext(appContext);
		
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(getServiceHost());
		sb.append(":");
		sb.append(getServicePort());
		sb.append("/");
		
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue("baseAddress", sb.toString());
		appContext.registerSingleton("jaxWsExporterBeanGen", getExporterObjectType(), props);
		appContext.refresh();
		
		return appContext.getBean("jaxWsExporterBeanGen");*/
		return new Object();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getProxyObjectType() {
		return JaxWsPortProxyFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getExporterObjectType() {
		//return SimpleJaxWsServiceExporter.class;
		return Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public String generateUrl(AbstractRemotingBase remoteBase) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(getServiceHost());
		sb.append(":");
		sb.append(getServicePort());
		sb.append("/");
		sb.append(getContextPath());
		sb.append("/");
		sb.append(remoteBase.getServiceName());
		sb.append("?wsdl");
		return sb.toString();
	}
	
	/**
	 * Does this protocol handle context passing on its own? Yes.
	 * @return Whether this protocol handles the context (<code>true</code>)
	 */
	public boolean getProtocolSpecificContextPassing() {
		return true;
	}
	
	/**
	 * Method providing an extension point do adapt the proxy service.
	 * @param props The service properties
	 */
	protected void adaptProxyServiceProperties(MutablePropertyValues props) {
	}
}