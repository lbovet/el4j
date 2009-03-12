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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

import ch.elca.el4j.util.encryption.AbstractPropertyEncryptor;
import ch.elca.el4j.util.encryption.EncryptionException;
import ch.elca.el4j.util.encryption.PasswordSource;
import ch.elca.el4j.util.env.PropertyEncryptionUtil;

/**
 * Specific property override configurer for the EL4J environment.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class EnvPropertyOverrideConfigurer extends PropertyOverrideConfigurer
	implements ApplicationContextAware {

	/**
	 * The env bean property location.
	 */
	public static final String ENV_BEAN_PROPERTY_PROPERTIES_LOCATION
		= "classpath*:env-bean-property.properties";

	/**
	 * This logger is used to print out some global debugging info. Consult it
	 * for info what is going on.
	 */
	protected static final Log s_logger
		= LogFactory.getLog(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);

	/**
	 * Is the used application context.
	 */
	protected ApplicationContext m_applicationContext;

	/**
	 * Should invalid bean names be ignored?
	 */
	protected boolean m_ignoreBeanNameNotFound = false;

	/* These handle encryption. */

	/**
	 * Property encryption handler.
	 */
	protected PropertyEncryptionUtil m_util = new PropertyEncryptionUtil();
	
	/**
	 * Handles the actual encryption.
	 */
	private AbstractPropertyEncryptor m_cryptor;

	/**
	 * Custom location and name for the cryptor.properties file.
	 */
	private String m_cryptorFile = null;
	
	/**
	 * Sets the location of the env bean property properties file. {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(
		ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Resource[] envBeanPropertyLocations = null;
		try {
			envBeanPropertyLocations = m_applicationContext
				.getResources(ENV_BEAN_PROPERTY_PROPERTIES_LOCATION);
		} catch (IOException e1) {
			s_logger
				.warn("No env bean property properties file could be found. The"
					+ " correct location for this file is '"
					+ ENV_BEAN_PROPERTY_PROPERTIES_LOCATION + "'.");
		}
		
		// reverse array to make nearer env files override farther env files
		ArrayUtils.reverse(envBeanPropertyLocations);
		
		// check that every env property location exists
		boolean atLeastOneEnvLocationExists = false;
		for (Resource envBeanPropertyLocation : envBeanPropertyLocations) {
			String envBeanPropertyLocationUrl = "no url";
			if (envBeanPropertyLocation.exists()) {
				atLeastOneEnvLocationExists = true;
				try {
					envBeanPropertyLocationUrl = envBeanPropertyLocation.getURL()
						.toString();
				} catch (IOException e) {
					envBeanPropertyLocationUrl = "unknown url";
				}
			}

			if (envBeanPropertyLocation.exists()) {
				s_logger
					.debug("The used env bean property properties file is '"
						+ envBeanPropertyLocationUrl + "'.");
			}
		}
		
		if (!atLeastOneEnvLocationExists) {
			s_logger
				.warn("No env bean property properties file could be found. The"
					+ " correct location for this file is '"
					+ ENV_BEAN_PROPERTY_PROPERTIES_LOCATION + "'.");
		}

		super.setLocations(envBeanPropertyLocations);

		super.postProcessBeanFactory(beanFactory);
	}

	/** {@inheritDoc} */
	@Override
	protected void applyPropertyValue(ConfigurableListableBeanFactory factory,
		String beanName, String property, String value) {
		try {
			super.applyPropertyValue(factory, beanName, property, value);
		} catch (NoSuchBeanDefinitionException e) {
			if (m_ignoreBeanNameNotFound) {
				s_logger
					.warn("No bean with name '" + beanName
						+ "' found. Therefore "
						+ "no properties could be applied.");
			} else {
				s_logger
					.error("No bean with name '" + beanName
						+ "' found. Therefore "
						+ "no properties could be applied.");
				throw e;
			}
		}
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
	 * @return Returns <code>true</code> if ignoreBeanNameNotFound is set.
	 */
	public boolean isIgnoreBeanNameNotFound() {
		return m_ignoreBeanNameNotFound;
	}

	/**
	 * @param ignoreBeanNameNotFound
	 *            Should invalid bean names be ignored?
	 */
	public void setIgnoreBeanNameNotFound(boolean ignoreBeanNameNotFound) {
		this.m_ignoreBeanNameNotFound = ignoreBeanNameNotFound;
	}

	/**
	 * This is meant to be used as a bean property to turn off encryption in an
	 * internal setting.
	 */
	public void setNoEncryption() {
		m_util.deactivate();
	}

	/**
	 * Set a true custom password.
	 *
	 * @param source A PasswordSource interface.
	 */
	public void setPasswordSource(PasswordSource source) {
		m_util.setSource(source);
	}

	/**
	 * @param file The file containing the encryption properties.
	 */
	public void setCryptorFile(String file) {
		m_cryptorFile = file;
	}

	/**
	 * Decrypts values read from the env-*.properties files.
	 *
	 * @param originalValue
	 *   The value read from env-*.properties The initialization cannot
	 *   go in constructor as we don't have all the required properties
	 *   there yet.
	 * @return The string with all encrypted values decrypted.
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
