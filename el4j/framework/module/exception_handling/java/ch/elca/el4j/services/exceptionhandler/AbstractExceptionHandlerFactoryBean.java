/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.services.exceptionhandler;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Convenience factory to create exception handler proxies.
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
public abstract class AbstractExceptionHandlerFactoryBean extends AdvisedSupport
    implements FactoryBean, InitializingBean {

    /** The proxy's target object which is either a bean or a TargetSource. */
    private Object m_target;
    
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
    
    /** Whether the factory creates singleton instances. */
    private boolean m_singleton = true;
    
    /** The singleton proxy. */
    private Object m_singletonProxy;
    
    /**
     * Sets the proxy's target, which is either the target bean or an instance
     * of {@link TargetSource}.
     * 
     * @param target
     *      The target to set.
     */
    public void setTarget(Object target) {
        m_target = target;
    }
    
    /**
     * @see AbstractExceptionHandlerInterceptor#setDefaultBehaviourConsume(boolean)
     */
    public void setDefaultBehaviourConsume(boolean defaultBehaviourConsume) {
        m_defaultBehaviourConsume = defaultBehaviourConsume;
    }

    /**
     * @see AbstractExceptionHandlerInterceptor#setForwardSignatureExceptions(boolean)
     */
    public void setForwardSignatureExceptions(
            boolean forwardSignatureExceptions) {
        m_forwardSignatureExceptions = forwardSignatureExceptions;
    }

    /**
     * @see AbstractExceptionHandlerInterceptor#setHandleRTSignatureExceptions(boolean)
     */
    public void setHandleRTSignatureExceptions(
            boolean handleRTSignatureExceptions) {
        m_handleRTSignatureExceptions = handleRTSignatureExceptions;
    }
    
    /**
     * Sets whether the factory returns a singleton proxy.
     * 
     * @param singleton
     *      Whether to create a singleton proxy.
     */
    public void setSingleton(boolean singleton) {
        m_singleton = singleton;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_target == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The property 'target' or 'interfaces' is required.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        Object proxy;
        
        if (isSingleton()) {
            if (m_singletonProxy == null) {
                m_singletonProxy = createNewProxy();
            }
            proxy = m_singletonProxy;
        
        } else {
            proxy = createNewProxy();
        }
        
        return proxy;
    }

    /**
     * Create a new proxy instance.
     * 
     * @return Returns a new proxy instance.
     */
    private Object createNewProxy() {
        AbstractExceptionHandlerInterceptor interceptor
            = createExceptionHandlerInterceptor();
        interceptor.setDefaultBehaviourConsume(m_defaultBehaviourConsume);
        interceptor.setForwardSignatureExceptions(m_forwardSignatureExceptions);
        interceptor.setHandleRTSignatureExceptions(
                m_handleRTSignatureExceptions);
        
        addAdvice(interceptor);
        
        TargetSource targetSource = createTargetSource(m_target);
        setTargetSource(targetSource);
        
        return createAopProxy().getProxy();
    }

    /**
     * {@inheritDoc}
     */
    public Class getObjectType() {
        Class clazz = null;
        if (m_target instanceof TargetSource) {
            clazz = ((TargetSource) m_target).getTargetClass();
        } else if (m_target != null) {
            clazz = m_target.getClass();
        }
        return clazz;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return m_singleton;
    }
    
    /**
     * Creates a TargetSource for the given object or just casts it, if it's
     * already a TargetSoucre.
     * 
     * @param target
     *      The object to wrap into a TargetSource.
     *      
     * @return Returns a TargetSource pointing to the given target parameter.
     */
    protected TargetSource createTargetSource(Object target) {
        if (target instanceof TargetSource) {
            return (TargetSource) target;
        } else {
            return new SingletonTargetSource(target);
        }
    }
    
    /**
     * @return Returns the exception handler interceptor that is installed in
     *      the proxy created by this factory.
     */
    protected abstract AbstractExceptionHandlerInterceptor
    createExceptionHandlerInterceptor();
}
