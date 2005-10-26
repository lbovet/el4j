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

package ch.elca.el4j.services.remoting.protocol.soap.axis.encoding;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class is a value object to describe a type mapping.
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
public class TypeMapping implements InitializingBean {
    
    /**
     * Are the types which must be mapped.
     * Must contain minimum one type.
     */
    protected List m_types;

    /**
     * Is the serializer factory to create a serializer for the given type.
     * Must be set.
     */
    protected Class m_serializerFactory;

    /**
     * Is the deserializer factory to create a deserializer for the given type.
     * Must be set.
     */
    protected Class m_deserializerFactory;

    /**
     * Is the encoding style. Used by de- and serializer. Default is "". 
     */
    protected String m_encodingStyle;
    
    /**
     * Is the special namespace uri for this type mapping.
     */
    protected String m_namespaceUri;
    
    /**
     * @return Returns the deserializerFactory.
     */
    public Class getDeserializerFactory() {
        return m_deserializerFactory;
    }

    /**
     * @param deserializerFactory The deserializerFactory to set.
     */
    public void setDeserializerFactory(Class deserializerFactory) {
        m_deserializerFactory = deserializerFactory;
    }

    /**
     * @return Returns the serializerFactory.
     */
    public Class getSerializerFactory() {
        return m_serializerFactory;
    }

    /**
     * @param serializerFactory The serializerFactory to set.
     */
    public void setSerializerFactory(Class serializerFactory) {
        m_serializerFactory = serializerFactory;
    }

    /**
     * @return Returns the types.
     */
    public List getTypes() {
        return m_types;
    }

    /**
     * @param types The types to set.
     */
    public void setTypes(List types) {
        m_types = types;
    }

    /**
     * @return Returns the encodingStyle.
     */
    public String getEncodingStyle() {
        return m_encodingStyle;
    }

    /**
     * @param encodingStyle
     *            The encodingStyle to set.
     */
    public void setEncodingStyle(String encodingStyle) {
        m_encodingStyle = encodingStyle;
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
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_types == null || m_types.size() == 0) {
            CoreNotificationHelper.notifyLackingEssentialProperty("type", this);
        } else if (m_serializerFactory == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                "serializerFactory", this);
        } else if (m_deserializerFactory == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                "deserializerFactory", this);
        }
        
        if (!StringUtils.hasLength(m_encodingStyle)) {
            m_encodingStyle = "";
        }
    }
}
