/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.remoting;

import java.util.Arrays;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.remoting.protocol.soap.SoapHelper;
import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.service.SpecialCalculatorException;
import ch.elca.el4j.tests.remoting.service.soap.ExceptionThrower;
import ch.elca.el4j.tests.remoting.service.soap.RemoteExceptionWithData;
import ch.elca.el4j.tests.remoting.service.soap.impl.ExceptionThrowerImpl;

import junit.framework.TestCase;



/**
 * This class is a test for the calculator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorSoapTest extends TestCase {
    /**
     * Is the delta to doubles can have to be equal.
     */
    private static final double DOUBLE_TOLERANCE = 0.000000001;
    
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
    public void setUp() {
        ModuleApplicationContext appContext 
            = new ModuleApplicationContext(
                new String[] {"classpath*:mandatory/*.xml",
                    "client/remotingtests-soap-client-config.xml"}, true);
        m_calc 
            = (Calculator) appContext.getBean("calculator");
        m_soapExceptionThrower 
            = (ExceptionThrower) appContext.getBean("soapExceptionThrower");
    }

    /**
     * This test tests the area calulation method.
     */
    public void testAreaCalculation() {
        final double VALUE_A = 2.3;
        final double VALUE_B = 5.7;
        final double FAULT_DELTA = 0.00000001;
        double result = m_calc.getArea(VALUE_A, VALUE_B);
        assertEquals("The area is not correctly calculated.", result, 
            VALUE_A * VALUE_B, FAULT_DELTA);
    }
    
    /**
     * This test tests the exception handling.
     */
    public void testExceptionBehaviour() {
        try {
            m_calc.throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException e) {
            // It's okay.
            String stackTrace = SoapHelper.getLastServerSideStackTrace();
            System.err.println(stackTrace);
        }
    }
    
    /**
     * This test tests a special exception behaviour.
     */
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
            System.err.println(stackTrace);
        }
    }
    
    /**
     * This test tests the counting of uppercase letters.
     */
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
    public void testEchoOfValueObject() {
        final int MY_INT = 449312154;
        final long MY_LONG = 3121846575454654L;
        final double MY_DOUBLE = 6994.641368469;
        final String MY_STRING 
            = "I can not find any ä, ö, ü, é, è or à character on my keyboard.";
        final byte[] MY_BYTE_ARRAY = MY_STRING.getBytes();
        
        CalculatorValueObject o = new CalculatorValueObject();
        o.setMyInt(MY_INT);
        o.setMyLong(MY_LONG);
        o.setMyDouble(MY_DOUBLE);
        o.setMyString(MY_STRING);
        o.setMyByteArray(MY_BYTE_ARRAY);
        
        final double FAULT_DELTA = 0.00000001;
        CalculatorValueObject echo = m_calc.echoValueObject(o);
        assertEquals("Int values are not equals.", 
            o.getMyInt(), echo.getMyInt());
        assertEquals("Long values are not equals.", 
            o.getMyLong(), echo.getMyLong());
        assertEquals("Double values are not equals.", 
            o.getMyDouble(), echo.getMyDouble(), FAULT_DELTA);
        assertEquals("Strings are not equals.",
            o.getMyString(), echo.getMyString());
        assertTrue("Byte arrays are not equals.",
            Arrays.equals(o.getMyByteArray(), echo.getMyByteArray()));
    }
    
    /**
     * This test is used to check if a soap conform exception, which is not 
     * translated by a EL4J component, is received correctly on client side.
     */
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
