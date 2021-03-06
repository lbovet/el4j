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
package ch.elca.el4j.services.remoting.protocol.jaxws;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jdom.Namespace;
import org.slf4j.Logger;

/**
 *
 * Baseclass for the JAXB context handlers, implementing some
 * common functionalities.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philippe Jacot (PJA)
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractJaxwsJaxbContextHandler
	implements SOAPHandler<SOAPMessageContext> {
	
	/**
	 * Name of the element in the header containing the context.
	 */
	protected static final String CONTEXT_ELEMENT_NAME = "context";
	
	/**
	 * Name of the namespace of the element containing the context.
	 */
	protected static final Namespace CONTEXT_NAMESPACE = Namespace.getNamespace(
		"ch.elca.el4j.services.remoting.protocol.jaxws");
	
	/**
	 * The context used to serialize the implicit context.
	 */
	private JAXBContext m_jaxbContext;
	
	/**
	 * Creates a new hander with a context.
	 * @param jaxbContext The <code>JAXBContext</code> to serialize the context
	 */
	public AbstractJaxwsJaxbContextHandler(JAXBContext jaxbContext) {
		if (jaxbContext == null) {
			try {
				m_jaxbContext = JAXBContext.newInstance(new Class[]{});
			} catch (JAXBException e) {
				throw new NullPointerException("No JAXBContext passed and "
					+ "unable to create an empty one: " + e.getMessage());
			}
		} else {
			m_jaxbContext = jaxbContext;
		}
	}
	
	/**
	 * Get a JaxbContext.
	 * @return A JaxbContext
	 */
	protected JAXBContext getJaxbContext() {
		return m_jaxbContext;
	}
	
	/**
	 * Convenience method to get a marshaller.
	 * @return A marshaller
	 */
	protected Marshaller getMarshaller() {
		try {
			return m_jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			getLogger().error(
				"Unable to create marshaller for context passing", e);
			return null;
		}
	}
	
	/**
	 * Convenience method to get an unmarshaller.
	 * @return A marshaller
	 */
	protected Unmarshaller getUnmarshaller() {
		try {
			return m_jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			getLogger().error(
				"Unable to create unmarshaller for context passing", e);
			return null;
		}
	}
	
	/**
	 * Get the logger for this class.
	 * @return The Logger
	 */
	protected abstract Logger getLogger();
	
}
