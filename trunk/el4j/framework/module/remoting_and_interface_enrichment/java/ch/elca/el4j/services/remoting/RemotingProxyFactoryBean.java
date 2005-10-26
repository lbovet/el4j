/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;

/**
 * This class is the global remote proxy bean.
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
public class RemotingProxyFactoryBean extends AbstractRemotingBase implements
        FactoryBean {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(RemotingProxyFactoryBean.class);

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
        
        if (useImplicitContextPassing) {
            s_logger.info("Implicit context passing in enabled.");
            
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
            s_logger.warn("Implicit context passing in disabled.");
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