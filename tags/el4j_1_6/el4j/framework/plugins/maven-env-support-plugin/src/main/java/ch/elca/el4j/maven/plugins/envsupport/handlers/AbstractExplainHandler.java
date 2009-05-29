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

import org.apache.maven.plugin.logging.Log;
import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;

import ch.elca.el4j.env.InvalidEnvXmlContentException;
import ch.elca.el4j.env.xml.ResolverUtils;
import ch.elca.el4j.env.xml.handlers.AbstractInheritablePropertyHandler;
import ch.elca.el4j.maven.plugins.envsupport.AbstractEnvSupportMojo;

/**
 * A verbose version of {@link AbstractInheritablePropertyHandler}.
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
public abstract class AbstractExplainHandler extends AbstractInheritablePropertyHandler {
	/**
	 * The maven logger.
	 */
	protected final Log m_logger;
	
	/**
	 * Is the resource info header already printed to logger?
	 */
	protected boolean m_resourceHeaderPrinted = false;
	
	/**
	 * The current mojo.
	 */
	protected final AbstractEnvSupportMojo m_mojo;
	
	/**
	 * @param mojo      the current mojo
	 * @param logger    the maven logger
	 */
	public AbstractExplainHandler(AbstractEnvSupportMojo mojo, Log logger) {
		m_mojo = mojo;
		m_logger = logger;
	}
	
	/** {@inheritDoc} */
	public void startResource(Resource resource) {
		m_currentResource = resource;
		m_resourceHeaderPrinted = false;
	}
	
	/** {@inheritDoc} */
	protected void addProperty(Attributes attributes) {
		String entryName = attributes.getValue("name");
		String entryValue = attributes.getValue("value");
		String entryType = attributes.getValue("type");
		
		if (entryValue == null) {
			entryValue = "${" + entryName + "}";
		}
		
		if ("abstract".equalsIgnoreCase(entryType)) {
			m_logger.info("    (abstract) " + entryName);
			m_abstractProperties.put(entryName, m_currentResource);
		} else {
			if ("final".equalsIgnoreCase(entryType)) {
				m_logger.info("    (final) " + entryName + " = " + entryValue);
				m_properties.put(entryName, entryValue);
				m_finalProperties.put(entryName, m_currentResource);
			} else {
				if (m_finalProperties.containsKey(entryName)) {
					throw new InvalidEnvXmlContentException(
						"It is not allowed to overwrite final property '" + entryName + "' in "
						+ m_mojo.getArtifactNameFromResource(m_currentResource)
						+ ".\nAlternatively, you might want to recompile artifact containing '"
						+ m_mojo.getArtifactNameFromResource(m_finalProperties.get(entryName))
						+ "' using the correct settings.");
				}
				m_logger.info("    " + entryName + " = " + entryValue);
			}
			m_abstractProperties.remove(entryName);
			m_properties.put(entryName, entryValue);
		}
	}
	
	/** {@inheritDoc} */
	protected void removeProperty(Attributes attributes) {
		String entryName = attributes.getValue("name");
		m_logger.info("    (remove) " + entryName);
		m_properties.remove(entryName);
		m_abstractProperties.remove(entryName);
		m_finalProperties.remove(entryName);
	}
	
	/** {@inheritDoc} */
	public void filterData(Properties properties) {
		if (m_properties.size() > 0) {
			/*m_logger.info("");
			m_logger.info("  Merged and resolved variables:");
			for (Object objectKey : properties.keySet()) {
				String key = (String) objectKey;
				String value = properties.getProperty(key);
				m_logger.info("    " + key + " = " + value);
			}*/
			m_logger.info("");
			m_logger.info("  Evaluated properties:");
			for (Object objectKey : m_properties.keySet()) {
				String key = (String) objectKey;
				String value = m_properties.getProperty(key);
				String resolvedValue = ResolverUtils.resolve(value, properties);
				// add property to resolved properties map
				properties.setProperty(key, resolvedValue);
				
				m_logger.info("    " + key + " = " + resolvedValue);
				
				m_properties.setProperty(key, resolvedValue);
			}
		}
	}
	
	/** {@inheritDoc} */
	public Object getData() {
		if (m_properties.size() > 0) {
			m_logger.info("");
			if (!m_abstractProperties.isEmpty()) {
				m_logger.info("  Checking properties:");
				for (String abstractProperty : m_abstractProperties.keySet()) {
					m_logger.warn("     Abstract property '" + abstractProperty + "' defined in '"
						+ m_mojo.getArtifactNameFromResource(m_abstractProperties.get(abstractProperty))
						+ "' is not set.");
				}
			} else {
				m_logger.info("  Checking properties: OK");
			}
			m_logger.info("");
		}
		return m_properties;
	}
}
