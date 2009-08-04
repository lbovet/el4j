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
package ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker;

import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;

/**
 * Class that visits a node during object graph traversal. 
 * <p>
 * For documentation see GraphWalker.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface NodeVisitor {

	/**
	 * Called on a UNSEEN node prior to visiting children. This is useful
	 * if the object was already processed in another graph so we can skip
	 * all recursion.
	 * @param node The node.
	 * @return <ul><li>UNSEEN if the node should be processed normally.</li>
	 * <li>PROCESSED if the node has already been dealt with.</li>
	 * <li>ERROR if the node has become "in error" already.</li></ul>
	 */
	NodeState preVisit(Object node);
	
	/**
	 * Visit a node that is about to become PROCESSED (or ERROR).
	 * @param node The node to visit.
	 * @throws NodeException To mark the node as in error.
	 */
	void visit(Object node) throws NodeException;
	
	/**
	 * Mark a node as in error due to an error child being found. This is
	 * called instead of visit.
	 * @param node The node to mark.
	 * @param cause An error child. If several exist it is only guaranteed that
	 * one is passed, the order is undefined.
	 * @return The error message for this object.
	 */
	Object markError(Object node, Object cause);
	
}
