/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.refdb.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection.collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.refdb.dao.impl.hibernate.HibernateFileDao;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.core.ModuleTestContextLoader;
import ch.elca.el4j.tests.core.context.ExtendedContextConfiguration;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import ch.elca.el4j.tests.refdb.jpa.dao.FileJpaDao;
import ch.elca.el4j.tests.refdb.jpa.dao.PersonJpaDao;


/**
 * This class contains test method that verify the correctness of the extent-loading
 * functionality in the GenericJpaDao class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
@ExtendedContextConfiguration(
	allowBeanDefinitionOverriding = "true",
	exclusiveConfigLocations = {"classpath*:scenarios/dataaccess/jpa/jpaEntityManagerFactory.xml" })
@ContextConfiguration(
	locations = { "classpath*:scenarios/dataaccess/jpa/extent/extent-test-jpa-config.xml" },
	loader = ModuleTestContextLoader.class)
public class JpaDaoExtentTest extends AbstractJpaDaoTest {

	/**
	 * private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(JpaDaoExtentTest.class);
	
	/**
	 * Method to add a fake reference to database.
	 *
	 * @param name
	 *            Is the name the fake reference must have.
	 * @return Returns the key of the created reference.
	 */
	protected Reference addFakeReference(String name) {
		Link link = new Link();
		link.setName(name);
		getLinkDao().persist(link);
		getLinkDao().flush();
		return link;
	}
	
	/**
	 * Method to add a default fake reference to database.
	 *
	 * @return Returns the primary key of the fake reference.
	 */
	protected Reference addDefaultFakeReference() {
		return addFakeReference("Fake reference");
	}
	
	/**
	 * This test checks the explicit loading of file content
	 * using {@see DataExtent#with}.
	 */
	@Test
	public void testInsertFileEagerContent() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileJpaDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		dao.persist(file);
		dao.flush();
		
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
	 * This test inserts a simple Person.
	 */
	@Test
	public void testInsertPerson() {
		
		PersonJpaDao dao = getPersonDao();
		Person person = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person.setBrain(b);
		dao.persist(person);
		dao.flush();
	}
	
	/**
	 * This test inserts a complex Person structure.
	 */
	@Test
	public void testInsertPersons() {
		
		PersonJpaDao dao = getPersonDao();
		Person person1 = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person1.setBrain(b);
		dao.persist(person1);
		
		Person person2 = new Person("Max Muster");
		b = new Brain();
		b.setIq(99);
		person2.setBrain(b);
		dao.persist(person2);
		
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
		dao.persist(person3);
		dao.flush();
	}
	
	/**
	 * This test checks lazy loading of persons brain (bidirectional OneToOne relation).
	 */
	@Test
	public void testLazyBrain() {
		
		PersonJpaDao dao = getPersonDao();
		Person person = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person.setBrain(b);
		person = dao.saveOrUpdate(person);
		
		dao.flush();
		
		// detach to ensure the next findById call will return a fresh instance.
		entityManager.detach(person);
		
		Person person2 = dao.findById(person.getKey());
		
		// explicitly detach person2 to expose the lazy initialization
		entityManager.detach(person2);
		
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
			entityManager.detach(person2);
			try {
				entityManager.detach(person2.getBrain());
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
		entityManager.detach(person2);
		try {
			person2.getBrain().getIq();
		} catch (LazyInitializationException e) {
			fail("Brain has not been loaded though forced explicitly.");
		}
	}
	
	/**
	 * This test checks lazy loading of persons with a recursion depth > 1 on ManyToOne relations.
	 */
	@Test
	public void testLazyBestFriend() {
		
		PersonJpaDao dao = getPersonDao();
		Person person = new Person("Peter Muster");
		Brain b = new Brain();
		b.setIq(99);
		person.setBrain(b);
		
		Person friend = new Person("Jean-Pierre Luc");
		Brain b2 = new Brain();
		friend.setBrain(b2);
		person.setBestFriend(friend);
		
		Person friendsFriend = new Person("John Doe");
		Brain b3 = new Brain();
		b3.setIq(99);
		friendsFriend.setBrain(b3);
		friend.setBestFriend(friendsFriend);
		
		Person buddy = new Person("John Doe's Buddy");
		Brain b4 = new Brain();
		b4.setIq(50);
		buddy.setBrain(b4);
		friendsFriend.setBestFriend(buddy);
		
		dao.persist(buddy);
		dao.persist(friendsFriend);
		dao.persist(friend);
		dao.persist(person);
		dao.flush();
		
		entityManager.detach(person);
		entityManager.detach(person.getBestFriend());
		
		Person person2 = dao.findById(person.getKey());
		entityManager.detach(person2);
		try {
			entityManager.detach(person2.getBestFriend());
			person2.getBestFriend().getName();
			fail("Could access person's best friend which should not have been loaded.");
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		
		// Set Extent explicitly to "with brain"
		try {
			DataExtent withBestFriend = new DataExtent(Person.class).with("bestFriend");
			person2 = dao.findById(person.getKey(), withBestFriend);
			entityManager.detach(person2);
			try {
				entityManager.detach(person2.getBestFriend());
				person2.getBestFriend().getName();
			} catch (LazyInitializationException e) {
				fail("Brain has not been loaded though forced explicitly.");
			}
		} catch (NoSuchMethodException e1) {
			fail("Fields doesnt exist.");
		}
		
		// Set Extent explicitly to "all" with recursion depth 3
		DataExtent wholePerson = new DataExtent(Person.class).all(3);
		
		person2 = dao.findById(person.getKey(), wholePerson);
		entityManager.detach(person2);
		try {
			entityManager.detach(person2.getBestFriend());
			person2.getBestFriend().getName();
		} catch (LazyInitializationException e) {
			fail("Brain has not been loaded though forced explicitly.");
		}
	}
	
	/**
	 * Inserts some persons into Dao and sets some friends.
	 */
	private void insertFriendsScenario() {
		PersonJpaDao dao = getPersonDao();
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
		
		dao.persist(person1);
		dao.persist(person2);
		dao.persist(person3);
		dao.persist(person4);
		
		dao.flush();
		
		entityManager.detach(person1);
		entityManager.detach(person2);
		entityManager.detach(person3);
		entityManager.detach(person4);
	}
	
//	/**
//	 * This test checks the lazy loading of a persons friends (OneToMany relation).
//	 */
//	@Test
//	public void testLazyFriends() {
//		insertFriendsScenario();
//		PersonJpaDao dao = getPersonDao();
//		
//		QueryBuilder q = QueryBuilder.select(select)
//		
//		QueryObject query = new QueryObject();
//		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
//		
//		try {
//			Person mary = dao.findByQuery(query).get(0);
//			entityManager.detach(mary);
//			List<Person> f = mary.getFriends();
//			assertFalse("Friend list must not be empty", f.isEmpty());
//			f.get(0).getName();
//			
//			fail("Could access friends list of lazy loaded person");
//		} catch (IndexOutOfBoundsException e) {
//			fail("Couldn't retrieve person inserted shortly before.");
//		} catch (LazyInitializationException e) {
//			s_logger.debug("Expected exception catched.", e);
//		}
//		
//		// Add the friends to the extent
//		DataExtent ex;
//		try {
//			ex = new DataExtent(Person.class).with("friends");
//			Person mary = dao.findByQuery(query, ex).get(0);
//			entityManager.detach(mary);
//			List<Person> f = mary.getFriends();
//			
//			assertFalse("Friend list must not be empty", f.isEmpty());
//			f.get(0).getName();
//		} catch (IndexOutOfBoundsException e) {
//			fail("Couldn't retrieve person inserted shortly before.");
//		} catch (LazyInitializationException e) {
//			fail("Could not access friends list of explicitly loaded person");
//		} catch (NoSuchMethodException e1) {
//			fail("Field friends doesnt exist");
//		}
//		
//	}
//	
//	/**
//	 * This test checks the if circular references results in infinite loops.
//	 */
//	@Test(timeout = 10000)
//	public void testCircularFriends() {
//		insertFriendsScenario();
//		PersonJpaDao dao = getPersonDao();
//		
//		QueryObject query = new QueryObject();
//		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
//		
//		// Check circular reference in Extent
//		try {
//			DataExtent ex = new DataExtent(Person.class);
//			ex.withSubentities(collection("friends", ex.getRootEntity()));
//			
//			Person mary = dao.findByQuery(query, ex).get(0);
//			
//			entityManager.detach(mary);
//			
//			List<Person> f = mary.getFriends();
//			assertFalse("Friend list must not be empty", f.isEmpty());
//			
//			for (int i = 0; i < f.size(); i++) {
//				List<Person> f2 = f.get(i).getFriends();
//				for (int j = 0; j < f2.size(); j++) {
//					f2.get(j).getName();
//				}
//			}
//			
//			
//		} catch (NoSuchMethodException e) {
//			fail("Could not add list friends to extent.");
//		} catch (IndexOutOfBoundsException e) {
//			fail("Couldn't retrieve person inserted shortly before.");
//		} catch (LazyInitializationException e) {
//			fail("Could not access friends' friends list of explicitly loaded person");
//		}
//	}
//	
//	/**
//	 * Test reload of the given extent: does it revert whole graph?.
//	 */
//	@Test
//	public void testReloadReverting() {
//		insertFriendsScenario();
//		
//		PersonJpaDao dao = getPersonDao();
//		
//		QueryObject query = new QueryObject();
//		query.addCriteria(LikeCriteria.caseInsensitive("name", "%Mary Muster%"));
//		DataExtent ex = new DataExtent(Person.class);
//		ex.all(3);
//		
//		try {
//			Person person1 = dao.findByQuery(query, ex).get(0);
//			entityManager.detach(person1);
//			
//			person1.getFriends().get(0).setName("Jean-Pierre");
//			
//			try {
//				Person p1 = dao.reload(person1);
//				entityManager.detach(p1);
//				try {
//					assertTrue("Hibernate did revert the Person's Friends although not told.", p1.getFriends()
//						.get(0).getName().equals("Jean-Pierre"));
//				} catch (LazyInitializationException e) {
//					s_logger.debug("Expected Exception catched.", e);
//				}
//
//				Person p2 = dao.reload(person1, ex);
//				entityManager.detach(p2);
//				p2.getName();
//				
//				assertFalse("Hibernate did not revert the Persons Friends.", p2.getFriends()
//					.get(0).getName().equals("Jean-Pierre"));
//			} catch (LazyInitializationException e) {
//				fail("Could not accesss friends of person with extent depth 3.");
//			}
//		} catch (Exception e) {
//			fail("Loading person and accessing friends failed.");
//		}
//	}
		
}
