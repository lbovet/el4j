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
package ch.elca.el4j.services.remoting.protocol;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.codehaus.xfire.handler.Handler;
import org.springframework.beans.MutablePropertyValues;

import ch.elca.el4j.services.remoting.protocol.xfire.XFireJaxbContextInHandler;
import ch.elca.el4j.services.remoting.protocol.xfire.XFireJaxbContextOutHandler;

/**
 * 
 * This class is an extension to the default XFire protocol and provides the
 * ability to transfer the implicit context inside the SOAP header using Jaxb
 * to serialize the parameters and context.
 * It is intended to be used together with the {@link JaxbServiceFactory} or the
 * {@link JaxbServiceFactoryWithoutAnnotations}.
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
public class XFireSoapHeaderContextPassing extends XFire {
    /**
     * The {@link JAXBContext} instance used to serialize the several
     * Objects in the implicit context.
     */
    private JAXBContext m_contextPassingContext = null;
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void adaptExporterProperties(MutablePropertyValues properties) {
        super.adaptExporterProperties(properties);
        
        // If context passing is enabled, add a handler
        if (getImplicitContextPassingRegistry() != null 
            && getProtocolSpecificContextPassing()) {
            // Enable implicit context passing
            List<Handler> inHandlers = new LinkedList<Handler>();
            inHandlers.add(new XFireJaxbContextInHandler(
                getImplicitContextPassingRegistry(), 
                getContextPassingContext()));
            properties.addPropertyValue("inHandlers", inHandlers);
        }
        
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void adaptProxyProperties(MutablePropertyValues properties) {
        super.adaptProxyProperties(properties);
        
        if (getImplicitContextPassingRegistry() != null 
            && getProtocolSpecificContextPassing()) {
            // Enable implicit context passing
            List<Handler> outHandlers = new LinkedList<Handler>();
            outHandlers.add(new XFireJaxbContextOutHandler(
                getImplicitContextPassingRegistry(), 
                getContextPassingContext()));
            
            // Add the Handler to add
            properties.addPropertyValue("outHandlers", outHandlers);
        }
    }
    
    /**
     * Get the {@link JAXBContext} used to serialize the implicit context.
     * @return The used {@link JAXBContext} 
     */
    public JAXBContext getContextPassingContext() {
        return m_contextPassingContext;
    }
    
    /**
     * Set the {@link JAXBContext} used to serialize the implicit context.
     * @param context The {@link JAXBContext} to use
     */
    public void setContextPassingContext(JAXBContext context) {
        m_contextPassingContext = context;
    }
}
