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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.AllStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.Person;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.SimplePerson;


/**
 * Tests that do not depend on the offlining strategy. These are run once with the ALL
 * strategy.
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
@Component
public class StrategyIndependentTest extends AbstractTest {

	/**
	 * Check that a new element inserted into REMOTE is identified as REMOTE, and same for LOCAL.
	 */
	public void testKeyStrategy() {
		m_offliner.setOnline(true);
		SimplePerson r = new SimplePerson();
		r.setName("Alice");
		r.setEmail("alice@remote.org");
		saveSimplePerson(r);
		assertTrue("The keys in the remote database must be > 0", r.getId() > 0L);
		
		m_offliner.setOnline(false);
		SimplePerson l = new SimplePerson();
		l.setName("Alice");
		l.setEmail("alice@local.org");
		saveSimplePerson(l);
		assertTrue("The keys in the local database must be < 0", l.getId() < 0L);
	}
	
	/**
	 * Check offline operation works.
	 * Check dependent objects are offlined too.
	 */
	public void testOffline() {
		usePersonsSmall();
		
		// Offline some data. Bob has parent Alice who should come too.
		Conflict[] conflicts = m_offliner.offline(getSimplePerson("Carol"));
		assertEquals(0, conflicts.length);
		conflicts = m_offliner.offline(getPerson("Bob"));
		assertEquals(0, conflicts.length);
		
		m_offliner.setOnline(false);
		assertFalse(m_offliner.isOnline());
		List<SimplePerson> simplePeople = getSimplePersonDao().getAll();
		assertEquals(1, simplePeople.size());
		assertEquals("Carol", simplePeople.get(0).getName());
		
		List<Person> people = getPersonDao().getAll();
		// Check if Alice is present too. 
		// getPerson fails if the person of that name does not exist.
		assertEquals(2, people.size());
		getPerson("Alice");
		
	}
	
	
	/**
	 * Check several offline operations in sequence do not produce clones in the local db.
	 */
	public void testOfflineIdentity() {
		usePersonsSmall();
		
		// Offline three times. This must result in one entry only in the local db.
		Conflict[] conflicts = m_offliner.offline(getSimplePerson("Carol"));
		assertEquals(0, conflicts.length);
		conflicts = m_offliner.offline(getSimplePerson("Carol"));
		assertEquals(0, conflicts.length);
		conflicts = m_offliner.offline(getSimplePerson("Carol"));
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		List<SimplePerson> simple = getSimplePersonDao().getAll();
		assertEquals(1, simple.size());
	}
	
	/**
	 * Check offline corrrectly updates new objects from the server.
	 */
	public void testOfflineServerNew() {
		usePersonsSmall();
		
		// Offline some data.
		Conflict[] conflicts = m_offliner.offline(getSimplePerson("Carol"));
		assertEquals(0, conflicts.length);

		// Change it online.
		SimplePerson carol = getSimplePerson("Carol");
		carol.setName("Carol Smith");
		saveSimplePerson(carol);
		
		// Check the offliner has the original one offline.
		m_offliner.setOnline(false);
		assertFalse(m_offliner.isOnline());
		List<SimplePerson> simple = getSimplePersonDao().getAll();
		assertEquals("Carol", simple.get(0).getName());
		
		// Update. This is ok as the offline data is unchanged.
		m_offliner.setOnline(true);
		assertTrue(m_offliner.isOnline());
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		assertFalse(m_offliner.isOnline());
		
		// Check the change propagated.
		simple = getSimplePersonDao().getAll();
		assertEquals(1, simple.size());
		assertEquals("Carol Smith", simple.get(0).getName());
	}
	
	/**
	 * Check the offline fails if the local instance was updated.
	 */
	public void testOfflineFailOnNew() {
		usePersonsSmall();
		offlineAll();
		
		// Change data while offline.
		m_offliner.setOnline(false);
		Person bob = getPerson("Bob");
		bob.setName("Bob Smith");
		savePerson(bob);
		
		// Change again while online.
		m_offliner.setOnline(true);
		bob = getPerson("Bob");
		bob.setName("Bob Jones");
		savePerson(bob);
		
		// Bob should conflict.
		Conflict[] conflicts = m_offliner.offline(getPerson("Bob Jones"));
		assertEquals(1, conflicts.length);
		
		// Trying to offline Eve who depends on Bob.
		conflicts = m_offliner.offline(getPerson("Eve"));
		assertEquals(2, conflicts.length);
	}
	
	/**
	 * Create a conflict then resolve it by forcing the remote version.
	 * Also check a force on a dependent conflict fails.
	 */
	public void testForceRemote() {
		forceTest(false);
		
		// Check the correct force happened.
		// As usual, getPerson fails if he doesn't exist.
		m_offliner.setOnline(false);
		getPerson("Bob Smith");
		m_offliner.setOnline(true);
		getPerson("Bob Smith");
	}
	
	/**
	 * Create a conflict then resolve it by forcing the local version.
	 * Also check a force on a dependent conflict fails.
	 */
	public void testForceLocal() {
		forceTest(true);
		
		// Check the correct force happened.
		// As usual, getPerson fails if he doesn't exist.
		m_offliner.setOnline(false);
		getPerson("Bob Jones");
		m_offliner.setOnline(true);
		getPerson("Bob Jones");
	}
	
	/**
	 * Common method fo forceLocal and forceRemote test as they are 90% equal.
	 * The only difference is the force type to use.
	 * @param local Whether this is the local or remote test.
	 */
	private void forceTest(boolean local) {
		usePersonsSmall();
		offlineAll();
		
		// While online, update Bob.
		Person bob = getPerson("Bob");
		bob.setName("Bob Smith");
		savePerson(bob);
		
		// Go offline, update Bob again.
		m_offliner.setOnline(false);
		bob = getPerson("Bob");
		bob.setName("Bob Jones");
		savePerson(bob);
		m_offliner.setOnline(true);
		
		// We are online. Ensure there is a conflict.
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(1, conflicts.length);
		
		// Try and force Eve. This must fail.
		Person eve = getPerson("Eve");
		if (local) {
			conflicts = m_offliner.forceLocal(eve);
		} else {
			conflicts = m_offliner.forceRemote(eve);
		}
		assertEquals(2, conflicts.length);
		
		// Check no-one's touched Bob.
		// getPerson fails if the person of that name does not exist.
		bob = getPerson("Bob Smith");
		m_offliner.setOnline(false);
		bob = getPerson("Bob Jones");
		m_offliner.setOnline(true);
		
		// Force Bob. This should work.
		bob = getPerson("Bob Smith");
		if (local) {
			conflicts = m_offliner.forceLocal(bob);
		} else {
			conflicts = m_offliner.forceRemote(bob);
		}
		assertEquals(0, conflicts.length);
		
		// Run a sync. As the conflict is gone, it must run through.
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
	}
	
	/**
	 * Provoke a deletion conflict and ensure the resolutuion strategy works.
	 */
	public void testDeleteResolution() {
		usePersonsSmall();
		
		// Offline Alice and delete her offline causing a mark for deletion.
		m_offliner.offline(getPerson("Alice"));
		m_offliner.setOnline(false);
		getPersonDao().delete(getPerson("Alice"));
		
		// The sync must fail as Bob depends on Alice on the server.
		m_offliner.setOnline(true);
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(1, conflicts.length);
		
		// Resolve it by declaring the deletion void.
		// See sync works this time.
		m_offliner.eraseDeletes();
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
	}
	
	/** {@inheritDoc} */
	@Override
	protected Map<Class<?>, ChunkingStrategyImpl> getStrategy() {
		LinkedHashMap<Class<?>, ChunkingStrategyImpl> map = new LinkedHashMap<Class<?>, ChunkingStrategyImpl>();
		ChunkingStrategyImpl strategy = new AllStrategyImpl();
		map.put(Person.class, strategy);
		map.put(SimplePerson.class, strategy);
		return map;
	}
}
