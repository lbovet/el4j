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

//Checkstyle: EmptyBlock off
//Checkstyle: MagicNumber off

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;

/**
 * 
 * This class is the basic class for XFire Tests.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
public abstract class AbstractXFireTest {
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
    @Before
    public void setUp() {
        m_appContext 
            = new ModuleApplicationContext(
                new String[] {"classpath*:mandatory/*.xml",
                    "scenarios/client/remotingtests-xfire-client-config.xml"}, 
                    false);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @After
    public void tearDown() {
        m_appContext.close();
    }

    /**
     * This test tests the area calulation method.
     */
    @Test
    public void testAreaCalculation() {
        final double a = 2.3;
        final double b = 5.7;
        final double delta = 0.00000001;
        double result = getCalc().getArea(a, b);
        assertEquals("The area is not correctly calculated.", result, 
            a * b, delta);
    }
    
    /**
     * This test tests the exception handling.
     */
    @Test
    public void testExceptionBehaviour() {
        // Checkstyle: EmptyBlock off
        try {
            getCalc().throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException e) {
            // This is expected
        }
    }
    
    /**
     * This test tests the counting of uppercase letters.
     */
    @Test
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
    }
    
    /**
     * Get the calculator to use.
     * @return Calculator to use
     */
    public abstract Calculator getCalc();
    
    
    /**
     * Get the Applicationcontext.
     * @return The ApplicationContext
     */
    protected ApplicationContext getApplicationContext() {
        return m_appContext;
    }
}
