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

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OfflinerProperty;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningStateTable;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.PropertyDaoInterface;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;


/**
 * The information the offliner client needs. This is a messenger class which it can pass to
 * the synchronizer and allows initialization to be factored out from the actual offliner implementation.
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
public class OfflinerInfo {

	/** The client-side dao registry. */
	private DaoRegistry m_clientDaoRegistry;
	
	/** The server-side dao registry. */
	private DaoRegistry m_serverDaoRegistry;
	
	/** The object wrapper. */
	private ObjectWrapper m_wrapper;
	
	/** 
	 * The list of all domain classes and their strategies.
	 * The order is relevant for synchronization. 
	 */
	private Map<Class<?>, ChunkingStrategyImpl> m_classes;
	
	/** The server. */
	private OffliningServer m_server;
	
	/** The local state table. */
	private OffliningStateTable m_stateTable;

	/**
	 * Get the clientDaoRegistry.
	 * @return The clientDaoRegistry.
	 */
	public DaoRegistry getClientDaoRegistry() {
		return m_clientDaoRegistry;
	}

	/**
	 * Get the serverDaoRegistry.
	 * @return The serverDaoRegistry.
	 */
	public DaoRegistry getServerDaoRegistry() {
		return m_serverDaoRegistry;
	}

	/**
	 * Get the wrapper.
	 * @return The wrapper.
	 */
	public ObjectWrapper getWrapper() {
		return m_wrapper;
	}

	/**
	 * Get the classes.
	 * @return The classes.
	 */
	public Map<Class<?>, ChunkingStrategyImpl> getClasses() {
		return m_classes;
	}

	/**
	 * Get the server.
	 * @return The server.
	 */
	public OffliningServer getServer() {
		return m_server;
	}

	/**
	 * Get the stateTable.
	 * @return The stateTable.
	 */
	public OffliningStateTable getStateTable() {
		return m_stateTable;
	}

	/**
	 * Setter for clientDaoRegistry.
	 * @param clientDaoRegistry The new clientDaoRegistry to set.
	 */
	public void setClientDaoRegistry(DaoRegistry clientDaoRegistry) {
		m_clientDaoRegistry = clientDaoRegistry;
	}

	/**
	 * Setter for serverDaoRegistry.
	 * @param serverDaoRegistry The new serverDaoRegistry to set.
	 */
	public void setServerDaoRegistry(DaoRegistry serverDaoRegistry) {
		m_serverDaoRegistry = serverDaoRegistry;
	}

	/**
	 * Setter for wrapper.
	 * @param wrapper The new wrapper to set.
	 */
	public void setWrapper(ObjectWrapper wrapper) {
		m_wrapper = wrapper;
	}

	/**
	 * Setter for classes.
	 * @param classes The new classes to set.
	 */
	public void setClasses(Map<Class<?>, ChunkingStrategyImpl> classes) {
		m_classes = classes;
	}

	/**
	 * Setter for server.
	 * @param server The new server to set.
	 */
	public void setServer(OffliningServer server) {
		m_server = server;
	}

	/**
	 * Setter for stateTable.
	 * @param stateTable The new stateTable to set.
	 */
	public void setStateTable(OffliningStateTable stateTable) {
		m_stateTable = stateTable;
	}
	
	/**
	 * @return Whether all properties have been set.
	 */
	public boolean isComplete() {
		boolean isComplete = true;
		isComplete &= (m_clientDaoRegistry != null);
		isComplete &= (m_serverDaoRegistry != null);
		isComplete &= (m_wrapper != null);
		isComplete &= (m_classes != null);
		isComplete &= (m_server != null);
		isComplete &= (m_stateTable != null);
		
		return isComplete;
	}
	
	/**
	 * @return The mapping table dao.
	 */
	public ConvenienceGenericHibernateDao<MappingEntry, Integer> getMapDao() {
		return (ConvenienceGenericHibernateDao<MappingEntry, Integer>)
			getClientDaoRegistry().getFor(MappingEntry.class);
	}
	
	/**
	 * @return The property dao.
	 */
	public PropertyDaoInterface getPropertyDao() {
		return (PropertyDaoInterface) getClientDaoRegistry().getFor(OfflinerProperty.class);
	}
	
}
