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

package ch.elca.el4j.services.remoting;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;

/**
 * This class is the global remote proxy bean.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class RemotingProxyFactoryBean extends AbstractRemotingBase implements
		FactoryBean {
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory
			.getLogger(RemotingProxyFactoryBean.class);

	/**
	 * This is the service proxy to work with.
	 */
	private Object m_serviceProxy;

	/** Whether the factory creates singleton beans or not. */
	private boolean m_singleton = true;
	
	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		getRemoteProtocol().checkRemotingProxy(this);
	}
	
	/**
	 * @return Returns a freshly created service proxy.
	 */
	protected Object getFreshServiceProxy() {
		
		Object serviceProxy;
		
		boolean useImplicitContextPassing = getRemoteProtocol().
			getImplicitContextPassingRegistry() != null;
		
		if (useImplicitContextPassing
			&& !getRemoteProtocol().getProtocolSpecificContextPassing()) {
			s_logger.info("Implicit context passing enabled.");
			
			/**
			 * Get the context class loader from current thread.
			 */
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			
			/**
			 * Create the service interface to be able to send context
			 * information.
			 */
			InterfaceEnricher interfaceIndirector = new InterfaceEnricher();
			EnrichmentDecorator interfaceDecorator
				= new ContextEnrichmentDecorator();
			Class serviceInterfaceWithContext = interfaceIndirector
				.createShadowInterfaceAndLoadItDirectly(
					getServiceInterface(), interfaceDecorator, cl);
			
			/**
			 * Create the inner proxy bean.
			 */
			Object innerProxyBean = getRemoteProtocol().createProxyBean(this,
					serviceInterfaceWithContext);
			
			/**
			 * Generate the proxy.
			 */
			ClientContextInvocationHandler invocationHandler
				= getRemoteProtocol().getClientContextInvocationHandler(
					innerProxyBean, serviceInterfaceWithContext);
			
			Class[] proxyInterface = getRemoteProtocol().
					getProxyInterface(getServiceInterface());
			
			serviceProxy = Proxy.newProxyInstance(cl,
					proxyInterface, invocationHandler);
			
		} else {
			if (!getRemoteProtocol().getProtocolSpecificContextPassing()) {
				s_logger.warn("Implicit context passing disabled.");
			} else {
				s_logger.info(
					"Protocol specific implicit context passing enabled.");
			}

			serviceProxy = getRemoteProtocol().createProxyBean(this,
					getServiceInterface());
		}
		
		return serviceProxy;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getObject() {
		Object serviceProxy;
		
		if (isSingleton()) {
			if (m_serviceProxy == null) {
				m_serviceProxy = getFreshServiceProxy();
			}
			serviceProxy = m_serviceProxy;
			
		} else {
			serviceProxy = getFreshServiceProxy();
		}
		return serviceProxy;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getObjectType() {
		return (this.m_serviceProxy != null) ? this.m_serviceProxy.getClass()
				: getServiceInterface();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton() {
		return m_singleton;
	}

	/**
	 * Defines whether the factory creates singleton bean instances or
	 * prototypes.
	 *
	 * @param singleton
	 *      <code>true</code> to crate singletons, <code>false</code> for
	 *      prototypes.
	 */
	public void setSingleton(boolean singleton) {
		m_singleton = singleton;
	}
}
