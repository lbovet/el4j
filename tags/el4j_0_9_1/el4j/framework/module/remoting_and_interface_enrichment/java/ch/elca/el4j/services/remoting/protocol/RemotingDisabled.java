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

package ch.elca.el4j.services.remoting.protocol;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * Empty protocol to simulate protocols. Just the implicit context passing will be enabled.
 * Client and server must be in same JVM.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class RemotingDisabled extends AbstractRemotingProtocol {

    /**
     * Map to cache service objects.
     */
    private Map m_serviceObjects = new HashMap();

    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceWithContext) {
        
        Object result = null;
        
        String serviceName = proxyBean.getServiceName();
        if (StringUtils.hasText(serviceName)) {
            Object serviceObject = m_serviceObjects.get(serviceName);
            if (serviceObject == null) {
                CoreNotificationHelper.notifyMisconfiguration(
                        "There is no service for service name '" + serviceName
                                + "'. Be sure that server side is started "
                                + "before client side.");
            } else {
                result = serviceObject;
            }
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                    "Service name can not be null or empty!");
        }
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy) {
        String serviceName = exporterBean.getServiceName();
        if (StringUtils.hasText(serviceName)) {
            m_serviceObjects.put(serviceName, serviceProxy);
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                    "Service name can not be null or empty!");
        }
        return serviceProxy;
    }

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return Object.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return Object.class;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        // No properties exits for this protocol.
    }
}
