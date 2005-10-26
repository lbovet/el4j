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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This class implements all needed things for the rmi protocol.
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
public class Rmi extends AbstractInetSocketAddressProtocol {

    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceWithContext) {
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues props = new MutablePropertyValues();
        props.addPropertyValue("serviceInterface", serviceInterfaceWithContext);
        props.addPropertyValue("serviceUrl", generateUrl(proxyBean));
        appContext.registerSingleton("rmiProxyBeanGen", getProxyObjectType(),
                props);
        return appContext.getBean("rmiProxyBeanGen");
    }

    /**
     * {@inheritDoc}
     */
    public Object createExporterBean(RemotingServiceExporter exporterBean,
            Class serviceInterfaceWithContext, Object serviceProxy) {
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues props = new MutablePropertyValues();
        props.addPropertyValue("service", serviceProxy);
        props.addPropertyValue("serviceInterface", serviceInterfaceWithContext);
        props.addPropertyValue("serviceName", exporterBean.getServiceName());
        props.addPropertyValue("registryPort", new Integer(getServicePort()));

        appContext.registerSingleton("rmiExporterBeanGen",
                getExporterObjectType(), props);
        return appContext.getBean("rmiExporterBeanGen");
    }

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return RmiProxyFactoryBean.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return RmiServiceExporter.class;
    }

    /**
     * {@inheritDoc}
     */
    public String generateUrl(AbstractRemotingBase remoteManager) {
        StringBuffer sb = new StringBuffer();
        sb.append("rmi://");
        sb.append(getServiceHost());
        sb.append(":");
        sb.append(getServicePort());
        sb.append("/");
        sb.append(remoteManager.getServiceName());
        return sb.toString();
    }
}