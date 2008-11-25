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


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;
import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection.collection;
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
public class DataExtentTest extends AbstractTestCaseBase {
	/**
	 * Private logger.
	 */
	private static Log s_logger
		= LogFactory.getLog(GenericHibernateDaoTest.class);
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml"};
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getExcludeConfigLocations() {
		return new String[] {
			"classpath*:scenarios/dataaccess/hibernate/refdb/refdb-core-hibernate-config.xml"};
	}
	
	/**
	 * This test checks the consistency of the Extent with-method.
	 */
	@Test
	public void testWith() {
		DataExtent ex = new DataExtent(Person.class);
		
		// Try to add some existing fields
		try {
			ex.with("name", "friends", "brain");
		} catch (NoSuchMethodException e) {
			fail("Could not add existing fields, entities and collections to person extent.");
		}
		
		// Try to add some non-existing fields
		try {
			ex.with("non-existingname");
			fail("Could add non-existing field to person extent");
		} catch (NoSuchMethodException e) {
			s_logger.debug("Catched expected exception.");
		}
	}
	
	/**
	 * This test checks the consistency of the Extent without-method.
	 */
	@Test
	public void testWithout() {
		DataExtent ex = new DataExtent(Person.class);
		
		// Try to add some existing fields
		try {
			ex.with("name", "friends", "brain");
			ex.without("friends");
			List<ExtentCollection> cs = ex.getRootEntity().getCollections();
			for (ExtentCollection c : cs) {
				if (c.getName().equals("friends")) {
					fail("Removed field 'friends' still in person extent.");
				}
			}
			ex.without("name");
			if (ex.getRootEntity().getFields().contains("name")) {
				fail("Removed field 'name' still in person extent.");
			}
			ex.without("brain");
			List<ExtentEntity> es = ex.getRootEntity().getChildEntities();
			for (ExtentEntity e : es) {
				if (e.getName().equals("brain")) {
					fail("Removed field 'brain' still in person extent.");
				}
			}
		} catch (NoSuchMethodException e) {
			fail("Could not add existing fields, entities and collections to person extent.");
		}
	}
	
	/**
	 * This test checks the consistency of the Extent withSubentity-method.
	 */
	@Test
	public void testWithSubentity() {
		DataExtent ex = new DataExtent(Person.class);
		
		// Try to add some existing entities
		try {
			ex.withSubentities(entity(Brain.class), collection("friends", Person.class));
			boolean found = false;
			
			List<ExtentCollection> cs = ex.getRootEntity().getCollections();
			for (ExtentCollection c : cs) {
				if (c.getName().equals("friends")) {
					found = true;
				}
			}
			if (!found) {
				fail("withSubentity feature does not add collection friends.");
			}
			found = false;
			
			List<ExtentEntity> es = ex.getRootEntity().getChildEntities();
			for (ExtentEntity e : es) {
				if (e.getName().equals("brain")) {
					found = true;
				}
			}
			if (!found) {
				fail("withSubentity feature does not add entity brain");
			}
		} catch (NoSuchMethodException e) {
			fail("Could not add existing sub-entities and sub-collections to person extent.");
		}
		
		// Try to add some non-existing fields
		try {
			ex.withSubentities(entity(Tooth.class), collection("friends", Brain.class));
			fail("Could add non-existing field to person extent");
		} catch (NoSuchMethodException e) {
			s_logger.debug("Catched expected exception.");
		}
	}
	
	/**
	 * This test checks the consistency of the Extent all-method.
	 */
	@Test
	public void testAll() {
		DataExtent ex = new DataExtent(Person.class);
		boolean found = false;
		ex.all();
		List<ExtentCollection> cs = ex.getRootEntity().getCollections();
		for (ExtentCollection c : cs) {
			if (c.getName().equals("friends")) {
				found = true;
			}
		}
		if (!found) {
			fail("All feature does not add field friends.");
		}
		found = false;
		
		if (!ex.getRootEntity().getFields().contains("name")) {
			fail("All feature does not add field name.");
		}
		found = false;
		
		List<ExtentEntity> es = ex.getRootEntity().getChildEntities();
		for (ExtentEntity e : es) {
			if (e.getName().equals("brain")) {
				found = true;
			}
		}
		if (!found) {
			fail("All feature does not add field brain");
		}
	}
	
	/**
	 * This test checks various features of the DataExtent fluent API.
	 */
	@Test
	public void testVarious() {
		// Test all feature to depth 10
		DataExtent ex = null;
		for (int i = 1; i < 10; i++) {
			ex = new DataExtent(Person.class);
			ex.all(i);
		}
		// Test include a "deep" entity structure
		ex = new DataExtent(Person.class);
		try {
			ex.with("name").withSubentities(collection("friends", entity(Person.class).all(2)));
			ex.getRootEntity().getCollections().get(0).getContainedEntity().getClass().equals(Person.class);
		} catch (NoSuchMethodException e) {
			fail("DataExtent includeList feature failed.");
		} catch (Exception e) {
			fail("DataExtent includeList feature failed.");
		}
	}
	
	/**
	 * This test checks various features of the DataExtent fluent API.
	 */
	@Test
	public void testVarious2() {
		DataExtent ex = new DataExtent(Person.class);
		
		// Test if we can build incorrect extents
		try {
			ex.withSubentities(entity(File.class));
			fail("DataExtent possible with incorrect structure.");
		} catch (NoSuchMethodException e) {
			s_logger.debug("Expected Exception catched.");
		}
		
		// Create a cycle-reference extent
		ex = new DataExtent(Person.class);
		try {
			ex.withSubentities(
				collection("teeth",
					entity(Tooth.class)
						.with("owner")
				),
				collection("friends", ex.getRootEntity())
			);
		} catch (NoSuchMethodException e) {
			fail("Friends construction not accepted by validator.");
		}
		
	}
}
