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

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.exceptionhandler.ContextExceptionHandlerInterceptor;
import ch.elca.el4j.services.exceptionhandler.MissingContextException;

import junit.framework.TestCase;

/**
 * This class tests the context exception handler.
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
public class ContextExceptionHandlerTest extends TestCase {

    /** The application context. */
    private ApplicationContext m_appContext;
    
    /** The bean that is guarded by the security facade. */
    private A m_a;
    
    /**
     * Default constructor.
     */
    public ContextExceptionHandlerTest() {
        m_appContext = new ModuleApplicationContext(
            "scenarios/services/exceptionhandler/" 
            + "contextExceptionHandlerTest.xml", false);
        m_a = (A) m_appContext.getBean("A");
    }
    
    /**
     * Tests the context semantics. This test has to be driven manually. First
     * a dialogue, then a log entry with stack trace and finally, again a
     * dialogue is printed to the standard out.
     */
    public void testContextSemantics() {
        ContextExceptionHandlerInterceptor.setContext("gui");
        step(1, 1, 0);
        ContextExceptionHandlerInterceptor.setContext("batch");
        step(2, 0, 1);
        step(2, 0, 1);
    }
    
    /**
     * Tests whether the situation with a missing context is handled correclty.
     */
    public void testMissingContext() {
        ContextExceptionHandlerInterceptor.setContext(null);
        // Checkstyle: EmptyBlock off
        try {
            step(1, 1, 0);
            fail("Expected a MissingContextException but didn't catch one.");
        } catch (MissingContextException mce) { }
        // Checkstyle: EmptyBlock on
    }

    /**
     * Performs a division operation and checks whether the correct method was
     * invoked.
     * 
     * @param call
     *      The call's number used for displaying assertion failures.
     *      
     * @param expectedGuiCalls
     *      The expected number of GUI error handling code invocations.
     *      
     * @param expectedBatchCalls
     *      The expected number of batch error handling code invocations.
     */
    private void step(int call, int expectedGuiCalls, int expectedBatchCalls) {
        MessageBoxExceptionHandler.s_numberOfHandleCalls = 0;
        LogExceptonHandler.s_numberOfHandleCalls = 0;
        
        int result = -1;
        try {
            result = m_a.div(1, 0);
        } catch (MissingContextException mce) {
            throw mce;
        } catch (Throwable t) {
            fail("Caught unexpected exception: " + t);
        }
        
        assertEquals("null was not transformed to 0.", 0, result);
        assertEquals("Used wrong context (" + call + ")",
                expectedGuiCalls,
                MessageBoxExceptionHandler.s_numberOfHandleCalls);
        assertEquals("Used wrong context (first call)",
                expectedBatchCalls, LogExceptonHandler.s_numberOfHandleCalls);
    }
}
