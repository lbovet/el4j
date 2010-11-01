/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.aspects;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ch.elca.el4j.tests.aspects.interceptor.ServiceLoggerInterceptor;
import ch.elca.el4j.tests.aspects.util.InvocationMonitor;

/**
 * Aspects tests class with transaction interseptor.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class InvokationWithoutTrJ5ATest extends AbstractInvokationTests {
	/**
	 * Tests the double invokation problem.
	 */
	@Test
	public void testDoubleInvokation() {
		InvocationMonitor.clear();
		commonDoubleInvokationTest();
		
		// Check invocation order
		List<Class<?>> invocationList = InvocationMonitor.getInvocationList();
		assertEquals(1, invocationList.size());
		assertEquals(ServiceLoggerInterceptor.class, invocationList.get(0));
	}
}
