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

package ch.elca.el4j.services.remoting;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class is used to manage the given remote protocol.
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
public abstract class AbstractRemotingBase implements InitializingBean {
    /**
     * Suffix of service name if it is generated. This is needed to be able to
     * filter incomming requests, i.e. in the web.xml.
     */
    public static final String SERVICE_NAME_SUFFIX = ".remoteservice";
    
    /**
     * This is the used remote protocol.
     */
    private AbstractRemotingProtocol m_remoteProtocol;

    /**
     * Interface to show to the outside.
     */
    private Class m_serviceInterface;

    /**
     * Optional property. It is possible to set the name of the used service
     * manually, otherwise it will be generated automatically.
     */
    private String m_serviceName;
    
    /**
     * This member contains protocol specific configuration. This will only be 
     * used if it is really necessary.
     */
    private ProtocolSpecificConfiguration m_protocolSpecificConfiguration;

    /**
     * @return Returns the remoteProtocol.
     */
    public AbstractRemotingProtocol getRemoteProtocol() {
        return m_remoteProtocol;
    }

    /**
     * @param remoteProtocol
     *            The remoteProtocol to set.
     */
    public void setRemoteProtocol(AbstractRemotingProtocol remoteProtocol) {
        m_remoteProtocol = remoteProtocol;
    }

    /**
     * @return Returns the serviceInterface.
     */
    public Class getServiceInterface() {
        return m_serviceInterface;
    }

    /**
     * @param serviceInterface
     *            The serviceInterface to set.
     */
    public void setServiceInterface(Class serviceInterface) {
        m_serviceInterface = serviceInterface;
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        if (!StringUtils.hasText(m_serviceName)) {
            m_serviceName = m_serviceInterface.getName() + SERVICE_NAME_SUFFIX; 
        }
        return m_serviceName;
    }

    /**
     * @param serviceName
     *            The serviceName to set.
     */
    public void setServiceName(String serviceName) {
        m_serviceName = serviceName;
    }

    /**
     * @return Returns the protocolSpecificConfiguration.
     */
    public ProtocolSpecificConfiguration getProtocolSpecificConfiguration() {
        return m_protocolSpecificConfiguration;
    }

    /**
     * @param protocolSpecificConfiguration
     *            The protocolSpecificConfiguration to set.
     */
    public void setProtocolSpecificConfiguration(
        ProtocolSpecificConfiguration protocolSpecificConfiguration) {
        m_protocolSpecificConfiguration = protocolSpecificConfiguration;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                getRemoteProtocol(), "remoteProtocol", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                getServiceInterface(), "serviceInterface", this);
    }
}