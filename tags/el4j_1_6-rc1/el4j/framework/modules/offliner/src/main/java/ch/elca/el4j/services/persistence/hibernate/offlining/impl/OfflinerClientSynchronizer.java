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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.Chunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.DeleteChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.OfflineChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ReturnChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.UpdateChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ObjectCollectingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.OffliningStateWrappable;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OfflinerProperty;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningState;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.PropertyDaoInterface;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;


/**
 * All client-side synchronization stuff in an extra class as OfflinerClientImpl got too big.
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
public class OfflinerClientSynchronizer {

	/** The offliner information. */
	private final OfflinerInfo m_info;
	
	/** The client that called us. Needed to launch a offline operation. */
	private final Offliner m_client;

	/** The conflicts encountered during one run. Order is relevant. */
	private TreeSet<Conflict> m_conflicts;
	
	/**
	 * Called from OfflinerClientImpl with its members.
	 * @param info The offliner info.
	 * @param client The client.
	 */
	public OfflinerClientSynchronizer(OfflinerInfo info, Offliner client) {
		m_info = info;
		m_client = client;
		m_conflicts = new TreeSet<Conflict>(new Comparator<Conflict>() {
			/**
			 * Compare conflicts by source element.
			 * @param o1 The first element to compare.
			 * @param o2 The second element to compare.
			 * @return As defined for compare().
			 */
			public int compare(Conflict o1, Conflict o2) {
				UniqueKey k1 = m_info.getWrapper().wrap(UniqueKeyed.class,
					o1.getLocalObject()).getUniqueKey();
				UniqueKey k2 = m_info.getWrapper().wrap(UniqueKeyed.class,
					o2.getLocalObject()).getUniqueKey();
				return k1.compareTo(k2);
			}
		});
		
		
	}
	
	/** {@inheritDoc} */
	public Conflict[] synchronize() {
		
		synchronizePhase1();
		
		if (m_conflicts.isEmpty()) {
			synchronizePhase2();
		}
		
		if (m_conflicts.isEmpty()) {
			synchronizePhase3();
		}
		
		return m_conflicts.toArray(new Conflict[m_conflicts.size()]);
	}
	
	/**
	 * Phase 1 - Update data changed in the local db.
	 */
	private void synchronizePhase1() {
		for (Class<?> cls : m_info.getClasses().keySet()) {
			// list() returns a list with no generics. 
			// This cast is safe because we only use list locally and
			// its elements are certainly Objects.
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)
				((ConvenienceGenericHibernateDao<?, ?>)
					m_info.getClientDaoRegistry().getFor(cls)).getAll();
			
			// Drop anything unchanged.
			for (ListIterator<Object> iterator = list.listIterator();
				iterator.hasNext();) {
				Object current = iterator.next();
				OffliningState state = m_info.getWrapper().wrap(
					OffliningStateWrappable.class, current).getState();
				if (state == OffliningState.OFFLINED 
					|| state == OffliningState.PROCESSED 
					|| state == OffliningState.CONFLICTED) {
					iterator.remove();
				}
			}
			
			// The actual sync.
			Conflict[] syncConflicts = synchronizeByChunks(list,
				m_info.getClasses().get(cls));
			for (Conflict conflict : syncConflicts) {
				m_conflicts.add(conflict);
			}
		}
	}
	
	/**
	 * Phase 2 - Delete data marked for deletion.
	 */
	private void synchronizePhase2() {
		List<MappingEntry> toDelete = m_info.getMapDao().findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class)
				.add(Restrictions.not(Restrictions.eq("deleteVersion", 0L))));

		if (!toDelete.isEmpty()) {
			Collections.sort(toDelete, new MappingEntry.ByDeleteVersion());
			DeleteChunk deleteChunk = new DeleteChunk(toDelete.toArray(
				new MappingEntry[toDelete.size()]));
			
			ReturnChunk deleteResult = m_info.getServer()
				.synchronizeDeleteChunk(deleteChunk);
			
			if (deleteResult.getConflicts().length > 0) {
				for (Conflict conflict : deleteResult.getConflicts()) {
					m_conflicts.add(conflict);
				}
				toDelete.removeAll(Arrays.asList(deleteResult.getMappings()));
			} 
		
			// Successfully deleted objects vanish from the mapping table.
			m_info.getMapDao().delete(toDelete);
		}
	}
	
	/**
	 * Phase 3 - Update unchanged data in the local db (it might have changed on the server).
	 */
	private void synchronizePhase3() {
		PropertyDaoInterface props = (PropertyDaoInterface) m_info.getPropertyDao();
		int syncVersion = props.getIntProperty(OfflinerProperty.LAST_COMMIT_PROP);
		List<MappingEntry> unchangedEntries = m_info.getMapDao().findByCriteria(
			DetachedCriteria.forClass(MappingEntry.class).add(
				Restrictions.lt("synchronizeVersion", syncVersion)
			)
		);
		UpdateChunk chunk = new UpdateChunk(unchangedEntries.toArray(
			new MappingEntry[unchangedEntries.size()]));
		
		OfflineChunk result = m_info.getServer().synchronizeUpdateChunk(chunk);

		// Update the objects.
		for (Object obj : result.getObjects()) {
			m_client.offline(obj);
		}
		
		// Update the mapping entries.
		for (MappingEntry entry : unchangedEntries) {
			entry.setSynchronizeVersion(syncVersion);
			m_info.getMapDao().saveOrUpdate(entry);
		}
	}
	
	/**
	 * Synchronize a class with a chunking strategy. 
	 * @param list The list of objects.
	 * @param strategy The chunking strategy.
	 * @return The conficts encountered.
	 */	
	private Conflict[] synchronizeByChunks(List<Object> list, ChunkingStrategyImpl strategy) {
		Conflict[] result;
		result = strategy.apply(m_info, list);
		return result;
	}
	
	/**
	 * Force the local version of an object to database.
	 * If it works, update the mapping entries.
	 * @param object The object.
	 * @return The conflicts encountered.
	 */
	public Conflict[] forceLocal(Object object) {
		// Object must be LOCAL.
		// If not, load the local one instead.
		KeyType type = m_info.getWrapper().wrap(Typed.class, object)
			.getType();
		
		Object obj;
		if (type == KeyType.LOCAL) {
			obj = object;
		} else if (type == KeyType.REMOTE) {
			// Load local version into obj.
			Serializable localKey = m_info.getWrapper()
				.wrap(Mapped.class, object)
				.getEntry().getLocalKey().getKey();
			obj = TotallyGenericDaoUtility.getDao(
				m_info.getClientDaoRegistry(), object.getClass())
				.findById(localKey);
		} else {
			throw new IllegalArgumentException("forceLocal on NULL key.");
		}

		// From here on, use obj not object.
		
		ObjectCollectingVisitor collector = new ObjectCollectingVisitor(
			m_info.getWrapper());
		GraphWalker walker = new GraphWalker(collector, m_info.getWrapper());
		walker.run(obj);
		Object[] objects = collector.getObjects();
		MappingEntry[] entries = collector.getEntries();

		// Ensure our object is at pos. 0
		int pos = -1;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == obj) {
				pos = i;
				break;
			}
		}
		Object tmp = objects[0];
		MappingEntry tmpEntry = entries[0];
		objects[0] = objects[pos];
		entries[0] = entries[pos];
		objects[pos] = tmp;
		entries[pos] = tmpEntry;
		
		Chunk chunk = new Chunk(objects, entries);
		
		ReturnChunk ret = m_info.getServer().forceLocal(chunk);
		if (ret.getConflicts().length == 0) {
			// We succeeded. Bump all the mapping entries.
			int last = m_info.getPropertyDao().getIntProperty(OfflinerProperty.LAST_COMMIT_PROP);
			for (MappingEntry entry : ret.getMappings()) {
				entry.setSynchronizeVersion(last);
				m_info.getMapDao().saveOrUpdate(entry);
			}
		}
		return ret.getConflicts();
	}
}
