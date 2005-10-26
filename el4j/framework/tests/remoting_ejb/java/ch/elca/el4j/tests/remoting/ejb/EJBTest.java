/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.tests.remoting.ejb;

import junit.framework.TestCase;
import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.remoting.protocol.ejb.EJBLifecycleManager;
import ch.elca.el4j.tests.remoting.ejb.service.Calculator;
import ch.elca.el4j.tests.remoting.ejb.service.Library;

/**
 * EJB integration tests.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class EJBTest extends TestCase {
    
    /** The mandatory config locations. */
    private static final String MANDATORY_CONFIG = "classpath*:mandatory/*.xml";
    
    /** The standard configuration location to use. */
    private static final String CONFIG_LOCATION
        = "classpath*:gui/client-config.xml";
    
    /** The sample's book id. */
    private static final int BOOK_ID = 1;
    
    /** The sample's book title. */
    private static final String BOOK_TITLE = "2001 Space Odyssey";
    
    /**
     * The stateful calculator with context object.
     */
    private Calculator m_statelessCalcWithCtx;
    
    /** The stateful library. */
    private Library m_statefulLibWithoutCtx;
    
    /** The second stateful library instance. */
    private Library m_statefulLibWithoutCtx2;
    
    /**
     * {@inheritDoc}
     */
    public void setUp() {
        ModuleApplicationContext appContext
            = new ModuleApplicationContext(
                new String[] {MANDATORY_CONFIG, CONFIG_LOCATION},
                true);
        
        m_statelessCalcWithCtx
            = (Calculator) appContext.
                getBean("CalculatorStatelessWithCtxEjbBean");
        
        m_statefulLibWithoutCtx
            = (Library) appContext.
                getBean("LibraryStatefulWithCtxEjbBean");
        
        m_statefulLibWithoutCtx2
            = (Library) appContext.
                getBean("LibraryStatefulWithCtxEjbBean");
    }
    
    /**
     * Tests the stateless calculator bean.
     */
    public void testStatelessCalculator() {
        
        boolean removeSucceeded;
        int result;
        
        result = m_statelessCalcWithCtx.pow(2, 3);
        assertEquals("2^3 = 8 but result is: " + result, 8, result);
        
        result = m_statelessCalcWithCtx.pow(2, 3);
        assertEquals("2^3 = 8 but result is: " + result, 8, result);
        
        removeSucceeded = EJBLifecycleManager.removeEjb(m_statelessCalcWithCtx);
        assertTrue("invocation of remove() failed.", removeSucceeded);
        
        result = m_statelessCalcWithCtx.pow(2, 3);
        assertEquals("2^3 = 8 but result is: " + result, 8, result);
        
        EJBLifecycleManager.removeEjb(m_statelessCalcWithCtx);
    }
    
    /**
     * Tests a simple stateful library bean.
     */
    public void testStatefulLibrary() {
        boolean succeeded;
        String title;
        
        succeeded = m_statefulLibWithoutCtx.putBook(BOOK_ID, BOOK_TITLE);
        assertTrue("The book with id = " + BOOK_ID + " already exists!",
                succeeded);
        
        title = m_statefulLibWithoutCtx.getBookTitle(BOOK_ID);
        assertNotNull("The bean is not stateful!", title);
        
        m_statefulLibWithoutCtx.putBook(2, "foo");
        m_statefulLibWithoutCtx.putBook(3, "bar");
        int size = m_statefulLibWithoutCtx.getSize();
        assertEquals("not as many books in library as registered", size, 7);
        
        succeeded = EJBLifecycleManager.removeEjb(m_statefulLibWithoutCtx);
        assertTrue("invocation of remove() failed.", succeeded);
    }
    
    /**
     * Tests two stateful library beans working concurrently.
     */
    public void testTwoClients() {
        m_statefulLibWithoutCtx.putBook(BOOK_ID, BOOK_TITLE);
        
        String title = m_statefulLibWithoutCtx.getBookTitle(BOOK_ID);
        assertNotNull("The bean is not stateless!", title);
        
        m_statefulLibWithoutCtx.putBook(2, "foo");
        m_statefulLibWithoutCtx.putBook(3, "bar");
        
        // client 2
        String[] books = m_statefulLibWithoutCtx2.getAllBooks();
        System.out.println("Books:");
        for (int i = 0; i < books.length; i++) {
            System.out.println("\t" + books[i]);
        }
        assertEquals("bean is already initialized...",
                4, m_statefulLibWithoutCtx2.getSize());
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) { }
        
        m_statefulLibWithoutCtx2.putBook(1, "test");
        assertEquals("not as many books as requierd",
                5, m_statefulLibWithoutCtx2.getSize());
        
        assertEquals(7, m_statefulLibWithoutCtx.getSize());
    }
    
    /**
     * Tests whether a java.lang.Exception is thrown back to the client.
     * 
     * <p><b>Note</b>: This method should fail according to the EJB 2.1 spec
     * (18.1.1) but works on JBoss.
     */
//    public void testException() {
//        /* This test is commented out by default and is used for compatibility
//         * checks only.
//         */
//        try {
//            m_statelessCalcWithCtx.throwException();
//            fail("No exception has been thrown.");
//        } catch (Exception e) {
//            assertEquals("Failing in this test just means that the container "
//                    + "is very strictly conforming the standard.",
//                    "A test exception.", e.getMessage());
//        }
//    }
    
    /**
     * Tests whether an application exception is thrown back to the client.
     */
    public void testIllegalAccessException() {
        try {
            m_statelessCalcWithCtx.throwIllegalAccessException();
            fail("No exception has been thrown.");
        } catch (IllegalAccessException iae) {
        } catch (Exception e) {
            fail("Caught an exception of wrong type.");
        }
    }
    
    /**
     * Tests whether runtime exceptions are thrown back to the client.
     */
    public void testRTException() {
        try {
            m_statelessCalcWithCtx.throwRTException();
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertEquals("Caught exception has wrong content",
                    "A RTException", e.getMessage());
        } catch (Throwable t) {
            fail("Wrong exception has been thrown: " + t);
        }
    }
    
    /**
     * Tests the clients behaviour on receiving a unknown runtime exception.
     * 
     * <p>This test works only if the FooRTException is not on the classpath.
     */
    public void testFooRTException() {
        try {
            m_statelessCalcWithCtx.throwFooRtException();
            fail("No exception has been thrown");
        } catch (RuntimeException rte) {
            assertTrue("Caught unexpected runtime exception. " + rte,
                    rte.getMessage().startsWith(
                            "Runtime exception thrown on server side"));
            
        } catch (Throwable t) {
            fail("Wrong exception has been thrown: " + t);
        }
    }
}
