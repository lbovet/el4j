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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.target.HotSwappableTargetSource;

/**
 * This class provides functionality to handle exceptions centrally (with
 * respect to a number of classes or the whole system).
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
public abstract class AbstractExceptionHandlerInterceptor
    implements MethodInterceptor {

    /** Holds the number of retries. */
    protected static ThreadLocal s_retries = new ThreadLocal();
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            AbstractExceptionHandlerInterceptor.class);

    /**
     * Holds the exception interceptor's default behaviour when no appropriate
     * exception handler was found.
     */
    private boolean m_defaultBehaviourConsume = true;
    
    /** 
     * Holds whether to handle all exceptions, even those which are defined in
     * a method's signature.
     */
    private boolean m_forwardSignatureExceptions;
    
    /** 
     * Holds whether to handle runtime exceptions that are listed in a
     * method's signature.
     */
    private boolean m_handleRTSignatureExceptions;
    
    /**
     * @return Returns the number of retries left, allowing
     *      {@link 
     *      ch.elca.el4j.services.exceptionhandler.handler.AbstractRetryExceptionHandler}s
     *      to determine whether they have to call their handling routines.
     *      <code>-1</code> signals that no retires have been registered so far.
     */
    public static int getRetries() {
        Object retries = s_retries.get();
        if (retries == null) {
            return -1;
        } else {
            return ((Integer) retries).intValue();
        }
    }

    /**
     * Sets the number of retries. Each retry reinvokes the the interceptor
     * chain as it would have been called by the client directly.
     * 
     * @param retries
     *      The number of retries to set.
     */
    protected static void setRetries(int retries) {
        s_retries.set(new Integer(retries));
    }

    /**
     * Sets whether runtime exceptions, that are listed in a method's signature,
     * have to be handled.
     * 
     * @param handleRTSignatureExceptions
     *      <code>true</code> to handle unchecked exceptions by this exception
     *      handler, <code>false</code> to rethorw them.
     */
    public void setHandleRTSignatureExceptions(
        boolean handleRTSignatureExceptions) {
        m_handleRTSignatureExceptions = handleRTSignatureExceptions;
    }

    /**
     * Sets the interceptor's exception handling default policy. This affects
     * exceptions that aren't handled by any exception handler only. Default is
     * to consume them.
     * 
     * @param defaultBehaviourConsume
     *      <code>true</code> to consume all exceptions that are not handled by
     *      an exception handler, <code>false</code> to forward all unhandled
     *      exceptions.
     */
    public void setDefaultBehaviourConsume(boolean defaultBehaviourConsume) {
        m_defaultBehaviourConsume = defaultBehaviourConsume;
    }

    /**
     * Sets whether all exceptions have to be handled by this safety facade,
     * even those which are listed in the signature.
     * 
     * @param forwardSignatureException
     *      <code>true</code> forces to forward any exceptions listed in a
     *      method's interface (even unchecked exceptions, if they are in the
     *      signature). <code>false</code> handles all exceptions by an
     *      appropriate registered exception handler.
     *      
     * @see #setHandleRTSignatureExceptions(boolean)
     */
    public void setForwardSignatureExceptions(
        boolean forwardSignatureException) {
        m_forwardSignatureExceptions = forwardSignatureException;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        
        try {
            HotSwappableTargetSource swapper = null;
            
            while (true) {
                try {
                    if (swapper == null) {
                        /* Clones the invocation before each execution. This
                         * allows to execute the MethodInvocation.proceed()
                         * several times (see p. 163 in Java Development with
                         * the Spring Framework, Rod Johnson et al., 2005,
                         * Wiley, ISBN 0-7645-7483-3). 
                         */
                        MethodInvocation localInvocation
                            = ((ReflectiveMethodInvocation) invocation).
                                invocableClone();
                        
                        result = doInvoke(localInvocation);
                        
                    } else {
                        result = invokeHotSwappable(swapper, invocation);
                    }
                    break;
                } catch (RetryException re) {
                    int retries = getRetries();
                    if (retries == -1) {
                        retries = re.getRetries();
                        setRetries(retries);
                    }
                    if (retries > 0) {
                        setRetries(retries - 1);
                        swapper = re.getSwapper();
                        
                    } else {
                        break;
                    }
                }
            }
        } finally {
            // clean up thread local
            s_retries.set(null);
        }
        
        return result;
    }
    
    /**
     * Performs the actual invocation of the
     * <code>MethodInvocation.prceed()</code> method.
     *  
     * @param invocation
     *      The <code>MethodInvocation</code> to call <code>proceed()</code> on.
     *      
     * @return Returns the target's result;
     * 
     * @throws RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Any exception thrown by the original method's invocation or by
     *      one of the used exception handlers.
     */
    protected Object doInvoke(MethodInvocation invocation) 
        throws RetryException, Throwable {
        Object result = null;
        try {
            result = invocation.proceed();
            
        } catch (RetryException re) {
            throw re;
        } catch (Throwable t) {
            if (m_forwardSignatureExceptions) {
                handleInterfaceExceptions(invocation, t);
            }
            result = handleException(t, invocation);
        }
        return result;
    }
    
    /**
     * Handles the given exception that was thrown in the given method
     * invocation's execution.
     * 
     * @param t
     *      The exception to handle.
     *      
     * @param invocation
     *      The method invocation that threw the exception.
     *      
     * @return Returns an object which is treated as the original invocation's
     *      result.
     *      
     * @throws RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Any exception thrown by a exception handler.
     */
    protected abstract Object handleException(Throwable t,
        MethodInvocation invocation) throws RetryException, Throwable;

    /**
     * Handles exceptions that are listed in a method's signature.
     * 
     * @param invocation
     *      The called method invocation.
     *      
     * @param t
     *      The caught exception.
     *      
     * @throws Throwable
     *      The given exception <code>t</code> if it has to be forwarded to the
     *      invoker.
     */
    protected void handleInterfaceExceptions(MethodInvocation invocation,
        Throwable t) throws Throwable {
        if (m_handleRTSignatureExceptions && t instanceof RuntimeException) {
            return;
        }
        
        Class[] exceptions = invocation.getMethod().getExceptionTypes();
        for (int i = 0; i < exceptions.length; i++) {
            if (t.getClass() == exceptions[i]) {
                s_logger.debug("Rethrowing exception (defined on signature).");
                throw t;
            }
        }
    }

    /**
     * Handles the given exception that was thrown given method invocation using
     * the provided exception configurations.
     * 
     * @param t
     *      The exception to handle.
     *      
     * @param invocation
     *      The method invocation that threw the exception.
     *      
     * @param exceptionConfigurations
     *      The exception handler configuration to use.
     *      
     * @return Returns an object which is treated as the original invocation's
     *      result.
     *      
     * @throws RetryException
     *      Signals that the complete invocation has to be rerun.
     *      
     * @throws Throwable
     *      Any exception thrown by a exception handler.
     */
    protected Object doHandleException(Throwable t, MethodInvocation invocation,
        ExceptionConfiguration[] exceptionConfigurations)
        throws RetryException, Throwable {
        for (int i = 0; i < exceptionConfigurations.length; i++) {
            
            ExceptionConfiguration next = exceptionConfigurations[i];
            
            if (next.handlesExceptions(t, invocation)) {
                s_logger.debug(
                    "Found an appropriate exception handler.");
                
                try {
                    return next.getExceptionHandler().
                        handleException(t, this, invocation);
                } catch (InappropriateHandlerException whe) {
                    break;
                }
            }
        }
        
        s_logger.info("No appropriate exception handler found.", t);
        if (!m_defaultBehaviourConsume) {
            throw t;
        }
        
        return null;
    }
    
    /**
     * Invokes the method on the object provided by the target source.
     * 
     * @param swapper
     *      The hot swappable target source to get the target from.
     *      
     * @param invocation
     *      The original invocation.
     *      
     * @return Returns the result that was expected by the call on the original
     *      method.
     *       
     * @throws Throwable
     *      Any exception.
     */
    private Object invokeHotSwappable(HotSwappableTargetSource swapper,
            MethodInvocation invocation) throws Throwable {
       
        /* HACK The swappable exception handler swaps the target of th
         *      TargetSource. But this change is not reflected in the current
         *      MethodInvocation which still points to the original target.
         *      Retrieving the TargetSource in the retry exception allows to
         *      get all the needed information to call the new target.
         *      This solution does not check if the called method does really
         *      interchange the original one (i.e. that its class is in the
         *      same hierarchy as the original one) which potentially leads to
         *      the problem that the invocation works if the original target's
         *      invocation fails and the retry is started in this method, but
         *      that it doesn't if the method is invoked again on the proxy.
         */
        Object result = null;
        
        Object newTarget = swapper.getTarget();
        Method origMethod = invocation.getMethod();
        try {
            Method method = newTarget.getClass().getMethod(
                    origMethod.getName(), origMethod.getParameterTypes());
            
            result = method.invoke(newTarget, invocation.getArguments());
            
        } catch (NoSuchMethodException nsme) {
            // The new target does not implement the same interface. The
            // RetryException forces to change the target again.
            throw new RetryException(getRetries());
            
        } catch (IllegalAccessException iae) {
            // same as for NoSuchMethodException
            throw new RetryException(getRetries());
            
        } catch (InvocationTargetException ite) {
            Throwable te = ite.getTargetException();
            if (te instanceof UnsupportedOperationException) {
                // same as for NoSuchMethodException
                throw new RetryException(getRetries());
            }
            throw te;
        }
        
        return result;
    }
}
