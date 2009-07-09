/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.env.beans;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.env.xml.EnvXml;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.encryption.AbstractPropertyEncryptor;
import ch.elca.el4j.util.encryption.EncryptionException;
import ch.elca.el4j.util.encryption.PasswordSource;
import ch.elca.el4j.util.env.PropertyEncryptionUtil;

/**
 * Specific property placeholder configurer for the EL4J environment.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Stefan Wismer (SWI)
 */
public class EnvPropertyPlaceholderConfigurer
	extends PropertyPlaceholderConfigurer implements ApplicationContextAware {
	
	/**
	 * The correct env location.
	 */
	public static final String ENV_PLACEHOLDER_PROPERTIES_LOCATION
		= "classpath:env-placeholder.properties";
	
	/**
	 * The deprecated env location.
	 */
	public static final String OLD_ENV_PROPERTIES_LOCATION
		= "classpath:env/env.properties";

	/**
	 * This logger is used to print out some global debugging info.
	 * Consult it for info what is going on.
	 */
	protected static final Logger s_logger
		= LoggerFactory.getLogger(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);
	
	/**
	 * Is the used application context.
	 */
	protected ApplicationContext m_applicationContext;
	
	/**
	 * This handles encryption.
	 */
	protected PropertyEncryptionUtil m_util = new PropertyEncryptionUtil();
	
	/** The cryptor is available in internal and handles resource decryption.
	 *  It is initialized by util.
	 */
	private AbstractPropertyEncryptor m_cryptor;

	/**
	 * Custom location and name for the cryptor.properties file.
	 */
	private String m_cryptorFile = null;
	
	/**
	 * Sets the location of the env placeholder properties file. A fallback to
	 * the old env location is possible.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		// very old env location is not supported anymore
		Resource oldEnvLocation = m_applicationContext.getResource(OLD_ENV_PROPERTIES_LOCATION);
		if (oldEnvLocation.exists()) {
			throw new MisconfigurationRTException(
				"DEPRECATED: The used env placeholder properties file '"
					+ oldEnvLocation.toString()
					+ "' is deprecated. Please use the new loaction '"
					+ ENV_PLACEHOLDER_PROPERTIES_LOCATION + "'.");
		}
		
		// new inheritable env support
		boolean envXmlFound = false;
		EnvXml envXmlConfigLoader;
		if (m_applicationContext instanceof ModuleApplicationContext) {
			ModuleApplicationContext mac = (ModuleApplicationContext) m_applicationContext;
			envXmlConfigLoader = new EnvXml(m_applicationContext, mac.isMostSpecificResourceLast());
		} else {
			envXmlConfigLoader = new EnvXml();
		}
		if (envXmlConfigLoader.hasValidConfigurations()) {
			super.setProperties((Properties) envXmlConfigLoader.getGroupConfiguration(EnvXml.ENV_GROUP_PLACEHOLDERS));
			super.setLocalOverride(true);
			envXmlFound = true;
		}
		
		// old only-one-env-on-classpath strategy
		Resource envLocation = m_applicationContext.getResource(ENV_PLACEHOLDER_PROPERTIES_LOCATION);
		String envLocationUrl = "no url";
		if (envLocation.exists()) {
			try {
				envLocationUrl = envLocation.getURL().toString();
			} catch (IOException e) {
				envLocationUrl = "unknown url";
			}
		}
		
		if (!envXmlFound) {
			if (envLocation.exists()) {
				s_logger.debug("The used env placeholder properties file is '"
					+ envLocationUrl + "'.");
				super.setLocation(envLocation);
			} else {
				s_logger.warn(
					"No env placeholder properties file could be found. The "
						+ "correct location for this file is '"
						+ EnvXml.ENV_XML_LOCATION + "'.");
			}
		}
		
		super.postProcessBeanFactory(beanFactory);
	}
	
	/**
	 * NOT ALLOWED TO USE!
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(Resource location) {
		CoreNotificationHelper.notifyMisconfiguration(
			"It is not allowed to set the location manually!");
	}
	
	/**
	 * NOT ALLOWED TO USE!
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setLocations(Resource[] locations) {
		CoreNotificationHelper.notifyMisconfiguration(
			"It is not allowed to set the locations manually!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		m_applicationContext = applicationContext;
	}
	
	/**
	 * This is meant to be used as a bean property to turn off encryption
	 * in an internal setting.
	 */
	public void setNoEncryption() {
		m_util.deactivate();
	}
	
	/**
	 * Set a true custom password.
	 * @param source A PasswordSource (interface).
	 */
	public void setPasswordSource(PasswordSource source) {
		m_util.setSource(source);
	}
	
	/**
	 * @param file The file containing the cryptor settings.
	 */
	public void setCryptorFile(String file) {
		m_cryptorFile = file;
	}
	
	/**
	 * Decrypts values read from the env-*.properties files.
	 * 
	 * @param originalValue  The value read from env-*.properties
	 * @return The value with all encrypted values decrypted.
	 */
	protected String convertPropertyValue(String originalValue) {
		String value = originalValue;
		
		if (!m_util.isInited()) {
			if (m_cryptorFile != null) {
				m_util.init(m_applicationContext, m_cryptorFile);
			} else {
				m_util.init(m_applicationContext);
			}
			if (m_util.isActive()) {
				m_cryptor = m_util.getCryptor();
			}
		}
		if (m_util.isActive()) {
			try {
				value = m_cryptor.processString(originalValue);
			} catch (EncryptionException e) {
				throw new BeanDefinitionStoreException(
					"Error during decryption.");
			}
		}
		
		return super.convertPropertyValue(value);
	}
}
