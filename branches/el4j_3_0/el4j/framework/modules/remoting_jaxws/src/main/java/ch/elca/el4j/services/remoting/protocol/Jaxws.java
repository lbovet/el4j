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
package ch.elca.el4j.services.remoting.protocol;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.apache.commons.collections.CollectionUtils;
import org.jvnet.jax_ws_commons.spring.SpringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.transport.http.servlet.SpringBinding;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.util.codingsupport.AopHelper;

/**
 * This class implements all needed things for the soap protocol using JAX-WS.
 * Since <b>EL4J 1.7</b> it is possible to define JaxWs handlers.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 * @author Reynald Borer (RBR)
 */
public class Jaxws extends AbstractInetSocketAddressWebProtocol {
	/**
	 * Private logger.
	 */
	protected static final Logger s_logger = LoggerFactory.getLogger(Jaxws.class);

	/**
	 * List of JaxWs handlers.
	 */
	@SuppressWarnings("unchecked")
	private List<Handler> handlers = new ArrayList<Handler>();
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Object createExporterBean(RemotingServiceExporter exporterBean,
			Class serviceInterfaceWithContext, Object serviceProxy) {
		
		Object bean = exporterBean.getApplicationContext().getBean(
			exporterBean.getService());
		
		SpringService service = new SpringService();
		service.setBean(bean);
		
		// JAX-WS look for web-service annotation on the target class and not the interface; in case it is a dynamic
		// proxy, we should set the correct class in SpringService!
		if (AopHelper.isAopProxy(bean)) {
			service.setImpl(AopHelper.getTargetClass(bean));
		}
		
		// give potential subclasses the chance to adapt the service
		adaptExporterService(service);
		
		SpringBinding binding = new SpringBinding();
		binding.setUrl("/" + exporterBean.getServiceName());
		try {
			binding.setService(service.getObject());
		} catch (Exception e) {
			CoreNotificationHelper.notifyMisconfiguration(
				"Could not create JAX-WS binding for " + bean, e);
		}
		
		return binding;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
			Class serviceInterfaceWithContext) {
		Object createdProxy = null;

		Class serviceInterface = proxyBean.getServiceInterface();
		String serviceName = serviceInterface.getName();
		
		try {
			Class wsServiceClass;
			String portName;
			
			ProtocolSpecificConfiguration cfg
				= proxyBean.getProtocolSpecificConfiguration();
			if (cfg != null && cfg instanceof JaxwsProtocolConfiguration) {
				JaxwsProtocolConfiguration jaxWsConfig
					= (JaxwsProtocolConfiguration) cfg;
				
				// use wsimport-generated classes directly (no dynamic proxies)
				wsServiceClass = jaxWsConfig.getServiceImplementation();
				portName = "get" + serviceInterface.getSimpleName();
			} else {
				// use generated classes directly (no dynamic proxies)
				wsServiceClass = Class.forName(serviceName + "Service");
				portName = "get" + serviceInterface.getSimpleName() + "Port";
			}
			
			Service clientService = (Service) wsServiceClass.newInstance();
			
			// give potential subclasses the chance to adapt the service
			adaptProxyService(clientService);
			
			Method portGetter = wsServiceClass.getMethod(portName);
			Object port = portGetter.invoke(clientService);

			// overwrite endpoint address
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> context = bindingProvider.getRequestContext();
			
			context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, generateUrl(proxyBean));
			
			createdProxy = port;
		} catch (Exception e) {
			CoreNotificationHelper.notifyMisconfiguration(
				"Could not create JAX-WS binding for "
				+ serviceInterface, e);
		}
		return createdProxy;
	}
	
	/**
	 * Does this protocol handle context passing on its own? Yes.
	 * @return Whether this protocol handles the context (<code>true</code>)
	 */
	public boolean getProtocolSpecificContextPassing() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
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

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Class getExporterObjectType() {
		return SpringBinding.class;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Class getProxyObjectType() {
		return SEIStub.class;
	}

	/**
	 * Method providing an extension point do adapt the proxy service.
	 * @param service The service
	 */
	protected void adaptProxyService(Service service) {
		if (CollectionUtils.isNotEmpty(handlers)) {
			service.setHandlerResolver(new HandlerResolver() {
				@SuppressWarnings("unchecked")
				public List<Handler> getHandlerChain(PortInfo portInfo) {
					List<Handler> list = new ArrayList<Handler>(handlers.size());
					list.addAll(handlers);
					return list;
				}
			});
		}
	}

	/**
	 * Method providing an extension point do adapt the exporter service.
	 * @param service The service
	 */
	protected void adaptExporterService(SpringService service) {
		if (CollectionUtils.isNotEmpty(handlers)) {
			service.setHandlers(handlers);
		}
	}
	
	/**
	 * @return Returns the JaxWs handlers.
	 * @since 1.7
	 */
	@SuppressWarnings("unchecked")
	public List<Handler> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers Are the JaxWs handlers to set.
	 * @since 1.7
	 */
	@SuppressWarnings("unchecked")
	public void setHandlers(List<Handler> handlers) {
		this.handlers = handlers;
	}
}
