/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.services.exceptionhandler;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

// Checkstyle: MagicNumber off
// Checkstyle: EmptyBlock off

/**
 * This class tests the security facade.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class SafetyFacadeTest extends TestCase {

    /** The application context. */
    private ClassPathXmlApplicationContext m_appContext;
    
    /** The bean that is guarded by the security facade. */
    private A m_a;
    
    /** The bare bean. */
    private A m_unsafeA;
    
    /**
     * Default constructor.
     */
    public SafetyFacadeTest() {
        m_appContext = new ClassPathXmlApplicationContext(
            "services/exceptionhandler/safetyFacadeTest.xml");
        
        m_a = (A) m_appContext.getBean("A");
        m_unsafeA = (A) m_appContext.getBean("unsafeA");
    }
    
    /**
     * Checks on the guarded bean that no exception is thrown and that the
     * result is converted correctly.
     */
    public void testA() {
        try {
            int result = m_a.div(1, 0);
            assertEquals("null was not transformed to 0.", 0, result);
        } catch (Throwable t) {
            fail("Caught exception although it wasn't expected. " + t);
        }
    }

    /**
     * Checks that the bare bean is still available.
     */
    public void testUnsafeA() {
        try {
            m_unsafeA.div(1, 0);
            fail("Didn't catch any exception.");
        } catch (ArithmeticException ae) {
            // expected
        } catch (Throwable t) {
            fail("Caught wrong exception: " + t);
        }
    }
    
    /**
     * Checks that an runtime exception is consumed by the security facade.
     */
    public void testRTException() {
        try {
            m_a.throwRTException();
        } catch (Throwable t) {
            fail("Caught exception although it wasn't expected. " + t);
        }
    }
    
    /**
     * Tests whether the checked application exception is forwarded correctly.
     */
    public void testForwardInterfaceExceptions() {
        try {
            m_a.throwException();
        } catch (ApplicationException ae) {
            // expected
        } catch (RuntimeException re) {
            fail("Expected ApplicationException but caught RunitmeException.");
        } catch (Throwable t) {
            fail("Expected RuntimeException but caught another one: " + t);
        }
    }
    
    /**
     * Tests the reconfiguration of a bean. A delegates add invocations to
     * bean B, which throws an unsupported operation exception. After that, A is
     * reconfigured to use C.
     * 
     * <p/>First, we check that B and C are called exactly once. From now on,
     * A is configured to use C. A subsequent call does not touch B at all,
     * which is tested after the counters are reset.
     */
    public void testReconfigurationExceptionHandler() {
        int result = 0;
        try {
            result = m_a.add(4, 5);
        } catch (Throwable t) {
            fail("Caught exception although it wasn't expected. " + t);
        }
        assertEquals("Wrongly added (first call).", 9, result);
        assertEquals("Called wrong method (B, first call)",
                1, B.s_numberOfAddCalls);
        assertEquals("Called wrong method (C, first call)",
                1, C.s_numberOfAddCalls);
        
        B.reset();
        C.reset();
        
        result = m_a.add(4, 5);
        assertEquals("Wrongly added (second call).", 9, result);
        assertEquals("Called wrong method (B, second call)",
                0, B.s_numberOfAddCalls);
        assertEquals("Called wrong method (C, second call)",
                1, C.s_numberOfAddCalls);
    }
    
    /**
     * Tests the round robin exception handler. First, the call on A fails
     * letting the system switch to B. Then, B will be called directly (the
     * proxy's target has been changed). In the third test it's assured that
     * the round robin really cycles back to A and in the last round we just
     * make sure that A is again referenced by the proxy as its target.
     */
    public void testRoundRobinExceptonHandler() {
        String foo = "foo";
        String bar = "bar";
        String result = null;
        try {
            result = m_a.concat(foo, bar);
        } catch (Throwable t) {
            fail("Caught exception although it wasn't expected. " + t);
        }
        assertEquals("Strings are badly concatenated (first call).",
                foo.concat(bar), result);
        assertEquals("Called wrong method (A, first call)",
                1, AImpl.s_numberOfConcatCalls);
        assertEquals("Called wrong method (B, first call)",
                1, B.s_numberOfConcatCalls);
        
        AImpl.reset();
        B.reset();
        
        // now we should use B
        result = m_a.concat(foo, bar);
        assertEquals("Strings are badly concatenated (second call).",
                foo.concat(bar), result);
        assertEquals("Called wrong method (A, second call)",
                0, AImpl.s_numberOfConcatCalls);
        assertEquals("Called wrong method (B, second call)",
                1, B.s_numberOfConcatCalls);
        
        AImpl.reset(); AImpl.s_concatFails = false;
        B.reset(); B.s_concatFails = true;
        
        // call B which fails, call A
        result = m_a.concat(foo, bar);
        assertEquals("Strings are badly concatenated (third call).",
                foo.concat(bar), result);
        assertEquals("Called wrong method (A, third call)",
                1, AImpl.s_numberOfConcatCalls);
        assertEquals("Called wrong method (B, third call)",
                1, B.s_numberOfConcatCalls);
        
        AImpl.reset(); AImpl.s_concatFails = false;
        B.reset(); B.s_concatFails = true;
        
        // finally we should use A
        result = m_a.concat(foo, bar);
        assertEquals("Strings are badly concatenated (fourth call).",
                foo.concat(bar), result);
        assertEquals("Called wrong method (A, fourth call)",
                1, AImpl.s_numberOfConcatCalls);
        assertEquals("Called wrong method (B, fourth call)",
                0, B.s_numberOfConcatCalls);
    }
    
    /**
     * Tests the retry exception handler. First we check that it retries several
     * times without exceeding the specified number of maximum retries. Then
     * the number of retries is increased and the invocation is expected to
     * fail, as tested in the second part.
     */
    public void testRetry() {
        int result = 0;
        try {
            result = m_a.sub(10, 8);
        } catch (IllegalArgumentException rt) {
            fail("Caught unexpected IllegalArgumentException.");
        }
        assertEquals("Calculated wrong value (first call)", 2, result);
        
        m_a.setRetries(6);
        try {
            m_a.sub(10, 8);
            fail("Didn't caught the expected IllegalArgumentException"
                    + "(second call).");
        } catch (IllegalArgumentException rt) {
            // expected
        } catch (Throwable t) {
            fail("Caught unexpected exception (second call): " + t);
        }
    }
}
//Checkstyle: MagicNumber on
//Checkstyle: EmptyBlock on
