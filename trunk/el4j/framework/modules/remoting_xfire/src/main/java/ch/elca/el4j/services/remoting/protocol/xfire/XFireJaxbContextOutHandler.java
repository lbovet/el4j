/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol.xfire;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.stax.JDOMStreamWriter;
import org.jdom.Element;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class is a XFire handler that takes care of appending the implicit
 * context to the Soap header. 
 * 
 * <script type="text/javascript">printFileStatus
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$",
 *  "$Author$" 
 * );</script>
 * 
 * @author Philippe Jacot (PJA)
 */
public class XFireJaxbContextOutHandler 
    extends AbstractXFireJaxbContextHandler {
    
    /**
     * The logger.
     */
    private static Logger s_logger = Logger.getLogger(
        XFireJaxbContextOutHandler.class);

    /**
     * The registry to get the context from.
     */
    private ImplicitContextPassingRegistry m_contextPassingRegistry;
    

    /**
     * Create a new XFire Handler to add the context to a soap message.
     * 
     * @param registry The registry to take the context from
     * @param jaxbContext The context to serialize the implicit context with
     */
    public XFireJaxbContextOutHandler(ImplicitContextPassingRegistry registry, 
        JAXBContext jaxbContext) {
        super(jaxbContext);
        this.m_contextPassingRegistry = registry;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void invoke(MessageContext context) throws Exception {
        Element implicitContext = new Element(CONTEXT_ELEMENT_NAME,
            CONTEXT_NAMESPACE);
        context.getCurrentMessage().getOrCreateHeader().addContent(
            implicitContext);

        Marshaller marshaller = getMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        Map assembledContext = m_contextPassingRegistry
            .getAssembledImplicitContext();
        Set<String> keys = (Set<String>) assembledContext.keySet();
        for (String key : keys) {
            JDOMStreamWriter writer = new JDOMStreamWriter(implicitContext);
            try {
                Object value = assembledContext.get(key);
                marshaller.marshal(new JAXBElement(new QName("", key), 
                    Object.class, value), writer);
            } catch (JAXBException e) {
                s_logger.error("Unable to marshal context for " + key);
                throw e;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return s_logger;
    }
}
