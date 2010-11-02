/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.core.context.ModuleApplicationContextCreationListener;

/**
 * Application context loader for tests with the module application context.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class ModuleTestContextLoader extends AbstractContextLoader
	implements ModuleApplicationContextCreationListener {
	/**
	 * Private logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(ModuleTestContextLoader.class);

	/**
	 * Loads a ModuleApplicationContext from the supplied <code>locations</code>.
	 * 
	 * {@inheritDoc}
	 */
	public ConfigurableApplicationContext loadContext(String... locations) throws Exception {
		if (s_logger.isDebugEnabled()) {
			s_logger.debug("Loading ModuleApplicationContext for locations ["
				+ StringUtils.arrayToCommaDelimitedString(locations) + "].");
		}
		ModuleApplicationContextConfiguration config = new ModuleApplicationContextConfiguration();
		config.setInclusiveConfigLocations(locations);
		config.setModuleApplicationContextCreationListener(this);
		customizeModuleApplicationContextConfiguration(config);
		ModuleApplicationContext context = new ModuleApplicationContext(config);
		return context;
	}

	/**
	 * Interception method to customize the configuration of the module application context, 
	 * before the module application context will be created.
	 * 
	 * @param config Is the module application context configuration.
	 */
	protected void customizeModuleApplicationContextConfiguration(ModuleApplicationContextConfiguration config) { }

	/**
	 * Returns &quot;<code>-context.xml</code>&quot;.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceSuffix() {
		return "-context.xml";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		AnnotationConfigUtils.registerAnnotationConfigProcessors((BeanDefinitionRegistry) beanFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finishRefresh(ModuleApplicationContext context) {
		context.registerShutdownHook();
	}
}
