/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.services.remoting.protocol.soap.axis;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.Constants;
import org.apache.axis.constants.Enum;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.utils.XMLUtils;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class is used to help creating wssd documents for axis deployment.
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
public class WsddHelper {
    /**
     * Flag to indicate if the proxy bean provider is already registered.
     */
    private static boolean s_isProxyBeanProviderRegistered = false;

    /**
     * General document to build wsdd elements.
     */
    private final Document m_doc;

    /**
     * Default constructor. 
     */
    public WsddHelper() {
        try {
            m_doc = XMLUtils.newDocument();
        } catch (ParserConfigurationException e) {
            throw new BaseRTException(
                "No new xml document could be created.", e);
        }
    }
    
    /**
     * Registers the "ProxyBeanProvider" in class WSDDProvider, so it can be 
     * used by following soap services.
     */
    private static synchronized void registerProxyBeanProvider() {
        if (!s_isProxyBeanProviderRegistered) {
            QName providerQName = new QName(
                WSDDConstants.URI_WSDD_JAVA, 
                ProxyBeanProvider.PROVIDER_PROXY_BEAN);
            WSDDProvider.registerProvider(
                providerQName, new WsddProxyBeanProvider());
            s_isProxyBeanProviderRegistered = true;
        }
    }
    
    /**
     * Registers the given proxy. It secures too that the proxy bean provider is
     * registered.
     * 
     * @param proxyInterfaceName 
     *              Is the interface, which the given proxy bean implements.
     * @param proxyBean
     *              Is the proxy bean, which does all the work for the soap 
     *              service.
     */
    public static void registerProxyBean(String proxyInterfaceName, 
        Object proxyBean) {
        registerProxyBeanProvider();
        ProxyBeanProvider.registerProxyBean(proxyInterfaceName, proxyBean);
    }

    /**
     * This method is used to pack a root element into a wsdd document.
     * 
     * @param root 
     *          Is the root element. Normally this is a wsdd deployment element.
     * @return Returns the created wsdd document.
     */
    public WSDDDocument packRootElementInDocument(Element root) {
        try {
            return new WSDDDocument(root);
        } catch (WSDDException e) {
            throw new BaseRTException(
                "Error while creating wsdd elements.", e);
        }
    }
    
    /**
     * Creates root element "deployment" without any children.
     * 
     * @return Returns the created element.
     */
    public Element createElementDeployment() {
        Element e = m_doc.createElementNS(WSDDConstants.URI_WSDD, "deployment");
        e.setAttributeNS(Constants.NS_URI_XMLNS, "xmlns", 
            WSDDConstants.URI_WSDD);
        e.setAttributeNS(Constants.NS_URI_XMLNS, "xmlns:java", 
            WSDDConstants.URI_WSDD_JAVA);
        return e;        
    }

    /**
     * Creates element "service" with its attributes but without its children.
     * Element "service" is a child of element "deployment".
     * 
     * @param name Is the name of the soap service.
     * @param style Is the style of the soap message.
     * @param use Declares the use of the soap message.
     * @return Returns the created element.
     */
    public Element createElementService(String name, Enum style, Enum use) {
        Element e = m_doc.createElementNS(WSDDConstants.URI_WSDD, "service");
        e.setAttribute("name", name);
        e.setAttribute(
            "provider", ProxyBeanProvider.WSDD_PROXY_BEAN_PROVIDER_DEFINITION);
        e.setAttribute("style", style.toString());
        e.setAttribute("use", use.toString());
        return e;
    }
    
    /**
     * Creates element "parameter" with its attributes but without its children.
     * Element "parameter" is a child of element "service".
     * 
     * @param name Is the name of the parameter.
     * @param value Is the value of the parameter. 
     * @return Returns the created element.
     */
    public Element createElementParameter(String name, String value) {
        Element e = m_doc.createElementNS(WSDDConstants.URI_WSDD, "parameter");
        e.setAttribute("name", name);
        e.setAttribute("value", value);
        return e;        
    }
    
    /**
     * Creates element "namespace" with the given string as value.
     * Element "namespace" is a child of element "service".
     * 
     * @param ns Is the given namespace.
     * @return Returns the created element.
     */
    public Element createElementNamespace(String ns) {
        Element e = m_doc.createElementNS(
            WSDDConstants.URI_WSDD, WSDDConstants.ELEM_WSDD_NAMESPACE);
        Text textNode = m_doc.createTextNode(ns);
        e.appendChild(textNode);
        return e;
    }
    
    /**
     * Creates element "typeMapping" with its attributes but without its 
     * children. Element "typeMapping" is a child of element "service".
     * 
     * @param classFullName 
     *              Is the class which has to be mapped.
     * @param serializerFactoryName 
     *              Is the factory to get a serializer for the given type.
     * @param deserializerFactoryName
     *              Is the factory to get a deserializer for the given type.
     * @param targetNamespace
     *              Is the namespace where the given type is used.
     * @param encodingStyle 
     *              Is the used encoding style.
     * @return Returns the created type mapping element.
     */
    public Element createElementTypeMapping(String classFullName, 
        String serializerFactoryName, String deserializerFactoryName, 
        String targetNamespace, String encodingStyle) {
        final String NAMESPACE_TYPE_MAPPING = "ns";
        final String NAMESPACE_JAVA = "java";

        String classShortName = ClassUtils.getShortName(classFullName);
        
        Element e 
            = m_doc.createElementNS(WSDDConstants.URI_WSDD, "typeMapping");
        e.setAttributeNS(Constants.NS_URI_XMLNS, 
            "xmlns:" + NAMESPACE_TYPE_MAPPING, targetNamespace);
        e.setAttribute("qname", NAMESPACE_TYPE_MAPPING + ":" + classShortName);
        e.setAttribute("type", NAMESPACE_JAVA + ":" + classFullName);
        e.setAttribute("serializer", serializerFactoryName);
        e.setAttribute("deserializer", deserializerFactoryName);
        e.setAttribute("encodingStyle", encodingStyle);
        return e;        
    }
}
