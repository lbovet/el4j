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

package ch.elca.el4j.services.remoting.protocol.ejb;

import javax.ejb.EJBObject;
import javax.naming.NamingException;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Convenience factory for remote EJB session proxies.
 * 
 * <p/>Copied from
 * {@link org.springframework.ejb.access.SimpleRemoteSlsbInvokerInterceptor}.
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
public abstract class AbstractRemoteSessionProxyFactoryBean extends
        AbstractRemoteSessionBeanInvokerInterceptor
        implements FactoryBean {

    /** The business interface of the EJB we're proxying. */
    private Class m_businessInterface;
    
    /** The EJBObject. */
    private Object m_proxy;
    
    /**
     * Set the business interface of the EJB we're proxying.
     * This will normally be a super-interface of the EJB remote component
     * interface. Using a business methods interface is a best practice when
     * implementing EJBs. <p>You can also specify a matching non-RMI business
     * interface, i.e. an interface that mirrors the EJB business methods but
     * does not declare RemoteExceptions. In this case, RemoteExceptions thrown
     * by the EJB stub will automatically get converted to Spring's generic
     * RemoteAccessException.
     * 
     * @param businessInterface
     *      The business interface of the EJB.
     */
    public void setBusinessInterface(Class businessInterface) {
        this.m_businessInterface = businessInterface;
    }

    /**
     * @return Returns the business interface of the EJB we're proxying.
     */
    public Class getBusinessInterface() {
        return m_businessInterface;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_businessInterface, "businessInterface", this);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(this.m_businessInterface);
        proxyFactory.addInterface(EJBObject.class);
        proxyFactory.addAdvice(this);
        this.m_proxy = proxyFactory.getProxy();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getObject() {
        return this.m_proxy;
    }
    
    /**
     * {@inheritDoc}
     */
    public Class getObjectType() {
        return (this.m_proxy != null)
            ? this.m_proxy.getClass() : this.m_businessInterface;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

}
