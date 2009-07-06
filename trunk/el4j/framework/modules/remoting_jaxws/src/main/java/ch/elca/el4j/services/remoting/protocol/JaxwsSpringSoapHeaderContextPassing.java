/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.springframework.beans.MutablePropertyValues;

import ch.elca.el4j.services.remoting.protocol.jaxws.JaxwsContextHandler;

/**
 * This class is an extension to the default JAX-WS SOAP class (using Spring) and provides the
 * ability to transfer the implicit context inside the SOAP header using Jaxb
 * to serialize the parameters and context.
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
public class JaxwsSpringSoapHeaderContextPassing extends JaxwsSpring {
	/**
	 * The {@link JAXBContext} instance used to serialize the several
	 * Objects in the implicit context.
	 */
	private JAXBContext m_contextPassingContext = null;
	
	/** {@inheritDoc} */
	@Override
	protected void adaptProxyServiceProperties(MutablePropertyValues props) {
		super.adaptProxyServiceProperties(props);
		
		// If context passing is enabled, add a handler
		if (getImplicitContextPassingRegistry() != null) {
			// Enable implicit context passing
			props.addPropertyValue("handlerResolver", new HandlerResolver() {
				@SuppressWarnings("unchecked")
				public List<Handler> getHandlerChain(PortInfo portInfo) {
					List<Handler> list = new ArrayList<Handler>();
					list.add(new JaxwsContextHandler(
						getImplicitContextPassingRegistry(),
						m_contextPassingContext));
					return list;
				}
			});
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
