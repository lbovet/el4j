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
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This class implements all needed things for the httpinvoker protocol.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Rashid Waraich (RWA)
 */
public class HttpInvoker  extends AbstractInetSocketAddressWebProtocol {

	/**
	 * {@inheritDoc}
	 */
	public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
			Class serviceInterfaceWithContext) {
		StaticApplicationContext appContext = new StaticApplicationContext(
				m_parentApplicationContext);
		registerChildApplicationContext(appContext);
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue("serviceInterface", serviceInterfaceWithContext);
		props.addPropertyValue("serviceUrl", generateUrl(proxyBean));
		appContext.registerSingleton("httpInvokerProxyBeanGen",
			getProxyObjectType(), props);
		appContext.refresh();
		return appContext.getBean("httpInvokerProxyBeanGen");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object createExporterBean(RemotingServiceExporter exporterBean,
			Class serviceInterfaceWithContext, Object serviceProxy) {
		StaticApplicationContext appContext = new StaticApplicationContext(
				m_parentApplicationContext);
		registerChildApplicationContext(appContext);
		MutablePropertyValues props = new MutablePropertyValues();
		props.addPropertyValue("service", serviceProxy);
		props.addPropertyValue("serviceInterface", serviceInterfaceWithContext);
		appContext.registerSingleton("httpInvokerExporterBeanGen",
				getExporterObjectType(), props);
		appContext.refresh();
		return appContext.getBean("httpInvokerExporterBeanGen");
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getProxyObjectType() {
		return HttpInvokerProxyFactoryBean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getExporterObjectType() {
		return HttpInvokerServiceExporter.class;
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
		return sb.toString();
	}
}
