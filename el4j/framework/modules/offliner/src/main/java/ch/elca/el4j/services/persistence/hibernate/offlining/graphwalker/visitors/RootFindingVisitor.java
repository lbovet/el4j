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
package ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors;

import java.util.HashSet;
import java.util.Set;

import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.Linked;


/**
 * The RootFindingVisitor finds all "roots" of an object graph. The correct definition of a root is a node that cannot
 * be reached from any other node. The definition we use is:
 * <ul><li>Traversing an object graph from all roots must traverse the whole graph.</li> 
 * <li>As few nodes as possible should be roots.</li></ul>
 * The idea is that instead of running <code>synchronize</code> on all nodes of an object graph, it suffices to run
 * on all roots. For instance, in the graph <code>A --> B --> C</code>, it suffices to run (recursively) on A.
 * In a graph that contains or is a cycle, there may be no true roots which lead to the whole graph. For this reason,
 * this implementation takes the first node it sees as the root in a cycle and may add up to one unnecessary root
 * per cycle. 
 * <p>
 * <i>"root" is thus not a well-defined term as it depends on the order in which the graph is traversed.</i>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class RootFindingVisitor implements NodeVisitor {

	/** All objects given to the finder. */
	private Set<Object> m_objects;
	
	/** Keys of all roots. */
	private Set<UniqueKey> m_roots;
	
	/** Keys of all inner/linked nodes. */
	private Set<UniqueKey> m_nodes;
	
	/** The object wrapper. */
	private final ObjectWrapper m_wrapper;
	
	/** 
	 * The last seen root. If we find a different root on a walk,
	 * we can make it a node.
	 */
	private UniqueKey m_lastRoot;
	
	/**
	 * Initialize fields.
	 * @param wrapper The object wrapper.
	 */
	public RootFindingVisitor(ObjectWrapper wrapper) {
		m_objects = new HashSet<Object>();
		m_roots = new HashSet<UniqueKey>();
		m_nodes = new HashSet<UniqueKey>();
		m_wrapper = wrapper;
		m_lastRoot = null;
	}
	
	/**
	 * Get all objects. The result can be safely modified.
	 * @return The set of all objects.
	 */
	public Set<Object> getObjects() {
		Set<Object> set = new HashSet<Object>();
		set.addAll(m_objects);
		return set;
	}
	
	/**
	 * Get all roots. The result collection is safe to modify.
	 * @return The set of all roots.
	 */
	public Set<UniqueKey> getRoots() {
		Set<UniqueKey> set = new HashSet<UniqueKey>();
		set.addAll(m_roots);
		return set;
	}
	
	/**
	 * Get all nodes. The result collection is safe to modify.
	 * @return The set of all nodes.
	 */
	public Set<UniqueKey> getNodes() {
		Set<UniqueKey> set = new HashSet<UniqueKey>();
		set.addAll(m_nodes);
		return set;
	}
	
	/**
	 * Clear all objects.
	 */
	public void clear() {
		m_nodes.clear();
		m_objects.clear();
		m_roots.clear();
	}
		
	/*
	 * Visitor methods.
	 */
	
	/** {@inheritDoc} */
	public Object markError(Object node, Object cause) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	public NodeState preVisit(Object node) {
		if (m_objects.contains(node)) {
			// An object is added to m_objects on preVisit().
			// This node is handled.
			return NodeState.PROCESSED;
		} else {
			UniqueKey nodeKey = m_wrapper
				.wrap(UniqueKeyed.class, node).getUniqueKey();
			if (!m_nodes.contains(nodeKey)) {
				// We're previsiting an unseen node. Make it a root for now.
				m_lastRoot = nodeKey;
				m_roots.add(nodeKey);
			}
			
			// Mark all new children as nodes. 
			Object[] children = m_wrapper.wrap(Linked.class, node)
				.getAllLinked();
			for (Object child : children) {
				
				UniqueKey childKey = m_wrapper.wrap(
					UniqueKeyed.class, child).getUniqueKey();
				if (m_roots.contains(childKey) && !childKey.equals(m_lastRoot)) {
					// We found a "root" different to ours. 
					// Make it a node.
					m_roots.remove(childKey);
					m_nodes.add(childKey);
				} else if (!m_nodes.contains(childKey)) {
					// An unseen child. Make it a node.
					m_nodes.add(childKey);
				}
			}
			m_objects.add(node);
			
			return NodeState.UNSEEN;
		}
	}

	/** {@inheritDoc} */
	public void visit(Object node) { }
}
