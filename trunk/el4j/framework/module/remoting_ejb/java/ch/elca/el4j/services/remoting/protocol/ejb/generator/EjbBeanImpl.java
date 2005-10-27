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

package ch.elca.el4j.services.remoting.protocol.ejb.generator;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletException;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTagGenerator;
import ch.elca.el4j.util.codingsupport.ClassUtils;

/**
 * This class provides all meta data needed to generate EJB session beans.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class EjbBeanImpl implements EjbBean {

    /** The delimiter used to separate configuration locations. */
    public static final String CONFIG_DELIMITER = ", ";
    
    /** Reserved names that are not allowed to be used in service beans. */
    public static final String[] METHOD_NAME_BLACKLIST = {
        "ejbActivate",
        "ejbPassivate",
        "ejbRemove",
        "setSessionContext",
        "afterBegin",
        "beforeCompletion",
        "afterCompletion",
        "remove"
    };
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(EjbBeanImpl.class);
    
    /** The remoting service exporter that exposes the service described by this
     *  instance. */
    private RemotingServiceExporter m_exporter;
    
    /** The name of the exporter bean. */
    private String m_exporterBeanName;
    
    /** The inclusive configuration locations. */
    private String m_inclusiveLocations;
    
    /** The exclusive configuration locations. */
    private String m_exclusiveLocations;
    
    /** The type of the service bean. */
    private Class m_serviceBeanType;
    
    /** The type of the service bean's interface or its enriched version. */
    private Class m_serviceInterface;

    /** The XDoclet tag generator used for this EJB bean. */
    private XDocletTagGenerator m_xDocletTagGenerator;
    
    /**
     * {@inheritDoc}
     */
    public Object getConfigurationObject() {
        return m_exporter.getProtocolSpecificConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isContextPassingAvailable() {
        return m_exporter.getRemoteProtocol()
                .getImplicitContextPassingRegistry() != null;
    }

    /**
     * {@inheritDoc}
     */
    public String getInterfacePackage() {
        return ClassUtils.getPackageName(m_serviceInterface);
    }

    /**
     * {@inheritDoc}
     */
    public String getServiceName() {
        return m_exporter.getServiceName();
    }

    /**
     * {@inheritDoc}
     */
    public String getServicePackage() {
        return m_serviceBeanType.getPackage().getName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStateful() {
        return ((EjbConfigurationObject) getConfigurationObject()).isStateful();
    }

    /**
     * {@inheritDoc}
     */
    public String getExclusiveLocations() {
        return m_exclusiveLocations;
    }

    /**
     * {@inheritDoc}
     */
    public String getInclusiveLocations() {
        return m_inclusiveLocations;
    }

    /**
     * {@inheritDoc}
     */
    public String getExporterBeanName() {
        return m_exporterBeanName;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getMethodSignatures() {
        Method[] methods = m_serviceInterface.getMethods();
        MethodSignature[] methodSignatures 
            = new MethodSignature[methods.length];
        
        boolean contextPassing = isContextPassingAvailable();
        for (int i = 0; i < methods.length; i++) {
            if (isBadMethodName(methods[i].getName())) {
                s_logger.warn("Method '" + methods[i] + "' cannot be wrapped "
                        + "(method name used in EJB). Removing it from the "
                        + "list of exported methods.");
                continue;
            }
            
            methodSignatures[i] = new MethodSignature(
                    methods[i], contextPassing,
                    isWrapRTExceptions(), m_xDocletTagGenerator);
        }
        return methodSignatures;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getXDocletTags() {
        try {
            return m_xDocletTagGenerator.getTagsForClass().toString();
        } catch (XDocletException e) {
            s_logger.fatal("Could not create XDoclet class tags for service '"
                    + getServiceName() + "'.");
        }
        return null;
    }

    /**
     * @return Returns whether runtime exceptions are wrapped and pushed to
     *      client.
     */
    public boolean isWrapRTExceptions() {
        return ((EjbConfigurationObject) getConfigurationObject()).
                isWrapRTExceptions();
    }
    
    /**
     * Concatenates an array of strings, separated by the provided delimiter.
     * 
     * @param strings
     *      The string so concatenate.
     *      
     * @param delimiter
     *      The delimiter used to separate.
     *       
     * @return Returns the concatenated strings separated by the provided
     *      delimiter. 
     */
    private String concatenate(String[] strings, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < strings.length; i++) {
            buffer.append("\"");
            buffer.append(strings[i]);
            buffer.append("\"");
            
            if (i < strings.length - 1) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * Checks whether the provided name is a reserved method name.
     * 
     * @param name
     *      a method name to check.
     *      
     * @return Returns <code>true</code> if the given name is a reserved
     *      method name.
     */
    private boolean isBadMethodName(String name) {
        for (int i = 0; i < METHOD_NAME_BLACKLIST.length; i++) {
            if (METHOD_NAME_BLACKLIST[i].equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    // ----------------------------------------------------------------- setters
    
    /**
     * Sets the exclusive configuration locations.
     * 
     * @param exclusiveLocations
     *      The configuration locations to exclude.
     */
    public void setExclusiveLocations(String[] exclusiveLocations) {
        this.m_exclusiveLocations = concatenate(exclusiveLocations,
                CONFIG_DELIMITER);
    }

    /**
     * Sets the inclusive configuration locations.
     * 
     * @param inclusiveLocations
     *      The configuration locations to include.
     */
    public void setInclusiveLocations(String[] inclusiveLocations) {
        this.m_inclusiveLocations = concatenate(inclusiveLocations,
                CONFIG_DELIMITER);
    }

    /**
     * Sets the exporter bean's name.
     * 
     * @param exporterBeanName
     *      The name of the exporter bean.
     */
    public void setExporterBeanName(String exporterBeanName) {
        this.m_exporterBeanName = exporterBeanName;
    }

    /**
     * Sets the remoting service exporter.
     * 
     * @param exporter
     *      The remoting service exporter.
     */
    public void setExporter(RemotingServiceExporter exporter) {
        m_exporter = exporter;
    }

    /**
     * Sets the service bean's type.
     * 
     * @param serviceBeanType
     *      The type of the service bean.
     */
    public void setServiceBeanType(Class serviceBeanType) {
        this.m_serviceBeanType = serviceBeanType;
    }

    /**
     * Sets the service's interface type.
     * 
     * @param serviceInterface
     *      The type of the service interface.
     */
    public void setServiceInterface(Class serviceInterface) {
        m_serviceInterface = serviceInterface;
    }

    /**
     * Sets the XDoclet tag generator.
     * 
     * @param docletTagGenerator
     *      The XDoclet tag generator.
     */
    public void setXDocletTagGenerator(XDocletTagGenerator docletTagGenerator) {
        m_xDocletTagGenerator = docletTagGenerator;
    }
}
