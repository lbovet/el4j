/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate.offlining.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;

import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.Person;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.SimplePerson;


/**
 * Base class of tests. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public abstract class AbstractTest {

	/** The offliner. */
	protected Offliner m_offliner;
	
	/**
	 * Set up the offliner.
	 */
	@Before public void setUp() {
		// Hack to load the offliner, context etc. ONCE
		TestRunOnce once = new TestRunOnce(getStrategy());
		m_offliner = once.getOffliner();
		
		m_offliner.setOnline(true);
		assertTrue(m_offliner.isOnline());
		
		clearAll();
	}

	/**
	 * Called at the start of a test to indicate the default person data
	 * should be available in the database.
	 */
	protected void usePersons() {
		// Test data. The names are copied from Sun's JList example.
		String[] names = {
			"Arlo", "Cosmo", "Elmo", "Hugo",
			"Jethro", "Laszlo", "Milo", "Nemo",
			"Otto", "Ringo", "Rocco", "Rollo"
		};
		for (String name : names) {
			SimplePerson p = new SimplePerson();
			p.setName(name);
			p.setEmail(name.toLowerCase(Locale.getDefault()) + "@testdata.org");
			getSimplePersonDao().saveOrUpdate(p);
		}
		
		/*
		 * Family tree:
		 * Adam
		 *   ¦- Arlo
		 *   ¦   ¦- Jethro
		 *   ¦   ¦- Laszlo
		 *   ¦   ¦- Milo
		 *   ¦   ¦- Nemo
		 *   ¦
		 *   ¦- Cosmo
		 *   ¦   ¦- Otto
		 *   ¦   ¦- Ringo
		 *   ¦   ¦- Rocco
		 *   ¦   ¦- Rollo
		 *   ¦
		 *   ¦- Elmo
		 *   ¦- Hugo
		 */
		
		List<Person> people = new LinkedList<Person>();
		Person adam = Person.create();
		adam.setName("Adam");
		people.add(adam);
		
		for (int i = 0; i < 4; i++) {
			Person p = new Person(adam);
			p.setName(names[i]);
			people.add(p);
		}
		
		Person father = people.get(1);
		for (int i = 4; i < 8; i++) {
			Person p = new Person(father);
			p.setName(names[i]);
			people.add(p);
		}
		
		father = people.get(2);
		for (int i = 8; i < 12; i++) {
			Person p = new Person(father);
			p.setName(names[i]);
			people.add(p);
		}
		
		for (Person p : people) {
			savePerson(p);
			/*
			Person savedP = getPersonDao().saveOrUpdateAndFlush(p);
			p.setId(savedP.getId());
			*/
		}
	}
	
	/**
	 * Simplified data in the db.
	 */
	protected void usePersonsSmall() {
		
		/*
		 * Alice <-- Bob <-- Eve
		 */
		
		Person alice = Person.create();
		alice.setName("Alice");
		savePerson(alice);
		Person bob = new Person(alice);
		bob.setName("Bob");
		savePerson(bob);
		Person eve = new Person(bob);
		eve.setName("Eve");
		savePerson(eve);
		
		SimplePerson carol = new SimplePerson();
		carol.setName("Carol");
		carol.setEmail("carol@testdata.org");
		saveSimplePerson(carol);
	}
	
	/**
	 * Clear the local and remote databases.
	 * PRE : Online.
	 */
	protected void clearAll() {
		assertTrue(m_offliner.isOnline());
		m_offliner.clearLocal();
		
		getPersonDao().deleteAll();
		getSimplePersonDao().deleteAll();
		assertEquals(0, getPersonDao().findCountByCriteria(
			DetachedCriteria.forClass(Person.class)));
		assertEquals(0, getSimplePersonDao().findCountByCriteria(
			DetachedCriteria.forClass(SimplePerson.class)));
	}
	
	/**
	 * Offline all data. 
	 */
	protected void offlineAll() {
		assertEquals(true, m_offliner.isOnline());
		List<SimplePerson> sPeople = getSimplePersonDao().getAll();
		Conflict[] conflicts = m_offliner.offline((Object[]) sPeople.toArray(
			new SimplePerson[sPeople.size()]));
		assertEquals(0, conflicts.length);
		
		List<Person> people = getPersonDao().getAll();
		conflicts = m_offliner.offline((Object[]) people.toArray(
			new Person[people.size()]));
		assertEquals(0, conflicts.length);
	}
	
	/*
	 * Utility methods.
	 */
	
	/**
	 * Get a person by name.
	 * @param name The name.
	 * @return The corresponding person from the current registry.
	 */
	protected Person getPerson(String name) {
		DetachedCriteria c = DetachedCriteria.forClass(Person.class); 
		c.add(Restrictions.eq("name", name));
		c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		List<Person> people = getPersonDao().findByCriteria(c);
		
		assertEquals(1, people.size());
		if (people.size() != 1) {
			System.err.println("ERROR: Object returned several times by db.");
		}
		Person p = people.get(0);
		assertEquals(name, p.getName());
		return p;
	}
	
	/**
	 * Get a simple person by name.
	 * @param name The name.
	 * @return The corresponding simple person from the current registry.
	 */
	protected SimplePerson getSimplePerson(String name) {
		DetachedCriteria c = DetachedCriteria.forClass(SimplePerson.class);
		c.add(Restrictions.eq("name", name));

		List<SimplePerson> people = getSimplePersonDao().findByCriteria(c);
		assertEquals(1, people.size());
		SimplePerson p = people.get(0);
		assertEquals(name, p.getName());
		return p;
	}
	
	/**
	 * @return The SimplePerson DAO.
	 */
	protected ConvenienceGenericHibernateDao<SimplePerson, Serializable>
	getSimplePersonDao() {
		return (ConvenienceGenericHibernateDao<SimplePerson, Serializable>)
			m_offliner.getFor(SimplePerson.class);
	}
	
	/**
	 * @return The Person DAO.
	 */
	protected ConvenienceGenericHibernateDao<Person, Serializable>
	getPersonDao() {
		return (ConvenienceGenericHibernateDao<Person, Serializable>)
			m_offliner.getFor(Person.class);
	}
	
	/**
	 * Remoting-fixed save person method.
	 * @param person The person to save. Parameter is modifed.
	 */
	protected void savePerson(Person person) {
		Person saved = getPersonDao().saveOrUpdateAndFlush(person);
		person.setId(saved.getId());
		person.setVersion(saved.getVersion());
	}
	
	/**
	 * Remoting-fixed save simple person method.
	 * @param simplePerson The simple person to save. Parameter is modifed.
	 */
	protected void saveSimplePerson(SimplePerson simplePerson) {
		SimplePerson saved = getSimplePersonDao().saveOrUpdateAndFlush(simplePerson);
		simplePerson.setId(saved.getId());
		simplePerson.setVersion(saved.getVersion());
	}
	
	/**
	 * @return The current offlining strategy.
	 */
	protected abstract Map<Class<?>, ChunkingStrategyImpl> getStrategy();
}
