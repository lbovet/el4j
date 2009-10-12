/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.env.xml;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.elca.el4j.env.xml.handlers.EnvGroupHandler;

/**
 * The main SAX handler to parse the env.xml file.
 * It delegates the env group sections to the handlers specified in the constructor.
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
public class EnvSaxHandler extends DefaultHandler {
	/**
	 * The env group handlers.
	 */
	private Map<String, EnvGroupHandler> m_handlers;
	
	/**
	 * The active group handler.
	 */
	private EnvGroupHandler m_currentGroupHandler;
	
	/**
	 * The current depth in the XML tree.
	 */
	private int m_depth = 0;
	
	/**
	 * @param handlers    the handlers that handle a specific env group (e.g. "placeholders" -> PlaceholdersHandler).
	 */
	public EnvSaxHandler(Map<String, EnvGroupHandler> handlers) {
		m_handlers = handlers;
	}
	
	/**
	 * Notify which resource will be parsed next.
	 * @param resource    the resource that will be parsed next
	 */
	public void startResource(Resource resource) {
		for (EnvGroupHandler handler : m_handlers.values()) {
			if (handler != null) {
				handler.startResource(resource);
			}
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (m_depth == 0) {
			assert qName.equals("env");
		} else if (m_depth == 1) {
			if (qName.equals("group")) {
				m_currentGroupHandler = m_handlers.get(attributes.getValue("type"));
			} else {
				m_currentGroupHandler = m_handlers.get(qName);
			}
		} else if (m_currentGroupHandler != null) {
			m_currentGroupHandler.startElement(uri, localName, qName, attributes);
		}
		m_depth++;
	}
	
	/** {@inheritDoc} */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (m_currentGroupHandler != null) {
			m_currentGroupHandler.characters(ch, start, length);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		if (m_currentGroupHandler != null) {
			m_currentGroupHandler.ignorableWhitespace(ch, start, length);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (m_currentGroupHandler != null) {
			m_currentGroupHandler.endElement(uri, localName, name);
		}
		m_depth--;
	}
}
