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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.graphwalker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors.RootFindingVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;

import junit.framework.TestCase;

/**
 * Test of root finder.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
@Component
public class RootFinderTest extends TestCase {

	/** The test object. */
	private RootFindingVisitor m_visitor;
	
	/** The graph walker. */
	private GraphWalker m_walker;
	
	/** The object wrapper. */
	private ObjectWrapper m_wrapper;
	
	/**
	 * Set up the root finder. 
	 */
	public void setUp() {
		m_wrapper = new ObjectWrapper();
		// We don't need to register any wrappers because the LinkedObject class implements them itself.
		
		m_visitor = new RootFindingVisitor(m_wrapper);
		m_walker = new GraphWalker(m_visitor, m_wrapper);
	}
	
	/**
	 * Test on fixed graph.
	 */
	public void testFixedGraph() {
		
		/*
		 * A, B --> C, C-->D-->E-->F-->C, E --> G, H
		 */
		
		LinkedObject a = new LinkedObject("A");
		LinkedObject b = new LinkedObject("B");
		LinkedObject c = new LinkedObject("C");
		LinkedObject d = new LinkedObject("D");
		LinkedObject e = new LinkedObject("E");
		LinkedObject f = new LinkedObject("F");
		LinkedObject g = new LinkedObject("G");
		LinkedObject h = new LinkedObject("H");
		
		c.addAncestor(a);
		c.addAncestor(b);
		d.addAncestor(c);
		e.addAncestor(d);
		f.addAncestor(e);
		c.addAncestor(f);
		g.addAncestor(e);
		h.addAncestor(e);
		
		for (LinkedObject obj : new LinkedObject[]{h, e, a, b, f}) {
			m_walker.run(obj);
		}
		
		assertEquals(8, m_visitor.getObjects().size());
		assertEquals(3, m_visitor.getRoots().size());
		Set<String> rootNames = new HashSet<String>();
		rootNames.add("A");
		rootNames.add("B");
		// E is not a root, but marked as one in the algorithm
		// ("cyclic root").
		rootNames.add("E");
		Set<String> foundRootNames = new HashSet<String>();
		
		for (UniqueKey root : m_visitor.getRoots()) {
			foundRootNames.add((String) root.getKey());
		}
		assertEquals(rootNames, foundRootNames);
	}
	
	/**
	 * Test on a randomized graph.
	 */
	public void testRandomGraph() {
		for (int i = 0; i < 10; i++) {
			m_visitor.clear();
			doTestRandomGraph();
		}
	}
	
	/**
	 * Test random graph code.
	 */
	private void doTestRandomGraph() {
		List<LinkedObject> objects = new ArrayList<LinkedObject>(100);
		Random r = new Random();
		
		for (int i = 0; i < 100; i++) {
			LinkedObject current = new LinkedObject(Integer.toString(i));
			if (objects.isEmpty()) {
				objects.add(current);
			} else {
				// 50% Chance of an ancestor link.
				if (r.nextBoolean()) {
					LinkedObject other = objects.get(r.nextInt(objects.size()));
					current.addAncestor(other);
				}
				// 50% Chance of a successor link.
				if (r.nextBoolean()) {
					LinkedObject other = objects.get(r.nextInt(objects.size()));
					other.addAncestor(current);
				}
				objects.add(current);
			}
		}

		for (Object object : objects) {
			m_walker.run(object);
		}
		
		Set<UniqueKey> roots = m_visitor.getRoots();
				
		List<LinkedObject> allObjects = new ArrayList<LinkedObject>();
		for (Object obj : m_visitor.getObjects()) {
			allObjects.add((LinkedObject) obj);
		}
		
		m_visitor.clear();
		assertEquals(0, m_visitor.getObjects().size());

		// Re-add only those from the roots list.
		for (LinkedObject obj : allObjects) {
			if (roots.contains(m_wrapper.wrap(UniqueKeyed.class, obj)
				.getUniqueKey())) {
				m_walker.run(obj);
			}
		}

		assertEquals(100, m_visitor.getObjects().size());
	}
}
