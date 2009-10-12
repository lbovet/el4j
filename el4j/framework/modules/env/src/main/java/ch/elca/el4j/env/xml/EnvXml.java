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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;
import ch.elca.el4j.core.io.support.OrderedPathMatchingResourcePatternResolver;
import ch.elca.el4j.env.InvalidEnvXmlContentException;
import ch.elca.el4j.env.xml.handlers.BeanOverridesHandler;
import ch.elca.el4j.env.xml.handlers.EnvGroupHandler;
import ch.elca.el4j.env.xml.handlers.PlaceholdersHandler;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.PropertiesHelper;

/**
 * The central class get env properties configured via env.xml files.
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
public class EnvXml {
	/**
	 * The group type name for env placeholders.
	 */
	public static final String ENV_GROUP_PLACEHOLDERS = "placeholders";
	
	/**
	 * The group type name for env bean override properties.
	 */
	public static final String ENV_GROUP_BEAN_OVERRIDES = "bean-overrides";
	
	/**
	 * The env.xml location pattern.
	 */
	public static final String ENV_XML_LOCATION = "classpath*:env.xml";
	
	/**
	 * The env-values.properties location pattern.
	 */
	private static final String ENV_VALUES_PROPERTIES_LOCATION = "classpath*:env-values.properties";
	
	/**
	 * The env-constants.properties location pattern (for final properties).
	 */
	private static final String ENV_CONSTANTS_PROPERTIES_LOCATION = "classpath*:env-constants.properties";
	
	/**
	 * The registered env group handlers.
	 */
	private Map<String, EnvGroupHandler> m_handlers = new HashMap<String, EnvGroupHandler>();
	
	/**
	 * The cached configuration.
	 */
	private Map<String, Object> m_configuration = null;
	
	/**
	 * The resolver to ask for the resources.
	 */
	private final ResourcePatternResolver m_resourcePatternResolver;

	/**
	 * Is most specific resource (that resolver returns) last? 
	 */
	private final boolean m_isMostSpecificResourceLast;
	
	/**
	 * Override values for variable resolution.
	 */
	private Properties m_overrideValues;
	
	/**
	 * The env.xml configuration reader.
	 */
	public EnvXml() {
		ListResourcePatternResolverDecorator resolver = new ListResourcePatternResolverDecorator(
			new ManifestOrderedConfigLocationProvider(),
			new OrderedPathMatchingResourcePatternResolver());
		// most specific resource has to be last, because later properties will overwrite previously set ones.
		resolver.setMostSpecificResourceLast(true);
		resolver.setMergeWithOuterResources(true);
		
		m_resourcePatternResolver = resolver;
		m_isMostSpecificResourceLast = true;
		createDefaultHandlers();
	}
	/**
	 * The env.xml configuration reader.
	 * @param resourcePatternResolver     the resolver to ask for the resources
	 * @param mostSpecificResourceLast    is most specific resource last
	 */
	public EnvXml(ResourcePatternResolver resourcePatternResolver, boolean mostSpecificResourceLast) {
		m_resourcePatternResolver = resourcePatternResolver;
		m_isMostSpecificResourceLast = mostSpecificResourceLast;
		createDefaultHandlers();
	}
	
	/**
	 * Create and register default group handlers.
	 */
	private void createDefaultHandlers() {
		m_handlers.put(ENV_GROUP_PLACEHOLDERS, new PlaceholdersHandler());
		m_handlers.put(ENV_GROUP_BEAN_OVERRIDES, new BeanOverridesHandler());
	}
	
	/**
	 * @return    the configuration for each group
	 */
	private Map<String, Object> retrieveConfiguration() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser saxParser = factory.newSAXParser();
			
			EnvSaxHandler saxHandler = new EnvSaxHandler(m_handlers);
			
			Resource[] xmlResources = m_resourcePatternResolver.getResources(ENV_XML_LOCATION);
			if (!m_isMostSpecificResourceLast) {
				ArrayUtils.reverse(xmlResources);
			}
			for (Resource resource : xmlResources) {
				saxHandler.startResource(resource);
				saxParser.parse(resource.getInputStream(), saxHandler);
			}
			
			// read values from properties files
			Resource[] propResources = m_resourcePatternResolver.getResources(ENV_VALUES_PROPERTIES_LOCATION);
			if (!m_isMostSpecificResourceLast) {
				ArrayUtils.reverse(propResources);
			}
			Properties values = PropertiesHelper.loadPropertiesFromResources(propResources);
			
			// read constants from properties files
			Resource[] constPropResources = m_resourcePatternResolver.getResources(ENV_CONSTANTS_PROPERTIES_LOCATION);
			if (!m_isMostSpecificResourceLast) {
				ArrayUtils.reverse(constPropResources);
			}
			values.putAll(PropertiesHelper.loadPropertiesFromResources(constPropResources));
			
			// override values
			if (m_overrideValues != null) {
				values.putAll(m_overrideValues);
			}
			
			for (EnvGroupHandler handler : m_handlers.values()) {
				handler.filterData(values);
			}
			
			Map<String, Object> config = new HashMap<String, Object>();
			for (String handlerName : m_handlers.keySet()) {
				config.put(handlerName, m_handlers.get(handlerName).getData());
			}
			return config;
		} catch (InvalidEnvXmlContentException e) {
			throw e;
		} catch (Exception e) {
			CoreNotificationHelper.notifyMisconfiguration("Error while loading env.xml files.", e);
			return null;
		}
	}
	
	/**
	 * @return    <code>true</code> if at least one env.xml file could be found.
	 */
	public boolean hasValidConfigurations() {
		try {
			return m_resourcePatternResolver.getResources(ENV_XML_LOCATION).length > 0;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @return    the override values for variable resolution
	 */
	public Properties getOverrideValues() {
		return m_overrideValues;
	}
	
	/**
	 * @param overrideValues    the override values for variable resolution
	 */
	public void setOverrideValues(Properties overrideValues) {
		m_overrideValues = overrideValues;
	}
	
	/**
	 * Get the configuration of a specif env group.
	 * @param groupType    the group type name
	 * @return             the configuration
	 */
	public Object getGroupConfiguration(String groupType) {
		if (m_configuration == null) {
			m_configuration = retrieveConfiguration();
		}
		return m_configuration.get(groupType);
	}
	
	/**
	 * Register an env group handler.
	 * @param groupType    the group type name
	 * @param handler      the handler
	 */
	public void registerHandler(String groupType, EnvGroupHandler handler) {
		if (handler == null) {
			m_handlers.remove(groupType);
		} else {
			m_handlers.put(groupType, handler);
		}
		m_configuration = null;
	}
	
	/**
	 * Unregister an env group handler.
	 * @param groupType    the group type name
	 */
	public void unregisterHandler(String groupType) {
		m_handlers.remove(groupType);
		m_configuration = null;
	}
}
