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
package ch.elca.el4j.services.persistence.hibernate.offlining.chunk;

import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ObjectCollectingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OfflinerInfo;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OfflinerProperty;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.PropertyDaoInterface;

/**
 * Base class of chunking strategies.
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
public abstract class AbstractChunkingStrategyImpl implements
	ChunkingStrategyImpl {

	/**
	 * Synchronize a collector's collection in one chunk and clear the collector.
	 * @param info The information object.
	 * @param collector The collector.
	 * @return The conflicts.
	 */
	protected Conflict[] synchronizeChunk(OfflinerInfo info, ObjectCollectingVisitor collector) {
		Chunk chunk = new Chunk(collector.getObjects(), collector.getEntries());

		// The actual synchronization.
		ReturnChunk result = info.getServer().synchronizeChunk(chunk);
		
		PropertyDaoInterface props = (PropertyDaoInterface) info.getPropertyDao();
		int syncVersion = props.getIntProperty(OfflinerProperty.LAST_COMMIT_PROP);
		for (MappingEntry entry : result.getMappings()) {
			entry.setSynchronizeVersion(syncVersion);
			info.getMapDao().saveOrUpdate(entry);
		}
		collector.clear();
		return result.getConflicts();
	}
	
	/**
	 * Utility method to concatenate two arrays.
	 * @param previous An array.
	 * @param newOnes An array.
	 * @return The two arrays combined.
	 */
	protected static Conflict[] arrayAdd(Conflict[] previous, Conflict[] newOnes) {
		Conflict[] conflicts = new Conflict[previous.length + newOnes.length];
		System.arraycopy(previous, 0, conflicts, 0, previous.length);
		System.arraycopy(newOnes, 0, conflicts, previous.length, newOnes.length);
		return conflicts;
			
	}
}
