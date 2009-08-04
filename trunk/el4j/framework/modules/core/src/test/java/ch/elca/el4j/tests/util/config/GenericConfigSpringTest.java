/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.util.config.GenericConfig;

// Checkstyle: MagicNumber off
// Checkstyle: EmptyBlock off

/**
 * This class tests {@link GenericConfig} using Spring.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class GenericConfigSpringTest extends AbstractGenericConfigTest {
	
	/** The application context. */
	final ApplicationContext m_appContext;

	/**
	 * Default constructor.
	 */
	public GenericConfigSpringTest() {
		m_appContext = new ModuleApplicationContext(
			"classpath:scenarios/util/config/genericConfig.xml", false);
	}
	
	/** {@inheritDoc} */
	@Override
	protected GenericConfig getDefaultConfig() {
		return (GenericConfig) m_appContext.getBean("DefaultConfig");
	}
	
	/** {@inheritDoc} */
	@Override
	protected GenericConfig getSpecificConfig() {
		return (GenericConfig) m_appContext.getBean("SpecificConfig");
	}
	
	/** {@inheritDoc} */
	@Override
	protected GenericConfig getMoreSpecificConfig() {
		return (GenericConfig) m_appContext.getBean("MoreSpecificConfig");
	}
	
	/***/
	public void testMoreSpecificConfigUsingProperties() {
		GenericConfig config = (GenericConfig)
			m_appContext.getBean("MoreSpecificConfigUsingProperties");
		assertTrue(config.get("class").equals(
			"MoreSpecificConfigUsingProperties"));
		assertTrue(config.get("MoreSpecificConfigUsingProperties")
			.equals("MoreSpecificConfigUsingProperties"));
		assertTrue(config.get("SpecificConfig").equals("SpecificConfig"));
		assertTrue(config.getMap().size() == 4);
	}
}
// Checkstyle: EmptyBlock on
// Checkstyle: MagicNumber on