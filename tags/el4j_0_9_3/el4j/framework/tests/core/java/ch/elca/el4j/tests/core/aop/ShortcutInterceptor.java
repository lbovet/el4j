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

package ch.elca.el4j.tests.core.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Simple test interceptor that shortcuts all method invocations on methods
 * with a given name and returns the given result.
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
public class ShortcutInterceptor 
    implements MethodInterceptor, InitializingBean {

    /** The result to return on matching methods. */
    private Object m_result;
    
    /** Method name of methods to intercept. */
    private String m_methodName;
    
    /**
     * Sets the object to return if the method name matches.
     * 
     * @param result
     *      The object to set.
     */
    public void setResult(Object result) {
        m_result = result;
    }

    /**
     * @return Returns the object used as return whenever the intercepted
     *      method's name matches the configured one.
     */
    public Object getResult() {
        return m_result;
    }

    /**
     * @return Returns the method name this interceptor is configured for.
     */
    public String getMethodName() {
        return m_methodName;
    }

    /**
     * Sets the method name this interceptor has to be configured for.
     * 
     * @param methodName
     *      The name of methods to intercept.
     */
    public void setMethodName(String methodName) {
        m_methodName = methodName;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (m_methodName.equals(invocation.getMethod().getName())) {
            return m_result;
        } else {
            return invocation.proceed();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_methodName, "methodName", this);
    }
}
