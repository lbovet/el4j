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

import java.util.List;

import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.ObjectCollectingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OfflinerInfo;

/**
 * Implementation of OUT_INDEPENDENT chunking strategy.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class OutIndependentStrategyImpl extends AbstractChunkingStrategyImpl {

	/** 
	 * The length a chunk can grow to before it is ready for sending to the server.
	 */
	private final int m_length;
	
	/**
	 * Create an instance of the strategy. Instances can be freely shared.
	 * @param length The length a chunk can grow to before it is ready to submit.
	 */
	public OutIndependentStrategyImpl(int length) {
		m_length = length;
	}
	
	/** {@inheritDoc} */
	public Conflict[] apply(OfflinerInfo info, List<Object> list) {
		Conflict[] conflicts = new Conflict[0];
		ObjectCollectingVisitor collector = new ObjectCollectingVisitor(
			info.getWrapper());
		GraphWalker walker = new GraphWalker(collector, info.getWrapper());
		for (Object obj : list) {
			walker.run(obj);
			
			// Arbitrary chunking limit.
			if (collector.getObjects().length > m_length) {
				Conflict[] newConflicts = synchronizeChunk(info, collector);
				conflicts = arrayAdd(conflicts, newConflicts);
			}
		}
		// The last block might still be waiting to be synchronized.
		if (collector.getObjects().length > 0) {
			Conflict[] newConflicts = synchronizeChunk(info, collector);
			conflicts = arrayAdd(conflicts, newConflicts);
		}
		
		return conflicts;
	}

}
