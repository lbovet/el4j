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
package ch.elca.el4j.tests.util.config;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.elca.el4j.util.config.GenericConfig;

//Checkstyle: MagicNumber off
//Checkstyle: EmptyBlock off

/**
 * This class tests {@link GenericConfig}.
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
public abstract class AbstractGenericConfigTest {
	/***/
	@Test
	public void testGenericConfig() {
		GenericConfig config = getDefaultConfig();
		assertTrue(config.get("class").equals("DefaultConfig"));
		assertTrue(config.get("DefaultConfig").equals("DefaultConfig"));
		assertTrue(config.getMap().size() == 2);
	}
	
	/***/
	@Test
	public void testSpecificConfig() {
		GenericConfig config = getSpecificConfig();
		assertTrue(config.get("class").equals("SpecificConfig"));
		assertTrue(config.get("DefaultConfig").equals("DefaultConfig"));
		assertTrue(config.get("SpecificConfig").equals("SpecificConfig"));
		assertTrue(config.getMap().size() == 3);
	}
	
	/***/
	@Test
	public void testMoreSpecificConfig() {
		GenericConfig config = getMoreSpecificConfig();
		assertTrue(config.get("class").equals("MoreSpecificConfig"));
		assertTrue(config.get("MoreSpecificConfig")
			.equals("MoreSpecificConfig"));
		assertTrue(config.get("SpecificConfig").equals("SpecificConfig"));
		assertTrue(config.get("DefaultConfig").equals("DefaultConfig"));
		assertTrue(config.getMap().size() == 4);
	}
	
	/**
	 * @return    the default configuration
	 */
	protected abstract GenericConfig getDefaultConfig();
	
	/**
	 * @return    the specific configuration
	 */
	protected abstract GenericConfig getSpecificConfig();
	
	/**
	 * @return    the more specific configuration
	 */
	protected abstract GenericConfig getMoreSpecificConfig();
}

//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
