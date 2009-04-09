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
package ch.elca.el4j.tests.gui.swing;

import java.awt.Toolkit;

import org.junit.Test;

import static org.junit.Assert.*;

import ch.elca.el4j.services.gui.swing.ActionsContext;
import ch.elca.el4j.tests.gui.swing.actions.ChildActions;
import ch.elca.el4j.tests.gui.swing.actions.GrandparentActions;
import ch.elca.el4j.tests.gui.swing.actions.ParentActions;

import sun.awt.HeadlessToolkit;

/**
 * Test {@link ActionsContext}s.
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
public class ActionsContextTest {

	/**
	 * Test the subclasses support of {@link ActionsContext}.
	 */
	@Test
	public void testSubClasses() {
		if (skipTests()) {
			// see comment in skipTests()
			return;
		}
		ValueHolder<String> stateHolder = new ValueHolder<String>();
		stateHolder.setValue("nothing");
		
		ChildActions child = new ChildActions("", stateHolder);
		ActionsContext actionsContext = ActionsContext.create(child);
		
		actionsContext.getAction("doA").actionPerformed(null);
		assertEquals("Grandparent.doA", stateHolder.getValue());
		
		actionsContext.getAction("doB").actionPerformed(null);
		assertEquals("Parent.doB", stateHolder.getValue());
		
		actionsContext.getAction("doC").actionPerformed(null);
		assertEquals("Parent.doC", stateHolder.getValue());
		
		actionsContext.getAction("doD").actionPerformed(null);
		assertEquals("Child.doD", stateHolder.getValue());
		
		GrandparentActions grandparent = new GrandparentActions("", stateHolder);
		actionsContext.getAction(grandparent, "doB").actionPerformed(null);
		assertEquals("Grandparent.doB", stateHolder.getValue());
			
		actionsContext.getAction(child, "doB").actionPerformed(null);
		assertEquals("Parent.doB", stateHolder.getValue());
	}
	
	/**
	 * Test the chain support of {@link ActionsContext}.
	 */
	@Test
	public void testChain() {
		if (skipTests()) {
			// see comment in skipTests()
			return;
		}
		ValueHolder<String> stateHolder = new ValueHolder<String>();
		stateHolder.setValue("nothing");
		
		GrandparentActions grandparent = new GrandparentActions("1_", stateHolder);
		ParentActions parent = new ParentActions("2_", stateHolder);
		ChildActions child = new ChildActions("3_", stateHolder);
		ActionsContext actionsContext = ActionsContext.create(grandparent, parent, child);
		
		actionsContext.getAction("doA").actionPerformed(null);
		assertEquals("1_Grandparent.doA", stateHolder.getValue());
		
		actionsContext.getAction("doB").actionPerformed(null);
		assertEquals("1_Grandparent.doB", stateHolder.getValue());
		
		actionsContext.getAction("doC").actionPerformed(null);
		assertEquals("2_Parent.doC", stateHolder.getValue());
		
		actionsContext.getAction("doD").actionPerformed(null);
		assertEquals("3_Child.doD", stateHolder.getValue());
		
		actionsContext.getAction(parent, "doB").actionPerformed(null);
		assertEquals("2_Parent.doB", stateHolder.getValue());
	}
	
	/**
	 * Test the parent context support of {@link ActionsContext}.
	 */
	@Test
	public void testParentContext() {
		if (skipTests()) {
			// see comment in skipTests()
			return;
		}
		ValueHolder<String> stateHolder = new ValueHolder<String>();
		stateHolder.setValue("nothing");
		
		GrandparentActions grandparent = new GrandparentActions("1_", stateHolder);
		ParentActions parent = new ParentActions("2_", stateHolder);
		ChildActions child = new ChildActions("3_", stateHolder);
		ActionsContext parentContext = ActionsContext.create(parent, child);
		ActionsContext actionsContext = ActionsContext.extend(parentContext, grandparent);
		
		actionsContext.getAction("doA").actionPerformed(null);
		assertEquals("1_Grandparent.doA", stateHolder.getValue());
		
		actionsContext.getAction("doB").actionPerformed(null);
		assertEquals("1_Grandparent.doB", stateHolder.getValue());
		
		actionsContext.getAction("doC").actionPerformed(null);
		assertEquals("2_Parent.doC", stateHolder.getValue());
		
		actionsContext.getAction("doD").actionPerformed(null);
		assertEquals("3_Child.doD", stateHolder.getValue());
		
		actionsContext.getAction(parent, "doB").actionPerformed(null);
		assertEquals("2_Parent.doB", stateHolder.getValue());
	}
	
	/**
	 * @return    <code>true</code> if tests should be skipped (see comment)
	 */
	private boolean skipTests() {
		// getAction(object, action) makes ResourceMap execute getMenuShortcutKeyMask() on built-in
		// "quit"-action, which does not work in headless mode
		return Toolkit.getDefaultToolkit() instanceof HeadlessToolkit;
	}
}
