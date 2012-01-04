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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.core.context.ModuleApplicationContextCreationListener;
import ch.elca.el4j.tests.core.context.ExtendedContextConfiguration;
import ch.elca.el4j.tests.core.context.junit4.EL4JJunit4ClassRunner;

/**
 * Application context loader for tests with the module application context.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 * @author Martin Zeltner (MZE)
 */
public class ModuleTestContextLoader extends AbstractContextLoader
	implements ModuleApplicationContextCreationListener {
	
	/**
	 * Private logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(ModuleTestContextLoader.class);

	/**
	 * Set by {@link EL4JJunit4ClassRunner} upon instanciation of the test class.
	 * Needed to discover <code>@ExtendedContextConfiguration</code> annotations which
	 * influence the configuration of the ModuleApplicationContext to be loaded.
	 */
	private static ThreadLocal<Class<?>> s_testedClass = new ThreadLocal<Class<?>>();
	
	/**
	 * Loads a ModuleApplicationContext from the supplied <code>locations</code>
	 * using the ModuleApplicationContextConfiguration created in 
	 * <code>createModuleApplicationContextConfiguration</code>.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ConfigurableApplicationContext loadContext(String... locations) throws Exception {
		if (s_logger.isDebugEnabled()) {
			s_logger.debug("Loading ModuleApplicationContext for locations ["
				+ StringUtils.arrayToCommaDelimitedString(locations) + "].");
		}
		ModuleApplicationContextConfiguration config = createModuleApplicationContextConfiguration();
		config.setInclusiveConfigLocations(locations);
		config.setModuleApplicationContextCreationListener(this);
		customizeModuleApplicationContextConfiguration(config);
		ModuleApplicationContext context = new ModuleApplicationContext(config);
		return context;
	}
	
	/**
	 * Finds <code>@ExtendedContextConfiguration</code> annotations along the inheritance
	 * graph of the test class and aggregates the information into an instance of
	 * ModuleApplicationsContextConfiguration.
	 * @throws IllegalStateException if the tested class or a superclass of it is annotated
	 *         with a <code>@ExtendedContextConfiguration</code> but none of its transitive
	 *         superclasses are annotated with a <code>@ContextConfiguration</code>.
	 * @return the aggregated context configuration
	 */
	private ModuleApplicationContextConfiguration createModuleApplicationContextConfiguration() 
		throws IllegalStateException {
		ModuleApplicationContextConfiguration resultingConfiguration = new ModuleApplicationContextConfiguration();
		recursivelyConfigure(s_testedClass.get(), resultingConfiguration);
		return resultingConfiguration;
	}
	
	/**
	 * recusively walks through the inheritance tree and applies attributes from
	 * the <code>@ExtendedContextConfiguration</code> annotations most-specific-last.
	 * @param annotatedClass the class whose annotations should get inspected
	 * @param config the ModuleApplicationContextConfiguration to modify
	 */
	private void recursivelyConfigure(Class<?> annotatedClass, ModuleApplicationContextConfiguration config) {
		// end recursion just after Object
		if (annotatedClass == null) {
			return;
		}
		
		// parent goes first such that the child overrides the parent configuration
		recursivelyConfigure(annotatedClass.getSuperclass(), config);
		
		ContextConfiguration configuration 
			= annotatedClass.getAnnotation(ContextConfiguration.class);
		ExtendedContextConfiguration extendedConfiguration 
			= annotatedClass.getAnnotation(ExtendedContextConfiguration.class);
		
		if (extendedConfiguration != null && configuration == null) {
			throw new IllegalStateException(
				"@ExtendedContextConfiguration without @ContextConfiguration in class "
				+ annotatedClass.getName());
		}
		
		if (extendedConfiguration == null) {
			// no point in going further up the class hierarchy
			return;
		}
		
		// iterate over all attributes of the annotation
		TernaryBoolean b;
		
		// if 'inheritLocations' attribute from '@ContextConfiguration' is true, merge paths
		// otherwise override them
		if (configuration.inheritLocations()) {
			Set<String> exclusiveLocations = new HashSet<String>();
			for (String path : config.getExclusiveConfigLocations()) {
				exclusiveLocations.add(path);
			}
			for (String path : extendedConfiguration.exclusiveConfigLocations()) {
				exclusiveLocations.add(path);
			}
			config.setExclusiveConfigLocations(exclusiveLocations.toArray(new String[0]));
		} else {
			config.setExclusiveConfigLocations(extendedConfiguration.exclusiveConfigLocations());
		}
		
		// set boolean properties if specified
		b = new TernaryBoolean(extendedConfiguration.allowBeanDefinitionOverriding());
		if (b.isSet) {
			config.setAllowBeanDefinitionOverriding(b.value);
		}
		
		b = new TernaryBoolean(extendedConfiguration.mergeWithOuterResources());
		if (b.isSet) {
			config.setMergeWithOuterResources(b.value);
		}
		
		b = new TernaryBoolean(extendedConfiguration.mostSpecificBeanDefinitionCounts());
		if (b.isSet) {
			config.setMostSpecificBeanDefinitionCounts(b.value);
		}
		
		b = new TernaryBoolean(extendedConfiguration.mostSpecificResourceLast());
		if (b.isSet) {
			config.setMostSpecificResourceLast(b.value);
		}
		
	}
	
	/**
	 * Helper class.
	 */
	private class TernaryBoolean {

		public TernaryBoolean(String sValue) {
			if ("true".equals(sValue) || "false".equals(sValue)) {
				isSet = true;
				value = "true".equals(sValue);
			} else {
				isSet = false;
			}
		}
		
		public boolean isSet;
		public boolean value;
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

	/**
	 * @param testClass the test class which is annotated with <code>@ConfigurationContext</code>
	 * and possibly <code>@ExtendedConfigurationContext</code>
	 */
	public static void setTestedClass(Class<?> testClass) {
		s_testedClass.set(testClass);
	}
}
