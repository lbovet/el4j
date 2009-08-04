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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import org.junit.Test;

import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixerMergePolicy;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.services.persistence.hibernate.HibernatePrimaryKeyObjectIdentityFixer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for identity fixer.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class IdentityFixerTest {
	@Test
	public void testAdvancedUsage() {
		Example anchor = new Example("Anchor");
		Example anchorChild1 = new Example("Child 1");
		
		anchor.parent = anchor;
		
		ArrayList<Example> childrenList = new ArrayList<Example>();
		anchor.children = childrenList;
		anchor.children.add(anchorChild1);
		anchor.ints = new int[] {3, 9};
		
		anchorChild1.parent = anchor;
		
		Example updated = new Example("Anchor Updated");
		Example updatedChild1 = new Example("Child 1 Updated");
		updated.parent = updated;
		updated.children = new ArrayList<Example>();
		updated.children.add(updatedChild1);
		updated.ints = new int[] {3, 100};
		
		updatedChild1.parent = updated;
		
		updated.setKey(1);
		updatedChild1.setKey(2);
		
		IdentityHashMap<Object, Object> collectionEntryMapping = new IdentityHashMap<Object, Object>();
		collectionEntryMapping.put(updated, anchor);
		collectionEntryMapping.put(updatedChild1, anchorChild1);
		
		List<Object> objectsToUpdate = new ArrayList<Object>();
		objectsToUpdate.add(anchor);
		// do NOT update anchorChild1
		AbstractIdentityFixer idFixer = new HibernatePrimaryKeyObjectIdentityFixer();
		Example merged = idFixer.merge(anchor, updated, 
			IdentityFixerMergePolicy.reloadObjectsPolicy(objectsToUpdate, collectionEntryMapping));
		
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
		merged = idFixer.merge(anchor, updated, IdentityFixerMergePolicy.reloadAllPolicy(collectionEntryMapping));
		
		assertEquals(anchorChild1.name, updatedChild1.name);
		assertEquals(anchorChild1.parent, anchor);
		
		// add item to collection
		Example updatedChild2 = new Example("Child 2 Updated");
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
	public void testArraysAndCollections() {
		performArraysAndCollectionsTest(false);
		performArraysAndCollectionsTest(true);
		
		performCollectionMergeTest();
	}

	public void performArraysAndCollectionsTest(boolean addAdditionalChild) {
		// Step 1: create anchor
		Example anchor = createExample("Anchor", false);
		
		// Step 2: create updated
		Example updated = createExample("Updated", true);
		
		Example updatedChild4 = new Example("Child 4 Updated");
		if (addAdditionalChild) {
			updated.children.add(updatedChild4);
			updatedChild4.setKey(5);
		}
		
		// help idFixer to correct collections
		IdentityHashMap<Object, Object> collectionEntryMapping = new IdentityHashMap<Object, Object>();
		collectionEntryMapping.put(updated, anchor);
		collectionEntryMapping.put(updated.children.get(0), anchor.children.get(0));
		collectionEntryMapping.put(updated.children.get(1), anchor.children.get(1));
		collectionEntryMapping.put(updated.children.get(2), anchor.children.get(2));
		
		
		// Step 3: fix identity
		AbstractIdentityFixer idFixer = new HibernatePrimaryKeyObjectIdentityFixer();
		Example merged = idFixer.merge(anchor, updated, 
			IdentityFixerMergePolicy.reloadAllPolicy(collectionEntryMapping));
		
		
		// Step 4: test results
		assertEquals(merged, anchor);
		
		// do not modify references
		assertTrue(anchor.children.get(0) != updated.children.get(0));
		assertTrue(anchor.children.get(1) != updated.children.get(1));
		assertTrue(anchor.children.get(2) != updated.children.get(2));
		
		assertEquals(anchor.children.get(0).name, updated.children.get(0).name);
		assertEquals(anchor.children.get(1).name, updated.children.get(1).name);
		assertEquals(anchor.children.get(2).name, updated.children.get(2).name);
		if (addAdditionalChild) {
			assertEquals(anchor.children.get(3).name, updatedChild4.name);
		}
		
		assertEquals(anchor.childrenArray[0].name, updated.children.get(0).name);
		assertEquals(anchor.childrenArray[1].name, updated.children.get(1).name);
		assertEquals(anchor.childrenArray[2].name, updated.children.get(2).name);
	}
	
	private void performCollectionMergeTest() {
		// test merging collections directly
		// Step 1: create anchor
		Example anchor = createExample("Anchor", false);
		anchor.children.get(0).childrenArray = new Example[] {anchor.children.get(1)};
		
		// Step 2: create updated
		Example updated = createExample("Updated", true);
		updated.children.get(0).childrenArray = new Example[] {updated.children.get(1)};
		
		// help idFixer to correct collections
		IdentityHashMap<Object, Object> collectionEntryMapping = new IdentityHashMap<Object, Object>();
		collectionEntryMapping.put(updated, anchor);
		collectionEntryMapping.put(updated.children.get(0), anchor.children.get(0));
		collectionEntryMapping.put(updated.children.get(1), anchor.children.get(1));
		collectionEntryMapping.put(updated.children.get(2), anchor.children.get(2));
		
		
		AbstractIdentityFixer idFixer = new HibernatePrimaryKeyObjectIdentityFixer();
		idFixer.merge(anchor.children, updated.children, 
			IdentityFixerMergePolicy.reloadAllPolicy(collectionEntryMapping));
	}
	
	@Test
	public void testCollectionReplacing() {
		Example anchor = new Example("Anchor");
		Example anchorChild1 = new Example("Child 1");
		
		anchor.parent = anchor;
		
		ArrayList<Example> childrenList = new ArrayList<Example>();
		anchor.children = childrenList;
		anchor.children.add(anchorChild1);
		anchor.ints = new int[] {3, 9};
		
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
	
	private Example createExample(String postfix, boolean setKeys) {
		Example root = new Example("Anchor " + postfix);
		Example child1 = new Example("Child 1 " + postfix);
		Example child2 = new Example("Child 2 " + postfix);
		Example child3 = new Example("Child 3 " + postfix);
		
		// build collection
		ArrayList<Example> childrenList = new ArrayList<Example>();
		childrenList.add(child1);
		childrenList.add(child2);
		childrenList.add(child3);
		root.children = childrenList;
		
		// build array
		root.ints = new int[] {1, 2, 3};
		root.childrenArray = new Example[] {child1, child2, child3};
		
		if (setKeys) {
			// set primary keys
			root.setKey(1);
			child1.setKey(2);
			child2.setKey(3);
			child3.setKey(4);
		}
		
		return root;
	}
	
	@Entity
	private class Example extends AbstractIntKeyIntOptimisticLockingDto {
		public Example() { }
		public Example(String name) {
			this.name = name;
		}
		// public field are enough for this test
		public String name;
		public int[] ints;
		public Example[] childrenArray;
		public List<Example> children;
		public Example parent;
		
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Example [name: " + name + ", ints: " + (ints != null ? ints.toString() : "null") + "]";
		}
	}
}
