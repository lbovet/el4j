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
package ch.elca.el4j.services.remoting.protocol;

import java.lang.reflect.Method;

import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.jvnet.jax_ws_commons.spring.SpringService;

import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.transport.http.servlet.SpringBinding;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.jaxws.JaxwsInvoker;

/**
 * This class implements all needed things for the soap protocol using JAX-WS.
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
public class Jaxws extends AbstractInetSocketAddressWebProtocol {
    
    /**
     * The logger.
     */
    private static Logger s_logger = Logger.getLogger(Jaxws.class);
    
    /**
     * The JAX-WS Binding.
     */
    private SpringBinding m_jaxwsBinding;

    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Object createExporterBean(RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy) {
        
        Object bean = exporterBean.getApplicationContext().getBean(
            exporterBean.getService());
        
        SpringService service = new SpringService();
        service.setBean(bean);
        
        // give potential subclasses the chance to adapt the service
        adaptExporterService(service);
        
        SpringBinding binding = new SpringBinding();
        binding.setUrl(generateUrl(exporterBean));
        try {
            binding.setService(service.getObject());
            m_jaxwsBinding = binding;
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Could not create JAX-WS binding for " + bean, e);
        }
        
        // just return something that can be instantiated.
        // WSSpringServlet will search and register all bindings 
        return new String();
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
            String methodName;
            if (serviceName.endsWith("WS") && serviceName.contains(".gen.")) {
                // use generated classes directly (no dynamic proxies)
                wsServiceClass = Class.forName(serviceName + "Service");
                methodName = "get" + serviceInterface.getSimpleName() + "Port";
            } else {
                // create dynamic proxies
                // therefore the client stubs implement the service interface
                wsServiceClass = Class.forName(
                    serviceInterface.getPackage().getName() + ".gen."
                    + serviceInterface.getSimpleName() + "WSService");
                methodName = "get" + serviceInterface.getSimpleName()
                    + "WSPort";
            }
            
            Service clientService = (Service) wsServiceClass.newInstance();
            
            // give potential subclasses the chance to adapt the service
            adaptProxyService(clientService);
            
            Method getter = wsServiceClass.getMethod(methodName);

            Object bean = getter.invoke(clientService);
            if (serviceInterface.isInstance(bean)) {
                createdProxy = bean;
            } else {
                // create dynamic proxy
                createdProxy = JaxwsInvoker.newInstance(bean, serviceInterface,
                    serviceInterface.getPackage().getName() + ".gen");
            }
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
        // ATTENTION: The complete url is defined manually in the wsdl
        return "/" + remoteBase.getServiceName();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Class getExporterObjectType() {
        return String.class;
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
    }

    /**
     * Method providing an extension point do adapt the exporter service.
     * @param service The service
     */
    protected void adaptExporterService(SpringService service) {
    }

    /**
     * @return Returns the JAX-WS Binding.
     * Used in @see WSSpringServlet
     */
    public SpringBinding getJaxwsBinding() {
        return m_jaxwsBinding;
    }
}
