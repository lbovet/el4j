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
package ch.elca.el4j.env.xml.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ch.elca.el4j.env.InvalidEnvXmlContentException;
import ch.elca.el4j.env.xml.ResolverUtils;

/**
 * An {@link EnvGroupHandler} that handles inheritable properties (which can also be abstract or final).
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
public abstract class AbstractInheritablePropertyHandler extends DefaultHandler implements EnvGroupHandler {
	/**
	 * The ordinary properties.
	 */
	protected Properties m_properties = new Properties();
	
	/**
	 * The abstract property names and the resource they are declared.
	 */
	protected Map<String, Resource> m_abstractProperties = new HashMap<String, Resource>();
	
	/**
	 * The final property names and the resource they are declared.
	 */
	protected Map<String, Resource> m_finalProperties = new HashMap<String, Resource>();
	
	/**
	 * The resource currently being propcessed. 
	 */
	protected Resource m_currentResource;
	
	/** {@inheritDoc} */
	public void startResource(Resource resource) {
		m_currentResource = resource;
	}
	
	/**
	 * Add a property given by an xml tag having the attributes 'name' (required), 'value' and 'type'.
	 * @param attributes    the tag attributes
	 */
	protected void addProperty(Attributes attributes) {
		String entryName = attributes.getValue("name");
		String entryValue = attributes.getValue("value");
		String entryType = attributes.getValue("type");
		
		if (entryValue == null) {
			entryValue = "${" + entryName + "}";
		}
		
		if ("abstract".equalsIgnoreCase(entryType)) {
			m_abstractProperties.put(entryName, m_currentResource);
		} else {
			if ("final".equalsIgnoreCase(entryType)) {
				m_properties.put(entryName, entryValue);
				m_finalProperties.put(entryName, m_currentResource);
			} else {
				if (m_finalProperties.containsKey(entryName)) {
					throw new InvalidEnvXmlContentException(
						"It is not allowed to overwrite final property '" + entryName + "' in "
						+ m_currentResource.toString()
						+ ".\nAlternatively, you might want to recompile artifact containing '"
						+ m_finalProperties.get(entryName).toString());
				}
			}
			m_abstractProperties.remove(entryName);
			m_properties.put(entryName, entryValue);
		}
	}
	
	/**
	 * Remove a property given by an xml tag having the attribute 'name' (required).
	 * @param attributes    the tag attributes
	 */
	protected void removeProperty(Attributes attributes) {
		String entryName = attributes.getValue("name");
		m_properties.remove(entryName);
		m_abstractProperties.remove(entryName);
		m_finalProperties.remove(entryName);
	}
	
	/** {@inheritDoc} */
	public void filterData(Properties properties) {
		for (Object objectKey : m_properties.keySet()) {
			String key = (String) objectKey;
			String value = m_properties.getProperty(key);
			
			String resolvedValue;
			if (m_finalProperties.containsKey(key)) {
				resolvedValue = properties.getProperty(key);
			} else {
				resolvedValue = ResolverUtils.resolve(value, properties);
				
				// add property to resolved properties map
				properties.setProperty(key, resolvedValue);
			}
			m_properties.setProperty(key, resolvedValue);
		}
	}
	
	
	/** {@inheritDoc} */
	public Object getData() {
		// env plugin already warns if it detects abstract properties
		/*if (!m_abstractProperties.isEmpty()) {
			for (String abstractProperty : m_abstractProperties.keySet()) {
				s_logger.warn(
					"Abstract property '" + abstractProperty + "' defined in '"
					+ m_abstractProperties.get(abstractProperty).toString() + "' must be set.");
			}
		}*/
		return m_properties;
	}
}
