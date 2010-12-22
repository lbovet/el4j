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

package ch.elca.el4j.tests.util.codingsupport;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.FindBugsSuppressWarnings;

// Checkstyle: EmptyBlock off

/**
 * This tests check the behavior of the {@link
 * ch.elca.el4j.util.codingsupport.Reject} class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public class RejectTest {

	/**
	 * Checks {@link Reject#ifNull(Object)}.
	 */
	@Test
	public void testIfNull() {
		try {
			Reject.ifNull(null);
			fail("Bad implementatino of Reject.isNull(null)");
		} catch (Exception e) { }
		try {
			Reject.ifNull(new Object());
		} catch (Exception e) {
			fail("Bad implementatino of Reject.isNull(new Object())");
		}
	}
	
	/**
	 *  Check with a subclass of RuntimeException
	 * Checks {@link Reject#ifNull(Object)}.
	 */
	@Test
	public void testIfNull2() {
		try {
			Reject.ifNull(null, IllegalArgumentException.class, "My String");
			fail("Bad implementation of Reject.isNull()");
		} catch (IllegalArgumentException e) { }
		try {
			Reject.ifEmpty("", IllegalArgumentException.class, "My String");
			fail("Bad implementation of Reject.isEmpty()");
		} catch (IllegalArgumentException e) { }
		try {
			Reject.ifCondition(true, IllegalArgumentException.class,
				"My String");
			fail("Bad implementation of Reject.ifCondition()");
		} catch (IllegalArgumentException e) { }
		
		try {
			Reject.ifNull(new Object(), IllegalArgumentException.class, "");
		} catch (IllegalArgumentException e) {
			fail("Bad implementation of Reject.isNull(new Object())");
		}
	}
	
	/**
	 * Checks {@link Reject#ifEmpty(Collection)}.
	 */
	@Test
	@SuppressWarnings("unchecked")
	@FindBugsSuppressWarnings(value = "DE_MIGHT_IGNORE",
						justification = "Exception handling not important as test is supposed to fail.")
	public void testIfEmptyCollection() {
		try {
			Reject.ifEmpty((List) null);
			fail("Bad implementatino of Reject.isEmpty((List) null)");
		} catch (Exception e) { }
		ArrayList list = new ArrayList();
		try {
			Reject.ifEmpty(list);
			fail("Bad implementatino of Reject.isEmpty(new ArrayList())");
		} catch (Exception e) { }
		try {
			list.add(new Object());
			Reject.ifEmpty(list);
		} catch (Exception e) {
			fail("Bad implementatino of Reject.isEmpty('nonempty list')");
		}
	}
	
	/**
	 * Checks {@link Reject#ifEmpty(String)}.
	 */
	@Test
	public void testIfEmptyString() {
		try {
			Reject.ifEmpty((String) null);
			fail("Bad implementatino of Reject.isEmpty((String) null)");
		} catch (Exception e) { }
		try {
			Reject.ifEmpty(" ");
			fail("Bad implementatino of Reject.isEmpty(\" \")");
		} catch (Exception e) { }
		try {
			Reject.ifEmpty(" test");
		} catch (Exception e) {
			fail("Bad implementatino of Reject.isEmpty(\" test\")");
		}
	}
}
//Checkstyle: EmptyBlock on
