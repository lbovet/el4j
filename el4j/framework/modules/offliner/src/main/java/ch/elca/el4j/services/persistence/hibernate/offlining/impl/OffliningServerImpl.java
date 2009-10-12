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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.Chunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.DeleteChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.OfflineChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ReturnChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.UpdateChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ForcedSynchronizingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ServerSynchronizingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningStateTable;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.ServerMappingTable;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Offlining server. This runs on the server and recieves the offlining client's calls.
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
public class OffliningServerImpl implements OffliningServer {

	/** The server dao registry. */
	private final DaoRegistry m_daoRegistry;

	/** The mapping table. */
	private final ServerMappingTable m_mappingTable;
	
	/** The object wrapper. */
	private final ObjectWrapper m_wrapper;
	
	/** The state table. */
	private final OffliningStateTable m_stateTable;
	
	/**
	 * Create the offlining server.
	 * @param daoRegistry The dao registry.
	 * @param mappingTable The mapping table.
	 * @param wrapper The object wrapper.
	 * @param stateTable The state table.
	 */
	public OffliningServerImpl(DaoRegistry daoRegistry, ServerMappingTable mappingTable, ObjectWrapper wrapper,
		OffliningStateTable stateTable) {
		m_daoRegistry = daoRegistry;
		m_mappingTable = mappingTable;
		m_wrapper = wrapper;
		m_stateTable = stateTable;
	}

	/**
	 * Synchronize a chunk.
	 * @param chunk The chunk.
	 * @return The return chunk. 
	 */
	public ReturnChunk synchronizeChunk(Chunk chunk) {
		m_mappingTable.clear();
		m_stateTable.clear();
		for (MappingEntry entry : chunk.getMappings()) {
			m_mappingTable.saveEntry(entry);
		}
		
		Conflict[] conflicts = new Conflict[0];
		for (Object obj : chunk.getObjects()) {
			GraphWalker walker = new GraphWalker(
				new ServerSynchronizingVisitor(m_wrapper, m_daoRegistry),
				m_wrapper
			);
			Object[] reds = walker.run(obj);
			Conflict[] newConflicts = new Conflict[reds.length];
			for (int i = 0; i < reds.length; i++) {
				newConflicts[i] = (Conflict) reds[i];
			}
			conflicts = arrayAdd(conflicts, newConflicts);
		}
		
		return new ReturnChunk(m_mappingTable.getAll(), conflicts);
	}
	
	/**
	 * Delete a chunk of entries.
	 * @param chunk The chunk.
	 * @return The result of deletion.
	 */
	public ReturnChunk synchronizeDeleteChunk(DeleteChunk chunk) {
		List<Conflict> conflicts = new LinkedList<Conflict>();
		List<MappingEntry> undeletable = new LinkedList<MappingEntry>();
		for (MappingEntry entry : chunk.getEntries()) {
			try {
				Class<?> cls = entry.getRemoteKey().getObjectClass();
				Serializable key = entry.getRemoteKey().getKey();
				TotallyGenericDaoUtility.getDao(m_daoRegistry, cls).deleteById(key);
			} catch (OptimisticLockingFailureException ex) {
				conflicts.add(new Conflict(Conflict.Phase.DELETE, ex, null, entry.getRemoteKey()));
				undeletable.add(entry);
			} catch (DataAccessException ex) {
				conflicts.add(new Conflict(Conflict.Phase.DELETE, ex, null, entry.getRemoteKey()));
				undeletable.add(entry);
			} 
		}
		return new ReturnChunk(undeletable.toArray(new MappingEntry[undeletable.size()]),
			conflicts.toArray(new Conflict[conflicts.size()]));
	}
	
	/**
	 * Utility method to concatenate two arrays.
	 * @param previous An array.
	 * @param newOnes An array.
	 * @return The two arrays combined.
	 */
	private static Conflict[] arrayAdd(Conflict[] previous, Conflict[] newOnes) {
		Conflict[] conflicts = new Conflict[previous.length + newOnes.length];
		System.arraycopy(previous, 0, conflicts, 0, previous.length);
		System.arraycopy(newOnes, 0, conflicts, previous.length, newOnes.length);
		return conflicts;
	}

	/** {@inheritDoc} */
	public OfflineChunk synchronizeUpdateChunk(UpdateChunk chunk) {
		List<Object> changedObjects = new LinkedList<Object>();
		for (MappingEntry entry : chunk.getEntries()) {
			Class<?> cls = entry.getObjectClass();
			Serializable id = entry.getRemoteKey().getKey();
			Serializable version = entry.getRemoteBaseVersion();
			Object dbInstance = TotallyGenericDaoUtility.getDao(m_daoRegistry, cls).findById(id);
			Serializable dbVersion = m_wrapper.wrap(
				KeyedVersioned.class, dbInstance).getVersion();
			if (!version.equals(dbVersion)) {
				changedObjects.add(dbInstance);
			}
		}
		return new OfflineChunk(changedObjects.toArray());
	}

	/** {@inheritDoc} */
	public ReturnChunk forceLocal(Chunk chunk) {
		// This is a copy of the real synchronize except that we use a 
		// special visitor.
		
		m_mappingTable.clear();
		m_stateTable.clear();
		for (MappingEntry entry : chunk.getMappings()) {
			m_mappingTable.saveEntry(entry);
		}
		
		Conflict[] conflicts = new Conflict[0];
		for (Object obj : chunk.getObjects()) {
			GraphWalker walker = new GraphWalker(
				new ForcedSynchronizingVisitor(m_wrapper, m_daoRegistry,
					chunk.getObjects()[0]),
				m_wrapper
			);
			Object[] reds = walker.run(obj);
			Conflict[] newConflicts = new Conflict[reds.length];
			for (int i = 0; i < reds.length; i++) {
				newConflicts[i] = (Conflict) reds[i];
			}
			conflicts = arrayAdd(conflicts, newConflicts);
		}
		
		return new ReturnChunk(m_mappingTable.getAll(), conflicts);
	}
	
	
}
