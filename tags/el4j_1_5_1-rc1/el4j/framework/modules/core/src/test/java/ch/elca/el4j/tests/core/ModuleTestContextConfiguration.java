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

/**
 * This interface allows to store a {@link ModuleApplicationContext} configuration in an external class
 * (outside of subclass of {@link AbstractTest}) in a standardized way.
 * @see ch.elca.el4j.core.context.ModuleTestContextCache
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
public interface ModuleTestContextConfiguration {
	/**
	 * @return    the string array with inclusive locations.
	 */
	String[] getInclusiveConfigLocations();
	
	/**
	 * @return    the string array with exclusive locations.
	 */
	String[] getExclusiveConfigLocations();
	
	/**
	 * @return    <code>true</code> if bean definition overriding should be allowed.
	 */
	boolean isBeanOverridingAllowed();
}
