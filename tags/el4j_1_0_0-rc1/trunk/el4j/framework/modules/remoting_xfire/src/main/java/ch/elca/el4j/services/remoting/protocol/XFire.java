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

import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
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
 * This class implements all needed things for the burlap protocol.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
public class XFire extends AbstractInetSocketAddressWebProtocol {
    
    /**
     * Needed suffix to get the wsdl of the given service.
     */
    private static final String URL_WSDL_SUFFIX = "?wsdl";
    
    public ServiceFactory serviceFactory;
    public org.codehaus.xfire.XFire xfire;
    
    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceOptionallyWithContext) {     
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues proxyProps = new MutablePropertyValues();
        proxyProps.addPropertyValue("serviceClass",
            serviceInterfaceOptionallyWithContext);
        proxyProps.addPropertyValue("wsdlDocumentUrl", 
            generateUrl(proxyBean));
        appContext.registerSingleton("xFireProxyBeanGen",
                getProxyObjectType(), proxyProps);
        
        return appContext.getBean("xFireProxyBeanGen");
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
        props.addPropertyValue("serviceFactory", serviceFactory);
        props.addPropertyValue("xfire", xfire);
        props.addPropertyValue("serviceBean", serviceProxy);

        // In the case of a JaxBFactory the serviceClass property should not be
        // set, because of SOAPBinding problems.
        if (!serviceFactory.getClass().equals(JaxbServiceFactory.class)) {
            props.addPropertyValue("serviceClass", 
                serviceInterfaceOptionallyWithContext);
        }

        // register the XFireExporter bean with the appContext
        appContext.registerSingleton("xFireExporterBeanGen",
                getExporterObjectType(), props);
        
        // URL Mapping for the service is added and registered 
        // with the appContext
        props = new MutablePropertyValues();
        HashMap hm = new HashMap();
        hm.put(exporterBean.getServiceName(),
            appContext.getBean("xFireExporterBeanGen"));
        props.addPropertyValue("urlMap", hm);
        appContext.registerSingleton("anonymous",
            SimpleUrlHandlerMapping.class, props);
        
        return appContext.getBean("xFireExporterBeanGen");
    }

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

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public org.codehaus.xfire.XFire getXfire() {
        return xfire;
    }

    public void setXfire(org.codehaus.xfire.XFire xfire) {
        this.xfire = xfire;
    }


}
