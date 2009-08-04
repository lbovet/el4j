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
package ch.elca.el4j.util.env;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.elca.el4j.env.beans.EnvPropertyOverrideConfigurer;
import ch.elca.el4j.env.beans.EnvPropertyPlaceholderConfigurer;
import ch.elca.el4j.env.xml.EnvXml;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.PropertiesHelper;

/**
 * This class provides access to the currently used environment properties.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public class EnvPropertiesUtils {
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(CollectionUtils.class);
	
	/**
	 * Hide default constructor.
	 */
	protected EnvPropertiesUtils() { }
	
	/**
	 * Retrieves the currently used environment properties.
	 * @return The currently used environment properties.
	 *
	 * @deprecated Use method {@link #getEnvPlaceholderProperties()} instead.
	 */
	@Deprecated
	public static Properties getEnvProperties() {
		s_logger.debug(
			"DEPRECATED: Use method 'getEnvPlaceholderProperties' instead.");
		return getEnvPlaceholderProperties();
	}
	
	/**
	 * Retrieves the currently used placeholder environment properties.
	 * @return The currently used placeholder environment properties.
	 */
	public static Properties getEnvPlaceholderProperties() {
		return getEnvPlaceholderProperties(null, true);
	}
	
	/**
	 * Retrieves the currently used placeholder environment properties.
	 * @param resourcePatternResolver     the resolver to ask for the resources
	 * @param mostSpecificResourceLast    is most specific resource last
	 * @return The currently used placeholder environment properties.
	 */
	public static Properties getEnvPlaceholderProperties(
		ResourcePatternResolver resourcePatternResolver, boolean mostSpecificResourceLast) {
		Properties properties;
		EnvXml parser;
		if (resourcePatternResolver != null) {
			parser = new EnvXml(resourcePatternResolver, mostSpecificResourceLast);
		} else {
			parser = new EnvXml();
		}
		if (parser.hasValidConfigurations()) {
			properties = (Properties) parser.getGroupConfiguration(EnvXml.ENV_GROUP_PLACEHOLDERS);
		} else {
			properties = new Properties();
		}
		properties.putAll(new PropertiesHelper().loadProperties(
			EnvPropertyPlaceholderConfigurer.ENV_PLACEHOLDER_PROPERTIES_LOCATION));
		
		return properties;
	}
	/**
	 * Retrieves the currently used bean property environment properties.
	 * @return The currently used bean override properties.
	 */
	public static Properties getEnvBeanPropertyProperties() {
		return getEnvBeanPropertyProperties(null, true);
	}
	/**
	 * Retrieves the currently used bean property environment properties.
	 * @param resourcePatternResolver     the resolver to ask for the resources
	 * @param mostSpecificResourceLast    is most specific resource last
	 * @return The currently used bean override properties.
	 */
	public static Properties getEnvBeanPropertyProperties(
		ResourcePatternResolver resourcePatternResolver, boolean mostSpecificResourceLast) {
		
		Properties properties;
		EnvXml parser;
		if (resourcePatternResolver != null) {
			parser = new EnvXml(resourcePatternResolver, mostSpecificResourceLast);
		} else {
			parser = new EnvXml();
		}
		if (parser.hasValidConfigurations()) {
			properties = (Properties) parser.getGroupConfiguration(EnvXml.ENV_GROUP_BEAN_OVERRIDES);
		} else {
			properties = new Properties();
		}
		properties.putAll(new PropertiesHelper().loadProperties(
			EnvPropertyOverrideConfigurer.ENV_BEAN_PROPERTY_PROPERTIES_LOCATION));
		
		return properties;
	}
}
