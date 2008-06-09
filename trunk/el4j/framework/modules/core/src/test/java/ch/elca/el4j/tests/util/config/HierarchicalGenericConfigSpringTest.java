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
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.util.config.HierarchicalGenericConfig;

//Checkstyle: MagicNumber off
//Checkstyle: EmptyBlock off

/**
 * This class tests {@link HierarchicalGenericConfigSpringTest}
 * using pure Java (no Spring).
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
public class HierarchicalGenericConfigSpringTest
	extends AbstractHierarchicalGenericConfigTest {
	
	/** The application context. */
	final ApplicationContext m_appContext;

	/**
	 * Default constructor.
	 */
	public HierarchicalGenericConfigSpringTest() {
		m_appContext = new ModuleApplicationContext(
			"classpath:scenarios/util/config/hierarchicalGenericConfig.xml",
			false);
	}
	
	/** {@inheritDoc} */
	@Override
	protected HierarchicalGenericConfig getDefaultConfig() {
		return (HierarchicalGenericConfig)
			m_appContext.getBean("DefaultConfig");
	}
	
	/** {@inheritDoc} */
	@Override
	protected HierarchicalGenericConfig getSpecificConfig() {
		return (HierarchicalGenericConfig)
			m_appContext.getBean("SpecificConfig");
	}
	
	/***/
	@Test
	public void testMultipleParents() {
		HierarchicalGenericConfig config = (HierarchicalGenericConfig)
			m_appContext.getBean("configABCD");
		
		HierarchicalGenericConfig subConfig
			= config.getSubConfig("ch.elca.el4j.default");
		assertTrue(subConfig.getMap().size() == 4);
		assertTrue(subConfig.get("a").equals("defaultA"));
		assertTrue(subConfig.get("b").equals("defaultB2"));
		assertTrue(subConfig.get("c.d").equals("defaultCD"));
		assertTrue(subConfig.get("c.e").equals("defaultCE2"));
	}
}

//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
