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

package ch.elca.el4j.services.remoting.protocol;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.bytecode.ParamNameExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Element;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.ContextEnrichmentDecorator;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.RmiEnrichmentDecorator;
import ch.elca.el4j.services.remoting.protocol.soap.SoapRmiEnrichmentDecorator;
import ch.elca.el4j.services.remoting.protocol.soap.SoapSpecificConfiguration;
import ch.elca.el4j.services.remoting.protocol.soap.axis.AxisPortProxyFactoryBean;
import ch.elca.el4j.services.remoting.protocol.soap.axis.ProxyBeanProvider;
import ch.elca.el4j.services.remoting.protocol.soap.axis.WsddHelper;
import ch.elca.el4j.services.remoting.protocol.soap.axis.encoding.TypeMapping;
import ch.elca.el4j.services.remoting.protocol.soap.axis.faulthandling.ClientSoapInvocationHandler;
import ch.elca.el4j.services.remoting.protocol.soap.axis.faulthandling.ServerSoapInvocationHandler;
import ch.elca.el4j.services.remoting.protocol.soap.axis.faulthandling.SoapExceptionManager;
import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;

/**
 * This class implements all needed things for the soap protocol.
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
public class Soap extends AbstractInetSocketAddressWebProtocol 
    implements ServletContextAware {
    
    /**
     * Is the type mapping name option name of the wsdd file. 
     */
    public static final String OPTION_TYPE_MAPPING_VERSION 
        = "typeMappingVersion";
    
    /**
     * This is the default type mapping version.
     */
    public static final String DEFAULT_TYPE_MAPPING_VERSION = "1.2";

    /**
     * Bean name for generated proxy bean.
     */
    public static final String NEW_PROXY_BEAN_NAME = "soapProxyBeanGen";
    
    /**
     * Bean name for registered type mappings on client side.
     */
    public static final String PROXY_TYPE_MAPPING_BEAN_NAME 
        = NEW_PROXY_BEAN_NAME + "TypeMappings";

    /**
     * Bean name for soap header implicit context passing registry on client
     * side.
     */
    public static final String PROXY_SOAP_HEADER_ICP_REGISTRY_BEAN_NAME 
        = NEW_PROXY_BEAN_NAME + "SoapHeaderImplicitContextPassingRegistry";
    
    /**
     * Needed suffix to get the wsdl of the given service.
     */
    private static final String URL_WSDL_SUFFIX = "?wsdl";
    
    /**
     * Name of servlet, where other soap services already running.
     */
    private static final String DEFAULT_SERVLET_NAME = "AxisServlet";

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(Soap.class);

    /**
     * Servlet context of the current web appliaciton.
     */
    private ServletContext m_servletContext;
    
    /**
     * Soap exception manager is used to handle business exception.
     */
    private SoapExceptionManager m_exceptionManager 
        = new SoapExceptionManager();
    
    /**
     * Flag to enable/disable exception translation. If this is disabled, 
     * business exceptions must extend <code>java.rmi.RemoteException</code>
     * and have a getter and a setter method for each property. Further every 
     * buiness exception must be registered with a bean type mapping. See class 
     * <code>ch.elca.el4j.services.remoting.protocol.soap.axis.encoding.BeanTypeMapping</code>
     * which must be used in bean 
     * <code>ch.elca.el4j.services.remoting.protocol.soap.SoapSpecificConfiguration</code>.
     */
    private boolean m_exceptionTranslationEnabled = true;
    
    /**
     * Implicit context passing registry which should use the soap header for 
     * implicit context passing.
     */
    private ImplicitContextPassingRegistry 
        m_soapHeaderImplicitContextPassingRegistry;    

    /**
     * This method is used to enrich the given interface, that the new interface
     * is rmi conform.
     * 
     * @param serviceInterface Is the given interface.
     * @return Returns the enriched rmi conform interface.
     */
    protected Class getRmiEnrichedInterface(Class serviceInterface) {
        /**
         * Get the context class loader from current thread.
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        /**
         * Add rmi needs to interface.
         */
        InterfaceEnricher interfaceIndirector = new InterfaceEnricher();
        EnrichmentDecorator interfaceDecorator;
        if (isExceptionTranslationEnabled()) {
            interfaceDecorator = new SoapRmiEnrichmentDecorator();
        } else {
            interfaceDecorator = new RmiEnrichmentDecorator();
        }
        Class serviceInterfaceRmi = interfaceIndirector
                .createShadowInterfaceAndLoadItDirectly(
                    serviceInterface, interfaceDecorator, cl);
        return serviceInterfaceRmi;
    }
    
    /**
     * This method is used to connect a rmi interface with a non rmi service.
     * 
     * @param service
     *            Is the real service.
     * @param serviceInterface
     *            Is the interface which is implemented by the given service.
     * @param serviceInterfaceRmi
     *            Has the same methods as the <code>serviceInterface</code>
     *            but is rmi conform.
     * @return Returns the proxy which implements the
     *         <code>serviceInterfaceRmi</code>.
     */
    protected Object getRmiProxiedObject(Object service,
        Class serviceInterface, Class serviceInterfaceRmi) {
        /**
         * Get the context class loader from current thread.
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        ServerSoapInvocationHandler invocationHandler 
            = new ServerSoapInvocationHandler(service, serviceInterface, 
                m_exceptionManager, m_exceptionTranslationEnabled);
        Object serviceProxy = Proxy.newProxyInstance(cl,
            new Class[] {serviceInterfaceRmi},
            invocationHandler);
        
        return serviceProxy;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
        Class serviceInterfaceWithContext) {
        
        /**
         * Enrich the given interface with rmi needs.
         */
        Class serviceInterfaceWithContextRmi 
            = getRmiEnrichedInterface(serviceInterfaceWithContext);

        /**
         * Get special config if exist and merge them if necessary. Prepare
         * all other configurations too.
         */
        SoapSpecificConfiguration specialConfigClientProxy 
            = (SoapSpecificConfiguration) 
            proxyBean.getProtocolSpecificConfiguration();

        SoapSpecificConfiguration specialConfigProtocol 
            = (SoapSpecificConfiguration) 
            proxyBean.getRemoteProtocol().getProtocolSpecificConfiguration();
        
        String soapNamespaceUri = getNamespaceUri(proxyBean, 
            specialConfigClientProxy, specialConfigProtocol);

        List soapTypeMappings = getMergedTypeMappings(specialConfigClientProxy, 
            specialConfigProtocol);
        
        String wsdlDocumentUrl = getWsdlDocumentUrl(proxyBean, 
            specialConfigClientProxy, specialConfigProtocol);
        String soapServiceName = getSoapServiceName(proxyBean);
        String soapPortName = getSoapPortName(proxyBean, 
            specialConfigClientProxy, specialConfigProtocol);
        
        String encodingStyleUri = getEncodingStyleUri(
            specialConfigClientProxy, specialConfigProtocol);
        boolean defaultTypeMapping = isDefaultTypeMapping(
            specialConfigClientProxy, specialConfigProtocol);
        
        boolean pruneTopNodeImplicitContextPassing 
            = isPruneTopNodeImplicitContextPassing(
                specialConfigClientProxy, specialConfigProtocol);
        
        /**
         * Create proxy bean.
         */
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues proxyProps = new MutablePropertyValues();

        proxyProps.addPropertyValue("serviceInterface",
            serviceInterfaceWithContext);
        proxyProps.addPropertyValue("portInterface",
            serviceInterfaceWithContextRmi);

        proxyProps.addPropertyValue("wsdlDocumentUrl",
            wsdlDocumentUrl);
        proxyProps.addPropertyValue("namespaceUri",
            soapNamespaceUri);
        
        proxyProps.addPropertyValue("serviceName",
            soapServiceName);
        proxyProps.addPropertyValue("portName",
            soapPortName);
        
        proxyProps.addPropertyValue("encodingStyleUri",
            encodingStyleUri);
        proxyProps.addPropertyValue("defaultTypeMapping",
            Boolean.valueOf(defaultTypeMapping));
        
        proxyProps.addPropertyValue("pruneTopNodeImplicitContextPassing",
            Boolean.valueOf(pruneTopNodeImplicitContextPassing));
        
        ConfigurableListableBeanFactory configurableBeanFactory 
            = appContext.getBeanFactory();
        configurableBeanFactory.registerSingleton(
            PROXY_TYPE_MAPPING_BEAN_NAME, soapTypeMappings);
        if (m_soapHeaderImplicitContextPassingRegistry != null) {
            configurableBeanFactory.registerSingleton(
                PROXY_SOAP_HEADER_ICP_REGISTRY_BEAN_NAME,
                m_soapHeaderImplicitContextPassingRegistry);
        }
        
        
        appContext.registerSingleton(NEW_PROXY_BEAN_NAME,
                getProxyObjectType(), proxyProps);
        
        s_logger.info("Bean '" + NEW_PROXY_BEAN_NAME 
            + "' registered with following properties:\n" + proxyProps);
        
        Object newBean = null;
        try {
            newBean = appContext.getBean(NEW_PROXY_BEAN_NAME);
        } catch (BeansException e) {
            s_logger.error("Exception while getting bean '" 
                + NEW_PROXY_BEAN_NAME + "'.", e);
            throw e;
        }
        
        Object interceptedBean 
            = interceptProxyBean(newBean, serviceInterfaceWithContext, 
                proxyBean.getServiceInterface());
        
        return interceptedBean;
    }

    /**
     * Method to intercept a service. This is used to make exception translation
     * possible.
     * 
     * @param service
     *            Is the service which must be intercepted.
     * @param serviceInterface
     *            Is the interface which the service and the to create proxy has
     *            to implement.
     * @param businessInterface
     *            Is the interface which was actually written by user.
     * @return Returns the created service proxy.
     */
    private Object interceptProxyBean(
        Object service, Class serviceInterface, Class businessInterface) {
        /**
         * Get the context class loader from current thread.
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        ClientSoapInvocationHandler invocationHandler 
            = new ClientSoapInvocationHandler(service, businessInterface,
                m_exceptionManager, m_exceptionTranslationEnabled);
        Object serviceProxy = Proxy.newProxyInstance(cl,
            new Class[] {serviceInterface},
            invocationHandler);
        
        return serviceProxy;
    }

    /**
     * {@inheritDoc}
     */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy) {
        
        s_logger.info("Protocol SOAP method 'createExporterBean' entered.");
        
        /**
         * Enrich the given interface with rmi needs.
         */
        Class serviceInterfaceWithContextRmi 
            = getRmiEnrichedInterface(serviceInterfaceWithContext);

        /**
         * Check if the "end" interface fulfills the soap rules. 
         */
        checkInterface(serviceInterfaceWithContextRmi);
        
        /**
         * Create a proxy for the real service.
         */
        Object serviceProxyRmi = getRmiProxiedObject(serviceProxy, 
            serviceInterfaceWithContext, serviceInterfaceWithContextRmi);
        
        /**
         * Get special config if exist and merge them if necessary. Prepare
         * all other configurations too.
         */
        SoapSpecificConfiguration specialConfigExporter 
            = (SoapSpecificConfiguration) 
            exporterBean.getProtocolSpecificConfiguration();

        SoapSpecificConfiguration specialConfigProtocol 
            = (SoapSpecificConfiguration) 
            exporterBean.getRemoteProtocol().getProtocolSpecificConfiguration();
        
        String soapNamespaceUri = getNamespaceUri(exporterBean, 
            specialConfigExporter, specialConfigProtocol);
        
        String soapAllowedMethods = getAllowedMethods(specialConfigExporter, 
            specialConfigProtocol);

        String soapServiceName = getSoapServiceName(exporterBean);
        String soapPortName = getSoapPortName(exporterBean,
            specialConfigExporter, specialConfigProtocol);
        String soapClassName = serviceInterfaceWithContextRmi.getName();
        
        List soapTypeMappings = getMergedTypeMappings(specialConfigExporter, 
            specialConfigProtocol);
        String soapTypeMappingVersion = getTypeMappingVersion(
            specialConfigExporter, specialConfigProtocol);
        
        
        /**
         * Create wsdd document for soap service and register created proxy 
         * bean, so the special <code>ProxyBeanProvider</code> can access it.
         */
        WSDDDocument wsddDocument = getWsddDocument(soapNamespaceUri,
            soapAllowedMethods, soapServiceName, soapPortName, soapClassName,
            soapTypeMappings, soapTypeMappingVersion);
        
        WsddHelper.registerProxyBean(soapClassName, serviceProxyRmi);
        
        /**
         * Register soap service in axis.
         */
        AxisServer axisServer = retrieveAxisServer(DEFAULT_SERVLET_NAME);
        if (axisServer != null) {
            s_logger.info("Axis server found. Its name is '" 
                + axisServer.getName() + "'.");
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                    "Axis server could not be found!");
        }
        
        EngineConfiguration engineConfig = axisServer.getConfig();
        if (!(engineConfig instanceof WSDDEngineConfiguration)) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "EngineConfiguration of AxisServer " 
                    + "must be a WSDDEngineConfiguration.");
        }
        WSDDEngineConfiguration wsddEngineConfig 
            = (WSDDEngineConfiguration) engineConfig;
        
        WSDDDeployment dep = wsddEngineConfig.getDeployment();
        try {
            wsddDocument.deploy(dep);
        } catch (ConfigurationException e) {
            
            throw new BaseRTException(
                "Soap service could not be deployed to registry. " 
                + "Please check your configuration.", e);
        }
        
        try {
            axisServer.refreshGlobalOptions();
            /**
             * Do not save the configuration!
             * Otherwise people could mean they can configure the services by
             * editing the wsdd file directly.
             * 
             * axisServer.saveConfiguration();
             */
        } catch (ConfigurationException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Could not refresh axis server.", e);
        }

        SOAPService soapService = null;
        try {
            soapService = axisServer.getService(soapServiceName);
        } catch (AxisFault e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Soap service is not available.", e);
        }
        
        tryToUseRealParameterNames(
            soapService, exporterBean.getService().getClass(), 
            exporterBean.getServiceInterface());
        
        return soapService;
    }

    /**
     * Method to check if the given service interface fulfills the soap rules.
     * If there is any problem an exception will be thrown.
     * 
     * @param serviceInterface
     *            Is the interface to check.
     */
    private void checkInterface(Class serviceInterface) {
        Method[] methods = serviceInterface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Class[] exceptionTypes = method.getExceptionTypes();
            for (int j = 0; j < exceptionTypes.length; j++) {
                Class exceptionType = exceptionTypes[j];
                if (!(RemoteException.class.isAssignableFrom(exceptionType)
                    || RuntimeException.class.isAssignableFrom(
                        exceptionType))) {
                    String message = "The method \"{1}\" on interface "
                        + "\"{0}\" throws an exception which not extends "
                        + "\"java.rmi.RemoteException\".";
                    BaseRTException e 
                        = new BaseRTException(message, new Object[] {
                            serviceInterface.getName(), method.getName()});
                    s_logger.error(e.getMessage());
                    throw e;
                }
            }
        }
    }

    /**
     * This method tries to set the real parameter names of the soap operations
     * (methods). This is only possible if the original service class, which
     * implements the original interface, is compiled with debug information.
     * 
     * <b>For this method the original interface can not be used to extract the
     * parameter names, because parameter names are only saved in
     * implementations class files and only if this class is compiled with debug
     * information (compile attribute <code>-g</code>)!</b>
     * 
     * @param soapService
     *            Is the runnable soap service.
     * @param originalServiceClass
     *            Is the original service class.
     * @param originalServiceInterface
     *            Is the original service interface.
     */
    private void tryToUseRealParameterNames(SOAPService soapService,
        Class originalServiceClass, Class originalServiceInterface) {
        Map methodParameterNames = getMethodParameterNames(
            originalServiceClass, originalServiceInterface);
        if (methodParameterNames == null || methodParameterNames.isEmpty()) {
            return;
        }
        
        List operations = soapService.getServiceDescription().getOperations();
        Iterator itOperations = operations.iterator();
        while (itOperations.hasNext()) {
            OperationDesc oDesc = (OperationDesc) itOperations.next();
            Method m = oDesc.getMethod();
            String key 
                = getMethodIdentifier(m.getName(), m.getParameterTypes());
            String[] parameterNames = (String[]) methodParameterNames.get(key);
            List parameterList = oDesc.getAllInParams();
            Iterator itParameters = parameterList.iterator();
            if (parameterNames.length == parameterList.size()) {
                int i = 0;
                while (itParameters.hasNext()) {
                    ParameterDesc p = (ParameterDesc) itParameters.next();
                    p.setName(parameterNames[i]);
                    i++;
                }
            }
        }
    }

    /**
     * Method to extract method parameter names out of the given class.
     * 
     * @param originalServiceClass
     *            Is the original service class.
     * @param originalServiceInterface
     *            Is the original service interface.
     * @return Returns a map with method parameter name string arrays.
     */
    private Map getMethodParameterNames(
        Class originalServiceClass, Class originalServiceInterface) {
        Map methodParameterNames = new HashMap();
        
        Method[] originalMethods 
            = originalServiceClass.getMethods();
        Method[] originalInterfaceMethods
            = originalServiceInterface.getMethods();
        for (int i = 0; i < originalMethods.length; i++) {
            Method m = originalMethods[i];
            boolean isMethodFromInterface = false;
            for (int j = 0; !isMethodFromInterface 
                && j < originalInterfaceMethods.length; j++) {
                isMethodFromInterface = hasMethodsSameNameAndArguments(
                    m, originalInterfaceMethods[j]);
            }
            if (!isMethodFromInterface) {
                /**
                 * Method is not declared in original service interface.
                 */
                continue;
            }
            
            Class[] originalParameterTypes = m.getParameterTypes();
            String[] originalParameterNames 
                = ParamNameExtractor.getParameterNamesFromDebugInfo(m);
            if (originalParameterNames == null 
                && originalParameterTypes.length > 0) {
                /**
                 * Was not able to read the parameter names.
                 */
                continue;
            }
            MethodDescriptor mdOriginal = new MethodDescriptor();
            mdOriginal.setParameterTypes(originalParameterTypes);
            mdOriginal.setParameterNames(originalParameterNames);
            mdOriginal.setMethodName(m.getName());
            mdOriginal.setReturnType(m.getReturnType());
            mdOriginal.setThrownExceptions(m.getExceptionTypes());
            
            ContextEnrichmentDecorator edContext 
                = new ContextEnrichmentDecorator();
            RmiEnrichmentDecorator edRmi = new RmiEnrichmentDecorator();
            MethodDescriptor mdContext 
                = edContext.changedMethodSignature(mdOriginal);
            MethodDescriptor mdContextRmi 
                = edRmi.changedMethodSignature(mdContext);
            
            String key = getMethodIdentifier(mdContextRmi.getMethodName(), 
                mdContextRmi.getParameterTypes());
            methodParameterNames.put(key, mdContextRmi.getParameterNames());
        }
        return methodParameterNames;
    }
    
    /**
     * Method to create a method identifier out of the method name and the
     * parameter type names.
     * 
     * @param methodName
     *            Is the name of the method.
     * @param methodParameterTypes
     *            Are the parameter types of the method.
     * @return Returns the generated identifier.
     */
    private String getMethodIdentifier(
        String methodName, Class[] methodParameterTypes) {
        StringBuffer sb = new StringBuffer();
        sb.append(methodName);
        sb.append(';');
        for (int i = 0; i < methodParameterTypes.length; i++) {
            sb.append(methodParameterTypes[i].getName());
            sb.append(',');
        }
        return sb.toString();
    }

    /**
     * Method to test if the given methods have the same name and parameters.
     * 
     * @param m1
     *            Is the first method.
     * @param m2
     *            Is the second method.
     * @return Returns true if the methods have the same name and parameters.
     */
    private boolean hasMethodsSameNameAndArguments(Method m1, Method m2) {
        if (m1 != null && m2 != null && m1.getName().equals(m2.getName())) {
            Class[] params1 = m1.getParameterTypes();
            Class[] params2 = m2.getParameterTypes();
            if (params1.length == params2.length) {
                boolean hasMethodsSameNameAndArguments = true;
                for (int i = 0; hasMethodsSameNameAndArguments 
                    && i < params1.length; i++) {
                    if (params1[i] != params2[i]) {
                        hasMethodsSameNameAndArguments = false;
                    }
                }
                return hasMethodsSameNameAndArguments;
            }
        }
        return false;
    }
    
    /**
     * This method creates the wsdd document with help of the given parameters.
     * 
     * @param soapNamespaceUri
     *            Is the namespace uri which must be used by the new soap
     *            service.
     * @param soapAllowedMethods
     *            Contains the method which should be accessable.
     * @param soapServiceName
     *            Is the name of the service.
     * @param soapPortName
     *            Is the name of the port.
     * @param soapInterfaceClassName
     *            Is the class name of the interface which is implemented by the
     *            soap service.
     * @param soapTypeMappings
     *            Contains the type mappings, so complex type can be de- and
     *            serialized.
     * @param typeMappingVersion
     *            Is the type mapping version of soap messages.
     * @return Returns the generated wsdd document.
     */
    private WSDDDocument getWsddDocument(String soapNamespaceUri, 
        String soapAllowedMethods, String soapServiceName, String soapPortName, 
        String soapInterfaceClassName, List soapTypeMappings, 
        String typeMappingVersion) {
        WsddHelper h = new WsddHelper();
        Element wsddDeploymentElement = h.createElementDeployment();
        Element wsddServiceElement = h.createElementService(
            soapServiceName, Style.WRAPPED, Use.LITERAL);
        wsddDeploymentElement.appendChild(wsddServiceElement);
        wsddServiceElement.appendChild(h.createElementParameter(
            ProxyBeanProvider.OPTION_WSDL_TARGETNAMESPACE, soapNamespaceUri));
        wsddServiceElement.appendChild(h.createElementParameter(
            ProxyBeanProvider.OPTION_WSDL_SERVICEELEMENT, soapServiceName));
        wsddServiceElement.appendChild(h.createElementParameter(
            ProxyBeanProvider.OPTION_WSDL_SERVICEPORT, soapPortName));
        wsddServiceElement.appendChild(h.createElementParameter(
            ProxyBeanProvider.OPTION_CLASSNAME, soapInterfaceClassName));
        wsddServiceElement.appendChild(h.createElementParameter(
            ProxyBeanProvider.OPTION_ALLOWEDMETHODS, soapAllowedMethods));
        wsddServiceElement.appendChild(h.createElementParameter(
            OPTION_TYPE_MAPPING_VERSION, typeMappingVersion));
        
        // TODO Add support for implicit context passing via soap header.
        
        wsddServiceElement.appendChild(
            h.createElementNamespace(soapNamespaceUri));
        
        if (soapTypeMappings != null && soapTypeMappings.size() > 0) {
            Iterator itTypeMappings = soapTypeMappings.iterator();
            while (itTypeMappings.hasNext()) {
                TypeMapping tm = (TypeMapping) itTypeMappings.next();
                String serializerFactory 
                    = tm.getSerializerFactory().getName();
                String deserializerFactory 
                    = tm.getDeserializerFactory().getName();
                String encodingStyle = tm.getEncodingStyle();
                String namespaceUri = tm.getNamespaceUri();
                namespaceUri = StringUtils.hasText(namespaceUri) 
                    ? namespaceUri : soapNamespaceUri;
                Iterator itTypesMappingTypes = tm.getTypes().iterator();
                while (itTypesMappingTypes.hasNext()) {
                    String typeName = (String) itTypesMappingTypes.next();
                    Element wsddTypeMapping = h.createElementTypeMapping(
                        typeName, serializerFactory, deserializerFactory,
                        namespaceUri, encodingStyle);
                    wsddServiceElement.appendChild(wsddTypeMapping);
                }
            }
        }
 
        WSDDDocument wsddDocument 
            = h.packRootElementInDocument(wsddDeploymentElement);
        
        try {
            String wsddConfigString 
                = XMLUtils.DocumentToString(wsddDocument.getDOMDocument());
            s_logger.info("Wsdd document of soap service:\n" 
                + wsddConfigString);
        } catch (ConfigurationException e) {
            throw new BaseRTException(
                "The wsdd document of the soap service can not be " 
                + "displayed.", e);
        }
        
        return wsddDocument;
    }

    /**
     * Get wsdl document url first from exporter than from protocol and if it is
     * still not a usable value generate it.
     * 
     * @param serviceBean
     *            Is the service bean.
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the wsdl document url.
     */
    protected String getWsdlDocumentUrl(AbstractRemotingBase serviceBean, 
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String wsdlDocumentUrl = null;
        if (specialConfigService != null) {
            wsdlDocumentUrl = specialConfigService.getWsdlDocumentUrl();
        }
        if (specialConfigProtocol != null) {
            wsdlDocumentUrl = (StringUtils.hasText(wsdlDocumentUrl) 
                ? wsdlDocumentUrl : specialConfigProtocol.getWsdlDocumentUrl());
        }
        wsdlDocumentUrl = (StringUtils.hasText(wsdlDocumentUrl) 
            ? wsdlDocumentUrl : generateUrl(serviceBean) + URL_WSDL_SUFFIX);
        return wsdlDocumentUrl;
    }
    
    /**
     * Get namespace uri first from exporter than from protocol and if it is
     * still not a usable value generate it.
     * 
     * @param serviceBean
     *            Is the service bean.
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the namespace uri.
     */
    protected String getNamespaceUri(AbstractRemotingBase serviceBean, 
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String namespaceUri = null;
        if (specialConfigService != null) {
            namespaceUri = specialConfigService.getNamespaceUri();
        }
        if (specialConfigProtocol != null) {
            namespaceUri = (StringUtils.hasText(namespaceUri) 
                ? namespaceUri : specialConfigProtocol.getNamespaceUri());
        }
        namespaceUri = (StringUtils.hasText(namespaceUri) 
            ? namespaceUri : generateUrl(serviceBean));
        return namespaceUri;
    }
    
    /**
     * Get encoding style uri first from exporter than from protocol.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the encoding style uri.
     */
    protected String getEncodingStyleUri(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String encodingStyleUri = null;
        if (specialConfigService != null) {
            encodingStyleUri = specialConfigService.getEncodingStyleUri();
        }
        if (specialConfigProtocol != null) {
            encodingStyleUri = (StringUtils.hasText(encodingStyleUri) 
                ? encodingStyleUri 
                    : specialConfigProtocol.getEncodingStyleUri());
        }
        encodingStyleUri = (StringUtils.hasText(encodingStyleUri) 
            ? encodingStyleUri : "");
        return encodingStyleUri;
    }

    /**
     * Gets the flag default type mapping from exporter or if <code>null</code>
     * from protocol. Default is <code>false</code>.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the default type mapping flag.
     */
    protected boolean isDefaultTypeMapping(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        boolean defaultTypeMapping = false;
        if (specialConfigService != null) {
            defaultTypeMapping = specialConfigService.isDefaultTypeMapping();
        } else if (specialConfigProtocol != null) {
            defaultTypeMapping = specialConfigProtocol.isDefaultTypeMapping();
        }
        return defaultTypeMapping;
    }

    /**
     * Get type mapping version first from exporter than from protocol.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the type mapping version.
     */
    protected String getTypeMappingVersion(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String typeMappingVersion = null;
        if (specialConfigService != null) {
            typeMappingVersion = specialConfigService.getTypeMappingVersion();
        }
        if (specialConfigProtocol != null) {
            typeMappingVersion = (StringUtils.hasText(typeMappingVersion) 
                ? typeMappingVersion 
                    : specialConfigProtocol.getTypeMappingVersion());
        }
        typeMappingVersion = (StringUtils.hasText(typeMappingVersion) 
            ? typeMappingVersion : DEFAULT_TYPE_MAPPING_VERSION);
        return typeMappingVersion;
    }

    /**
     * Get allowed methods first from exporter than from protocol and if it is
     * still not a usable value use * to allow access to every method.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the allowed methods.
     */
    protected String getAllowedMethods(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String soapAllowedMethods = null;
        if (specialConfigService != null) {
            soapAllowedMethods = specialConfigService.getAllowedMethods();
        }
        if (specialConfigProtocol != null) {
            soapAllowedMethods = (StringUtils.hasText(soapAllowedMethods) 
                ? soapAllowedMethods 
                : specialConfigProtocol.getAllowedMethods());
        }
        soapAllowedMethods = (StringUtils.hasText(soapAllowedMethods) 
            ? soapAllowedMethods : "*");
        return soapAllowedMethods;
    }

    /**
     * Get the merged type mappings from special configurations of the service
     * and of the protocol if the type mapping it is not already included.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the merged type mappings.
     */
    protected List getMergedTypeMappings(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        Map alreadyMappedTypes = new HashMap();
        List mergedTypeMappings = new LinkedList();
        if (specialConfigService != null) {
            List typeMappingsFromService 
                = specialConfigService.getTypeMappings();
            addNotAlreadyExistingTypeMappings(alreadyMappedTypes, 
                typeMappingsFromService, mergedTypeMappings);
        }
        if (specialConfigProtocol != null) {
            List typeMappingsFromProtocol 
                = specialConfigProtocol.getTypeMappings();
            addNotAlreadyExistingTypeMappings(alreadyMappedTypes, 
                typeMappingsFromProtocol, mergedTypeMappings);
        }
        
        return mergedTypeMappings;
    }

    /**
     * Gets the flag prune top element of implicit context passing from exporter
     * or if <code>null</code> from protocol. Default is <code>false</code>.
     * 
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the prune top element of implicit context passing flag.
     */
    protected boolean isPruneTopNodeImplicitContextPassing(
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        boolean pruneTopNodeImplicitContextPassing = false;
        if (specialConfigService != null) {
            pruneTopNodeImplicitContextPassing = specialConfigService
                .isPruneTopNodeImplicitContextPassing();
        } else if (specialConfigProtocol != null) {
            pruneTopNodeImplicitContextPassing = specialConfigProtocol
                .isPruneTopNodeImplicitContextPassing();
        }
        return pruneTopNodeImplicitContextPassing;
    }

    /**
     * Method which adds not already existing type mappings to given list.
     * 
     * @param alreadyMappedTypes
     *            Is a map of already mapped types.
     * @param typeMappings
     *            Is a list of available type mappings.
     * @param mergedTypeMappings
     *            Is the big list with merged type mappings.
     */
    private void addNotAlreadyExistingTypeMappings(
        Map alreadyMappedTypes, List typeMappings, 
        List mergedTypeMappings) {
        if (typeMappings != null 
            && typeMappings.size() > 0) {
            Iterator it = typeMappings.iterator();
            while (it.hasNext()) {
                TypeMapping tm = (TypeMapping) it.next();
                List types = tm.getTypes();
                Iterator itTypes = types.iterator();
                while (itTypes.hasNext()) {
                    String className = (String) itTypes.next();
                    if (alreadyMappedTypes.containsKey(className)) {
                        itTypes.remove();
                    } else {
                        alreadyMappedTypes.put(className, Boolean.TRUE);
                    }
                }
                if (types.size() == 0) {
                    it.remove();
                }
            }
            if (typeMappings.size() > 0) {
                mergedTypeMappings.addAll(typeMappings);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return AxisPortProxyFactoryBean.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return SOAPService.class;
    }

    /**
     * Method to get the soap service name.
     * 
     * @param remoteBase
     *            Is the reference to get information about the service.
     * @return Returns the soap service name.
     */
    protected String getSoapServiceName(AbstractRemotingBase remoteBase) {
        return remoteBase.getServiceName();
    }

    /**
     * Method to get the soap port name.
     * 
     * @param remoteBase
     *            Is the reference to get information about the service.
     * @param specialConfigService
     *            Is the special configuration of the service.
     * @param specialConfigProtocol
     *            Is the special configuration of used protocol.
     * @return Returns the soap port name.
     */
    protected String getSoapPortName(AbstractRemotingBase remoteBase, 
        SoapSpecificConfiguration specialConfigService, 
        SoapSpecificConfiguration specialConfigProtocol) {
        String portName = null;
        if (specialConfigService != null) {
            portName = specialConfigService.getPortName();
        }
        if (specialConfigProtocol != null) {
            portName = (StringUtils.hasText(portName) 
                ? portName : specialConfigProtocol.getPortName());
        }
        portName = (StringUtils.hasText(portName) 
            ? portName : remoteBase.getServiceName() + ".Port");
        return portName;
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
        sb.append("/services/");
        sb.append(remoteBase.getServiceName());
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public void prepareExporterDependentBeans(
        RemotingServiceExporter exporterBean) {
        /**
         * Do nothing. Created exporter is special.
         */
    }
    
    /**
     * {@inheritDoc}
     */
    public void finalizeExporterDependentBeans(
        RemotingServiceExporter exporterBean) {
        /**
         * Do nothing. Created exporter is special.
         */
    }
    
    /**
     * {@inheritDoc}
     */
    public void setServletContext(ServletContext servletContext) {
        m_servletContext = servletContext;
    }

    /**
     * Method to retrieve the axis server from the servlet context. This only
     * works if the AxisServlet was started before this method is invoked.
     * 
     * @param servletName
     *            Is the name of the servlet, which is using the desired server.
     * @return Returns the axis server if it could found, otherwise null.
     */
    private AxisServer retrieveAxisServer(String servletName) {
        final String ATTR_AXIS_ENGINE = "AxisEngine";
        Object contextObject = m_servletContext.getAttribute(
            servletName + ATTR_AXIS_ENGINE);
        if (contextObject == null) {
            // if AxisServer not found:
            // fall back to the "default" AxisEngine
            contextObject = m_servletContext.getAttribute(ATTR_AXIS_ENGINE);
        }
        if (contextObject instanceof AxisServer) {
            AxisServer server = (AxisServer) contextObject;
            // if this is "our" Engine
            if (server != null 
                && servletName.equals(server.getName())) {
                return server;
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (m_exceptionTranslationEnabled && m_exceptionManager == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                "exceptionManager", this);
        } else {
            s_logger.info("Exception translation is disabled.");
        }
    }
    
    /**
     * @return Returns the exceptionManager.
     */
    public SoapExceptionManager getExceptionManager() {
        return m_exceptionManager;
    }

    /**
     * @param exceptionManager
     *            The exceptionManager to set.
     */
    public void setExceptionManager(SoapExceptionManager exceptionManager) {
        m_exceptionManager = exceptionManager;
    }

    /**
     * @return Returns the exceptionTranslationEnabled.
     */
    public boolean isExceptionTranslationEnabled() {
        return m_exceptionTranslationEnabled;
    }

    /**
     * @param exceptionTranslationEnabled
     *            The exceptionTranslationEnabled to set.
     */
    public void setExceptionTranslationEnabled(
        boolean exceptionTranslationEnabled) {
        m_exceptionTranslationEnabled = exceptionTranslationEnabled;
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
}