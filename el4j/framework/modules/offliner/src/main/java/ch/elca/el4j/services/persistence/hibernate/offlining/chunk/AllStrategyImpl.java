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
 * Implementation of ALL chunking strategy.
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
public class AllStrategyImpl extends AbstractChunkingStrategyImpl {

	/** {@inheritDoc} */
	public Conflict[] apply(OfflinerInfo info, List<Object> list) {
		ObjectCollectingVisitor collector = new ObjectCollectingVisitor(
			info.getWrapper());
		GraphWalker walker = new GraphWalker(collector, info.getWrapper());
		for (Object obj : list) {
			walker.run(obj);
		}
		
		return synchronizeChunk(info, collector);
	}

}
