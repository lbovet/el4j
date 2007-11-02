/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol.jaxws;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.ws.util.xml.NodeListIterator;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class is a JAX-WS Handler that injects the implicit context while
 * sending a SOAP message and extract it while retrieving one.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class JaxwsContextHandler extends AbstractJaxwsJaxbContextHandler {
    
    /**
     * The logger.
     */
    private static Logger s_logger 
        = Logger.getLogger(JaxwsContextHandler.class);
    
    /**
     * The registry to get the context from.
     */
    private ImplicitContextPassingRegistry m_contextPassingRegistry;
    
    
    /**
     * Create a new JAX-WS Handler to modify a soap message.
     * 
     * @param registry The registry to take the context from
     * @param jaxbContext The context to serialize the implicit context with
     */
    public JaxwsContextHandler(ImplicitContextPassingRegistry registry,
        JAXBContext jaxbContext) {
        super(jaxbContext);
        this.m_contextPassingRegistry = registry;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean) context
            .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (m_contextPassingRegistry != null) {
            SOAPMessage msg = context.getMessage();
            if (outboundProperty.booleanValue()) {
                handleOutgoingMessage(msg);
            } else {
                handleIncomingMessage(msg);
            }
        }
        return true;
    }

    /**
     * Handles an incoming SOAP message.
     * Adds the implicit context to the SOAP header.
     * 
     * @param msg    the SOAP message
     */
    @SuppressWarnings("unchecked")
    protected void handleIncomingMessage(SOAPMessage msg) {
        Node contextElement = null;
        try {
            if (msg.getSOAPHeader() != null) {
                NodeList list = msg.getSOAPHeader()
                    .getElementsByTagNameNS(CONTEXT_NAMESPACE.getURI(),
                        CONTEXT_ELEMENT_NAME);
                if (list.getLength() > 0) {
                    contextElement = (Node) list.item(0);
                }
            }
        } catch (Exception e) {
            s_logger.error("Error getting SOAP header.");
        }

        if (contextElement != null) {
            Unmarshaller unmarshaller = getUnmarshaller();

            Map<String, Object> map = new HashMap<String, Object>();
            NodeList nodeList = contextElement.getChildNodes();
            NodeListIterator it = new NodeListIterator(nodeList);

            while (it.hasNext()) {
                Node soapNode = (Node) it.next();

                try {
                    // Try to get the passed Object out of the xml
                    JAXBElement element = (JAXBElement) unmarshaller
                        .unmarshal(soapNode);

                    map.put(soapNode.getNodeName(), element.getValue());
                } catch (JAXBException e) {
                    s_logger
                        .error("Unable to unmarshall context element "
                            + soapNode.getNodeName());
                }
            }
            m_contextPassingRegistry.pushAssembledImplicitContext(map);
        }
    }

    /**
     * Handles an outgoing SOAP message.
     * Extracts the implicit context from the SOAP header.
     * 
     * @param msg    the SOAP message
     */
    @SuppressWarnings("unchecked")
    protected void handleOutgoingMessage(SOAPMessage msg) {
        SOAPHeaderElement newElement = null;

        try {
            if (msg.getSOAPHeader() == null) {
                // create header
                msg.getSOAPPart().getEnvelope().addHeader();
            }
            newElement = msg.getSOAPHeader().addHeaderElement(
                new QName(CONTEXT_NAMESPACE.getURI(),
                    CONTEXT_ELEMENT_NAME));
        } catch (Exception e) {
            s_logger.error("Error creating SOAP header.");
        }

        Marshaller marshaller = getMarshaller();
        try {
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT,
                Boolean.TRUE);
        } catch (PropertyException e) {
            s_logger.error("Error setting marshaller properties.");
        }

        Map assembledContext = m_contextPassingRegistry
            .getAssembledImplicitContext();
        Set<String> keys = (Set<String>) assembledContext.keySet();
        for (String key : keys) {
            try {
                Object value = assembledContext.get(key);
                marshaller.marshal(new JAXBElement(new QName("", key),
                    Object.class, value), newElement);
            } catch (JAXBException e) {
                s_logger.error("Unable to marshal context for " + key);
            }

        }
    }

    /** {@inheritDoc} */
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Set getHeaders() {
        return new TreeSet();
    }

    /** {@inheritDoc} */
    public void close(MessageContext context) { }


    /** {@inheritDoc} */
    @Override
    protected Logger getLogger() {
        return s_logger;
    }
}
