/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.refdb.dao;

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection.collection;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import ch.elca.el4j.apps.refdb.dao.impl.hibernate.GenericHibernateFileDaoInterface;
import ch.elca.el4j.apps.refdb.dao.impl.hibernate.HibernateFileDao;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernatePersonDaoInterface;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;
/**
 *
 * Test case for <code>GenericHibernateDao</code> to test
 * the Extent-functionality.
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
public class GenericHibernateDaoTest extends AbstractTestCaseBase {
	/**
	 * Private logger.
	 */
	protected static Log s_logger
		= LogFactory.getLog(GenericHibernateDaoTest.class);
	
	/**
	 * Person DAO. Created by application context.
	 */
	private GenericHibernatePersonDaoInterface m_personDao;
	
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
	 * This test checks the explicit loading of file content
	 * using {@see DataExtent#with}.
	 */
	@Test
	public void testInsertFileEagerContent() {
		Reference fakeReference = addDefaultFakeReference();
		
		// Use HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		dao.saveOrUpdate(file);
		
		File file2;
		try {
			DataExtent ex = new DataExtent(File.class);
			ex.with("content");
			file2 = dao.findById(file.getKey(), ex);
			if (file2.getContent() == null) {
				fail("Content has not been loaded though forced explicitly.");
			}
		} catch (NoSuchMethodException e) {
			fail("Content could not be added to the file extent.");
		} catch (LazyInitializationException e) {
			fail("Content has not been loaded though forced explicitly.");
		}
		
	}
	
	/**
	 * This test checks the explicit loading of file content
	 * using {@see HibernateFileDao.ALL} predefined constant.
	 */
	@Test
	public void testInsertFileEagerContent2() {
		Reference fakeReference = addDefaultFakeReference();
		
		// Use HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		dao.saveOrUpdate(file);
		
		File file2 = dao.findById(file.getKey(), HibernateFileDao.ALL);
		try {
			if (file2.getContent() == null) {
				fail("Content has not been loaded though forced explicitly.");
			}
		} catch (LazyInitializationException e) {
			fail("Content has not been loaded though forced explicitly.");
		}
	}
	
	
	/**
	 * This test inserts a simple Person.
	 */
	@Test
	public void testInsertPerson() {
		
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		Person person = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person.setBrain(b);
		dao.saveOrUpdate(person);
	}
	
	/**
	 * This test inserts a complex Person structure.
	 */
	@Test
	public void testInsertPersons() {
		
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		Person person1 = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person1.setBrain(b);
		dao.saveOrUpdate(person1);
		
		Person person2 = new Person("Max Muster");
		b = new Brain();
		b.setIq(99);
		person2.setBrain(b);
		dao.saveOrUpdate(person2);
		
		Person person3 = new Person("Mary Muster");
		b = new Brain();
		b.setIq(99);
		person3.setBrain(b);
		List<Tooth> t = new LinkedList<Tooth>();
		t.add(new Tooth());
		t.add(new Tooth());
		t.add(new Tooth());
		List<Person> friends = new LinkedList<Person>();
		friends.add(person1);
		friends.add(person2);
		person3.setFriends(friends);
		dao.saveOrUpdate(person3);
	}
	
	/**
	 * This test checks lazy loading of persons brain.
	 */
	@Test
	public void testLazyBrain() {
		
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		Person person = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person.setBrain(b);
		dao.saveOrUpdate(person);
		
		Person person2 = dao.findById(person.getKey());
		try {
			person2.getBrain().getIq();
			fail("Could access persons brain which should not have been loaded.");
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		
		// Set Extent explicitly to "with brain"
		try {
			DataExtent withBrain = new DataExtent(Person.class).with("brain");
			person2 = dao.findById(person.getKey(), withBrain);
			try {
				person2.getBrain().getIq();
			} catch (LazyInitializationException e) {
				fail("Brain has not been loaded though forced explicitly.");
			}
		} catch (NoSuchMethodException e1) {
			fail("Fields doesnt exist.");
		}
		
		// Set Extent explicitly to "all"
		DataExtent wholePerson = new DataExtent(Person.class).all();
		
		person2 = dao.findById(person.getKey(), wholePerson);
		try {
			person2.getBrain().getIq();
		} catch (LazyInitializationException e) {
			fail("Brain has not been loaded though forced explicitly.");
		}
	}
	
	/**
	 * Inserts some persons into Dao and sets some friends.
	 */
	private void insertFriendsScenario() {
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		Person person1 = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person1.setBrain(b);
		
		Person person2 = new Person("Max Muster");
		b = new Brain();
		b.setIq(99);
		person2.setBrain(b);
		
		Person person3 = new Person("Mary Muster");
		b = new Brain();
		b.setIq(99);
		person3.setBrain(b);
		List<Person> friends = new LinkedList<Person>();
		friends.add(person1);
		friends.add(person2);
		person3.setFriends(friends);
		
		Person person4 = new Person("Tom Muster");
		b = new Brain();
		person4.setBrain(b);
		friends = new LinkedList<Person>();
		friends.add(person3);
		person3.setFriends(friends);
		
		friends = new LinkedList<Person>();
		friends.add(person4);
		person1.setFriends(friends);
		
		dao.saveOrUpdate(person1);
		dao.saveOrUpdate(person2);
		dao.saveOrUpdate(person3);
		dao.saveOrUpdate(person4);
	}
	
	/**
	 * This test checks the lazy loading of a persons friends.
	 */
	@Test
	public void testLazyFriends() {
		insertFriendsScenario();
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		
		QueryObject query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
		
		try {
			Person mary = dao.findByQuery(query).get(0);
			
			List<Person> f = mary.getFriends();
			if (!f.isEmpty()) {
				f.get(0).getName();
			}
			fail("Could access friends list of lazy loaded person");
			
			
		} catch (IndexOutOfBoundsException e) {
			fail("Couldn't retrieve person inserted shortly before.");
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		
		// Add the friends to the extent
		DataExtent ex;
		try {
			ex = new DataExtent(Person.class).with("friends");
			
			Person mary = dao.findByQuery(query, ex).get(0);
			
			List<Person> f = mary.getFriends();
			if (!f.isEmpty()) {
				f.get(0).getName();
			}
			
			
		} catch (IndexOutOfBoundsException e) {
			fail("Couldn't retrieve person inserted shortly before.");
		} catch (LazyInitializationException e) {
			fail("Could not access friends list of explicitly loaded person");
		} catch (NoSuchMethodException e1) {
			fail("Field friends doesnt exist");
		}
		
	}
	
	/**
	 * This test checks the if circular references results in infinite loops.
	 */
	@Test(timeout = 10000)
	public void testCircularFriends() {
		insertFriendsScenario();
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		
		QueryObject query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
		
		// Check circular reference in Extent
		try {
			DataExtent ex = new DataExtent(Person.class);
			ex.withSubentities(collection("friends", ex.getRootEntity()));
			
			Person mary = dao.findByQuery(query, ex).get(0);
			
			List<Person> f = mary.getFriends();
			for (int i = 0; i < f.size(); i++) {
				List<Person> f2 = f.get(i).getFriends();
				for (int j = 0; j < f2.size(); j++) {
					f2.get(j).getName();
				}
			}
			
			
		} catch (NoSuchMethodException e) {
			fail("Could not add list friends to extent.");
		} catch (IndexOutOfBoundsException e) {
			fail("Couldn't retrieve person inserted shortly before.");
		} catch (LazyInitializationException e) {
			fail("Could not access friends' friends list of explicitly loaded person");
		}
	}
	
	/**
	 * Test reload of the given extent: does it revert whole graph?.
	 */
	@Test
	public void testReloadReverting() {
		insertFriendsScenario();
		
		GenericHibernatePersonDaoInterface dao = getPersonDao();
		
		QueryObject query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
		DataExtent ex = new DataExtent(Person.class);
		ex.all(3);
		
		try {
			Person person1 = dao.findByQuery(query, ex).get(0);
			
			person1.getFriends().get(0).setName("Jean-Pierre");
			try {
				Person p1 = dao.reload(person1);
				
				try {
					assertTrue("Hibernate did revert the Persons Friends although not told.", p1.getFriends()
						.get(0).getName().equals("Jean-Pierre"));
				} catch (LazyInitializationException e) {
					s_logger.debug("Expected Exception catched.", e);
				}

				Person p2 = dao.reload(person1, ex);
				p2.getName();
				
				assertFalse("Hibernate did not revert the Persons Friends.", p2.getFriends()
					.get(0).getName().equals("Jean-Pierre"));
			} catch (LazyInitializationException e) {
				fail("Could not accesss friends of person with extent depth 3.");
			}
		} catch (Exception e) {
			fail("Loading person and accessing friends failed.");
		}
	}
	
	/**
	 * @return Returns the person DAO.
	 */
	protected GenericHibernatePersonDaoInterface getPersonDao() {
		if (m_personDao == null) {
			DefaultDaoRegistry daoRegistry
				= (DefaultDaoRegistry) getApplicationContext()
					.getBean("daoRegistry");
			m_personDao = (GenericHibernatePersonDaoInterface) daoRegistry
				.getFor(Person.class);
		}
		return m_personDao;
	}

}
