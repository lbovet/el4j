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

import java.util.Map;

import ch.elca.el4j.core.context.ModuleApplicationListener;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;

/**
 * Spring-aware implementation of the offliner client. This waits with creating the actual client
 * until the context is complete and the DAOs are accessible.
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
public class OfflinerSpringImpl implements Offliner, ModuleApplicationListener {

	/** Temporarily save the info until the context is ready. */
	private OfflinerInfo m_tempInfo;

	/** The actual client. This is null until we are initialized. */
	private OfflinerClientImpl m_client;
	
	/**
	 * Create the client.
	 * @param info The offliner info.
	 */
	public OfflinerSpringImpl(OfflinerInfo info) {
		m_tempInfo = info;
		m_client = null;
	}
	
	/**
	 * Make sure the client is ready. Throw an exception if not.
	 */
	private void assertInit() {
		if (m_client == null) {
			throw new IllegalStateException("The spring context is not ready.");
		}
	}
	
	/**
	 * Check if the offliner is ready.
	 * @return <code>true</code> if the offliner is ready.
	 */
	public boolean isReady() {
		return m_client != null;
	}

	// Delegates to m_client.
	
	/** {@inheritDoc} */
	public void clearLocal() {
		assertInit();
		m_client.clearLocal();
	}

	/** {@inheritDoc} */
	public void eraseDeletes() {
		assertInit();
		m_client.eraseDeletes();
	}

	/** {@inheritDoc} */
	public void evict(Object... objects) {
		assertInit();
		m_client.evict(objects);
	}

	/** {@inheritDoc} */
	public Conflict[] forceLocal(Object object) {
		assertInit();
		return m_client.forceLocal(object);
	}

	/** {@inheritDoc} */
	public Conflict[] forceRemote(Object object) {
		assertInit();
		return m_client.forceRemote(object);
	}

	/** {@inheritDoc} */
	public <T> GenericDao<T> getFor(Class<T> entityType) {
		assertInit();
		return m_client.getFor(entityType);
	}

	/** {@inheritDoc} */
	public boolean isOnline() {
		assertInit();
		return m_client.isOnline();
	}

	/** {@inheritDoc} */
	public void markForDeletion(Object... objects) {
		assertInit();
		m_client.markForDeletion(objects);
	}

	/** {@inheritDoc} */
	public Conflict[] offline(Object... objects) {
		assertInit();
		return m_client.offline(objects);
	}

	/** {@inheritDoc} */
	public void setOnline(boolean online) {
		assertInit();
		m_client.setOnline(online);
	}

	/** {@inheritDoc} */
	public Conflict[] synchronize() {
		assertInit();
		return m_client.synchronize();
	}

	/** {@inheritDoc} */
	public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
		assertInit();
		return m_client.getDaos();
	}

	/**
	 * The context is copmlete. Create the real client.
	 */
	public void onContextRefreshed() {
		m_client = new OfflinerClientImpl(m_tempInfo);
	}
	

}
