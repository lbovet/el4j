/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.services.persistence.hibernate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import org.hibernate.collection.PersistentBag;
import org.junit.Test;

import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixerMergePolicy;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.services.persistence.hibernate.HibernatePrimaryKeyObjectIdentityFixer;

/**
 * Tests for identity fixer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class IdentityFixerTest {
	@Test
	public void testAdvancedUsage() {
		Example anchor = new Example();
		Example anchorChild1 = new Example();
		
		anchor.name = "Anchor";
		anchor.parent = anchor;
		
		ArrayList<Example> childrenList = new ArrayList<Example>();
		anchor.children = childrenList;
		anchor.children.add(anchorChild1);
		anchor.ints = new int[] {3, 9};
		
		anchorChild1.name = "Child 1";
		anchorChild1.parent = anchor;
		
		Example updated = new Example();
		Example updatedChild1 = new Example();
		updated.name = "Anchor Updated";
		updated.parent = updated;
		updated.children = new ArrayList<Example>();
		updated.children.add(updatedChild1);
		updated.ints = new int[] {3, 100};
		
		updatedChild1.name = "Child 1 Updated";
		updatedChild1.parent = updated;
		
		updated.setKey(1);
		updatedChild1.setKey(2);
		
		IdentityHashMap<Object, Object> hintMap = new IdentityHashMap<Object, Object>();
		hintMap.put(updated, anchor);
		hintMap.put(updatedChild1, anchorChild1);
		
		List<Object> objectsToUpdate = new ArrayList<Object>();
		objectsToUpdate.add(anchor);
		// do NOT update anchorChild1
		AbstractIdentityFixer idFixer = new HibernatePrimaryKeyObjectIdentityFixer();
		Example merged = idFixer.merge(anchor, updated, 
			IdentityFixerMergePolicy.reloadObjectsPolicy(objectsToUpdate, hintMap));
		
		assertEquals(merged, anchor);
		
		// anchor should have changed ...
		assertEquals(anchor.name, updated.name);
		assertEquals(anchor.parent, anchor);
		assertEquals(anchor.children, childrenList);
		assertEquals(anchor.children.get(0), anchorChild1);
		assertEquals(anchor.ints[0], 3);
		assertEquals(anchor.ints[1], 100);
		
		// ... but child should only have id-fixed reference to anchor 
		assertFalse(anchorChild1.name.equals(updatedChild1.name));
		assertEquals(anchorChild1.parent, anchor);
		
		// re-fix the updated, because the child list got modified
//		updated.children = new ArrayList<Example>();
//		updated.children.add(updatedChild1);
		
		// this time update all entities
		merged = idFixer.merge(anchor, updated, IdentityFixerMergePolicy.reloadAllPolicy(hintMap));
		
		assertEquals(anchorChild1.name, updatedChild1.name);
		assertEquals(anchorChild1.parent, anchor);
		
		// add item to collection
		Example updatedChild2 = new Example();
		updatedChild2.name = "Child 2 Updated";
		updatedChild2.parent = updated;
		updated.children.add(updatedChild2);
		
		// inserted item should appear in anchor
		merged = idFixer.merge(anchor, updated);
		
		assertEquals(merged, anchor);
		
		assertEquals(anchor.children.size(), 2);
		assertEquals(anchor.children.get(1), updatedChild2);
		// inserted item should be id-fixed
		assertEquals(anchor.children.get(1).parent, anchor);
	}
	
	@Test
	public void testCollectionReplacing() {
		Example anchor = new Example();
		Example anchorChild1 = new Example();
		
		anchor.name = "Anchor";
		anchor.parent = anchor;
		
		ArrayList<Example> childrenList = new ArrayList<Example>();
		anchor.children = childrenList;
		anchor.children.add(anchorChild1);
		anchor.ints = new int[] {3, 9};
		
		anchorChild1.name = "Child 1";
		anchorChild1.parent = anchor;
		
		
		AbstractIdentityFixer idFixer = new HibernatePrimaryKeyObjectIdentityFixer();
		
		// insert the anchor into the idFixer representatives
		idFixer.merge(null, anchor);
		
		assertTrue("List of children not untouched!", anchor.children == childrenList);
		
		// simulate giving the anchor to hibernate
		idFixer.reverseMerge(anchor, true);
		
		// play hibernate and create an updated list
		LinkedList<Example> updatedList = new LinkedList<Example>();
		updatedList.add(anchorChild1);
		org.hibernate.collection.PersistentBag updatedHibernateList 
			= new org.hibernate.collection.PersistentBag(null, updatedList);
		
		anchor.children = updatedHibernateList;
		// "generate key"
		anchor.name = "Anchor(withKey)";
		
		// merge with the original object
		idFixer.merge(null, anchor);
		
		assertTrue("List of children not untouched!", anchor.children == childrenList);
		assertEquals("Name not updated", "Anchor(withKey)", anchor.name);
	}
	
	@Entity
	private class Example extends AbstractIntKeyIntOptimisticLockingDto {
		// public field are enough for this test
		public String name;
		public int[] ints;
		public List<Example> children;
		public Example parent;
		
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Example [name: " + name + ", ints: " + (ints != null ? ints.toString() : "null") + "]";
		}
	}
}
