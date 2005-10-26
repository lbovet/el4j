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

package ch.elca.el4j.services.remoting.protocol.soap;

import java.util.List;

import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;

/**
 * This class is a value object and contains soap specific configurations.
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
public class SoapSpecificConfiguration 
    implements ProtocolSpecificConfiguration {
    
    /**
     * This is the location, where the wsdl file can be found.
     */
    protected String m_wsdlDocumentUrl;
    
    /**
     * This is the namespace uri for the exposed service.
     */
    protected String m_namespaceUri;

    /**
     * This the name of the port for the exposed service.
     */
    protected String m_portName;
    
    /**
     * This is a space separated list of method names, which should be exposed
     * by the service.
     */
    protected String m_allowedMethods;
    
    /**
     * This list contains all type mappings.
     */
    protected List m_typeMappings;
    
    /**
     * This is the encoding style uri to register the type mappings.
     */
    protected String m_encodingStyleUri;
    
    /**
     * Flag to indicate if the configured type mappings are the default one.
     */
    protected boolean m_defaultTypeMapping = false;
    
    /**
     * This is the type mapping version.
     */
    protected String m_typeMappingVersion;
    
    /**
     * Flag to indicate if the top element of implicit context passing should be
     * ignored. Default is set to <code>false</code>.
     */
    protected boolean m_pruneTopNodeImplicitContextPassing = false;
    
    /**
     * @return Returns the wsdlDocumentUrl.
     */
    public String getWsdlDocumentUrl() {
        return m_wsdlDocumentUrl;
    }

    /**
     * @param wsdlDocumentUrl
     *            The wsdlDocumentUrl to set.
     */
    public void setWsdlDocumentUrl(String wsdlDocumentUrl) {
        m_wsdlDocumentUrl = wsdlDocumentUrl;
    }

    /**
     * @return Returns the namespaceUri.
     */
    public String getNamespaceUri() {
        return m_namespaceUri;
    }

    /**
     * @param namespaceUri
     *            The namespaceUri to set.
     */
    public void setNamespaceUri(String namespaceUri) {
        m_namespaceUri = namespaceUri;
    }

    /**
     * @return Returns the portName.
     */
    public String getPortName() {
        return m_portName;
    }

    /**
     * @param portName
     *            The portName to set.
     */
    public void setPortName(String portName) {
        m_portName = portName;
    }

    /**
     * @return Returns the allowedMethods.
     */
    public String getAllowedMethods() {
        return m_allowedMethods;
    }

    /**
     * @param allowedMethods
     *            The allowedMethods to set.
     */
    public void setAllowedMethods(String allowedMethods) {
        m_allowedMethods = allowedMethods;
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
     * @return Returns the typeMappingVersion.
     */
    public String getTypeMappingVersion() {
        return m_typeMappingVersion;
    }

    /**
     * @param typeMappingVersion
     *            The typeMappingVersion to set.
     */
    public void setTypeMappingVersion(String typeMappingVersion) {
        m_typeMappingVersion = typeMappingVersion;
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
    public void afterPropertiesSet() throws Exception {
        // There is no work to do.
    }
}
