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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.LocalDaoProxy;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.notifications.Notification;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.notifications.NotificationProcessor;

import junit.framework.TestCase;

/**
 * Tests for the local dao proxy.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
@Component
public class LocalDaoProxyTest extends TestCase {

	/** Simulates a offliner. */
	private Offliner m_offliner;
	
	/** Simulates a dao. */
	private ConvenienceGenericHibernateDao<Object, Long> m_dao;
	
	/** The notification processor. */
	private NotificationProcessor m_processor;
	
	/** Set up the test objects. */
	@SuppressWarnings("unchecked")
	public void setUp() {
		m_offliner = new OfflinerStub();
		m_processor = new NotificationProcessor();
		m_dao = new DaoStub(m_processor);
		
		// Unchecked conversion ok because we are proxying an object and we
		// know what it is.
		m_dao = (ConvenienceGenericHibernateDao<Object, Long>) 
			Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				m_dao.getClass().getInterfaces(), new LocalDaoProxy(m_dao, m_offliner));
	}
	
	/** 
	 * deleteAll() test.
	 * 
	 */
	public void testDeleteAll() {
		m_processor.expect(
			new Notification("getAll"),
			new Notification("deleteAll"),
			new Notification("mark") {
				@Override public boolean validate(Notification target) {
					return super.validate(target)
						&& ((Object[]) target.getArgs()).length == 3;
				}
			}
		);
		m_dao.deleteAll();
		m_processor.validate();
		
	}
	
	/**
	 * Test the deprecated delete(ID) method.
	 */
	@SuppressWarnings("deprecation")
	public void testDeleteDeprecated() {
		m_processor.expect(
			new Notification("findById", 100L),
			new Notification("deprecatedDelete", 100L),
			new Notification("mark", "Object 100")
		);
		m_dao.delete(100L);
		m_processor.validate();
	}
	
	/**
	 * Test the (new) deleteById.
	 */
	public void testDeleteById() {
		m_processor.expect(
			new Notification("findById", 100L),
			new Notification("deleteById", 100L),
			new Notification("mark", "Object 100")
		);
		m_dao.deleteById(100L);
		m_processor.validate();
	}
	
	/**
	 * Test delete(Object).
	 */
	public void testDelete() {
		m_processor.expect(
			new Notification("delete", "Object"),
			new Notification("mark", "Object")
		);
		m_dao.delete("Object");
		m_processor.validate();
	}
	
	/**
	 * Test delete(Collection).
	 */
	public void testDeleteCollection() {
		List<Object> data = new ArrayList<Object>();
		data.add("first");
		data.add("second");
		m_processor.expect(
			new Notification("deleteCollection"),
			new Notification("mark", new Object[] {"first", "second"})
		);
		m_dao.delete(data);
		m_processor.validate();
	}
	
	/**
	 * Test that an exception is raised if we call an unknown delete method.
	 */
	public void unknownDelete() {
		try {
			((FooDeleter) m_dao).deleteFoo();
			fail("Unknown delete not recognized.");
		} catch (OfflinerInternalRTException ex) {
			// Because we're deliberately forcing a "this should never happen"
			// error, this is the one place a OfflinerInternalRTException isn't
			// a bug.
			assertTrue(true);
		}
	}
	
	/**
	 * Test a delete that fails on the dao (exception). This must
	 * not cause a mark.
	 */
	public void testDeleteFails() {
		m_processor.expect(
			new Notification("delete", "EXCEPTION")
		);
		try {
			m_dao.delete("EXCEPTION");
			fail("Where's my exception?");
		} catch (Exception ex) {
			// Ok.
			assertTrue(true);
		}
		m_processor.validate();
	}
	
	/**
	 * Simulate an offliner. Inner class so it can call out when its markForDeletion is called.
	 */
	class OfflinerStub implements Offliner {
	
		/** {@inheritDoc} */
		public Conflict[] offline(Object... objects) { return null; }
	
		/** {@inheritDoc} */
		public void clearLocal() {	}
	
		/** {@inheritDoc} */
		public void evict(Object... objects) {	}
	
		/** {@inheritDoc} */
		public <T> GenericDao<T> getFor(Class<T> entityType) {	
			return null;
		}
	
		/** {@inheritDoc} */
		public boolean isOnline() {
			return false;
		}
	
		/** {@inheritDoc} */
		public void markForDeletion(Object... objects) {
			m_processor.call(new Notification("mark", objects));
		}
	
		/** {@inheritDoc} */
		public void setOnline(boolean online) {	}
	
		/** {@inheritDoc} */
		public Conflict[] synchronize() {
			return null;
		}
	
		/** {@inheritDoc} */
		public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
			return null;
		}

		/** {@inheritDoc} */
		public Conflict[] forceLocal(Object object) {
			return null;
		}

		/** {@inheritDoc} */
		public Conflict[] forceRemote(Object object) {
			return null;
		}

		/** {@inheritDoc} */
		public void eraseDeletes() { }
	}

	
}
