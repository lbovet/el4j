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

package ch.elca.el4j.services.remoting.protocol;

import java.util.List;
import java.util.Properties;

import javax.ejb.EJBObject;
import javax.naming.Context;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.ClientContextInvocationHandler;
import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbClientContextInvocationHandler;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbServiceExporter;
import ch.elca.el4j.services.remoting.protocol.ejb.RemoteStatefulSessionProxyFactoryBean;
import ch.elca.el4j.services.remoting.protocol.ejb.RemoteStatelessSessionProxyFactoryBean;

/**
 * This class implements the protocol to access an EJB transparently.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Nicolas Schiper (NSC)
 * @author Andreas Bur (ABU)
 */
public class Ejb extends AbstractInetSocketAddressProtocol 
        implements InitializingBean {

    /** The URL prefix's property key. */
    public static final String JNDI_URL_PREFIX = "ch.elca.el4j.jndi.prefix";
    
    /** A properties file with the JNDI properties. */
    private Properties m_jndiEnvironment;
    
    /**
     * @return Returns the properties used to setup JNDI.
     */
    public Properties getJndiEnvironment() {
        return m_jndiEnvironment;
    }

    /**
     * Sets the properties used to setup JNDI.
     * 
     * @param jndiEnvironment
     *      The JNDI properties to use.
     */
    public void setJndiEnvironment(Properties jndiEnvironment) {
        m_jndiEnvironment = jndiEnvironment;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_jndiEnvironment, "jndiEnvironment", this);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceWithContext) {

        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues props = new MutablePropertyValues();
        props.addPropertyValue("jndiName", proxyBean.getServiceName());
        props.addPropertyValue("businessInterface",
                serviceInterfaceWithContext);

        m_jndiEnvironment.put(Context.PROVIDER_URL, generateUrl(proxyBean));
        props.addPropertyValue("jndiEnvironment", m_jndiEnvironment);
        
        EjbConfigurationObject vo = (EjbConfigurationObject) 
            proxyBean.getProtocolSpecificConfiguration();
        
        if (isStateful(proxyBean)) {
            props.addPropertyValue("createMethodSet",
                    Boolean.valueOf(StringUtils.hasText(vo.getCreate())));
            
            // set arguments for create(Object[]) EJB method
            List argList = vo.getCreateArgument();
            Object[] args = (Object[]) 
                argList.toArray(new Object[argList.size()]);
            props.addPropertyValue("createArgument", args);
            
            appContext.registerSingleton("ejbProxyBeanGen",
                    getStatefulProxyObjectType(), props);
            
        } else {
            appContext.registerSingleton("ejbProxyBeanGen",
                    getProxyObjectType(), props);
        }
        return appContext.getBean("ejbProxyBeanGen");
    }

    /**
     * Returns whether the EJB session bean is configured to be stateful or
     * steteless.
     * 
     * @param remotingBase
     *      The remoting bean factory or the remoting service exporter used to
     *      request or expose a bean, respectively.
     *      
     * @return Returns <code>true</code> if the bean is exposed as a stateful
     *      session bean, <code>false</code> if it's stateless.
     */
    private boolean isStateful(AbstractRemotingBase remotingBase) {
        EjbConfigurationObject valueObj = (EjbConfigurationObject)
                remotingBase.getProtocolSpecificConfiguration();
        return StringUtils.hasText(valueObj.getCreate());
    }
    
   /**
    * {@inheritDoc}
    */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy) {
        
        StaticApplicationContext appContext 
            = new StaticApplicationContext(m_parentApplicationContext);
        MutablePropertyValues props = new MutablePropertyValues();
        props.addPropertyValue("innerService", serviceProxy);
        props.addPropertyValue("innerServiceInterface",
                serviceInterfaceWithContext);

        if (isStateful(exporterBean)) {
            appContext.registerPrototype("ejbExporterBeanGen",
                    getExporterObjectType(), props);
        } else {
            appContext.registerPrototype("ejbExporterBeanGen",
                    getExporterObjectType(), props);
        }
        return appContext.getBean("ejbExporterBeanGen");
    }
    
    /**
     * @return Returns the class for the stateful proxy factory.
     */
    public Class getStatefulProxyObjectType() {
        return RemoteStatefulSessionProxyFactoryBean.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return RemoteStatelessSessionProxyFactoryBean.class;
    }


    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return EjbServiceExporter.class;
    }

    /**
     *
     * {@inheritDoc}
     */
    public String generateUrl(AbstractRemotingBase remoteManager) {

        StringBuffer buffer = new StringBuffer(
                m_jndiEnvironment.getProperty(JNDI_URL_PREFIX));
        buffer.append(getServiceHost());
        buffer.append(":");
        buffer.append(getServicePort());
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public Class[] getProxyInterface(Class serviceInterface) {
        return new Class[] {serviceInterface, EJBObject.class};
    }

    /**
     * {@inheritDoc}
     */
    public ClientContextInvocationHandler getClientContextInvocationHandler(
        Object innerProxyBean, Class serviceInterfaceWithContext) {
        
        return new EjbClientContextInvocationHandler(
                innerProxyBean, serviceInterfaceWithContext,
                getImplicitContextPassingRegistry());
    }
    
    /**
     * {@inheritDoc}
     */
    public void checkRemotingExporter(RemotingServiceExporter serviceExporter)
        throws Exception {

        checkFactoryLifestyle(serviceExporter);
        checkConfigurationObject(serviceExporter);
        checkServiceLifestyle(serviceExporter);
    }
    
    /**
     * {@inheritDoc}
     */
    public void checkRemotingProxy(RemotingProxyFactoryBean proxyFactory)
        throws Exception {
        
        checkFactoryLifestyle(proxyFactory);
        checkConfigurationObject(proxyFactory);
    }

    /**
     * Checks whether the lifestyle of the remoting proxy factory or the
     * remoting service exporter is set to <code>prototype</code>.
     * 
     * @param remotingBase
     *      The remoting base to test. Either a remoting bean factory or
     *      a remoting service exporter.
     *      
     * @throws Exception 
     *      Whenever a configuration error arises.
     */
    private void checkFactoryLifestyle(AbstractRemotingBase remotingBase)
        throws Exception {
        boolean singleton = ((FactoryBean) remotingBase).isSingleton();
        if (singleton) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Wrong lifestyle! Only 'prototype' is allowed.");
        }
    }
    
    /**
     * Checks whether the configuration object is setup properly and if it has
     * the requested type.
     * 
     * @param remotingBase
     *      The remoting base to test. Either a remoting bean factory or
     *      a remoting service exporter.
     *      
     * @throws Exception 
     *      Whenever a configuration error arises.
     */
    private void checkConfigurationObject(AbstractRemotingBase remotingBase)
        throws Exception {
        
        ProtocolSpecificConfiguration configObject
            = remotingBase.getProtocolSpecificConfiguration();
        
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                configObject, "configurationObject", remotingBase);
        if (!(configObject instanceof EjbConfigurationObject)) {
            CoreNotificationHelper.notifyMisconfiguration(
                "The property 'configurationObject' has to be of type "
                    + EjbConfigurationObject.class.getName());
        }
    }
    
    /**
     * Checks whether the service's lifestyle is set appropriate.
     * 
     * <p/>Note: Stateful session beans are allowed to be <code>prototype</code>
     * only whereas stateless beans can have both lifestyles.
     * 
     * @param serviceExporter
     *      The remoting base to test. Either a remoting bean factory or
     *      a remoting service exporter.
     *      
     * @throws Exception 
     *      Whenever a configuration error arises.
     */
    private void checkServiceLifestyle(RemotingServiceExporter serviceExporter) 
        throws Exception {
        
        ApplicationContext ctx = serviceExporter.getApplicationContext();
        
        EjbConfigurationObject configObj = (EjbConfigurationObject)
                serviceExporter.getProtocolSpecificConfiguration();
        
        if (configObj.isStateful()
                && ctx.isSingleton(serviceExporter.getService())) {
            
            CoreNotificationHelper.notifyMisconfiguration(
                    "The bean '" + serviceExporter.getService()
                    + "' used as a stateful session bean lives in a 'singleton'"
                    + "lifestyle. Change it to 'prototype");
        }
    }
}
