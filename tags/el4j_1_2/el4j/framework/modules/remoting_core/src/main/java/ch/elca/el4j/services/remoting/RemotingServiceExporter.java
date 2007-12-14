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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;

/**
 * This class is the global remote service exporter bean.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class RemotingServiceExporter extends AbstractRemotingBase implements
        FactoryBean, BeanNameAware, ApplicationContextAware {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(RemotingServiceExporter.class);

    /**
     * This is the application context, which was used to create this bean.
     */
    private ApplicationContext m_parentApplicationContext;

    /**
     * This the name of this bean. This is used in the url mapping.
     */
    private String m_beanName;

    /**
     * This is the internally created exporter bean.
     */
    private Object m_exporterBean;

    /**
     * The cached enriched service interface.
     */
    private Class m_serviceInterfaceWithContext;

    /**
     * Name of the service bean.
     */
    private String m_service;
    
    /**
     * Whether the objects returned by the factory are singletons. 
     */
    private boolean m_singleton = true;

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                getService(), "service", this);
        getRemoteProtocol().checkRemotingExporter(this);
    }

    /**
     * Lazily enriches the service's interface, adding the implicit context
     * passing stuff.
     * 
     * @param serviceInterface
     *      The interface to enrich.
     * 
     * @return Returns the enriched class.
     */
    public Class getServiceInterfaceWithContext(Class serviceInterface) {
        if (m_serviceInterfaceWithContext == null) {
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            
            /**
             * Create the service interface to be able to send context
             * information.
             */
            InterfaceEnricher interfaceIndirector = new InterfaceEnricher();
            EnrichmentDecorator interfaceDecorator 
                = new ContextEnrichmentDecorator();
            m_serviceInterfaceWithContext = interfaceIndirector
                .createShadowInterfaceAndLoadItDirectly(
                    serviceInterface, interfaceDecorator, cl);
        }
        
        return m_serviceInterfaceWithContext;
    }
    
    /**
     * Creates a fresh exporter bean wraps a potentially enriched service
     * interface.
     * 
     * @return Returns the exporter bean.
     */
    protected Object getFreshExporterBean() {
        
        Object exporterBean;
        
        boolean useImplicitContextPassing = getRemoteProtocol()
            .getImplicitContextPassingRegistry() != null;
        
        /**
         * Get service from application context. This allows to delegate
         * lifecycle handling. 
         */
        Object service = getApplicationContext().getBean(getService());
        
        if (useImplicitContextPassing && !getRemoteProtocol()
            .getProtocolSpecificContextPassing()) {
            s_logger.info("Implicit context passing in enabled.");
            
            /**
             * Get the context class loader from current thread.
             */
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            
            Class serviceInterfaceWithContext
                = getServiceInterfaceWithContext(getServiceInterface());
            
            /**
             * Wrap the service implementation with the generated interface to
             * be able to extract context information.
             */
            ServerContextInvocationHandler invocationHandler 
                = new ServerContextInvocationHandler(
                    service, getServiceInterface(),
                    getRemoteProtocol().getImplicitContextPassingRegistry());
            Object serviceProxy = Proxy.newProxyInstance(cl,
                    new Class[] {serviceInterfaceWithContext},
                    invocationHandler);
            
            /**
             * Create the exporter servlet.
             */
            exporterBean = getRemoteProtocol().createExporterBean(this,
                    serviceInterfaceWithContext, serviceProxy);
        } else {
            if (!getRemoteProtocol().getProtocolSpecificContextPassing()) {
                s_logger.warn("Implicit context passing in disabled.");
            } else {
                s_logger.info(
                    "Protocol Specific implicit context passing in enabled.");
            }
            
            exporterBean = getRemoteProtocol().createExporterBean(this,
                    getServiceInterface(), service);
        }
        
        /**
         * Prepare exporter dependent beans.
         */
        getRemoteProtocol().prepareExporterDependentBeans(this);
        
        return exporterBean;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        Object exporterBean;
        
        if (isSingleton()) {
            if (m_exporterBean == null) {
                m_exporterBean = getFreshExporterBean();
            }
            exporterBean = m_exporterBean;
            
        } else {
            s_logger.info("I'm a prototype");
            exporterBean = getFreshExporterBean();
        }

        /**
         * Finalize exporter dependent beans before returning the exporter.
         */
        getRemoteProtocol().finalizeExporterDependentBeans(this);

        return exporterBean;
    }

    /**
     * {@inheritDoc}
     */
    public Class getObjectType() {
        return getRemoteProtocol().getExporterObjectType();
    }

    /**
     * Sets whether the objects returned by the factory are singltons or
     * prototypes. Default is singletons.
     * 
     * @param singleton <code>true</code> for returning singletons.
     */
    public void setSingleton(boolean singleton) {
        m_singleton = singleton;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return m_singleton;
    }

    /**
     * @return Returns the beanName.
     */
    public String getBeanName() {
        return m_beanName;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(String name) {
        m_beanName = name;
    }

    /**
     * @return Returns the applicationContext.
     */
    public ApplicationContext getApplicationContext() {
        return m_parentApplicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        m_parentApplicationContext = applicationContext;
    }

    /**
     * @return Returns the service's bean name.
     */
    public String getService() {
        return m_service;
    }

    /**
     * Sets the service's bean name.
     * 
     * @param service The bean name to set.
     */
    public void setService(String service) {
        m_service = service;
    }
}
