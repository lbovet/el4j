/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.remoting;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ch.elca.el4j.tests.core.AbstractTest;
import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorOperation;

/**
 * This class is a test for the calculator.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Waraich Rashid (RWA)
 */

public abstract class AbstractCalculatorTest extends AbstractTest {
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(
		AbstractCalculatorTest.class);

	/**
	 * Instance of the calculator proxy.
	 */
	private Calculator m_calc;
	
	/**
	 * @return    the calculator object
	 */
	public Calculator getCalc() {
		if (m_calc == null) {
			m_calc = (Calculator) getApplicationContext().getBean("calculator");
		}
		return m_calc;
	}
	
	/**
	 * This test tests the area calculation method.
	 */
	@Test
	public void testAreaCalculation() {
		final double a = 2.3;
		final double b = 5.7;
		final double delta = 0.00000001;
		double result = getCalc().getArea(a, b);
		assertEquals("The area is not correctly calculated.", result, a * b, delta);
	}
	
	
	/**
	 * This test tests the exception handling.
	 * @throws Exception
	 */
	@Test
	public void testExceptionBehaviour() throws Throwable {
		try {
			getCalc().throwMeAnException();
			fail("No exception was thrown.");
		} catch (CalculatorException e) {
			s_logger.debug("Expected exception caught.", e);
		}
	}
	
	/**
	 * Tests if the protocol is able to handle enumerations.
	 */
	@Test
	public void testAbilityToHandleEnumerations() {
		// Checkstyle: MagicNumber off
		double result = getCalc().calculate(1.2, 2.5, CalculatorOperation.ADDITION);
		assertEquals(3.7, result, 0.1);
		result = getCalc().calculate(1.2, 2.5, CalculatorOperation.SUBTRACTION);
		assertEquals(-1.3, result, 0.1);
		//Checkstyle: MagicNumber on
	}

}
