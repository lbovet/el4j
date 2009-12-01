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
package ch.elca.el4j.tests.services.persistence.generic.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;

/**
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 */
public class DaoAutocollectionTest {

	@Test
	public void testDaoAutocollection() {
		ApplicationContext ac = new ModuleApplicationContext(
			new String[] {"scenarios/core/dao/springConfig.xml"}, false);
		
		//System.out.println("beans: "+StringUtils.arrayToCommaDelimitedString(ac.getBeanDefinitionNames()));
		
		DaoRegistry registry = (DaoRegistry) ac.getBean("registry");
		
		GenericDao<?> dao = registry.getFor(String.class);
		//System.out.println("registry: "+DataDumper.dump(((DefaultDaoRegistry)registry).getDaos()));
		
		// dao = registry.getFor(String.class);
		assertTrue(dao != null);
		assertTrue(dao instanceof Dao1);
		
		dao = registry.getFor(Long.class);
		assertTrue(dao != null);
		assertTrue(dao instanceof Dao2);
		
		assertTrue(ac.getBeanNamesForType(GenericDao.class).length == 2);
	}
	
	/**
	 * Test the dao name pattern matching functionality.
	 */
	@Test
	public void testDaoPatternMatching() {
		ApplicationContext ac =	new ModuleApplicationContext(
			new String[] {"scenarios/core/dao/springConfig.xml"}, false);
		
		// Pattern is "ti*"
		DaoRegistry registry = (DaoRegistry) ac.getBean("registryWithFilter");
		
		// This one is called "titi". It should work.
		GenericDao<?> dao = registry.getFor(String.class);
		assertTrue(dao != null);
		assertTrue(dao instanceof Dao1);
		
		// This one should not match.
		dao = registry.getFor(Long.class);
		assertTrue(dao == null);

	}
	
	/**
	 * Test if {@link DefaultDaoRegistry} blocks until Spring context is ready.
	 */
	@Test
	public void testConcurrent() {
		final ApplicationContext ac = new ModuleApplicationContext(
			new String[] {"scenarios/core/dao/springConfig.xml", "scenarios/core/dao/concurrentConfig.xml"}, false);
		
		ImpatientClass impatientClass = (ImpatientClass) ac.getBean("impatientClass");
		
		assertTrue("DaoRegistry didn't wait for completely initialized Spring context", impatientClass.join());
	}
	
}
