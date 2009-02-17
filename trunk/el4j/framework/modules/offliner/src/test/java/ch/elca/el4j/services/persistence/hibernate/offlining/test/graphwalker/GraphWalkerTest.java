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
package ch.elca.el4j.services.persistence.hibernate.offlining.test.graphwalker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.test.notifications.Notification;
import ch.elca.el4j.services.persistence.hibernate.offlining.test.notifications.NotificationProcessor;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;

import junit.framework.TestCase;

/**
 * Graph walker test.
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
@Component
public class GraphWalkerTest extends TestCase {

	/** The walker under test. */
	private GraphWalker m_walker;
	
	/** The notification processor. */
	private NotificationProcessor m_processor;
	
	/**
	 * Initialize.
	 */
	public void setUp() {
		ObjectWrapper wrapper = new ObjectWrapper();
		m_walker = new GraphWalker(new NotifyingVisitor(), wrapper);
		m_processor = new NotificationProcessor();
	}
	
	/**
	 * A succcessful walk.
	 */
	public void testWalk() {
		String[] names = {
			"/one", "/one/two", "/three", "/three/four", "/three/five"
		};
		LinkedObject root = createObjects(names);
		m_processor.expect(
			new Notification("visit", "LinkedObject /"),
			new Notification("visit", "LinkedObject /one"),
			new Notification("visit", "LinkedObject /one/two"),
			new Notification("visit", "LinkedObject /three"),
			new Notification("visit", "LinkedObject /three/four"),
			new Notification("visit", "LinkedObject /three/five")
		);
		m_walker.run(root);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * The visitor declares an element processed.
	 * This element and all children must be skipped, the rest visited.
	 */
	public void testWalkProcessed() {
		String[] names = {
			"/one", "/one/PROCESSED", "/one/PROCESSED/two"
		};
		LinkedObject root = createObjects(names);
		m_processor.expect(
			new Notification("visit", "LinkedObject /"),
			new Notification("visit", "LinkedObject /one")
		);
		m_walker.run(root);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * The visitor declares an object in error. 
	 * This element and all children must be skipped, 
	 * its ancestors marked as in error, the rest visited.
	 */
	public void testWalkVisitorError() {
		String[] names = {
			"/one", "/one/ERROR", "/one/ERROR/two", "/one/three"
		};
		LinkedObject root = createObjects(names);
		m_processor.expect(
			new Notification("markRed", "LinkedObject /"),
			new Notification("markRed", "LinkedObject /one"),
			new Notification("visit", "LinkedObject /one/three")
		);
		m_walker.run(root);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * An error appears on the walk. It appears as visited (because it is in
	 * visit that the error occurs), its ancestors must be marked,
	 * all others (+ its children) visited.
	 */
	public void testWalkError() {
		String[] names = {
			"/one", "/one/red", "/one/red/two", "/one/three"
		};
		LinkedObject root = createObjects(names);
		m_processor.expect(
			new Notification("markRed", "LinkedObject /"),
			new Notification("markRed", "LinkedObject /one"),
			new Notification("visit", "LinkedObject /one/red"),
			new Notification("visit", "LinkedObject /one/red/two"),
			new Notification("visit", "LinkedObject /one/three")
		);
		m_walker.run(root);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * Test a non-tree graph. This must not give an infinite loop.
	 */
	public void testCycle() {
		LinkedObject a = new LinkedObject("a");
		LinkedObject b = new LinkedObject("b");
		LinkedObject c = new LinkedObject("c");
		a.addAncestor(c);
		b.addAncestor(a);
		c.addAncestor(b);
		m_processor.expect(
			new Notification("visit", "LinkedObject a"),
			new Notification("visit", "LinkedObject b"),
			new Notification("visit", "LinkedObject c")
		);
		m_walker.run(a);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * Test a non-tree graph with an error.
	 * Nothing may pass in an invalid cycle; however the cause of the error
	 * must be visited to detect it.
	 */
	public void testCycleError() {
		LinkedObject a = new LinkedObject("a");
		LinkedObject b = new LinkedObject("b");
		LinkedObject c = new LinkedObject("c:red");
		a.addAncestor(c);
		b.addAncestor(a);
		c.addAncestor(b);
		m_processor.expect(
			new Notification("markRed", "LinkedObject a"),
			new Notification("markRed", "LinkedObject b"),
			new Notification("visit", "LinkedObject c:red")
		);
		m_walker.run(a);
		m_processor.validateAnyOrder();
	}
	
	/**
	 * Utility method to create a tree of objects.
	 * @param names The names.
	 * @return The root object for graph walking.
	 */
	private LinkedObject createObjects(String[] names) {
		
		Map<String, LinkedObject> map = new HashMap<String, LinkedObject>();
		
		LinkedObject root = new LinkedObject("/");
		map.put("/", root);
		
		for (String name : names) {
			String[] path = name.split("/");
			StringBuilder parent = new StringBuilder();
			for (int i = 0; i < path.length - 1; i++) {
				parent.append(path[i]);
				parent.append("/");
			}
			if (!parent.toString().equals("/")) {
				parent.deleteCharAt(parent.length() - 1);
			}
			LinkedObject obj = new LinkedObject(name);
			obj.addAncestor(map.get(parent.toString()));
			map.put(name, obj);
		}
		
		return root;
	}
	
	/** 
	 * Testing stub that raises notifications on mark or visit and can
	 * return all cases for us.
	 */
	class NotifyingVisitor implements NodeVisitor {

		/** {@inheritDoc} */
		public Object markError(Object node, Object cause) {
			m_processor.call(new Notification("markRed", node.toString()));
			return "Object [" + node + "] marked as error due to [" + cause + "].";
		}

		/** {@inheritDoc} */
		public NodeState preVisit(Object node) {
			NodeState color = NodeState.UNSEEN;
			if (node instanceof LinkedObject) {
				LinkedObject lio = (LinkedObject) node;
				if (lio.getName().contains("ERROR")) {
					color = NodeState.ERROR;
				}
				if (lio.getName().contains("PROCESSED")) {
					color = NodeState.PROCESSED;
				}
			}
			return color;
		}

		/** {@inheritDoc} */
		public void visit(Object node) throws NodeException {
			m_processor.call(new Notification("visit", node.toString()));
			if (node instanceof LinkedObject) {
				LinkedObject lio = (LinkedObject) node;
				if (lio.getName().endsWith("red")) {
					throw new NodeException("Object [" + node + "] is in error.");
				}
			}
		}
	}
}

