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

package ch.elca.el4j.services.remoting.protocol.loadbalancing.protocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.RemoteAccessException;

import ch.elca.el4j.services.persistence.generic.primarykey.PrimaryKeyGenerator;
import ch.elca.el4j.services.persistence.generic.primarykey.UuidPrimaryKeyGenerator;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.protocol.AbstractInetSocketAddressProtocol;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.NoProtocolAvailableRTException;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.AbstractPolicy;


/**
 * Manages the different protocols and connection to different servers. The
 * selection of a particular protocol for a particular invocation is based on
 * the policy defined by an implementation of
 * 
 * @{link ch.elca.el4j.loadbalancing.policy.AbstractPolicy}.
 *        
 * <script type="text/javascript">printFileStatus
*   ("$URL$",
    *    "$Revision$",
    *    "$Date$",
    *    "$Author$"
    * );</script>
 *        
 * @author Stefan Pleisch (SPL)
 */
public class ClientLoadBalancingInvocationHandler implements InvocationHandler,
    ApplicationContextAware {

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
        .getLog(ClientLoadBalancingInvocationHandler.class);

    private Object m_currentProtocolProxy;

    /** Defines the protocol selection policy. */
    private AbstractPolicy m_policy;

    private Class m_serviceInterfaceWithContext;
    
    private RemotingProxyFactoryBean m_proxyBean;

    /** Stores the previously loaded proxies to protocols. */
    private ProtocolProxyStore m_protocolProxyStore;

    /** Used to generate unique keys */
    private PrimaryKeyGenerator m_uniqueKeyGenerator;
    
    /**
     * Stores references to the instantiated protocols. The comparison is done
     * using "==" rather than the usual "equals" method. Thus, this class
     * implements its own simple data structures and cannot use predefined
     * structures such as Hashtable.
     */
    private static class ProtocolProxyStore {

        /** Array of protocols. */
        private AbstractRemotingProtocol[] m_protocols;
        
        /** Array of protocol proxies. */
        private Object[] m_protocolProxies;
        
        /**
         * Constructor.
         */
        public ProtocolProxyStore() { }

        /**
         * @param protocol
         *            ProtocolInfo of the required protocol proxy bean
         * @return The protocol proxy bean associated with pi
         */
        public Object retrieve(AbstractRemotingProtocol protocol) {
            if (m_protocols == null) {
                return null;
            } // if
            int index = getProtocolIndex(protocol);
            if (index < 0) {
                return null;
            } else {
                return m_protocolProxies[index];
            } 
        }

        /**
         * Stores the protocol proxy bean under its protocol information.
         * 
         * @param protocol
         *            protocol information of the proxy
         * @param proxy
         *            Proxy bean itself
         */
        public void store(AbstractRemotingProtocol protocol, Object proxy) {
            if (m_protocols == null) {
                m_protocols = new AbstractRemotingProtocol[1];
                m_protocolProxies = new Object[1];
                m_protocols[0] = protocol;
                m_protocolProxies[0] = proxy;
            } else {

                int index = getProtocolIndex(protocol);

                if (index < 0) {
                    AbstractRemotingProtocol[] tmp 
                        = new AbstractRemotingProtocol[m_protocols.length + 1];
                    Object[] tmpProxies = new Object[m_protocols.length + 1];
                    for (int i = 0; i < m_protocols.length; i += 1) {
                        tmp[i] = m_protocols[i];
                        tmpProxies[i] = m_protocolProxies[i];
                    }
                    tmp[m_protocols.length] = protocol;
                    tmpProxies[m_protocols.length] = proxy;
                    m_protocols = tmp;
                    m_protocolProxies = tmpProxies;
                }
            }
        }

        /**
         * Removes 'protocol' from storage. Does nothing if 'protocol' does not
         * exist
         * @param protocol The protocol to be removed.
         */
        public void remove(AbstractRemotingProtocol protocol) {
            if (m_protocols == null) {
                return;
            } else {

                int index = getProtocolIndex(protocol);

                if (index >= 0) {
                    AbstractRemotingProtocol[] tmp 
                        = new AbstractRemotingProtocol[m_protocols.length - 1];
                    Object[] tmpProxies = new Object[m_protocols.length - 1];
                    for (int i = 0; i < index; i += 1) {
                        tmp[i] = m_protocols[i];
                        tmpProxies[i] = m_protocolProxies[i];
                    }
                    for (int k = (index + 1); k < m_protocols.length; k += 1) {
                        tmp[k - 1] = m_protocols[k];
                        tmpProxies[k - 1] = m_protocolProxies[k];
                    }
                    m_protocols = tmp;
                    m_protocolProxies = tmpProxies;
                }
            }
        }
        
        
        private int getProtocolIndex(AbstractRemotingProtocol protocol) {
            boolean found = false;
            int index = -1;
            for (int i = 0; !found && (i < m_protocols.length); i += 1) {
                if (m_protocols[i] == protocol) {
                    found = true;
                    index = i;
                }
            }
            return index;
        }
    }

    /**
     * @param protocols
     * @param policy
     * @param proxyBean
     * @param serviceInterfaceWithContext
     */
    public ClientLoadBalancingInvocationHandler(AbstractPolicy policy,
        RemotingProxyFactoryBean proxyBean, Class serviceInterfaceWithContext) {
        m_proxyBean = proxyBean;
        m_policy = policy;
        m_serviceInterfaceWithContext = serviceInterfaceWithContext;
        m_protocolProxyStore = new ProtocolProxyStore();

        m_uniqueKeyGenerator = new UuidPrimaryKeyGenerator();
        // loadCurrentProtocol() ;
    }

    /** Returns the currently used protocol. */
    // public AbstractInetSocketAddressProtocol getCurrentProtocol() {
    // return m_currentProtocol ;
    // } // getCurrentProtocol()
    // //////////////////// From ApplicationContextAware /////////////////////
    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
    } // setApplicationContext()

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

        AbstractRemotingProtocol protocol = null;
        int attemptCount = 0;
        boolean proxyGenerationSucceeded = false;
        // Try each protocol at most once
        int protocolCount = m_policy.getProtocolCount();
        while ((!proxyGenerationSucceeded) && (attemptCount < protocolCount)) {
            try {

                protocol = m_policy.getNextProtocol();
                s_logger.debug("Attempting to connect to: " + protocol 
                    + " at host:port=" 
                    + ((AbstractInetSocketAddressProtocol) protocol)
                    .getServiceHost()
                    + ":" + ((AbstractInetSocketAddressProtocol) protocol)
                    .getServicePort());
                m_currentProtocolProxy = loadCurrentProtocol(protocol);
                proxyGenerationSucceeded = true;
            } catch (NoProtocolAvailableRTException npae) {
                s_logger.debug("No more protocols available, stop trying ...");
                throw npae;
            } catch (BeanCreationException bce) {
                s_logger.debug("Exception occurred:" + bce.getMessage());

                bce.printStackTrace();
                // throw e.getTargetException();
                notifyFailure(protocol);
                // Retry
            } 
            attemptCount += 1;
        } 

        if (!proxyGenerationSucceeded) {
            // If arrived here -> problem
            throw new NoProtocolAvailableRTException("Found no available "
                + "target, attemps exhausted.");
        } 

        Object result = null;
        try {
            result = method.invoke(m_currentProtocolProxy, args);
            return result;
        } catch (InvocationTargetException ite) {
            // If invocation failed due to connection problems, report those to
            // the policy component.
            if (ite.getTargetException() instanceof RemoteAccessException) {
                s_logger.debug("Expected exception: " + ite.getMessage());
                notifyFailure(protocol);
            }

            // Rethrow exception to mirror normal protocol behavior
            throw ite.getTargetException();
        } 
    }

   

    /**
     * Creates a proxy to the remoting protocol passed as an argument.
     * 
     * @param protocol
     *            The protocol to be proxied.
     * @return Proxy to 'protocol'
     */
    private Object getProtocolProxy(AbstractRemotingProtocol protocol) {
        return protocol.createProxyBean(m_proxyBean,
            m_serviceInterfaceWithContext);
    } // getCurrentProtocolProxy()

    /**
     * Retrieves new protocol information from the policy and loads, if needed,
     * the corresponding protocols. Side-effects: modifies m_currentProtocol,
     * m_currentProtocolProxy, m_protocolProxyStore
     * 
     * @param protocol
     *            Defines the protocol to be loaded
     */
    private Object loadCurrentProtocol(AbstractRemotingProtocol protocol) {
        Object currentProtocolProxyTmp = m_protocolProxyStore
            .retrieve(protocol);
        if (currentProtocolProxyTmp == null) {
            currentProtocolProxyTmp = getProtocolProxy(protocol);
            m_protocolProxyStore.store(protocol, currentProtocolProxyTmp);
        } // if
        return currentProtocolProxyTmp;
    } // loadCurrentProtocol()

    /** 
     * Takes care of notifying all concerned entities of the failure of a
     * particular protocol.
     * @param protocol The protocol whose usage caused a failure
     */
    private void notifyFailure(AbstractRemotingProtocol protocol) {
        // Notify the policy component of the failure
        m_policy.notifyFailure(protocol);
        // Remove the proxy from the protocol proxy store
        m_protocolProxyStore.remove(protocol);
    } // notifyFailure()
    
} // CLASS ClientLoadBalancingInvocationHandler
