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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static ch.elca.el4j.services.search.criterias.CriteriaHelper.and;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.services.persistence.jpa.dao.GenericJpaDao;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.services.search.criterias.Order;
import ch.elca.el4j.tests.core.ModuleTestContextLoader;
import ch.elca.el4j.tests.core.context.ExtendedContextConfiguration;
import ch.elca.el4j.tests.core.context.junit4.EL4JJunit4ClassRunner;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Person.LegalStatus;
import ch.elca.el4j.tests.refdb.jpa.dao.BrainJpaDao;
import ch.elca.el4j.tests.refdb.jpa.dao.PersonJpaDao;
/**
 * 
 * Tests the functionality provided by {@link GenericJpaDao}.
 * 
 * Also serves as a running example of the <code>@ExtendedContextConfiguration</code>.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
@RunWith(EL4JJunit4ClassRunner.class)
@ExtendedContextConfiguration(exclusiveConfigLocations = {
		"classpath*:mandatory/refdb-core-config.xml",
		"classpath*:mandatory/keyword-core-config.xml" })
@ContextConfiguration(
	locations = {
		"classpath*:mandatory/*.xml",
		"classpath*:scenarios/db/raw/*.xml",
		"classpath*:scenarios/dataaccess/jpa/*.xml",
		"classpath*:optional/jpadao/*.xml",
		"classpath*:scenarios/dataaccess/jpa/refdb/*.xml"
		},
	loader = ModuleTestContextLoader.class)
@Transactional
public class JpaDaoTest {

	/**
	 * Logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(JpaDaoTest.class);
	
	/**
	 * JPA DAO for people.
	 */
	@Inject
	private PersonJpaDao personJpaDao;
	
	/**
	 * JPA DAO for brain.
	 */
	@Inject
	private BrainJpaDao brainJpaDao;
	
	/**
	 * The {@link EntityManagerFactory} used to retrieve a CriteriaBuilder.
	 */
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * @return a new Person
	 * @param name the name of the new person
	 */
	private Person getNewPerson(String name) {
		Person p = new Person(name);
		Brain b = new Brain();
		b.setIq(99);
		p.setBrain(b);
		b.setOwner(p);
		
		return p;
	}
	
	/**
	 * Since other tests commit data to the DB, we have to wipe everything
	 * before we start our own tests. Note that the tests in this class
	 * are always rolled back.
	 */
	@Before
	public void wipeDB() {
		personJpaDao.deleteAll();
	}
	
	/**
	 * Tests the <code>persists</code> and <code>findById</code> methods.
	 */
	@Test
	public void testPersist() {
		Person p1 = getNewPerson("Donald");
		
		personJpaDao.persist(p1);
		
		Person p2 = personJpaDao.findById(p1.getKey());
		assertNotNull("Could not retrieve person from DAO", p2);
		assertEquals("Persisted and retrieved persons do not match", p1, p2);
	}
	
	/**
	 * Tests whether updates to an entity are correctly stored.
	 */
	@Test
	public void testMerge() {
		Person p1 = getNewPerson("Mr. Hyde");
		personJpaDao.persist(p1);
		
		p1.setName("Dr. Jekyll");
		personJpaDao.merge(p1);
		
		Person p2 = personJpaDao.findById(p1.getKey());
		
		assertNotNull("Could not retrieve person from DAO", p2);
		assertEquals("Persisted and retrieved persons do not match", p1, p2);
	}
	
	/**
	 * Tests the <code>saveOrUpdate</code> method.
	 */
	@Test
	public void testSaveOrUpdate() {
		Person p1 = getNewPerson("Balou");
		
		personJpaDao.saveOrUpdate(p1);
		
		// ensure it is there
		try {
			personJpaDao.findById(p1.getKey());
		} catch (ObjectRetrievalFailureException e) {
			fail("saveOrUpdate did not persist person " + p1.getName());
		}
		
		p1.getBrain().setIq(70);
		personJpaDao.saveOrUpdate(p1);
		Person p2 = personJpaDao.findById(p1.getKey());
		
		assertEquals("changes to person " + p1.getName() + " were not merged", 70, p2.getBrain().getIq());
	}
	
	/**
	 * Tests whether <code>refresh</code> correctly loads the data from the storage again.
	 */
	@Test
	public void testRefresh() {
		
		Person p1 = getNewPerson("Dagobert");
		p1 = personJpaDao.persist(p1);
		p1.setName("Dagobert Duck");
		p1 = personJpaDao.refresh(p1);
		
		// The @PrePersist callback in Person appends the comment. 
		assertEquals("property name was not refreshed", "Dagobert(modified when persisted)", p1.getName());
	}
	
	/**
	 * Tests whether <code>reload</code> correctly loads the data from the storage again.
	 */
	@Test
	public void testReload() {
		
		Person p1 = getNewPerson("Dagobert");
		
		p1 = personJpaDao.persist(p1);
		p1.setName("Dagobert Duck");
		p1 = personJpaDao.reload(p1);
		
		// The @PrePersist callback in Person appends the comment. 
		assertEquals("property name was not refreshed", "Dagobert(modified when persisted)", p1.getName());
	}
	
	/**
	 * Tests whether entities are actually deleted by the <code>delete</code> methods.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testDelete() {
		
		Person p1 = getNewPerson("Ghost1");
		Person p2 = getNewPerson("Ghost2");
		Person p3 = getNewPerson("Ghost3");
		p1 = personJpaDao.persist(p1);
		p2 = personJpaDao.persist(p2);
		p3 = personJpaDao.persist(p3);
		
		// delete by id
		personJpaDao.delete(p1.getKey());
		
		try {
			personJpaDao.findById(p1.getKey());
			fail(p1.getName() + " was not deleted!");
		} catch (ObjectRetrievalFailureException e) {
			s_logger.info("caught expected ObjectRetrievalFailureException");
		}
		
		// delete by id 2
		personJpaDao.deleteById(p3.getKey());
		
		try {
			personJpaDao.findById(p3.getKey());
			fail(p3.getName() + " was not deleted!");
		} catch (ObjectRetrievalFailureException e) {
			s_logger.info("caught expected ObjectRetrievalFailureException");
		}
		
		// delete by entity reference
		personJpaDao.delete(p2);
		
		try {
			personJpaDao.findById(p2.getKey());
			fail(p2.getName() + " was not deleted!");
		} catch (ObjectRetrievalFailureException e) {
			s_logger.info("caught expected ObjectRetrievalFailureException");
		}

	}
	
	/**
	 * Tests whether delete(Collection<T>) removes all given objects from the DB.
	 */
	@Test
	public void testDeleteCollection() {
		Person p1 = getNewPerson("Tick");
		Person p2 = getNewPerson("Trick");
		Person p3 = getNewPerson("Track");
		
		Collection<Person> collection = new ArrayList<Person>();
		collection.add(p1);
		collection.add(p2);
		collection.add(p3);
		
		for (Person p : collection) {
			personJpaDao.persist(p);
		}
		
		personJpaDao.delete(collection);
		
		for (Person p : collection) {
			try {
				personJpaDao.findById(p.getKey());
				fail("Person " + p.getName() + " was not deleted!");
			} catch (ObjectRetrievalFailureException e) {
				s_logger.info("caught expected ObjectRetrievalFailureException");
			}
		}
	}
	
	/**
	 * tests whether deleteAll removes all entity objects from the DB.
	 */
	@Test
	public void testDeleteAll() {
		List<Person> people = new ArrayList<Person>();
		for (int i = 0; i < 10; i++) {
			Person p = getNewPerson("Person #" + ((Integer) i).toString());
			people.add(p);
			personJpaDao.persist(p);
		}
		
		personJpaDao.deleteAll();
		
		for (int i = 0; i < people.size(); i++) {
			try {
				personJpaDao.findById(people.get(i).getKey());
			} catch (ObjectRetrievalFailureException e) {
				s_logger.info("caught expected ObjectRetrievalFailureException");
			} 
		}
	}
	
	/**
	 * tests whether getAll returns the stored people.
	 */
	@Test
	public void testGetAll() {
		List<Person> people = new ArrayList<Person>();
		for (int i = 0; i < 10; i++) {
			Person p = getNewPerson("Twin #" + ((Integer) i).toString());
			people.add(p);
			personJpaDao.persist(p);
		}
		
		List<Person> fetched = personJpaDao.getAll();

		assertNotNull("fetched list must not be null", fetched);
		// other tests store additional people
		assertTrue("there must be at least 10 people", fetched.size() >= people.size());
		
		for (Person p : people) {
			p = personJpaDao.merge(p);
			assertTrue("stored person " + p.getName() + " not contained in result list", fetched.contains(p));
		}
	}
	
	/**
	 * Tests <code>findCountByCriteria</code>.
	 */
	@Test
	public void testFindCountByCriteria1() {

		List<Person> people = personJpaDao.getAll();
		
		CriteriaQuery<Person> criteria = entityManagerFactory.getCriteriaBuilder().createQuery(Person.class);
		criteria.from(Person.class);
		
		int count = personJpaDao.findCountByCriteria(criteria);
		
		assertEquals("count should equal the number of stored persons", people.size(), count);
	}
	
	/**
	 * Tests <code>findCountByCriteria</code>.
	 */
	@Test
	public void testFindCountByCriteria2() {
		Person einstein = getNewPerson("Albert Einstein");
		einstein.getBrain().setIq(170);
		
		personJpaDao.persist(einstein);
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		
		// Same query, but readable:
		// SELECT *
		// FROM Brain b
		// WHERE b.iq = 170
		
		CriteriaQuery<Person> query = cb.createQuery(Person.class);
		Root<Person> root = query.from(Person.class);
		Join<Object, Object> brainJoin = root.join("brain");
		Predicate iqPredicate = cb.equal(brainJoin.<Integer>get("iq"), 170);
		query.select(root).where(iqPredicate);
		
		int count = personJpaDao.findCountByCriteria(query);
		
		assertEquals("findCountByCriteria reported wrong count", 1, count);
	}
	
	/**
	 * Tests the <code>findCountByQuery</code> method.
	 */
	@Test
	public void testFindCountByQuery() {
		Person john = getNewPerson("John the Ripper");
		john.setLegalStatus(LegalStatus.SINGLE);
		personJpaDao.persist(john);
		
		Person johnny = getNewPerson("Johnny99");
		johnny.setLegalStatus(LegalStatus.SINGLE);
		personJpaDao.persist(johnny);
		
		Person johndoe = getNewPerson("John Doe");
		johndoe.setLegalStatus(LegalStatus.MARRIED);
		personJpaDao.persist(johndoe);
		
		// SELECT *
		// FROM Person
		// WHERE name ILIKE "%JoHn%" AND legalStatus == SINGLE
		// ORDER BY name ASC
		
		QueryObject query = new QueryObject(Person.class);
		query.addCriteria(
			and(
				LikeCriteria.caseInsensitive("name", "%JoHn%"), 
				new ComparisonCriteria(
					"legalStatus", LegalStatus.SINGLE, "=", "LegalStatus"
				)
			)
		);
		query.addOrder(Order.asc("name"));
		
		int count = personJpaDao.findCountByQuery(query);
		
		assertEquals("Wrong number of persons returned", 2, count);
	}
	
	/**
	 * Tests the <code>findByCriteria</code> method.
	 */
	@Test
	public void testFindByCriteria() {
		Person frankenstein = getNewPerson("Frankenstein's Monster");
		frankenstein.getBrain().setIq(-1);
		personJpaDao.persist(frankenstein);
		
		// The SQL-Query
		//
		// SELECT p.*
		// FROM Person p JOIN Brain b
		// WHERE b.iq == 0
		//
		// is translated to... 
		
		CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
		
		CriteriaQuery<Person> criteria = criteriaBuilder.createQuery(Person.class);
		Root<Person> personRoot = criteria.from(Person.class);
		Join<Object, Object> brainJoin = personRoot.join("brain");
		Predicate deadBrain = criteriaBuilder.equal(brainJoin.<Integer>get("iq"), -1);
		criteria.select(personRoot).where(deadBrain);
		
		List<Person> retrieved = personJpaDao.findByCriteria(criteria);
		
		assertNotNull("retrieved list must not be null", retrieved);
		assertEquals("number of retrieved rows should be 1", 1, retrieved.size());
		assertEquals("returned person is not Frankenstein's Monster", frankenstein, retrieved.get(0));
	}

	/**
	 * Tests the <code>findByQuery</code> method.
	 */
	@Test
	public void testFindByQuery1() {
		Person john = getNewPerson("John the Ripper");
		john.setLegalStatus(LegalStatus.SINGLE);
		personJpaDao.persist(john);
		
		Person johnny = getNewPerson("Johnny99");
		johnny.setLegalStatus(LegalStatus.SINGLE);
		personJpaDao.persist(johnny);
		
		Person johndoe = getNewPerson("John Doe");
		johndoe.setLegalStatus(LegalStatus.MARRIED);
		personJpaDao.persist(johndoe);
		
		// SELECT *
		// FROM Person
		// WHERE name LIKE "%John%" AND legalStatus == SINGLE
		// ORDER BY name ASC
		
		QueryObject query = new QueryObject(Person.class);
		query.addCriteria(
			and(
				LikeCriteria.caseSensitive("name", "%John%"), 
				new ComparisonCriteria(
					"legalStatus", LegalStatus.SINGLE, "=", "LegalStatus"
				)
			)
		);
		query.addOrder(Order.asc("name"));
		
		List<Person> results = personJpaDao.findByQuery(query);
		
		assertNotNull("retrieved person list must not be null", results);
		assertEquals("Wrong number of persons returned", 2, results.size());
		assertEquals("Wrong person returned first", john, results.get(0));
		assertEquals("Wrong person returned last", johnny, results.get(1));
	}
	
	/**
	 * Tests the <code>findByQuery</code> method.
	 */
	@Test
	public void testFindByQuery2() {
		Person einstein = getNewPerson("Albert Einstein");
		Person homer = getNewPerson("Homer Simpson");
		Person lisa = getNewPerson("Lisa Simpson");
		Person bart = getNewPerson("Bart Simpson");
		
		einstein.getBrain().setIq(170);
		homer.getBrain().setIq(30);
		lisa.getBrain().setIq(100);
		bart.getBrain().setIq(80);
		
		personJpaDao.persist(einstein);
		personJpaDao.persist(homer);
		personJpaDao.persist(lisa);
		personJpaDao.persist(bart);

		QueryObject query = new QueryObject(Brain.class);
		query.addCriteria(new ComparisonCriteria("iq", 100, "<=", "Integer"));
		query.addOrder(Order.desc("iq"));
		query.setFirstResult(1);
		query.setMaxResults(1);
		
		List<Brain> result = brainJpaDao.findByQuery(query);
		
		assertNotNull("retrieved brain list must not be null", result);
		assertEquals("Wrong number of brains returned", 1, result.size());
		assertEquals("Wrong brain returned", bart.getBrain(), result.get(0));
	}
}
