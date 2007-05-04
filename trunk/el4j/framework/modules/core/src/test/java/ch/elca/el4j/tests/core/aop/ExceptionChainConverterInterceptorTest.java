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

package ch.elca.el4j.tests.core.aop;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.aopalliance.intercept.MethodInvocation;

import ch.elca.el4j.core.aop.ExceptionChainConversionInterceptor;

/**
 * Test case for exception chain converter.
 * @author pos
 */
public class ExceptionChainConverterInterceptorTest extends TestCase {

    /**
     * Test normal invocation usage
     */
    public void testBasicInterceptor() {
        MethodInvocation methodInvocation = (MethodInvocation) createMock(MethodInvocation.class);

        String myFancyObject = "alsdkjfasljfaslödjfasdo034023740";

        try {
            expect(methodInvocation.proceed()).andReturn(myFancyObject);
        } catch (Throwable e1) {
            fail();
        }

        replay(methodInvocation);

        ExceptionChainConversionInterceptor ecci = new ExceptionChainConversionInterceptor();

        Object result = null;
        try {
            result = ecci.invoke(methodInvocation);
        } catch (Throwable e) {
            fail();
        }
        assertEquals(result, myFancyObject);
        verify(methodInvocation);
    }

    /**
     * Test usage with exception
     */
    public void testInterceptorWithException() {
        MethodInvocation methodInvocation = (MethodInvocation) createMock(MethodInvocation.class);

        try {
            expect(methodInvocation.proceed()).andThrow(
                    new IllegalArgumentException("ttt", m_testThrowable));
        } catch (Throwable e1) {
        }

        replay(methodInvocation);

        ExceptionChainConversionInterceptor ecci = new ExceptionChainConversionInterceptor();

        try {
            ecci.invoke(methodInvocation);
        } catch (Throwable t) {
            checkThrowableIsCorrectlyHandled(t.getCause());
            assertTrue(t instanceof IllegalArgumentException);
            assertTrue(t.getMessage().equals("ttt"));
            verify(methodInvocation);
            return;
        }
        fail();
    }

    /**
     * Test isolated cause conversion
     */
    public void testConvertCause() {
        ExceptionChainConversionInterceptorChild ecci = new ExceptionChainConversionInterceptorChild();

        ecci.callConvertCause(null);

        Throwable result = ecci.callConvertCause(m_testThrowable);

        checkThrowableIsCorrectlyHandled(result);
    }

    Throwable m_testThrowable = new Throwable("t", new Exception("t2",
            new Exception("t3", new RuntimeException("t4", null))));

    private void checkThrowableIsCorrectlyHandled(Throwable result) {
        assertTrue(result instanceof Throwable);
        assertFalse(result.getCause() instanceof Exception);
        assertTrue(result.getCause() instanceof Throwable);
        assertFalse(result.getCause().getCause() instanceof Exception);
        assertTrue(result.getCause().getCause() instanceof Throwable);
        assertFalse(result.getCause().getCause().getCause() instanceof RuntimeException);
        assertTrue(result.getCause().getCause().getCause() instanceof Throwable);
    }

    // to be able to call the protected method of class to test
    static class ExceptionChainConversionInterceptorChild extends
            ExceptionChainConversionInterceptor {

        public Throwable callConvertCause(Throwable original) {
            return super.convertCause(original);
        }

    }

}
