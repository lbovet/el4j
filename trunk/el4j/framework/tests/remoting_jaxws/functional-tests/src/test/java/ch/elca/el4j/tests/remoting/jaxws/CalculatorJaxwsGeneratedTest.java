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
package ch.elca.el4j.tests.remoting.jaxws;

//Checkstyle: EmptyBlock off
//Checkstyle: MagicNumber off


import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.remoting.jaxws.service.IntMatrixAdapter;
import ch.elca.el4j.tests.remoting.jaxws.service.gen.CalculatorException_Exception;
import ch.elca.el4j.tests.remoting.jaxws.service.gen.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.jaxws.service.gen.CalculatorWS;
import ch.elca.el4j.tests.remoting.jaxws.service.gen.SomeIntValue;

import junit.framework.TestCase;

/**
 * 
 * This class is a test for JAX-WS using the generated classes directly.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 * @author Stefan Wismer (SWI)
 */
public class CalculatorJaxwsGeneratedTest extends TestCase {
    /**
     * Is the delta to doubles can have to be equal.
     */
    private static final double DOUBLE_TOLERANCE = 0.000000001;
        
    /**
     * ApplicationContext.
     */
    private ConfigurableApplicationContext m_appContext;

    /**
     * {@inheritDoc}
     */
    public void setUp() {
        m_appContext 
            = new ModuleApplicationContext(
                new String[] {"classpath*:mandatory/*.xml",
                    "scenarios/client/remotingtests-jaxws-client-config.xml"}, 
                    false);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        m_appContext.close();
        super.tearDown();
    }

    /**
     * This test tests the area calulation method.
     */
    public void testAreaCalculation() {
        final double VALUE_A = 2.3;
        final double VALUE_B = 5.7;
        final double FAULT_DELTA = 0.00000001;
        double result = getCalc().getArea(VALUE_A, VALUE_B);
        assertEquals("The area is not correctly calculated.", result, 
            VALUE_A * VALUE_B, FAULT_DELTA);
    }
    
    /**
     * This test tests the exception handling.
     */
    public void testExceptionBehaviour() {
        // Checkstyle: EmptyBlock off
        try {
            getCalc().throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException_Exception e) {
            // This is expected
        }
    }
    
    /**
     * This test tests the counting of uppercase letters.
     */
    public void testNumberOfUppercaseCharacters() {
        String message = "Hans Müller likes to pay with Euro.";
        int numberOfUppercaseLetters = 3;
        int result = getCalc().countNumberOfUppercaseLetters(message);
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
        
        // test int[]
        o.setMyByteArray(MY_BYTE_ARRAY);
        o.getMyIntArray().add(9);
        o.getMyIntArray().add(2);
        
        // test int[][]
        IntMatrixAdapter adapter = new IntMatrixAdapter();
        try {
            o.setMyIntMatrix(adapter.marshal(
                new int[][]{new int[] {5, 8}, new int[] {1, -4}}));
        } catch (Exception e) {
            fail("Error occured while marshalling matrix.");
        }
        
        SomeIntValue v = new SomeIntValue();
        v.setSomeValue(MY_INT);
        o.setSomeValue(v);

        o.getMyIntegerList().add(4);
        o.getMyIntegerList().add(7);
        
        o.getMyIntegerSet().add(2);
        o.getMyIntegerSet().add(5);
        
        
        CalculatorValueObject echo = getCalc().echoValueObject(o);
        
        assertEquals("Int values are not equals.", 
            o.getMyInt(), echo.getMyInt());
        assertEquals("Long values are not equals.", 
            o.getMyLong(), echo.getMyLong());
        assertEquals("Double values are not equals.", 
            o.getMyDouble(), echo.getMyDouble(), DOUBLE_TOLERANCE);
        assertEquals("Strings are not equals.",
            o.getMyString(), echo.getMyString());
        assertTrue("Byte arrays are not equals.",
            Arrays.equals(o.getMyByteArray(), echo.getMyByteArray()));
        assertEquals("SomeIntValue are not equals.",
            o.getSomeValue().getSomeValue(),
            echo.getSomeValue().getSomeValue());
        
        if (echo.getMyIntegerList().size() == 2) {
            assertTrue("List items are not equal.",
                echo.getMyIntegerList().get(0).equals(new Integer(4))
                && echo.getMyIntegerList().get(1).equals(new Integer(7)));
        } else {
            fail("List size is not equal.");
        }
        
        if (echo.getMyIntegerSet().size() == 2) {
            assertTrue("Set is not equal.",
                echo.getMyIntegerSet().contains(new Integer(2))
                && echo.getMyIntegerSet().contains(new Integer(5)));
        } else {
            fail("Set size is not equal.");
        }
        
        assertTrue("int[] is not equal.",
            echo.getMyIntArray().get(0).equals(9)
            && echo.getMyIntArray().get(1).equals(2));
        
        int[][] matrixEcho = null;
        try {
            matrixEcho = adapter.unmarshal(o.getMyIntMatrix());
        } catch (Exception e) {
            fail("Error occured while unmarshalling matrix.");
        }
        
        assertTrue("int[][] is not equal.",
            matrixEcho[0][0] == 5 && matrixEcho[0][1] == 8
            && matrixEcho[1][0] == 1 && matrixEcho[1][1] == -4);
    }
    
    /**
     * Get the calculator to use.
     * @return Calculator to use
     */
    public CalculatorWS getCalc() {
        return (CalculatorWS) getApplicationContext().
            getBean("calculatorGenerated");
    }
    
    
    /**
     * Get the Applicationcontext.
     * @return The ApplicationContext
     */
    protected ApplicationContext getApplicationContext() {
        return m_appContext;
    }
}
