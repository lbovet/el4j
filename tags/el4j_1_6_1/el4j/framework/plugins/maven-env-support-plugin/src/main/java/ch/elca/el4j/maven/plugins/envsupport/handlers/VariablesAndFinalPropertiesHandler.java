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
package ch.elca.el4j.maven.plugins.envsupport.handlers;

import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler looking for tags having the attributes 'value' or 'final'.
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
public class VariablesAndFinalPropertiesHandler extends DefaultHandler {
	/**
	 * The final properties.
	 */
	protected Properties m_finalProperties = new Properties();
	
	/**
	 * The concatenated values from all tags having a 'value' attribute.
	 */
	protected StringBuilder m_concatenatedValues = new StringBuilder();
	
	/**
	 * The current depth in the XML tree.
	 */
	private int m_depth = 0;
	
	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (m_depth == 0) {
			assert qName.equals("env");
		} else {
			String entryName = attributes.getValue("name");
			String entryValue = attributes.getValue("value");
			String entryType = attributes.getValue("type");
			if (entryName != null) {
				if (entryValue == null) {
					entryValue = "${" + entryName + "}";
				}
				m_concatenatedValues.append(entryValue);
				if ("final".equalsIgnoreCase(entryType)) {
					m_finalProperties.put(entryName, entryValue);
				}
			}
		}
		m_depth++;
	}
	
	/** {@inheritDoc} */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		m_depth--;
	}
	
	/**
	 * @return    all final properties
	 */
	public Properties getFinalProperties() {
		return m_finalProperties;
	}
	
	/**
	 * @return    the concatenated values from all tags having a 'value' attribute.
	 */
	public String getConcatenatedValues() {
		return m_concatenatedValues.toString();
	}
}
