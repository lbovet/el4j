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

package ch.elca.el4j.services.exceptionhandler;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class allows to handle exceptions centrally. Realized as an interceptor,
 * it can be added to an arbitrary bean using a proxy. The handler supports
 * different contexts which allows to handle a particular exception in different
 * fashions, depending on the current context.
 * 
 * <p>This class is configured with different policies, one for each context.
 * A policy is an <code>&lt;context, {@link
 * ch.elca.el4j.services.exceptionhandler.ExceptionConfiguration}&gt;</code>
 * pair.
 * 
 * <p><b>Important</b>: The context is stored in a {@link
 * java.lang.ThreadLocal}, allowing to use this class in multi threading
 * environments. Since it's considered cumbersome to set the context before
 * each invocation it isn't reset. <b>The programmer is responsible to set the
 * context appropriately.</b>
 * 
 * <p>Don't use this interceptor directly. Instead use the {@link
 * ch.elca.el4j.services.exceptionhandler.ContextExceptionHandlerFactoryBean}.
 * 
 * <p><b>Example</b>: Let's assume the exception configurations are
 * initialized properly.
 * <pre>&lt;bean id="contextExceptionHandlerInterceptor"
 *       class="ch.elca.el4j.services.exceptionhandler.ContextExceptionHandlerInterceptor"&gt;
 *       &lt;property name="policies"&gt;
 *           &lt;map&gt;
 *               &lt;entry key="gui"&gt;
 *                   &lt;list&gt;
 *                       &lt;ref local="guiExceptionConfiguration"/&gt;
 *                   &lt;/list&gt;
 *               &lt;/entry&gt;
 *               &lt;entry key="batch"&gt;
 *                   &lt;list&gt;
 *                       &lt;ref local="batchExceptionConfiguration"/&gt;
 *                   &lt;/list&gt;
 *               &lt;/entry&gt;
 *           &lt;/map&gt;
 *       &lt;/property&gt;
 *   &lt;/bean&gt;</pre>
 * And the Java fragment:
 * <pre>
 * ContextExceptionHandlerInterceptor.setContext("gui");
 * foo(); // an exception is handled by the gui policy
 * 
 * ContextExceptionHandlerInterceptor.setContext("batch");
 * foo(); // an exception is handled by the batch policy
 * 
 * foo(); // an exception is handled by the batch policy
 * </pre>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.services.exceptionhandler.ContextExceptionHandlerFactoryBean
 */
public class ContextExceptionHandlerInterceptor
    extends AbstractExceptionHandlerInterceptor {
    
    /** The static logger. */
    private static Log s_logger
        = LogFactory.getLog(ContextExceptionHandlerInterceptor.class);
    
    /** The thread specific context. */
    private static ThreadLocal s_context = new ThreadLocal();

    /** The policies. */
    private Map m_policies;

    /**
     * Default constructor. Configures the interceptor to handle only those
     * exceptions that are <b>not</b> defined in the signature (excluding
     * unchecked exceptions, which are handled always).
     */
    public ContextExceptionHandlerInterceptor() {
        super();
        // change this behaviour in the ContextExceptionHandlerFactoryBean too
        // (to be done manually since Java doesn't support multi inheritance).
        setForwardSignatureExceptions(true);
        setHandleRTSignatureExceptions(true);
    }

    /**
     * Sets the current thread's context.
     * 
     * @param context
     *      The context to set.
     * 
     * @return Returns the previous context.
     */
    public static Object setContext(Object context) {
        Object curContext = s_context.get();
        s_context.set(context);
        return curContext;
    }

    /**
     * Sets the policies.
     * 
     * @param policies
     *      The policies to set.
     */
    public void setPolicies(Map policies) {
        m_policies = policies;
    }

    /**
     * {@inheritDoc}
     */
    protected Object handleException(Throwable t, MethodInvocation invocation)
        throws Throwable {
        
        handleInterfaceExceptions(invocation, t);
        
        Object context = s_context.get();
        if (context == null) {
            s_logger.error(
                    "No context has been set.");
            throw new MissingContextException("No context has been set.", t);
        }
        
        List list = (List) m_policies.get(context);
        
        ExceptionConfiguration[] config = (ExceptionConfiguration[]) list.
        toArray(new ExceptionConfiguration[list.size()]);
        return doHandleException(t, invocation, config);
    }
}
