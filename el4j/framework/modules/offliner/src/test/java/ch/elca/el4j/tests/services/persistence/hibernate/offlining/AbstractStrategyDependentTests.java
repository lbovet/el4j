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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.dom.Person;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.dom.SimplePerson;

/**
 * Tests that depend on a strategy. The strategy is chosen in a subclass.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public abstract class AbstractStrategyDependentTests extends AbstractTest {

	/**
	 * Read test data and save it back again.
	 * This is simple offline-commit operation without modifying anything in the local db.
	 * It must not produce any problems.
	 */
	public void testOfflineAndCommit() {
		usePersons();
		offlineAll();
		m_offliner.setOnline(false);
		assertFalse(m_offliner.isOnline());

		List<SimplePerson> sPeople = getSimplePersonDao()
			.findByQuery(new QueryObject());
		assertEquals(12, sPeople.size());
		
		Person adam = getPerson("Adam");
		assertEquals(4, adam.getChildren().size());
		
		m_offliner.setOnline(true);
		Conflict[] result = m_offliner.synchronize();
		assertEquals(0, result.length);
		
		assertEquals(12, getSimplePersonDao()
			.findCountByQuery(new QueryObject(SimplePerson.class)));
	}
	
	/**
	 * Modify object contents and references in the local db.
	 * Ensure they are committed correctly.
	 */
	public void testModification() {
		usePersons();
		offlineAll();
		m_offliner.setOnline(false);
		assertFalse(m_offliner.isOnline());
		
		// Modify names.
		List<SimplePerson> sPeople = getSimplePersonDao()
			.findByQuery(new QueryObject());
		for (SimplePerson s : sPeople) {
			s.setName(s.getName() + " Smith");
			getSimplePersonDao().saveOrUpdate(s);
		}
		
		// Modify some references.
		Person cosmo = getPerson("Cosmo");
		
		Person arlo = getPerson("Arlo");
		
		// Cosmo becomes child of Arlo.
		arlo.adopt(cosmo);
		
		// Update. We only need to adapt cosmo as it's his parent reference
		// we modified. 
		getPersonDao().saveOrUpdate(cosmo);
		
		m_offliner.setOnline(true);
		Conflict[] c = m_offliner.synchronize();
		assertEquals(0, c.length);
		
		// Check the adoption and the names propagated.
		for (SimplePerson s : getSimplePersonDao().getAll()) {
			assertTrue(s.getName().endsWith("Smith"));
		}
		
		Person adam = getPerson("Adam");
		assertEquals(3, adam.getChildren().size());
		for (Person child : adam.getChildren()) {
			assertFalse(child.getName().equals("Cosmo"));
			if (child.getName().equals("Arlo")) {
				boolean found = false;
				for (Person grandchild : child.getChildren()) {
					if (grandchild.getName().equals("Cosmo")) {
						found = true;
					}
				}
				assertTrue(found);
			}
		}
	}
	
	/**
	 * Create new objects in local db.
	 * They only acquire a server key on synchronization. 
	 * Ensure they are written back correctly.
	 */
	public void testCreateOffline() {
		m_offliner.setOnline(false);
		Person eve = Person.create();
		eve.setName("Eve");
		Person alice = new Person(eve);
		alice.setName("Alice");
		
		getPersonDao().saveOrUpdate(eve);
		getPersonDao().saveOrUpdate(alice);
		
		m_offliner.setOnline(true);
		Conflict[] c = m_offliner.synchronize();
		assertEquals(0, c.length);
		
		Person e = getPerson("Eve");
		assertEquals(1, e.getChildren().size());
		for (Person p : e.getChildren()) {
			assertEquals("Alice", p.getName());
		}
	}
	
	/**
	 * Delete an object in the local db causing it to be marked for deletion
	 * in the metadata and deleted on sync. Ensure this happens.
	 */
	public void testDelete() {
		usePersons();
		// Delete Hugo from Person
		Conflict[] conflicts = m_offliner.offline(getPerson("Hugo"));
		assertEquals(0, conflicts.length);
		
		m_offliner.setOnline(false);
		// m_offliner.delete(getPerson("Hugo"));
		Person hugo = getPerson("Hugo");
		getPersonDao().delete(hugo);
		
		m_offliner.setOnline(true);
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		
		DetachedCriteria c = DetachedCriteria.forClass(Person.class); 
		c.add(Restrictions.eq("name", "Hugo"));
		c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		List<Person> people = getPersonDao().findByCriteria(c);
		assertTrue(people.isEmpty());
	}
	
	/**
	 * Delete an object in the local db that has a non-offlined object
	 * pointing to it on the server.
	 * Ensure the conflict is reported correctly.
	 */
	public void testDeleteConflict() {
		usePersons();
		Conflict[] conflicts = m_offliner.offline(getPerson("Adam"));
		assertEquals(0, conflicts.length);
		
		m_offliner.setOnline(false);
		getPersonDao().delete(getPerson("Adam"));
		m_offliner.setOnline(true);
		conflicts = m_offliner.synchronize();
		
		assertEquals(1, conflicts.length);
		assertEquals(Conflict.Type.CONSTRAINT , conflicts[0].getCauseType());
		
		// Resynchronize and assert the conflict is still there (fixes an earlier bug).
		conflicts = m_offliner.synchronize();
		assertEquals(1, conflicts.length);
	}
	
	/**
	 * Ensure deletes are performed in the correct order on the server
	 * (important when there are references between the objects to delete).
	 * Ensure deletes that already fail in the local db do not contribute to the order.
	 */
	public void testDeleteOrder() {
		/*
		 * Alice <-- Eve
		 */
		Person alice = Person.create();
		alice.setName("Alice");
		Person eve = new Person(alice);
		eve.setName("Eve");
		savePerson(alice);
		savePerson(eve);
		offlineAll();
		m_offliner.setOnline(false);
		
		try {
			// Fails, becuase Eve has a reference to Alice.
			getPersonDao().delete(getPerson("Alice"));
			fail();
		} catch (Exception ex) {
			// Ok.
			assertTrue(true);
		}
		
		// Check the offliner didn't mark anything.
		ConvenienceGenericHibernateDao<MappingEntry, Integer> mapDao
			= (ConvenienceGenericHibernateDao<MappingEntry, Integer>)
			m_offliner.getFor(MappingEntry.class);
		
		List<MappingEntry> deletedEntries = mapDao.findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class)
				.add(Restrictions.ne("deleteVersion", 0L)));
		
		assertTrue(deletedEntries.isEmpty());
		
		// Now, delete Alice and Eve - properly.
		getPersonDao().delete(getPerson("Eve"));
		getPersonDao().delete(getPerson("Alice"));
		
		// Just to be sure - check the order.
		deletedEntries = mapDao.findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class)
				.add(Restrictions.ne("deleteVersion", 0L)));
		
		assertEquals(2, deletedEntries.size());
		Collections.sort(deletedEntries, new MappingEntry.ByDeleteVersion());
		assertEquals(1, deletedEntries.get(0).getDeleteVersion());
		assertEquals(2, deletedEntries.get(1).getDeleteVersion());
		
		// And now, save the lot back.
		m_offliner.setOnline(true);
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
	}
	
	/**
	 * Force a conflict by messing with the delete order.
	 * Most of this method is like testDeleteOrder,
	 * except for a modification to break the delete order.
	 * DON'T EVER DO THIS !
	 */
	public void testWrongDeleteOrder() {
		/*
		 * Alice <-- Eve
		 */
		Person alice = Person.create();
		alice.setName("Alice");
		Person eve = new Person(alice);
		eve.setName("Eve");
		savePerson(alice);
		savePerson(eve);
		offlineAll();
		m_offliner.setOnline(false);
		
		getPersonDao().delete(getPerson("Eve"));
		getPersonDao().delete(getPerson("Alice"));
		
		ConvenienceGenericHibernateDao<MappingEntry, Integer> mapDao
			= (ConvenienceGenericHibernateDao<MappingEntry, Integer>)
				m_offliner.getFor(MappingEntry.class);
		
		List<MappingEntry> deletedEntries = mapDao.findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class)
				.add(Restrictions.ne("deleteVersion", 0L)));
		
		assertEquals(2, deletedEntries.size());
		Collections.sort(deletedEntries, new MappingEntry.ByDeleteVersion());
		
		// Break the delete order.
		deletedEntries.get(0).setDeleteVersion(2);
		deletedEntries.get(1).setDeleteVersion(1);
		
		mapDao.saveOrUpdate(deletedEntries.get(0));
		mapDao.saveOrUpdate(deletedEntries.get(1));
		// End of breaking.
		
		m_offliner.setOnline(true);
		Conflict[] conflicts = m_offliner.synchronize();
		
		// Can't delete Alice first.
		assertEquals(1, conflicts.length);
	}
	
	/**
	 * Modify data on the server which is also in the local db,
	 * then try and recommit.
	 * Check the conflict and its dependent conflicts are reported correctly.
	 */
	public void testConcurrentFailure() {
		usePersons();
		GenericDao<SimplePerson> dao = getSimplePersonDao();
		SimplePerson arlo = getSimplePerson("Arlo");
		long id = arlo.getId();
		long version = arlo.getVersion();
		
		Conflict[] conflicts = m_offliner.offline(arlo);
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		SimplePerson offlined = getSimplePerson("Arlo");
		offlined.setName("Arlo Smith");
		getSimplePersonDao().saveOrUpdate(offlined);
		
		// Though we are offline, this is the db dao.
		SimplePerson jones = new SimplePerson();
		jones.setName("Arlo Jones");
		jones.setId(id);
		jones.setVersion(version);
		jones.setEmail("none");
		dao.saveOrUpdate(jones);
		
		// This should cause a conflict.
		m_offliner.setOnline(true);
		Conflict[] c = m_offliner.synchronize();
		assertEquals(1, c.length);
		assertEquals(Conflict.Type.VERSION, c[0].getCauseType());
		
		// Check both objects are reported correctly in the conflict.
		Conflict conflict = c[0];
		SimplePerson localPerson = (SimplePerson) conflict.getLocalObject();
		SimplePerson remotePerson = (SimplePerson) conflict.getRemoteObject();
		
		assertEquals("Arlo Smith", localPerson.getName());
		assertEquals("Arlo Jones", remotePerson.getName());
	}
	
	/**
	 * Check that in a graph of objects where some are conflicted,
	 * those that are not conflicted are updated correctly.
	 */
	public void testDependentConflict() {
		// Bob <- Alice <- Eve
		//        ^ conflict
		
		Person bob = Person.create();
		bob.setName("Bob");
		Person alice = new Person(bob);
		alice.setName("Alice");
		Person eve = new Person(alice);
		eve.setName("Eve");
		
		savePerson(bob);
		savePerson(alice);
		savePerson(eve);
		
		// Save server id for later.
		Serializable aliceId = alice.getId();
		
		// Hold the server dao to produce conflicts.
		ConvenienceGenericHibernateDao<Person, Serializable>
			serverDao = getPersonDao();
		
		Conflict[] conflicts = m_offliner.offline(alice, eve);
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		
		// Force update. We need to "touch" everyone here.
		getPersonDao().saveOrUpdate(getPerson("Alice"));
		getPersonDao().saveOrUpdate(getPerson("Eve"));
		getPersonDao().saveOrUpdate(getPerson("Bob"));
		
		// Same on server. Note we can't use getPerson here.
		alice = serverDao.findById(aliceId);
		serverDao.saveOrUpdate(alice);
		
		// Synchronize. 
		m_offliner.setOnline(true);
		conflicts = m_offliner.synchronize();
		
		assertEquals(2, conflicts.length);
		
		checkDependentConflicts(conflicts);
		
		// Check Bob made it back to the server.
		bob = getPerson("Bob");
		assertEquals(1, bob.getVersion());	
	}
	
	/**
	 * Helper method to check the conflicts in testDependentConflict.
	 * <br>
	 * We have no idea of the conflicts order, so let's just count.
	 * Alice should throw a version conflict, eve holds a parent pointer
	 * to alice so she should throw a dependent conflict.
	 * @param conflicts The conflicts.
	 */
	private void checkDependentConflicts(Conflict[] conflicts) {
		
		int versionConflicts = 0;
		int dependentConflicts = 0;
		
		for (Conflict co : conflicts) {
			switch (co.getCauseType()) {
				case DEPENDENT:
					dependentConflicts++;
					break;
				case VERSION:
					versionConflicts++;
					break;
				default:
					fail();
			}
		}
		assertEquals(1, versionConflicts);
		assertEquals(1, dependentConflicts);	
	}
	
	/**
	 * Ensure that a server version > 0 of data does not change offliner semantics.
	 */
	public void testServerVersion() {
		usePersons();
		assertTrue(m_offliner.isOnline());
		SimplePerson alice = new SimplePerson();
		alice.setName("Alice");
		alice.setEmail("alice@one.org");
		saveSimplePerson(alice);
		alice.setEmail("alice@two.org");
		saveSimplePerson(alice);
		alice = getSimplePerson("Alice");
		assertTrue(alice.getVersion() > 0);
		offlineAll();
		
		m_offliner.setOnline(false);
		alice = getSimplePerson("Alice");
		alice.setEmail("alice@offline.org");
		getSimplePersonDao().saveOrUpdate(alice);
		m_offliner.setOnline(true);
		
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		alice = getSimplePerson("Alice");
		assertTrue(alice.getEmail().contains("offline"));
	}
	
	/**
	 * Test the offliner's evict function that removes an object
	 * from the local db without causing a delete on the server.
	 */
	public void testEvict() {
		usePersons();
		offlineAll();
		m_offliner.setOnline(false);
		
		Person e = getPerson("Elmo");
		e.setName("Ed");
		getPersonDao().saveOrUpdate(e);
		
		DetachedCriteria c = DetachedCriteria.forClass(Person.class);
		c.add(Restrictions.eq("name", "Elmo"));
		assertTrue(getPersonDao().findByCriteria(c).isEmpty());
		
		DetachedCriteria c2 = DetachedCriteria.forClass(Person.class);
		c2.add(Restrictions.eq("name", "Ed"));
		assertFalse(getPersonDao().findByCriteria(c2).isEmpty());
		
		m_offliner.evict(getPerson("Ed"));
		m_offliner.setOnline(true);
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		
		DetachedCriteria sc1 = DetachedCriteria.forClass(Person.class);
		sc1.add(Restrictions.eq("name", "Elmo"));
		assertFalse(getPersonDao().findByCriteria(sc1).isEmpty());
		
		DetachedCriteria sc2 = DetachedCriteria.forClass(Person.class);
		sc2.add(Restrictions.eq("name", "Ed"));
		assertTrue(getPersonDao().findByCriteria(sc2).isEmpty());
		
	}
	
	/**
	 * Check multiple synchronizations work as expected.
	 * Cases:
	 * <ul><li>Change in local db.</li>
	 * <li>Change on server.</li></ul>
	 */
	public void testMultipleSync() {
		usePersons();
		offlineAll();
		Conflict[] conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		
		// Offline, no change yet.
		Person arlo = getPerson("Arlo");
		assertEquals(arlo.getVersion(), 0);
		
		// Make a change offline.
		arlo.setName("Arlo Smith");
		getPersonDao().saveOrUpdate(arlo);
		m_offliner.setOnline(true);
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		
		// Check the change propagated online.
		arlo = getPerson("Arlo Smith");
		assertEquals(arlo.getVersion(), 1);

		// Make some online changes.
		arlo.setName("Arlo Jones");
		getPersonDao().saveOrUpdate(arlo);
		Person ringo = getPerson("Ringo");
		ringo.setName("Ringo Jones");
		getPersonDao().saveOrUpdate(ringo);
		
		// Check they propagate offline.
		conflicts = m_offliner.synchronize();
		assertEquals(0, conflicts.length);
		m_offliner.setOnline(false);
		arlo = getPerson("Arlo Jones");
		assertEquals(arlo.getVersion(), 2);
		ringo = getPerson("Ringo Jones");
		assertEquals(ringo.getVersion(), 1);
	}

}
