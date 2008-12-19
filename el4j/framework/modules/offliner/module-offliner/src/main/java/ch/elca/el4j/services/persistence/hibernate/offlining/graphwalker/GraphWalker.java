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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.Linked;



/**
 * <h1>Object graph walker</h1>
 * The object graph walker algorithm traverses an object graph starting from a root object and 
 * following all <i>links</i>. The precise meaning of link is defined externally by the {@link Linked}
 * passed in the wrapper object to the graph walker. Object graphs are directed; no other assumptions are made
 * (loops, multi-edges and cycles are all allowed).
 * <p>
 * The graph walker requires two parameters: An {@link ObjectWrapper} objects that provides {@link Linked} and
 * {@link UniqueKeyed} and a {@link NodeVisitor} that it can run on each node.
 * <p>
 * The following node stated are used: All nodes that have not yet been seen are UNSEEN. When a node is first 
 * discovered, it becomes PENDING. It is then passed to preVisit which can override its state; if an override happens
 * it is treated as if it had been that state before (i.e. if it is overridden as PROCESSED, no further recursion will
 * be done). Each node can be reached as UNSEEN at most once (exactly once unless overridden). 
 * <p>
 * A PENDING node becomes PROCESSED once all of its children have been successfully processed. 
 * Immediately before becoming PROCESSED, a node is passed to <code>visit</code>. PROCESSED nodes are not touched again.
 * <p>
 * A node can also become ERROR, indicating a problem. All nodes with links to ERROR nodes are guaranteed to end up as
 * ERROR as well. Nodes become ERROR in three ways:
 * <ol><li>visit throws a NodeException.</li>
 * <li>A linked/child node is ERROR.</li>
 * <li>preVisit overrides the node as ERROR.</li></ol>
 * ERROR nodes are not touched again by the graph walker.
 * <p>
 * <i>The node state determination is shared between the walker and the visitor. The walker resets its state memory 
 * every time it is run on a new root; any stored state information between runs must be handled by the visitor. This
 * is why the override mechanism exists. Only nodes that are UNSEEN from the walker's point of view are passed to
 * preVisit and are guaranteed to lose UNSEEN state afterwards (PENDING if the visitor does nothing,
 * ERROR or PROCESSED if it overrides).</i>
 * <p>
 * The contract between the walker and its visitor is as follows:
 * <ol><li>All nodes that are reachable from the current root will be passed to the visitor's <code>preVisit</code>
 * exactly once, namely at the time they are first discovered. The visitor can return UNSEEN to indicate normal 
 * processing or override with ERROR or PROCESSED. </li>
 * <li>Nodes that the visitor does not override are passed exactly once to the visitor again after all their children 
 * have been processed; they are passed to</li>
 * <ol><li><code>visit</code> if all children are PROCESSED.</li>
 * <li><code>markError</code> if any child is in error.</li></ol>
 * </ol>
 * 
 * The walker's algorithm on a node is:
 * <ol><li>If the node is not UNSEEN, ignore it.</li>
 * <li>If the node is UNSEEN, pass it to preVisit. If it overrides, mark it with the new state and ignore it.</li>
 * <li>If preVisit keeps it UNSEEN, set it to PENDING and recurse on all children.</li>
 * <li>If any children are ERROR, mark it as ERROR and call markError on the visitor.</li>
 * <li>If all children are PROCESSED, call visit. If it succeeds, make the node PROCESSED, otherwise ERROR.</li>
 * </ol>
 *
 * Node identity is determined by {@link UniqueKey} objects. Two nodes are equal if and only if their unique keys
 * are equal. If a different instance im memory is seen that is equal to a previously seen one, it is treated as the
 * same node for state purposes (<i>states are in fact stored as a map of UniqueKey to NodeState</i>).
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
public class GraphWalker {

	/** The logger. */
	private static final Logger s_log = Logger.getLogger(GraphWalker.class);
	
	/**
	 * The states a node can be in.
	 */
	public enum NodeState {
		/** Unseen until now. */
		UNSEEN,
		
		/** Seen but not processed. */
		PENDING,
		
		/** Processed succesfully. */
		PROCESSED,
		
		/** 
		 * Error during processing. 
		 * Cascades up to all nodes which refer to an error node.
		 */
		ERROR
	}

	/*
	 * Nodes can become error nodes in three ways:
	 * 1. preVisit returns error. Neither the node nor its children are processed.
	 * 2. visit throws a NodeException. The children are all processed but 
	 * the node itself is marked as in error and the message stored.
	 * 3. A child is in error. All children are processed but markError instead of 
	 * visit is called and the message added.
	 */

	/**
	 * The key to state mapping.
	 */
	private Map<UniqueKey, NodeState> m_stateMap;
	
	/**
	 * The object wrapper.
	 */
	private final ObjectWrapper m_wrapper;
	
	/**
	 * The node visitor to run on all nodes.
	 */
	private final NodeVisitor m_visitor;
	
	/**
	 * The error node messages gathered during graph processing.
	 */
	private List<Object> m_errorMessages;
	
	/**
	 * The current recursion depth. Used only for logging at the moment.
	 */
	private int m_depth;
	
	/**
	 * Create the walker.
	 * @param visitor The node visitor.
	 * @param wrapper The object wrapper.
	 */
	public GraphWalker(NodeVisitor visitor, ObjectWrapper wrapper) {
		m_wrapper = wrapper;
		m_visitor = visitor;
		m_errorMessages = new LinkedList<Object>();
	}
	
	/**
	 * Logging utility to return a string in parentheses.
	 * @param obj The object.
	 * @return "[obj.toString()]"
	 */
	private static String print(Object obj) {
		return "[" + obj.toString() + "]";
	}
	
	/**
	 * @return Indentation for the current recursion depth.
	 */
	private String indent() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < m_depth; i++) {
			builder.append(" ");
		}
		return builder.toString();
	}
		
	
	/**
	 * Run the graph walker on a root node.
	 * @param root The root node.
	 * @return The errors encountered.
	 */
	public Object[] run(Object root) {
		s_log.info("Running recursively on " + print(root));
		m_errorMessages.clear();
		m_stateMap = new HashMap<UniqueKey, NodeState>();
		m_depth = 0;
		runOnNode(root);
		return m_errorMessages.toArray();
	}	
		
	/**
	 * Get the state for a node. If it has none yet it becomes UNSEEN.
	 * @param node The node.
	 * @return The node's state.
	 */
	private NodeState getState(Object node) {
		UniqueKey key = m_wrapper.wrap(UniqueKeyed.class, node).getUniqueKey(); 
		NodeState state = m_stateMap.get(key);
		if (state == null) {
			state = NodeState.UNSEEN; 
			m_stateMap.put(key, NodeState.UNSEEN);
		}
		return state;
	}
	
	/**
	 * Set the state of a node.
	 * @param node The node.
	 * @param state The node's state.
	 */
	private void setState(Object node, NodeState state) {
		UniqueKey key = m_wrapper.wrap(UniqueKeyed.class, node).getUniqueKey();
		m_stateMap.put(key, state);
	}
	
	/**
	 * Run on a node.
	 * @param node The node.
	 */
	private void runOnNode(Object node) {
		s_log.debug(indent() + "Node " + print(node));
		NodeState state = getState(node);
		switch (state) {
			case UNSEEN:
				// New node.
				runOnNewNode(node);
				break;
			case PROCESSED:
			case ERROR:
			case PENDING:
				return;
			default:
				throw new GraphWalkerInternalRTException("Unhandled enum constant.");
		}
		
	}
	
	/**
	 * Run on a UNSEEN node: First call preVisit. If the node is returned as PENDING, visit
	 * children. If not, just return.
	 * @param node The node.
	 */
	private void runOnNewNode(Object node) {
		s_log.debug(indent() + "Node is UNSEEN.");
		setState(node, NodeState.PENDING);
		NodeState state = m_visitor.preVisit(node);
		if (state != NodeState.UNSEEN) {
			setState(node, state);
		}
		s_log.debug(indent() + "Visitor made it " + state.name());
		switch (state) {
			case UNSEEN:
				// A "normal" new node.
				Object errorChild = null;
				for (Object child : getChildren(node)) {
					s_log.debug(indent() + "Recursion on child " + print(child));
					m_depth++;
					runOnNode(child);
					m_depth--;
					if (getState(child) == NodeState.ERROR) {
						// An error child during processing
						// = new dependent conflict.
						errorChild = child;
					}
				}
				
				if (errorChild == null) {
					// Children ok, handle the node itself.
					try {
						m_visitor.visit(node);
						s_log.debug(indent() + "All children ok for " + print(node));
						setState(node, NodeState.PROCESSED);
					} catch (NodeException e) {
						s_log.debug(indent() + "Error child for " + print(node));
						m_errorMessages.add(e.getErrorCause());
						setState(node, NodeState.ERROR);
					}
				} else {
					Object message = m_visitor.markError(node, errorChild);
					m_errorMessages.add(message);
					setState(node, NodeState.ERROR);
				}
				
				break;
			case PROCESSED:
			case ERROR:
				// This node was processed earlier.
				break;
			default:
				throw new IllegalStateException("preVisit returned illegal state.");
		}
		if (getState(node) == NodeState.PENDING) {
			throw new GraphWalkerInternalRTException("Node PENDING after processing end.");
		}
	}
	
	/**
	 * Return all children of a parent object.
	 * @param parent The parent.
	 * @return The children.
	 */
	private List<Object> getChildren(Object parent) {
		List<Object> children = new LinkedList<Object>();
		Linked link = m_wrapper.wrap(Linked.class, parent);
		
		for (String child : link.getLinkNames()) {
			children.add(link.getlinkByName(child));
		}
		for (String child : link.getCollectionLinkNames()) {
			children.addAll(link.getCollectionLinkByName(child));
		}
		
		// Clear the nulls out again.
		for (Iterator<Object> iterator = children.iterator(); iterator.hasNext();) {
			Object current = iterator.next();
			if (current == null) {
				iterator.remove();
			}
		}
		
		return children;
	}
}
