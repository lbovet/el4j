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

import java.lang.reflect.Proxy;
import java.util.Map;

import org.springframework.aop.framework.ProxyFactoryBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

import ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.AbstractPolicy;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.RoundRobinPolicy;

/**
 * This class implements a load balancer on the protocol level. It composes
 * multiple protocols and selects a particular protocol instance according to
 * some policy in order to perform the remote call. This policy can be passed
 * as a configuration parameter -- random policy is used as default policy.
 * 
 * <p>
 * Upon failure in the first instantiation of protocol proxies the load balancer
 * automatically tries another protocol. Once a protocol is successfully proxied
 * the load balancer behaves similarly to any protocol instance. In particular,
 * connection exceptions are escalated to the user.  
 *
 * <script type="text/javascript">printFileStatus ("$URL$", "$Revision$",
 * "$Date$", "$Author$" );</script>
 * 
 * @author Stefan Pleisch (SPL)
 * @see ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.AbstractPolicy
 * @see ch.elca.el4j.services.remoting.protocol.loadbalancing.policy.LoadBalancingProtcolConfiguration
 * @see ch.elca.el4j.services.remoting.AbstractRemotingProtocol
 */
public class LoadBalancingProtocol extends AbstractRemotingProtocol {

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(getProtocolSpecificConfiguration(),
            "protocolSpecificConfiguration", this);
        if (!(getProtocolSpecificConfiguration() instanceof LoadBalancingProtocolConfiguration)) {
            CoreNotificationHelper.notifyMisconfiguration("The configuration " +
            "needs to be of type " + LoadBalancingProtocolConfiguration.class) ;
        } // if
   } // afterPropertiesSet()

    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
        Class serviceInterfaceWithContext) {

        // Get the context class loader from current thread.
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        LoadBalancingProtocolConfiguration conf = 
         (LoadBalancingProtocolConfiguration)getProtocolSpecificConfiguration();
        m_protocols = conf.getProtocols();
        m_policy = conf.getPolicy();
        if (m_policy == null) {
            // Install default policy
            m_policy = new RoundRobinPolicy();
        } // if
        m_policy.setProtocols(m_protocols);

        // Create invocation handler
        if (m_invocationHandler == null) {
            m_invocationHandler = new ClientLoadBalancingInvocationHandler(
                m_policy, proxyBean,
                serviceInterfaceWithContext);
        } // if

        return Proxy.newProxyInstance(cl,
            new Class[] {serviceInterfaceWithContext}, m_invocationHandler);

    } // createProxyBean()

    /**
     * {@inheritDoc}
     */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
        Class serviceInterfaceWithContext, Object serviceProxy) {
        throw new RuntimeException("No exporter of this type can be created.");
    } // createExporterBean()

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return ProxyFactoryBean.class;
    } // getProxyObjectType()

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        throw new RuntimeException(this.getClass().getName()
            + " cannot be used as an exporter");
    } // getExporterObjectType() ;


    /**
     * Stores temporarily the config input. Is modified upon creation of
     * ProtocolInfo instances, due to performance reasons.
     */
    private AbstractRemotingProtocol[] m_protocols;

    /** Defines the protocol selection policy. */
    private AbstractPolicy m_policy ;

    /** The invocation handler for the proxy installed by the current class. */
    private ClientLoadBalancingInvocationHandler m_invocationHandler;

} // CLASS LoadBalancingProtocol
