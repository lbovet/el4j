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

import org.junit.AfterClass;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This class is a base class for tests in the EL4J framework.
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
public abstract class AbstractTest {
	/**
	 * Application context to load beans.
	 */
	private ConfigurableApplicationContext m_applicationContext;

	/**
	 * Hide default constructor.
	 */
	protected AbstractTest() { }

	/**
	 * @return Returns the application context.
	 */
	protected synchronized ConfigurableApplicationContext getApplicationContext() {
		if (m_applicationContext == null) {
			
			m_applicationContext = ModuleTestContextCache.get(getIncludeConfigLocations(),
				getExcludeConfigLocations(), isBeanOverridingAllowed());
		}
		return m_applicationContext;
	}
	
	/**
	 * Close all application contexts. This method gets executed "@AfterClass", but something like
	 * TestNG's "@AfterSuite" would be more efficient.
	 */
	@AfterClass
	public static void closeAllApplicationContexts() {
		ModuleTestContextCache.clear();
	}

	/**
	 * @return Returns <code>true</code> if bean definition overriding should
	 *         be allowed.
	 */
	protected boolean isBeanOverridingAllowed() {
		return true;
	}

	/**
	 * @return Returns the string array with exclude locations.
	 */
	protected String[] getExcludeConfigLocations() {
		return null;
	}

	/**
	 * @return Returns the string array with include locations.
	 */
	protected abstract String[] getIncludeConfigLocations();
}
