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

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class tests the {@link ch.elca.el4j.core.aop.BeanTypeAutoProxyCreator}
 * using two {@link ch.elca.el4j.tests.core.aop.ShortcutInterceptor}s that
 * intercept either one or the other method invocation of the target classes.
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
public class BeanTypeAutoProxyCreatorTest extends TestCase {

    /** The application context. */
    private ApplicationContext m_appContext;
    
    /** The interceptor that intercepts all <code>doitA</code> invocations. */
    private ShortcutInterceptor m_shortcutA;
    
    /** The interceptor that intercepts all <code>doitB</code> invocations. */
    private ShortcutInterceptor m_shortcutB;
    
    /**
     * Default constructor.
     */
    public BeanTypeAutoProxyCreatorTest() {
        m_appContext = new ModuleApplicationContext(
                "classpath:scenarios/core/aop/" 
                + "testBeansForBeanTypeAutoProxyCreator.xml",
                false);
        m_shortcutA = (ShortcutInterceptor) m_appContext.getBean(
                "shortcutInterceptorA");
        m_shortcutB = (ShortcutInterceptor) m_appContext.getBean(
                "shortcutInterceptorB");
    }
    
    /**
     * Tests whether classes that implement {@link MarkerA} interface are
     * intercepted correctly.
     */
    public void testMarkerA() {
        A a = (A) m_appContext.getBean("A");
        assertEquals("Interceptor was not invoked.",
                m_shortcutA.getResult(), a.doitA());
        assertEquals("A wrong interceptor was not invoked.",
                AbstractDoit.DONE, a.doitB());
    }
    
    /**
     * Tests whether classes that implement {@link MarkerB} interface are
     * intercepted correctly.
     */
    public void testMarkerB() {
        B b = (B) m_appContext.getBean("B");
        assertEquals("A wrong interceptor was not invoked.",
                AbstractDoit.DONE, b.doitA());
        assertEquals("Interceptor was not invoked.",
                m_shortcutB.getResult(), b.doitB());
    }
    
    /**
     * Tests whether classes that implement {@link MarkerA} and {@link MarkerB}
     * interface are intercepted correctly.
     */
    public void testMarkerC() {
        C c = (C) m_appContext.getBean("C");
        assertEquals("Interceptor was not invoked (implementing 2 interfaces).",
                m_shortcutA.getResult(), c.doitA());
        assertEquals("Interceptor was not invoked (implementing 2 interfaces).",
                m_shortcutB.getResult(), c.doitB());
    }
    
    /**
     * Tests whether classes that implement {@link MarkerC} interface are
     * intercepted correctly.
     */
    public void testMarkerD() {
        D d = (D) m_appContext.getBean("D");
        assertEquals("Interceptor was not invoked (registered 2 interceptors).",
                m_shortcutA.getResult(), d.doitA());
        assertEquals("Interceptor was not invoked (registered 2 interceptors).",
                m_shortcutB.getResult(), d.doitB());
    }
}
