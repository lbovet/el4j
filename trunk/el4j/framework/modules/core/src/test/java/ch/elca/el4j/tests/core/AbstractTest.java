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

import org.springframework.context.ApplicationContext;

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
	private ApplicationContext m_applicationContext;

	/**
	 * Hide default constructor.
	 */
	protected AbstractTest() { }

	/**
	 * @return Returns the applicationContext.
	 */
	protected synchronized ApplicationContext getApplicationContext() {
		if (m_applicationContext == null) {
			
			m_applicationContext = ModuleTestContextCache.get(getIncludeConfigLocations(),
				getExcludeConfigLocations(), isBeanOverridingAllowed());
		}
		return m_applicationContext;
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
	protected abstract String[] getExcludeConfigLocations();

	/**
	 * @return Returns the string array with include locations.
	 */
	protected abstract String[] getIncludeConfigLocations();
}
