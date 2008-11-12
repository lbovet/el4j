/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.refdb.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.entity;

/**
 * Test case for <code>DataExtent</code> to test
 * the the fluent API and the functionality.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class DataExtentTest extends GenericHibernateDaoTest {
	
	/**
	 * This test checks the DataExtent fluent API.
	 */
	@Test
	public void testExtentAPI() {
		// Test all feature to depth 10
		DataExtent ex = null;
		for (int i = 1; i < 10; i++) {
			ex = new DataExtent(Person.class);
			ex.all(i);
		}
		// Test includeList
		ex = new DataExtent(Person.class);
		try {
			ex.with("name").includeList("friends", Person.class, 2);
			ex.getRootEntity().getCollections().get(0).getContainedEntity().getClass().equals(Person.class);
		} catch (NoSuchMethodException e) {
			fail("DataExtent includeList feature failed.");
		} catch (Exception e) {
			fail("DataExtent includeList feature failed.");
		}
		// Test include a parameterized list
		try {
			new DataExtent(Param.class).all(2);
		} catch (Exception e) {
			fail("DataExtent can't handle parametrized classes.");
		}
		// Test if exclude really excludes
		ex = new DataExtent(Person.class);
		if (ex.all().exclude("brain").getRootEntity().removeEntity("brain")) {
			fail("DataExtent exclude feature failed.");
		}
	}
	
	/**
	 * This test checks the DataExtent feature validate.
	 */
	@Test
	public void testExtentValidate() {
		DataExtent ex = new DataExtent(Person.class);
		
		// Test if we can build incorrect extents
		try {
			ex.include(File.class);
			fail("DataExtent validate fails on non-existent included entity.");
		} catch (NoSuchMethodException e) {
			s_logger.debug("Expected Exception catched.");
		}
		
		// Create a cycle-reference extent
		ex = new DataExtent(Person.class);
		try {
			ex.includeList(entity("teeth", Tooth.class)
				.include(entity("owner", Person.class).includeList("friends", ex.getRootEntity())));
		} catch (NoSuchMethodException e) {
			fail("Friends construction not accepted by validator.");
		}
		/* ExtentContainer teeth = new ExtentContainer("teeth", Tooth.class);
		ExtentEntity p1 = entity("owner", Person.class);
		ExtentContainer friends = new ExtentContainer("friends", Person.class);
		teeth.getContainedEntity().addChildEntity(p1);
		p1.addContainer(friends);
		friends.setContainedEntity(ex.getRootEntity());
		ex.getRootEntity().addContainer(teeth);*/
		
	}
}
