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

package ch.elca.el4j.services.remoting.protocol;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;


/**
 * This is an abstract <code>InetSocketAddress</code> protocol.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractInetSocketAddressProtocol 
    extends AbstractRemotingProtocol {
    /**
     * This is the host where the service is installed.
     */
    private String m_serviceHost;

    /**
     * This is the port where the service is installed.
     */
    private int m_servicePort;

    /**
     * @return Returns the serviceHost.
     */
    public String getServiceHost() {
        return m_serviceHost;
    }

    /**
     * @param serviceHost
     *            The serviceHost to set.
     */
    public void setServiceHost(String serviceHost) {
        m_serviceHost = serviceHost;
    }

    /**
     * @return Returns the servicePort.
     */
    public int getServicePort() {
        return m_servicePort;
    }

    /**
     * @param servicePort
     *            The servicePort to set.
     */
    public void setServicePort(int servicePort) {
        m_servicePort = servicePort;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                getServiceHost(), "serviceHost", this);
        if (getServicePort() <= 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The property 'servicePort' is required.");
        }
    }
    
    /**
     * Method to generate the url to be able to access the service.
     * 
     * @param remotingBase
     *            Is the reference to get information about the service.
     * @return Returns the generated service url.
     */
    public abstract String generateUrl(AbstractRemotingBase remotingBase);
}