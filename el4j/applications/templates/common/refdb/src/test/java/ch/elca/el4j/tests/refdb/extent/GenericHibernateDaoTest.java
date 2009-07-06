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
package ch.elca.el4j.tests.refdb.extent;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.impl.hibernate.GenericHibernateFileDaoInterface;
import ch.elca.el4j.apps.refdb.dao.impl.hibernate.HibernateFileDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernatePersonDaoInterface;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection.collection;
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
	 * Book DAO. Created by application context.
	 */
	private ConvenienceGenericHibernateDao<Book, Integer> m_hiberanteBookDao;
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:mandatory/refdb/*.xml",
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
		// use extent-test-hibernate-config.xml instead
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
	 * Test blob/clob handling of hibernate dao.
	 */
	@Test
	public void testBlobClobHandling() {
		createAndSaveData();
		
		// Change an annotation and save it
		try {
			Book b1 = getBookDao().getAll().iterator().next();
			Annotation a1 = b1.getAnnotations().iterator().next();
			a1.setAnnotator("New Annotator");
			getAnnotationDao().saveOrUpdate(a1);
			
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched", e);
		}
		
		// Retrieve annotation with extent of depth 3, change and save
		try {
			Book b2 = getHibernateBookDao().getAll(new DataExtent(Book.class).all(3)).iterator().next();
			Annotation a2 = b2.getAnnotations().iterator().next();
			a2.setAnnotator("Another Annotator");
			getAnnotationDao().saveOrUpdate(a2);
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Expected exception catched", e);
		}
		try {
			Annotation a3 = getAnnotationDao().getAll().iterator().next();
			a3.setAnnotator("Another Annotator");
			getAnnotationDao().saveOrUpdate(a3);
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Expected exception catched", e);
		}
		
		/* Remark: these tests have been added to test the blob/clob handling of hibernate with derby database drivers.
		 * It seems that the blob/clob loaded from db is not meet the expectations of hibernate itself,
		 * so after loading and saving again, we get an exception...
		 */
		
	}
	
	private void createAndSaveData() {
		Keyword k1 = new Keyword();
		k1.setName("hibernate");
		k1.setDescription("Hibernate persistence service");
		Keyword k2 = new Keyword();
		k2.setName("object-oriented programming");
		Keyword k3 = new Keyword();
		k3.setName("manual");
		Keyword k4 = new Keyword();
		k4.setName("relational databases");
		File f1 = new File();
		f1.setName("JavaPersistenceWithHibernate.pdf");
		f1.setMimeType("application/pdf");
		String content = "Hibernate is an object-relational mapping (ORM) library for the Java language, providing "
				+ "a framework for mapping an object-oriented domain model to a traditional relational database. "
				+ "Hibernate solves Object-Relational impedance mismatch problems by replacing direct "
				+ "persistence-related database accesses with high-level object handling functions. The "
				+ "Hibernate 2.1 framework won a Jolt Award in 2005. [1] Hibernate is free as open source "
				+ "software that is distributed under the GNU Lesser General Public License.";
		f1.setSize(content.length());
		f1.setContent(content.getBytes());
		Book b = new Book();
		b.setName("Hibernate in action");
		b.setHashValue("hia");
		b.setAuthorName("Christian Bauer, Gavin King");
		b.setPublisher("Greenwich: Manning Publications");
		b.setDescription("Persisting your Java objects to a relational database. The book for it.");
		b.setIncomplete(false);
		b.setKeywords(new HashSet<Keyword>(Arrays.asList(k1, k2, k3, k4)));
		f1.setReference(b);
		Annotation a = new Annotation();
		a.setAnnotator("John Miller");
		a.setContent("very interesting book");
		a.setGrade(10);
		a.setReference(b);
		Calendar c = Calendar.getInstance();
		c.set(2004, 5, 12);
		b.setDate(new Date(c.getTimeInMillis()));
		b.setPageNum(1683);
		Link l = new Link();
		l.setName("Sample Link");
		l.setUrl("www.sample.link");
		l.setDescription("some example link just for nothing");
		
		getKeywordDao().saveOrUpdate(k1);
		getKeywordDao().saveOrUpdate(k2);
		getKeywordDao().saveOrUpdate(k3);
		getKeywordDao().saveOrUpdate(k4);
		getHibernateBookDao().saveOrUpdate(b);
		getLinkDao().saveOrUpdate(l);
		getAnnotationDao().saveOrUpdate(a);
		getFileDao().saveOrUpdate(f1);
		
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
	
	/**
	 * @return Returns the hibernate book DAO.
	 */
	protected ConvenienceGenericHibernateDao<Book, Integer> getHibernateBookDao() {
		if (m_hiberanteBookDao == null) {
			DefaultDaoRegistry daoRegistry
				= (DefaultDaoRegistry) getApplicationContext()
					.getBean("daoRegistry");
			m_hiberanteBookDao
				= (ConvenienceGenericHibernateDao<Book, Integer>) daoRegistry.getFor(Book.class);
		}
		return m_hiberanteBookDao;
	}

}
