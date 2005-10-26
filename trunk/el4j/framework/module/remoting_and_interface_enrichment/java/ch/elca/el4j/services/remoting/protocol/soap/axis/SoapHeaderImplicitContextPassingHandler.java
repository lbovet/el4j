/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.message.PrefixedQName;
import org.springframework.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.CollectionUtils;

/**
 * Soap request handler for implicit context passing via soap header.
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
public class SoapHeaderImplicitContextPassingHandler extends GenericHandler {
    /**
     * Key for prune top element implicit context passing in config map.
     */
    public static final String PRUNE_TOP_NODE_IMPLICIT_CONTEXT_PASSING 
        = "pruneTopNodeImplicitContextPassing";

    /**
     * Key for registry in config map.
     */
    public static final String REGISTRY = "registry";

    /**
     * Is name of the attribute for the implicit context passer key.
     */
    public static final Name ATTRIBUTE_IMPLICIT_CONTEXT_PASSER 
        = new PrefixedQName(
            new QName("http://implicit.context.passing.el4j.elca.ch",
                "implicitcontextpasser")); 
    
    /**
     * Implicit context passing registry.
     */
    private ImplicitContextPassingRegistry m_registry;
    
    /**
     * Flag to indicate if the top element of implicit context passing should be
     * prune.
     */
    private boolean m_pruneTopNodeImplicitContextPassing;
    
    /**
     * {@inheritDoc}
     */
    public QName[] getHeaders() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void init(HandlerInfo config) {
        Map m = config.getHandlerConfig();
        m_registry = (ImplicitContextPassingRegistry) m.get(REGISTRY);
        if (m_registry == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Implicit context passing registry must not be empty.");
        }
        m_pruneTopNodeImplicitContextPassing 
            = ((Boolean) m.get(PRUNE_TOP_NODE_IMPLICIT_CONTEXT_PASSING))
                .booleanValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean handleRequest(MessageContext context) {
        Map contextMap = m_registry.getAssembledImplicitContext();
        CollectionUtils.containsOnlyObjectsOfType(
            contextMap.values(), Node.class);
        
        SOAPMessageContext smc = (SOAPMessageContext) context;
        SOAPMessage msg = smc.getMessage();
        try {
            SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
            SOAPHeader header = envelope.getHeader();
            Iterator it = contextMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String implicitContextPasserKey = (String) entry.getKey();
                Node topNode = (Node) entry.getValue();
                addSoapElements(header, topNode, 
                    implicitContextPasserKey);
            }
        } catch (SOAPException e) {
            throw new JAXRPCException(e);
        }
        return true;
    }

    /**
     * Method to add soap element to the given header. The elements must be
     * created out of the given node.
     * 
     * @param header
     *            Is the soap header.
     * @param topNode
     *            Is the top node.
     * @param implicitContextPasserKey
     *            Is the key of the implicit context passing registry.
     * @throws SOAPException
     *             If soap elements could not be created.
     */
    private void addSoapElements(SOAPHeader header, Node topNode, 
        String implicitContextPasserKey) throws SOAPException {
        if (topNode == null || header == null) {
            return;
        }
        
        if (m_pruneTopNodeImplicitContextPassing) {
            NodeList childNodes = topNode.getChildNodes();
            if (childNodes != null) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    addSoapElement(childNode, header);
                }
            }
        } else {
            SOAPElement topElement = addSoapElement(topNode, header);
            if (StringUtils.hasText(implicitContextPasserKey)) {
                topElement.addAttribute(ATTRIBUTE_IMPLICIT_CONTEXT_PASSER, 
                    implicitContextPasserKey);
            }
        }
        
    }
    
    /**
     * Method which recursively converts normal nodes to soap elements and adds
     * them to the given parent element.
     * 
     * @param node
     *            Is the node to get the information from.
     * @param parent
     *            Is the parameter element where to add created elements.
     * @return Returns the created child soap element of the given parent.
     * @throws SOAPException
     *             If soap elements could not be created.
     */
    private SOAPElement addSoapElement(Node node, SOAPElement parent) 
        throws SOAPException {
        if (node == null || parent == null) {
            return null;
        }
        String name = node.getNodeName();
        String prefix = node.getPrefix();
        prefix = StringUtils.hasText(prefix) ? prefix : "";
        String namespace = node.getNamespaceURI();
        namespace = StringUtils.hasText(namespace) ? namespace : "";
        SOAPElement element = parent.addChildElement(name, prefix, namespace);
        
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                element.addAttribute(
                    new PrefixedQName(attribute.getNamespaceURI(), 
                        attribute.getNodeName(), attribute.getPrefix()), 
                    attribute.getNodeValue());
            }
        }
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == Node.TEXT_NODE) {
                    String value = childNode.getNodeValue();
                    element.addTextNode(value);
                } else {
                    addSoapElement(childNode, element);
                }
            }
        }
        return element;
    }
}
