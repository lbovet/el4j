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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;

import static ch.elca.el4j.services.search.criterias.CriteriaHelper.and;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.services.persistence.jpa.dao.GenericJpaDao;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.services.search.criterias.Order;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Person.LegalStatus;
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
public class JpaDaoTest extends AbstractJpaDaoTest {

	/**
	 * Logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(JpaDaoTest.class);
	
	/**
	 * Tests the <code>persists</code> and <code>findById</code> methods.
	 */
	@Test
	public void testPersist() {
		Person p1 = getNewPerson("Donald");
		
		personJpaDao.persist(p1);
		personJpaDao.flush();
		
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
		personJpaDao.flush();
		
		entityManager.detach(p1);
		p1.setName("Dr. Jekyll");
		personJpaDao.merge(p1);
		personJpaDao.flush();
		
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
		
		p1 = personJpaDao.saveOrUpdate(p1);
		personJpaDao.flush();
		entityManager.detach(p1);
		
		// ensure it is there
		try {
			personJpaDao.findById(p1.getKey());
			entityManager.detach(p1);
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
		personJpaDao.flush();
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
		personJpaDao.flush();
		p1.setName("Dagobert Duck");
		entityManager.detach(p1);
		
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
		personJpaDao.flush();
		
		// delete by id
		personJpaDao.deleteById(p1.getKey());
		personJpaDao.flush();
		
		try {
			personJpaDao.findById(p1.getKey());
			fail(p1.getName() + " was not deleted!");
		} catch (ObjectRetrievalFailureException e) {
			s_logger.info("caught expected ObjectRetrievalFailureException");
		}
		
		// delete by id 2
		personJpaDao.deleteById(p3.getKey());
		personJpaDao.flush();
		
		try {
			personJpaDao.findById(p3.getKey());
			fail(p3.getName() + " was not deleted!");
		} catch (ObjectRetrievalFailureException e) {
			s_logger.info("caught expected ObjectRetrievalFailureException");
		}
		
		// delete by entity reference
		personJpaDao.delete(p2);
		personJpaDao.flush();
		
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
		personJpaDao.flush();
		
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
		personJpaDao.flush();
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
		personJpaDao.flush();
		
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
	 * Tests {@link GenericJpaDao#findCountByQuery(QueryBuilder)}.
	 */
	@Test
	public void testFindCountByQuery1() {

		List<Person> people = personJpaDao.getAll();
		
		QueryBuilder q = QueryBuilder.select("p").from("Person p");
		
		int count = personJpaDao.findCountByQuery(q);
		
		assertEquals("count should equal the number of stored persons", people.size(), count);
	}
	
	/**
	 * Tests {@link GenericJpaDao#findCountByQuery(QueryBuilder)}.
	 */
	@Test
	public void testFindCountByQuery2() {
		Person einstein = getNewPerson("Albert Einstein");
		einstein.getBrain().setIq(170);
		
		personJpaDao.persist(einstein);
		personJpaDao.flush();
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		
		// Same query, but readable:
		// SELECT *
		// FROM Brain b
		// WHERE b.iq = 170
		
		// JPA Criteria API madness:
//		CriteriaQuery<Person> query = cb.createQuery(Person.class);
//		Root<Person> root = query.from(Person.class);
//		Join<Object, Object> brainJoin = root.join("brain");
//		Predicate iqPredicate = cb.equal(brainJoin.<Integer>get("iq"), 170);
//		query.select(root).where(iqPredicate);
		
		QueryBuilder q = QueryBuilder.select("b").from("Brain b").startAnd().ifCond("b.iq = 170").end();
		
		int count = personJpaDao.findCountByQuery(q);
		
		assertEquals("findCountByCriteria reported wrong count", 1, count);
	}
	
	/**
	 * Tests {@link GenericJpaDao#findByQuery(QueryBuilder)} method.
	 */
	@Test
	public void testFindByQuery() {
		Person frankenstein = getNewPerson("Frankenstein's Monster");
		frankenstein.getBrain().setIq(-1);
		personJpaDao.persist(frankenstein);
		
		personJpaDao.flush();
		
		// The SQL-Query
		//
		// SELECT p.*
		// FROM Person p JOIN p.brain AS b
		// WHERE b.iq = 1
		//
		// is translated to... 
		
		// JPA Criteria API madness:
//		CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
//		CriteriaQuery<Person> criteria = criteriaBuilder.createQuery(Person.class);
//		Root<Person> personRoot = criteria.from(Person.class);
//		Join<Object, Object> brainJoin = personRoot.join("brain");
//		Predicate deadBrain = criteriaBuilder.equal(brainJoin.<Integer>get("iq"), -1);
//		criteria.select(personRoot).here(deadBrain);
		
		QueryBuilder q = QueryBuilder.select("p").from("Person p").join("p.brain AS b")
			.startAnd().ifCond("b.iq = -1").end();
		
		List<Person> retrieved = personJpaDao.findByQuery(q);
		
		assertNotNull("retrieved list must not be null", retrieved);
		assertEquals("number of retrieved rows should be 1", 1, retrieved.size());
		assertEquals("returned person is not Frankenstein's Monster", frankenstein, retrieved.get(0));
	}
	
}
