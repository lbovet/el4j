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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.spring.remoting.XFireClientFactoryBean;
import org.codehaus.xfire.spring.remoting.XFireExporter;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;


/**
 * This class implements all needed things for the xfire protocol.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 * @author Philippe Jacot (PJA)
 */
public class XFire extends AbstractInetSocketAddressWebProtocol {   
    /**
     * Needed suffix to get the wsdl of the given service.
     */
    private static final String URL_WSDL_SUFFIX = "?wsdl";
    
    /**
     * Name for the created exporter bean.
     */
    private static final String EXPORTER_BEAN_NAME = "xFireExporterBeanGen";
    
    /**
     * Name for the created proxy bean.
     */
    private static final String PROXY_BEAN_NAME = "xFireProxyBeanGen";
    
    /**
     * The Service Factory to work with.
     */
    private ServiceFactory m_serviceFactory;
    
    /**
     * The used XFire instance.
     */
    private org.codehaus.xfire.XFire m_xfire;
    
    /**
     * Properties for the XFire service.
     */
    private Map<String, Object> m_serviceProperties;
    
    /**
     * The used WSDL file. If none is provided a generated one is taken
     */
    private String m_wsdlDocumentUrl = "";

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return XFireClientFactoryBean.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return XFireExporter.class;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
       Class serviceInterfaceOptionallyWithContext, Object serviceProxy) {
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
      
        MutablePropertyValues props = new MutablePropertyValues();
        //setting the properties of the XFireExporter
        props.addPropertyValue("serviceFactory", getServiceFactory());
        props.addPropertyValue("xfire", getXfire());
        props.addPropertyValue("serviceBean", serviceProxy);
        if (getServiceProperties() != null) {
            props.addPropertyValue("properties", getServiceProperties());
        }
        

        
        // JSR 181 annotated classes do not have to specify the serviceClass
        // property as the implementing class already has its serviceClass
        // given by the endpointInterface attribute of the @WebService 
        // annotation. For the moment it is assumed that all JSR 181 processing
        // ServiceFactories are subclasses of AnnotationServiceFactory which
        // not always has to be true.
        if (!(getServiceFactory() instanceof AnnotationServiceFactory)) {
            props.addPropertyValue("serviceClass", 
                serviceInterfaceOptionallyWithContext);
        }

        
        // Give potential Subclasses the chance to adapt the properties
        adaptExporterProperties(props);
        
        // register the XFireExporter bean with the appContext
        appContext.registerSingleton(EXPORTER_BEAN_NAME,
                getExporterObjectType(), props);
               
        
        // URL Mapping for the service is added and registered 
        // with the appContext
        props = new MutablePropertyValues();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put(exporterBean.getServiceName(),
            appContext.getBean(EXPORTER_BEAN_NAME));
        props.addPropertyValue("urlMap", hm);
        appContext.registerSingleton("anonymous",
            SimpleUrlHandlerMapping.class, props);
        
        return appContext.getBean(EXPORTER_BEAN_NAME);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceOptionallyWithContext) {     
        
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues proxyProps = new MutablePropertyValues();
        proxyProps.addPropertyValue(
            "serviceClass", serviceInterfaceOptionallyWithContext);
        
        String wsdlUrl = StringUtils.isNotBlank(getWsdlDocumentUrl()) 
            ? getWsdlDocumentUrl() : generateUrl(proxyBean);
        
        proxyProps.addPropertyValue("wsdlDocumentUrl", wsdlUrl);
        proxyProps.addPropertyValue("serviceFactory", getServiceFactory());
        if (getServiceProperties() != null) {
            proxyProps.addPropertyValue("properties", getServiceProperties());
        }
        
        // Pass properties to possible subclasses
        adaptProxyProperties(proxyProps);
        
        appContext.registerSingleton(PROXY_BEAN_NAME,
            getProxyObjectType(), proxyProps);

        
        return appContext.getBean(PROXY_BEAN_NAME);
    }
    
    /**
     * Method providing an extension point do adapt the proxy properites.
     * @param properties The properties so far
     */
    protected void adaptProxyProperties(
        MutablePropertyValues properties) {
    }
    
    /**
     * Method providing an extension point do adapt the exported properites.
     * @param properties The properties so far
     */
    protected void adaptExporterProperties(
        MutablePropertyValues properties) {
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
        sb.append(URL_WSDL_SUFFIX);
        return sb.toString();
    }
    
    /**
     * Get the {@link ServiceFactory}.
     * @return The used {@link ServiceFactory}
     */
    public ServiceFactory getServiceFactory() {
        return m_serviceFactory;
    }

    /**
     * Set the {@link ServiceFactory}.
     * @param serviceFactory The {@link ServiceFactory}
     */
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.m_serviceFactory = serviceFactory;
    }

    /**
     * Get the {@link org.codehaus.xfire.XFire} instance.
     * @return The {@link org.codehaus.xfire.XFire} instance.
     */
    public org.codehaus.xfire.XFire getXfire() {
        return m_xfire;
    }

    /**
     * Set the {@link org.codehaus.xfire.XFire} instance.
     * @param xfire The {@link org.codehaus.xfire.XFire} instance.
     */
    public void setXfire(org.codehaus.xfire.XFire xfire) {
        this.m_xfire = xfire;
    }
    
    /**
     * Set the wsdl used by the client.
     * @param wsdlDocumentUrl The wsdl address to set
     */
    public void setWsdlDocumentUrl(String wsdlDocumentUrl) {
        m_wsdlDocumentUrl = wsdlDocumentUrl;
    }
    
    /**
     * Get the wsdl used by the client.
     * @return The wsdl document URL
     */
    public String getWsdlDocumentUrl() {
        return m_wsdlDocumentUrl;
    }
    
    /**
     * Get the properties to be set to the service.
     * @return A {@link java.util.Map} of properties
     */
    public Map<String, Object> getServiceProperties() {
        return m_serviceProperties;
    }
    
    /**
     * Set the properties to be set to the service.
     * @param serviceProperties A {@link java.util.Map} of properties
     */
    public void setServiceProperties(Map<String, Object> serviceProperties) {
        m_serviceProperties = serviceProperties;
    }
}
