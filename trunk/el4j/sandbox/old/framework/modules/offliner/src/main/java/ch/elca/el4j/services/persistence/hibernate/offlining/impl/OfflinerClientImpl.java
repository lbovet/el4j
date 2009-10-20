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
package ch.elca.el4j.services.persistence.hibernate.offlining.impl;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ForcedOffliningVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.OffliningVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.LocalDaoProxy;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OfflinerProperty;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;


/**
 * Offlining client. This is presented to an application under the Offliner interface.
 * It takes an offlining server as parameter (via the info)
 * and passes calls to it; synchronize() is chunked.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class OfflinerClientImpl implements Offliner {
	
	/** The offliner information. */
	private final OfflinerInfo m_info;
	
	/** Whether we are online. */
	private boolean m_online;
	
	/**
	 * The delete order. Used to store the order of deletes to replay them in the same
	 * order to the server.
	 */
	private long m_deleteOrder;

	/**
	 * Create an offliner client specifying explicitly which of online/offline to use.
	 * @param info The offliner info object.
	 * @param online Whether the mode should be online (<code>true</code>) or offline (<code>false</code>).
	 */
	public OfflinerClientImpl(OfflinerInfo info, boolean online) {
		this(info);
		setOnline(online);
	}
	
	/**
	 * Constructor.
	 * @param info The offliner info.
	 */
	public OfflinerClientImpl(OfflinerInfo info) {
		m_info = info;
		if (!m_info.isComplete()) {
			throw new IllegalArgumentException("Info object is not complete.");
		}
		
		if (!m_info.getPropertyDao().isPropertyPresent(OfflinerProperty.LAST_COMMIT_PROP)) {
			m_info.getPropertyDao().saveProperty(OfflinerProperty.LAST_COMMIT_PROP, 0);
		}
		if (!m_info.getPropertyDao().isPropertyPresent(OfflinerProperty.LAST_SUCCESSFUL_COMMIT_PROP)) {
			m_info.getPropertyDao().saveProperty(OfflinerProperty.LAST_SUCCESSFUL_COMMIT_PROP, 0);
		}
		if (!m_info.getPropertyDao().isPropertyPresent(OfflinerProperty.CURRENT_STATE)) {
			m_info.getPropertyDao().saveProperty(OfflinerProperty.CURRENT_STATE, 1);
		}
		
		if (!m_info.getPropertyDao().isPropertyPresent(OfflinerProperty.DELETE_ORDER)) {
			m_info.getPropertyDao().saveProperty(OfflinerProperty.DELETE_ORDER, 0L);
			m_deleteOrder = 0L;
		} else {
			m_deleteOrder = m_info.getPropertyDao().getLongProperty(OfflinerProperty.DELETE_ORDER);
		}
		
		m_online = (m_info.getPropertyDao().getIntProperty(OfflinerProperty.CURRENT_STATE) != 0);
	}
	
	/**
	 * Ensure we are online.
	 */
	private void assertOnline() {
		if (!m_online) {
			throw new IllegalStateException("Database operation called while "
				+ "offline.");
		}
	}
	
	/**
	 * Ensure we are offline.
	 */
	private void assertOffline() {
		if (m_online) {
			throw new IllegalStateException("offline operation called while online.");
		}
	}
	
	/** 
	 * Ensure this is not the event dispatch thread. This is because hibernate sessions are bound to
	 * threads and the daos do not work properly on the event dispatch thread.
	 */
	private void assertNotEDT() {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("Offliner operations cannot be performed on the swing event "
				+ "dispatch thread.");
		}
	}
	
	/** {@inheritDoc} */
	public Conflict[] offline(Object... objects) {
		assertNotEDT();
		OffliningVisitor visitor = new OffliningVisitor(m_info.getWrapper(),
			m_info.getClientDaoRegistry());
		GraphWalker walker = new GraphWalker(visitor, m_info.getWrapper());
		for (Object o : objects) {
			Object[] reds = walker.run(o);
			if (reds.length > 0) {
				// Got a conflict. 
				// Handle it then abort.
				Conflict[] conflicts = new Conflict[reds.length];
				for (int i = 0; i < conflicts.length; i++) {
					conflicts[i] = (Conflict) reds[i];
				}
				return conflicts;
			}
		}
		return new Conflict[0];
	}

	/** {@inheritDoc} */
	public void clearLocal() {
		for (Class<?> cls : m_info.getClasses().keySet()) {
			Object obj = m_info.getClientDaoRegistry().getFor(cls);
			ConvenienceGenericHibernateDao<?, ?> dao = (ConvenienceGenericHibernateDao<?, ?>) obj;
			dao.deleteAll();
		}

		m_info.getMapDao().deleteAll();
		if (m_info.getMapDao().findCountByCriteria(DetachedCriteria.forClass(
			MappingEntry.class)) != 0) {
			throw new OfflinerInternalRTException("Delete on mapping table failed.");
		}
		m_deleteOrder = 0L;
		m_info.getPropertyDao().saveProperty(OfflinerProperty.DELETE_ORDER, 0L);
	}

	/** {@inheritDoc} */
	public void evict(Object... objects) {
		assertOffline();
		
		for (Object obj : objects) {
			TotallyGenericDaoUtility.getDao(m_info.getClientDaoRegistry(), obj)
				.delete(obj);
			// Delete sets the mapping entry to deleted - remove it completely.
			MappingEntry entry = m_info.getWrapper().wrap(
				Mapped.class, obj).getEntry();

			m_info.getMapDao().delete(entry);
		}
	}
	
	/** {@inheritDoc} */
	public void setOnline(boolean online) {
		m_online = online;
		m_info.getPropertyDao().saveProperty(OfflinerProperty.CURRENT_STATE, online ? 1 : 0);
	}
	
	/** {@inheritDoc} */
	public boolean isOnline() {
		return m_online;
	}

	/** {@inheritDoc} */
	public void markForDeletion(Object... objects) {
		assertOffline();
		for (Object obj : objects) {
			Mapped mapped = m_info.getWrapper()
				.wrap(Mapped.class, obj); 
			MappingEntry entry = mapped.getEntry();
			
			// if there is no entry in the mappingEntry table (i.e. if the object was not offlined from the server)
			//  we do not need to mark it for deletion (a local deletion is enough)		
			if (entry != null) {
				
				// Deletes must be performed in the same order locally and
				// in the database to prevent constraint violations.
				// If an object is deleted locally, its server version
				// becomes useless and we use the field to store the delete order.
				++m_deleteOrder;
				m_info.getPropertyDao().saveProperty(OfflinerProperty.DELETE_ORDER, m_deleteOrder);
				entry.setDeleteVersion(m_deleteOrder);
				mapped.setEntry(entry);
			}
		}

	}
	
	/** {@inheritDoc} */
	public Conflict[] synchronize() {
		assertOnline();
		assertNotEDT();

		OfflinerClientSynchronizer sync = new OfflinerClientSynchronizer(m_info, this);
		
		int lastCommit = m_info.getPropertyDao().getIntProperty(
			OfflinerProperty.LAST_COMMIT_PROP);
		m_info.getPropertyDao().saveProperty(
			OfflinerProperty.LAST_COMMIT_PROP, lastCommit + 1);
		
		Conflict[] conflicts = sync.synchronize();
		
		if (conflicts.length == 0) {
			int lastSuccessfulCommit = m_info.getPropertyDao().getIntProperty(
				OfflinerProperty.LAST_SUCCESSFUL_COMMIT_PROP);
			m_info.getPropertyDao().saveProperty(
				OfflinerProperty.LAST_SUCCESSFUL_COMMIT_PROP,
				lastSuccessfulCommit + 1);
		}
		
		return conflicts;
	}

	/** {@inheritDoc} */
	public Conflict[] forceLocal(Object object) {
		assertOnline();
		assertNotEDT();
		
		OfflinerClientSynchronizer sync = new OfflinerClientSynchronizer(m_info, this);
		Conflict[] conflicts = sync.forceLocal(object);
		return conflicts;
	}
	
	/** {@inheritDoc} */
	public Conflict[] forceRemote(Object object) {
		// This is a clone of offline() except that we have one object
		// and a special visitor.
		
		assertNotEDT();
		
		OffliningVisitor visitor = new ForcedOffliningVisitor(m_info.getWrapper(),
			m_info.getClientDaoRegistry(), object);
		GraphWalker walker = new GraphWalker(visitor, m_info.getWrapper());

		Object[] reds = walker.run(object);
		if (reds.length > 0) {
			// Got a conflict. 
			// Handle it then abort.
			Conflict[] conflicts = new Conflict[reds.length];
			for (int i = 0; i < conflicts.length; i++) {
				conflicts[i] = (Conflict) reds[i];
			}
			return conflicts;
		}

		return new Conflict[0];
	}
	
	/** {@inheritDoc} */
	public void eraseDeletes() {
		List<MappingEntry> entries = m_info.getMapDao().findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class)
				.add(Restrictions.ne("deleteVersion", 0L)));
		m_info.getMapDao().delete(entries);
	}
	
	/*
	 * DaoRegistry.
	 */

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public <T> GenericDao<T> getFor(Class<T> entityType) {
		GenericDao<T> dao;
		if (m_online) {
			dao = m_info.getServerDaoRegistry().getFor(entityType);
		} else {
			dao = m_info.getClientDaoRegistry().getFor(entityType);
			// Unchecked cast ok because we are proxying an object.
			dao = (GenericDao<T>) 
				Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
					dao.getClass().getInterfaces() , new LocalDaoProxy(dao, this));
		}
		return dao;
	}

	/** {@inheritDoc} */
	public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
		if (m_online) {
			return m_info.getServerDaoRegistry().getDaos();
		} else {
			return m_info.getClientDaoRegistry().getDaos();
		}
	}
}
