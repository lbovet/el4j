/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.remoting;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This abstract class defines a base for remote protocols. It contains
 * basically the host name and port number.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractRemotingProtocol implements
        ApplicationContextAware, InitializingBean {
    /**
     * With this ApplicationContext the current bean has been created.
     */
    protected ApplicationContext m_parentApplicationContext;

    /**
     * This is the registry to implicit pass the context.
     */
    private ImplicitContextPassingRegistry m_implicitContextPassingRegistry;

    /**
     * This member contains protocol specific configuration. This will only be 
     * used if it is really necessary.
     */
    private ProtocolSpecificConfiguration m_protocolSpecificConfiguration;

    /**
     * @return Returns the implicitContextPassingRegistry.
     */
    public ImplicitContextPassingRegistry getImplicitContextPassingRegistry() {
        return m_implicitContextPassingRegistry;
    }

    /**
     * @param implicitContextPassingRegistry
     *            The implicitContextPassingRegistry to set.
     */
    public void setImplicitContextPassingRegistry(
            ImplicitContextPassingRegistry implicitContextPassingRegistry) {
        m_implicitContextPassingRegistry = implicitContextPassingRegistry;
    }

    /**
     * @return Returns the protocolSpecificConfiguration.
     */
    public ProtocolSpecificConfiguration getProtocolSpecificConfiguration() {
        return m_protocolSpecificConfiguration;
    }

    /**
     * @param protocolSpecificConfiguration
     *            The protocolSpecificConfiguration to set.
     */
    public void setProtocolSpecificConfiguration(
        ProtocolSpecificConfiguration protocolSpecificConfiguration) {
        m_protocolSpecificConfiguration = protocolSpecificConfiguration;
    }
    
    /**
     * Method to create the proxy bean.
     * 
     * @param proxyBean
     *            Is the bean where the method gets information about the proxy
     *            bean.
     * @param serviceInterfaceWithContext
     *            Is the modified interface.
     * @return Returns the created proxy bean.
     */
    public abstract Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceWithContext);

    /**
     * Method to create the exporter bean.
     * 
     * @param exporterBean
     *            Is the bean where the method gets information about the
     *            exporter bean.
     * @param serviceInterfaceWithContext
     *            Is the modified interface.
     * @param serviceProxy
     *            Is the bean which has to wrapped.
     * @return Returns the generated exporter bean.
     */
    public abstract Object createExporterBean(
            RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy);

    /**
     * Method to get the class type of the proxy object.
     * 
     * @return Returns the class type.
     */
    public abstract Class getProxyObjectType();

    /**
     * Method to get the class type of the exporter object.
     * 
     * @return Returns the class type.
     */
    public abstract Class getExporterObjectType();

    /**
     * This method will be called to preinstantiate beans, which depends on the
     * exporter bean.
     * 
     * @param exporterBean
     *            Is the reference to the dependent bean.
     */
    public void prepareExporterDependentBeans(
            RemotingServiceExporter exporterBean) {
        // Per default, do nothing.
    }

    /**
     * This method will be used to finalize the preinstantiated beans, which
     * depends on the exporter bean.
     * 
     * @param exporterBean
     *            Is the reference to the dependent bean.
     */
    public void finalizeExporterDependentBeans(
            RemotingServiceExporter exporterBean) {
        // Per default, do nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        m_parentApplicationContext = applicationContext;
    }
    
    /**
     * Defines the interface implemented by the Proxy that wraps the  enriched
     * service interface. Subclasses can override this method to supply protocol
     * specific interfaces.
     * 
     * @param serviceInterface The service interface to wrap.
     * @return Returns the interfaces implemented by the proxy.
     */
    public Class[] getProxyInterface(Class serviceInterface) {
        return new Class[] {serviceInterface};
    }
    
    /**
     * Creates a invocation handler used on the client proxy. Subclasses can
     * override this method to supply protocol specific behaviour.
     * 
     * @param innerProxyBean The inner proxy to wrap.
     * @param serviceInterfaceWithContext The inner proxy's interface.
     * @return Returns an invocation handler for the given inner proxy and its
     *      interface.
     */
    public ClientContextInvocationHandler getClientContextInvocationHandler(
            Object innerProxyBean, Class serviceInterfaceWithContext) {
        
        return new ClientContextInvocationHandler(
                innerProxyBean, serviceInterfaceWithContext,
                getImplicitContextPassingRegistry());
    }
    
    /**
     * Checks whether the service exporter is configured properly to be used
     * with this protocol. Does nothing by default.
     * Subclasses may override this behaviour. 
     * 
     * @param serviceExporter
     *      The remoting service exporter that is using this protocol.
     *      
     * @throws Exception
     *      Whenever something goes wrong.
     */
    public void checkRemotingExporter(RemotingServiceExporter serviceExporter)
        throws Exception {
        // do nothing
    }
    
    /**
     * Checks whether the proxy factory is configured properly to be used
     * with this protocol. Does nothing by default.
     * Subclasses may override this behaviour. 
     * 
     * @param proxyFactory
     *      The remoting proxy factory that is using this protocol.
     *      
     * @throws Exception
     *      Whenever something goes wrong.
     */
    public void checkRemotingProxy(RemotingProxyFactoryBean proxyFactory) 
        throws Exception {
        // do nothing
    }
}