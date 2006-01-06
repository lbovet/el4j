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

package ch.elca.el4j.services.remoting.protocol.soap.axis;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.remoting.jaxrpc.JaxRpcPortProxyFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.protocol.Soap;
import ch.elca.el4j.services.remoting.protocol.soap.axis.encoding.TypeMapping;
import ch.elca.el4j.util.codingsupport.CollectionUtils;

/**
 * This class is the proxy factory for the client side.
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
public class AxisPortProxyFactoryBean extends JaxRpcPortProxyFactoryBean 
    implements BeanFactoryAware {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AxisPortProxyFactoryBean.class);

    /**
     * Type mapping which must be registered.
     */
    private List m_typeMappings;
    
    /**
     * This is the encoding style uri to register the type mappings.
     */
    private String m_encodingStyleUri;
    
    /**
     * Flag to indicate if the type mapping is the default.
     */
    private boolean m_defaultTypeMapping;
    
    /**
     * Implicit context passing registry which should use the soap header for 
     * implicit context passing.
     */
    private ImplicitContextPassingRegistry 
        m_soapHeaderImplicitContextPassingRegistry;    

    /**
     * Flag to indicate if the top element of implicit context passing should be
     * ignored. Default is set to <code>false</code>.
     */
    private boolean m_pruneTopNodeImplicitContextPassing = false;

    /**
     * The bean factory this bean was created.
     */
    private BeanFactory m_beanFactory;
    
    /**
     * {@inheritDoc}
     */
    protected void postProcessJaxRpcService(Service service) {
        registerTypeMappings(service);
        registerSoapHeaderImplicitContextPassingRegistry(service);
    }

    /**
     * Method to register the soap header implicit context passing registry
     * handler.
     * 
     * @param service
     *            Is the service where to register the handler.
     */
    private void registerSoapHeaderImplicitContextPassingRegistry(
        Service service) {
        retrieveSoapHeaderImplicitContextPassingRegistry();
        
        if (m_soapHeaderImplicitContextPassingRegistry != null) {
            QName port = new QName(this.getNamespaceUri(), this.getPortName());
            List list = service.getHandlerRegistry().getHandlerChain(port);
            
            Map config = new HashMap();
            config.put(SoapHeaderImplicitContextPassingHandler.REGISTRY,
                m_soapHeaderImplicitContextPassingRegistry);
            config.put(SoapHeaderImplicitContextPassingHandler
                .PRUNE_TOP_NODE_IMPLICIT_CONTEXT_PASSING,
                    Boolean.valueOf(m_pruneTopNodeImplicitContextPassing));
            
            list.add(new HandlerInfo(
                SoapHeaderImplicitContextPassingHandler.class, config, null));
        }
    }

    /**
     * Method to register type mappings.
     * 
     * @param service
     *            Is the service where to register the type mappings.
     */
    private void registerTypeMappings(Service service) {
        retrieveTypeMappings();
        
        /**
         * Register every type mapping.
         */
        if (m_typeMappings != null && m_typeMappings.size() > 0) {
            TypeMappingRegistry registry = service.getTypeMappingRegistry();
            Iterator itTypeMappings = m_typeMappings.iterator();
            while (itTypeMappings.hasNext()) {
                TypeMapping tm = (TypeMapping) itTypeMappings.next();
                Class serializerFactory 
                    = tm.getSerializerFactory();
                Class deserializerFactory 
                    = tm.getDeserializerFactory();
                String encodingStyle = tm.getEncodingStyle();
                String namespaceUri = tm.getNamespaceUri();
                Iterator itTypesMappingTypes = tm.getTypes().iterator();
                while (itTypesMappingTypes.hasNext()) {
                    String typeName = (String) itTypesMappingTypes.next();
                    registerTypeMapping(registry, typeName, serializerFactory,
                        deserializerFactory, encodingStyle, namespaceUri);
                }
            }
        }
    }
    
    /**
     * Method to register a type mapping.
     * 
     * @param registry
     *            Where the type mapping has to be registered.
     * @param typeName
     *            Is the name of the type the mapping is for.
     * @param serializerFactory
     *            Is the factory for serialization.
     * @param deserializerFactory
     *            Is the factory for deserialization.
     * @param encodingStyle
     *            Is the encoding style of the type mapping.
     * @param namespaceUri
     *            Is the namespace uri specially used for this type mapping.
     */
    private void registerTypeMapping(TypeMappingRegistry registry,
        String typeName, Class serializerFactory,
        Class deserializerFactory, String encodingStyle, String namespaceUri) {
        /**
         * Get class loader form current thread.
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        /**
         * Get <code>java.lang.Class</code> of type which must be mapped.
         */
        Class type = null;
        try {
            type = cl.loadClass(typeName);
        } catch (ClassNotFoundException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "Class of type {0} could not be "
                    + "loaded.", new Object[] {typeName});
        }
        
        /**
         * Create qualified name for mapping.
         */
        String targetNamespace = StringUtils.hasText(namespaceUri) 
            ? namespaceUri : getNamespaceUri(); 
        QName qName = new QName(targetNamespace, ClassUtils.getShortName(type));
        
        /**
         * Create an instance of de- and serializer factory.
         */
        SerializerFactory serializerFactoryInstance 
            = (SerializerFactory) createNewFactory(
                serializerFactory, type, qName);
        DeserializerFactory deserializerFactoryInstance 
            = (DeserializerFactory) createNewFactory(
                deserializerFactory, type, qName);
        
        /**
         * Create a jaxrpc type mapping and fill it with the needed stuff.
         */
        javax.xml.rpc.encoding.TypeMapping mapping 
            = registry.createTypeMapping();
        mapping.register(type, qName, serializerFactoryInstance, 
            deserializerFactoryInstance);
        
        /**
         * Register the jaxrpc mapping on the jaxrpc type mapping registry.
         */
        String encodingStyleUri = getEncodingStyleUri();
        if (StringUtils.hasText(encodingStyleUri)) {
            registry.register(encodingStyleUri, mapping);
        } else {
            registry.register("", mapping);
        }
        if (isDefaultTypeMapping()) {
            registry.registerDefault(mapping);
        }
    }
    
    /**
     * Method to create a factory instance.
     * 
     * @param factory
     *            Is the factory of which an instance is needed.
     * @param type
     *            Is the type which has to be mapped.
     * @param qName
     *            Is the qualified name for this mapping.
     * @return Returns the created instance of the factory.
     */
    private Object createNewFactory(Class factory, Class type, QName qName) {
        Object instance = null;
        try {
            Constructor c = factory.getConstructor(
                new Class[] {Class.class, QName.class});
            instance = c.newInstance(new Object[] {type, qName});
            s_logger.info("Factory '" + factory.getName() + "' could be "
                + "instantiated successfully with constructor "
                + "(java.lang.Class, javax.xml.namespace.QName).");
        } catch (NoSuchMethodException e) {
            // try next constructor
            s_logger.info("Constructor (java.lang.Class, "
                + "javax.xml.namespace.QName) does not exist in factory '" 
                + factory.getName() + "'.");
        } catch (Exception e) {
            throw new BaseRTException("There was a problem while executing " 
                + "constructor (java.lang.Class, javax.xml.namespace.QName) "
                + "of factory {0}.", new Object[] {factory.getName()});
        }

        if (instance == null) {
            try {
                Constructor c = factory.getConstructor(
                    new Class[] {QName.class});
                instance = c.newInstance(new Object[] {qName});
                s_logger.info("Factory '" + factory.getName() + "' could be "
                    + "instantiated successfully with constructor "
                    + "(javax.xml.namespace.QName).");
            } catch (NoSuchMethodException e) {
                // try next constructor
                s_logger.info("Constructor (javax.xml.namespace.QName) does "
                    + "not exist in factory '" + factory.getName() + "' too.");
            } catch (Exception e) {
                throw new BaseRTException("There was a problem while executing" 
                    + " constructor (javax.xml.namespace.QName) of "
                    + "factory {0}.", new Object[] {factory.getName()});
            }
        }

        if (instance == null) {
            try {
                instance = factory.newInstance();
                s_logger.info("Factory '" + factory.getName() + "' could be "
                    + "instantiated successfully with default constructor.");
            } catch (Exception e) {
                s_logger.error("Factory '" + factory.getName() 
                    + "' could not be instantiated.");
                throw new BaseRTException("There was a problem while executing" 
                    + " the default constructor of factory {0}.", 
                    new Object[] {factory.getName()});
            }
        }
        
        return instance;
    }

    /**
     * @return Returns the typeMappings.
     */
    public List getTypeMappings() {
        return m_typeMappings;
    }

    /**
     * @param typeMappings
     *            The typeMappings to set.
     */
    public void setTypeMappings(List typeMappings) {
        m_typeMappings = typeMappings;
    }
    
    /**
     * Method to retrieve type mapping list from bean factory.
     */
    public void retrieveTypeMappings() {
        if (m_typeMappings != null) {
            return;
        }
        m_typeMappings = (List) 
            m_beanFactory.getBean(Soap.PROXY_TYPE_MAPPING_BEAN_NAME);
        
        if (CollectionUtils.isEmpty(m_typeMappings)) {
            s_logger.info("No type mappings are registered.");
        }
    }
    
    /**
     * @return Returns the encodingStyleUri.
     */
    public String getEncodingStyleUri() {
        return m_encodingStyleUri;
    }

    /**
     * @param encodingStyleUri
     *            The encodingStyleUri to set.
     */
    public void setEncodingStyleUri(String encodingStyleUri) {
        m_encodingStyleUri = encodingStyleUri;
    }

    /**
     * @return Returns the defaultTypeMapping.
     */
    public boolean isDefaultTypeMapping() {
        return m_defaultTypeMapping;
    }

    /**
     * @param defaultTypeMapping
     *            The defaultTypeMapping to set.
     */
    public void setDefaultTypeMapping(boolean defaultTypeMapping) {
        m_defaultTypeMapping = defaultTypeMapping;
    }

    /**
     * @return Returns the soapHeaderImplicitContextPassingRegistry.
     */
    public ImplicitContextPassingRegistry 
    getSoapHeaderImplicitContextPassingRegistry() {
        return m_soapHeaderImplicitContextPassingRegistry;
    }

    /**
     * @param soapHeaderImplicitContextPassingRegistry
     *            The soapHeaderImplicitContextPassingRegistry to set.
     */
    public void setSoapHeaderImplicitContextPassingRegistry(
        ImplicitContextPassingRegistry 
        soapHeaderImplicitContextPassingRegistry) {
        m_soapHeaderImplicitContextPassingRegistry 
            = soapHeaderImplicitContextPassingRegistry;
    }
    
    /**
     * Method to retrieve type mapping list from bean factory.
     */
    public void retrieveSoapHeaderImplicitContextPassingRegistry() {
        if (m_soapHeaderImplicitContextPassingRegistry != null
            || !m_beanFactory.containsBean(Soap
                .PROXY_SOAP_HEADER_ICP_REGISTRY_BEAN_NAME)) {
            return;
        }
        m_soapHeaderImplicitContextPassingRegistry 
            = (ImplicitContextPassingRegistry) m_beanFactory.getBean(Soap
                .PROXY_SOAP_HEADER_ICP_REGISTRY_BEAN_NAME);
        
        if (m_soapHeaderImplicitContextPassingRegistry == null) {
            s_logger.info(
                "No soap header implicit context passing registry registered.");
        }
    }
    
    /**
     * @return Returns the pruneTopNodeImplicitContextPassing.
     */
    public boolean isPruneTopNodeImplicitContextPassing() {
        return m_pruneTopNodeImplicitContextPassing;
    }

    /**
     * @param pruneTopNodeImplicitContextPassing
     *            The pruneTopNodeImplicitContextPassing to set.
     */
    public void setPruneTopNodeImplicitContextPassing(
        boolean pruneTopNodeImplicitContextPassing) {
        m_pruneTopNodeImplicitContextPassing 
            = pruneTopNodeImplicitContextPassing;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        m_beanFactory = beanFactory;
    }
}
