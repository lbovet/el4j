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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * 
 * This class is an XFire Handler that takes care of retrieving the context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
public class XFireJaxbContextInHandler extends AbstractXFireJaxbContextHandler {

    /**
     * The context registry.
     */
    private ImplicitContextPassingRegistry m_registry;
    
    /**
     * The logger.
     */
    private Logger m_logger = Logger.getLogger(XFireJaxbContextInHandler.class);
    
    /**
     * Create a new Context Handler that tries to get the implicit context from
     * the Soap header.
     * @param registry The registry to push the context into
     * @param jaxbContext The context to serialize the implicit context
     */
    public XFireJaxbContextInHandler(ImplicitContextPassingRegistry registry, 
        JAXBContext jaxbContext) {
        super(jaxbContext);
        
        this.m_registry = registry;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void invoke(MessageContext context) throws Exception {
        // Try to get implicit context from soap header
        if (m_registry != null && context.getCurrentMessage().hasHeader()) {
            Element implicitContext = context.getCurrentMessage().getHeader()
            .getChild(CONTEXT_ELEMENT_NAME, CONTEXT_NAMESPACE);
            
            if (implicitContext != null) {
                List<Element> children 
                    = (List<Element>) implicitContext.getChildren();
                
                Unmarshaller unmarshaller = getUnmarshaller();
                
                Map<String, Object> map = new HashMap<String, Object>();
                
                for (Element keyElement : children) {
                    try {
                        // Try to get the passed Object out of the xml
                        JAXBElement element = (JAXBElement) unmarshaller
                            .unmarshal(new JDOMStreamReader(keyElement));
                        
                        map.put(keyElement.getName(), element.getValue());
                    } catch (JAXBException e) {
                        m_logger.error("Unable to unmarshall context element " 
                            + keyElement.getName());
                        throw e;
                    }
                }                
                m_registry.pushAssembledImplicitContext(map);
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return m_logger;
    }
}


