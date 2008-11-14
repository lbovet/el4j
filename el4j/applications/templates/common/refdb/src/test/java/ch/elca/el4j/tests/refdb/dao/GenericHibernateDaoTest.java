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


import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.impl.hibernate.GenericHibernateFileDaoInterface;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernatePersonDaoInterface;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
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
public class GenericHibernateDaoTest extends AbstractReferenceDaoTest {
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
	 * This test checks the lazy loading of file content.
	 */
	@Test
	public void testInsertFileLazyContent() {
		int fakeReferenceKey = addDefaultFakeReference();
		
		// User HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setKeyToReference(fakeReferenceKey);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		dao.saveOrUpdate(file);
		
		// Reset the extent to null
		dao.setExtent(File.header());
		File file2 = dao.findById(file.getKey());
		try {
			file2.getContent();
			fail("Could access file content which should not have been loaded.");
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
	}
	
	/**
	 * This test checks the explicit loading of file content
	 * using {@see DataExtent#include}.
	 */
	@Test
	public void testInsertFileEagerContent() {
		int fakeReferenceKey = addDefaultFakeReference();
		
		// User HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setKeyToReference(fakeReferenceKey);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		// Set Extent explicitly to "with content"
		dao.setExtent(File.all());
		
		dao.saveOrUpdate(file);
		
		File file2 = dao.findById(file.getKey());
		try {
			file2.getContent();
		} catch (LazyInitializationException e) {
			fail("Content has not been loaded though forced explicitly.");
		}
	}
	
	/**
	 * This test checks the explicit loading of file content
	 * using {@see DataExtent#all}.
	 */
	@Test
	public void testInsertFileEagerContent2() {
		int fakeReferenceKey = addDefaultFakeReference();
		
		// User HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setKeyToReference(fakeReferenceKey);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		// Set Extent explicitly to "all"
		dao.setExtent(File.all());
		dao.saveOrUpdate(file);
		
		File file2 = dao.findById(file.getKey());
		try {
			file2.getContent();
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
	 * This test checks lazy lodaing of persons brain.
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
			dao.setExtent(new DataExtent(Person.class).include(Brain.class));
		} catch (NoSuchMethodException e1) {
			fail("Fields doesnt exist.");
		}
		
		person2 = dao.findById(person.getKey());
		try {
			person2.getBrain().getIq();
		} catch (LazyInitializationException e) {
			fail("Brain has not been loaded though forced explicitly.");
		}
		
		// Set Extent explicitly to "all"
		dao.setExtent(new DataExtent(Person.class).all());
		
		person2 = dao.findById(person.getKey());
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
		
		Person person4 = new Person("Mary Muster");
		b = new Brain();
		b.setIq(99);
		person4.setBrain(b);
		friends = new LinkedList<Person>();
		friends.add(person3);
		person3.setFriends(friends);
		
		friends = new LinkedList<Person>();
		friends.add(person4);
		
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
		
		// Reset the extent of the dao
		dao.setExtent(null);
		
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
			ex = new DataExtent(Person.class).includeList("friends", Person.class);
			dao.setExtent(ex);

			Person mary = dao.findByQuery(query).get(0);
			
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
		
		// Check circular reference in Extent
		try {
			ex = new DataExtent(Person.class);
			ex.includeList("friends", ex.getRootEntity());
			dao.setExtent(ex);
		
			Person mary = dao.findByQuery(query).get(0);
			
			List<Person> f = mary.getFriends();
			for (int i = 0; i < f.size(); i++) {
				List<Person> f2 = f.get(i).getFriends();
				for (int j = 0; j < f2.size(); i++) {
					f2.get(i).getName();
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
