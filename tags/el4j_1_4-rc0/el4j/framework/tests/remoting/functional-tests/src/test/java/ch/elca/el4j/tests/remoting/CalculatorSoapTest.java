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

package ch.elca.el4j.tests.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.remoting.protocol.soap.SoapHelper;
import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.service.SpecialCalculatorException;
import ch.elca.el4j.tests.remoting.service.soap.ExceptionThrower;
import ch.elca.el4j.tests.remoting.service.soap.RemoteExceptionWithData;
import ch.elca.el4j.tests.remoting.service.soap.impl.ExceptionThrowerImpl;

/**
 * This class is a test for the calculator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorSoapTest {
    /**
     * Is the delta to doubles can have to be equal.
     */
    private static final double DOUBLE_TOLERANCE = 0.000000001;
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractCalculatorTest.class);
    
    /**
     * Instance of the calculator proxy.
     */
    private Calculator m_calc;
    
    /**
     * Instance of the soap exception thrower.
     */
    private ExceptionThrower m_soapExceptionThrower;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        ApplicationContext appContext 
            = new ModuleApplicationContext(
                new String[] {"classpath*:mandatory/*.xml",
                    "scenarios/client/remotingtests-soap-client-config.xml"}, 
                    false);
        m_calc 
            = (Calculator) appContext.getBean("calculator");
        m_soapExceptionThrower 
            = (ExceptionThrower) appContext.getBean("soapExceptionThrower");
    }

    /**
     * This test tests the area calulation method.
     */
    @Test
    public void testAreaCalculation() {
        final double a = 2.3;
        final double b = 5.7;
        final double delta = 0.00000001;
        double result = m_calc.getArea(a, b);
        assertEquals("The area is not correctly calculated.", result, 
            a * b, delta);
    }
    
    /**
     * This test tests the exception handling.
     */
    @Test
    public void testExceptionBehaviour() {
        try {
            m_calc.throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException e) {
            // It's okay.
            String stackTrace = SoapHelper.getLastServerSideStackTrace();
            s_logger.debug("Expected Exception caught:");
            s_logger.debug(stackTrace);
        }
    }
    
    /**
     * This test tests a special exception behaviour.
     */
    @Test
    public void testSpecialExceptionBehaviour() {
        String action = "Hans Müller likes to pay with ¤.";
        try {
            m_calc.throwMeASpecialException(action);
            fail("No exception was thrown.");
        } catch (SpecialCalculatorException e) {
            // It's okay.
            
            SpecialCalculatorException sce 
                = new SpecialCalculatorException(action);
            assertEquals("The both exception messages are not equals.", 
                sce.getMessage(), e.getMessage());
            
            String stackTrace = SoapHelper.getLastServerSideStackTrace();
            s_logger.debug("Expected Exception caught:");
            s_logger.debug(stackTrace);
        }
    }
    
    /**
     * This test tests the counting of uppercase letters.
     */
    @Test
    public void testNumberOfUppercaseCharacters() {
        String message = "Hans Müller likes to pay with ¤.";
        int numberOfUppercaseLetters = 2;
        int result = m_calc.countNumberOfUppercaseLetters(message);
        assertEquals("The number of uppercase letter was not " 
            + "counted correctly.", result, numberOfUppercaseLetters);
    }
    
    /**
     * This test is used to test if a value object will be serialized and
     * deserialized correctly.
     */
    @Test
    public void testEchoOfValueObject() {
        final int myInt = 449312154;
        final long myLong = 3121846575454654L;
        final double myDouble = 6994.641368469;
        final String myString 
            = "I can not find any ä, ö, ü, é, è or à character on my keyboard.";
        final byte[] myByteArray = myString.getBytes();
        
        CalculatorValueObject o = new CalculatorValueObject();
        o.setMyInt(myInt);
        o.setMyLong(myLong);
        o.setMyDouble(myDouble);
        o.setMyString(myString);
        o.setMyByteArray(myByteArray);
        
        final double delta = 0.00000001;
        CalculatorValueObject echo = m_calc.echoValueObject(o);
        assertEquals("Int values are not equals.", 
            o.getMyInt(), echo.getMyInt());
        assertEquals("Long values are not equals.", 
            o.getMyLong(), echo.getMyLong());
        assertEquals("Double values are not equals.", 
            o.getMyDouble(), echo.getMyDouble(), delta);
        assertEquals("Strings are not equals.",
            o.getMyString(), echo.getMyString());
        assertTrue("Byte arrays are not equals.",
            Arrays.equals(o.getMyByteArray(), echo.getMyByteArray()));
    }
    
    /**
     * This test is used to check if a soap conform exception, which is not 
     * translated by a EL4J component, is received correctly on client side.
     */
    @Test
    public void testExceptionThrower() {
        try {
            m_soapExceptionThrower.throwExceptionWithData();
            fail("Expected exception was not thrown.");
        } catch (RemoteExceptionWithData e) {
            assertEquals("Exception index contains not the expected value.", 
                ExceptionThrowerImpl.EXCEPTION_INDEX, e.getIndex());
            assertEquals("Exception message contains not the expected value.",
                ExceptionThrowerImpl.EXCEPTION_MESSAGE, e.getMessage());
            assertTrue("Exception data contains not the expected value.",
                Arrays.equals(
                    ExceptionThrowerImpl.EXCEPTION_DATA, e.getData()));
            assertEquals("Exception calendar contains not the expected value.",
                ExceptionThrowerImpl.EXCEPTION_CALENDAR.getTime(), 
                e.getCalendar().getTime());
            
            CalculatorValueObject[] cArray = e.getCalculatorValueObjects();
            assertNotNull("There is no calculator value object array.", cArray);
            assertEquals("Wrong number of calculator value objects.", 
                2, cArray.length);
            CalculatorValueObject c1 = cArray[0];
            CalculatorValueObject c2 = cArray[1];
            assertEquals("First calculator value object contains not expected "
                + "double value.", ExceptionThrowerImpl.EXCEPTION_C1_MYDOUBLE, 
                    c1.getMyDouble(), DOUBLE_TOLERANCE);
            assertEquals("Second calculator value object contains not expected "
                + "string.", ExceptionThrowerImpl.EXCEPTION_C2_MYSTRING, 
                    c2.getMyString());
        }
    }
}
