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

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBObject;
import javax.naming.NamingException;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.ejb.access.SimpleRemoteSlsbInvokerInterceptor;

import ch.elca.el4j.services.remoting.protocol.ejb.exception.WrapperException;

/**
 * This class allows to invoke remote EJB Session beans, either stateless
 * or stateful. It stores the EBJObject for that purposes. Spring's
 * implementation supports stateless beans only.
 * 
 * <p/>Since the EJBObject has to be cached over several invocations you can
 * use the
 * {@link ch.elca.el4j.services.remoting.protocol.ejb.EJBLifecycleManager} to
 * remove it explicitely (but this is not required by the EJB standard).
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
public abstract class AbstractRemoteSessionBeanInvokerInterceptor extends
        SimpleRemoteSlsbInvokerInterceptor {

    /** The EJBObject. */
    private EJBObject m_ejb = null;
    
    /**
     * {@inheritDoc}
     */
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        if ("remove".equals(invocation.getMethod().getName())) {
            // remove EJBObject
            removeSessionBeanInstance(m_ejb);
            m_ejb = null;
            
        } else {
            try {
                result = super.doInvoke(invocation);
            } catch (WrapperException we) {
                Throwable wrappedException = we.getWrappedException();
                if (wrappedException == null) {
                    // wrapped exception class is not on classpath
                    RuntimeException rte = new RuntimeException(
                            "Runtime exception thrown on server side is not "
                            + "available on client. But the stack trace has"
                            + "been preserved.");
                    rte.setStackTrace(we.getStackTrace());
                    throw rte;
                } else {
                    throw we.getWrappedException();
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    protected EJBObject getSessionBeanInstance() throws NamingException,
            InvocationTargetException {
        if (m_ejb == null) {
            m_ejb = super.getSessionBeanInstance();
        }
        return m_ejb;
    }

    /**
     * {@inheritDoc}
     */
    protected void releaseSessionBeanInstance(EJBObject ejb) {
        // do nothing
    }
}
