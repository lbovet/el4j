/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * This class implements a cache for {@link ModuleApplicationContext}s.
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
public final class ModuleTestContextCache {
	/**
	 * The actual cache.
	 */
	private static Map<Configuration, ConfigurableApplicationContext> s_cache;
	
	
	/**
	 * The hidden constructor.
	 */
	private ModuleTestContextCache() {
		throw new AssertionError();
	}
	
	/**
	 * @param config    a {@link ModuleApplicationContext} configuration
	 * @return          the corresponding {@link ModuleApplicationContext}
	 */
	public static ConfigurableApplicationContext get(ModuleTestContextConfiguration config) {
		return get(new Configuration(config));
	}
	
	/**
	 * @param inclusiveConfigLocations         the string array with inclusive locations
	 * @param exclusiveConfigLocations         the string array with exclusive locations
	 * @param allowBeanDefinitionOverriding    <code>true</code> if bean definition overriding should be allowed
	 * @return                                 the corresponding {@link ModuleApplicationContext}
	 */
	public static ConfigurableApplicationContext get(String[] inclusiveConfigLocations,
			String[] exclusiveConfigLocations, boolean allowBeanDefinitionOverriding) {
		
		return get(new Configuration(inclusiveConfigLocations, exclusiveConfigLocations,
			allowBeanDefinitionOverriding));
	}
	
	/**
	 * Clear this ModuleApplicationContext cache.
	 */
	public static void clear() {
		for (ConfigurableApplicationContext context : s_cache.values()) {
			context.close();
		}
		s_cache.clear();
	}
	
	/**
	 * @param config    a {@link ModuleApplicationContext} configuration
	 * @return          the corresponding {@link ModuleApplicationContext}
	 */
	private static synchronized ConfigurableApplicationContext get(Configuration config) {
		if (s_cache == null) {
			s_cache = new HashMap<Configuration, ConfigurableApplicationContext>();
		}
		if (s_cache.containsKey(config)) {
			ConfigurableApplicationContext loadedContext = s_cache.get(config);
			// refresh context if necessary
			if (!loadedContext.isActive()) {
				loadedContext.refresh();
			}
			return loadedContext;
		} else {
			ConfigurableApplicationContext newContext = new ModuleApplicationContext(
				config.getInclusiveConfigLocations(), config.getExclusiveConfigLocations(),
				config.isBeanOverridingAllowed(), (ConfigurableApplicationContext) null);
			
			s_cache.put(config, newContext);
			return newContext;
		}
	}
	
	/**
	 * Data holder for {@link ModuleApplicationContext} configurations that can be used as key in {@link Map}s.
	 */
	private static class Configuration implements ModuleTestContextConfiguration {
		private final String[] m_inclusiveConfigLocations;
		private final String[] m_exclusiveConfigLocations;
		private final boolean m_allowBeanDefinitionOverriding;
		
		Configuration(String[] inclusiveConfigLocations, String[] exclusiveConfigLocations,
			boolean allowBeanDefinitionOverriding) {
			
			m_inclusiveConfigLocations = inclusiveConfigLocations;
			m_exclusiveConfigLocations = exclusiveConfigLocations;
			m_allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
		}
		
		Configuration(ModuleTestContextConfiguration config) {
			m_inclusiveConfigLocations = config.getInclusiveConfigLocations();
			m_exclusiveConfigLocations = config.getExclusiveConfigLocations();
			m_allowBeanDefinitionOverriding = config.isBeanOverridingAllowed();
		}
		
		/** {@inheritDoc} */
		public String[] getInclusiveConfigLocations() {
			return m_inclusiveConfigLocations;
		}
		
		/** {@inheritDoc} */
		public String[] getExclusiveConfigLocations() {
			return m_exclusiveConfigLocations;
		}
		
		/** {@inheritDoc} */
		public boolean isBeanOverridingAllowed() {
			return m_allowBeanDefinitionOverriding;
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
				+ (m_allowBeanDefinitionOverriding ? 1231 : 1237);
			result = prime * result
				+ Arrays.hashCode(m_exclusiveConfigLocations);
			result = prime * result
				+ Arrays.hashCode(m_inclusiveConfigLocations);
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Configuration other = (Configuration) obj;
			if (m_allowBeanDefinitionOverriding != other.m_allowBeanDefinitionOverriding)
				return false;
			if (!Arrays.equals(m_exclusiveConfigLocations,
				other.m_exclusiveConfigLocations))
				return false;
			if (!Arrays.equals(m_inclusiveConfigLocations,
				other.m_inclusiveConfigLocations))
				return false;
			return true;
		}
	}
}
