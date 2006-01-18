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

package ch.elca.el4j.services.remoting.protocol;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.remoting.caucho.BurlapProxyFactoryBean;
import org.springframework.remoting.caucho.BurlapServiceExporter;

import ch.elca.el4j.services.remoting.AbstractRemotingBase;
import ch.elca.el4j.services.remoting.RemotingProxyFactoryBean;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This class implements all needed things for the burlap protocol.
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
public class Burlap extends AbstractInetSocketAddressWebProtocol {
    /**
     * {@inheritDoc}
     */
    public Object createProxyBean(RemotingProxyFactoryBean proxyBean,
            Class serviceInterfaceWithContext) {
        StaticApplicationContext appContext = new StaticApplicationContext(
                m_parentApplicationContext);
        MutablePropertyValues proxyProps = new MutablePropertyValues();
        proxyProps.addPropertyValue("serviceInterface",
                serviceInterfaceWithContext);
        proxyProps.addPropertyValue("serviceUrl", generateUrl(proxyBean));
        appContext.registerSingleton("burlapProxyBeanGen",
                getProxyObjectType(), proxyProps);

        return appContext.getBean("burlapProxyBeanGen");
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
        appContext.registerSingleton("burlapExporterBeanGen",
                getExporterObjectType(), props);
        return appContext.getBean("burlapExporterBeanGen");
    }

    /**
     * {@inheritDoc}
     */
    public Class getProxyObjectType() {
        return BurlapProxyFactoryBean.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class getExporterObjectType() {
        return BurlapServiceExporter.class;
    }

    /**
     * {@inheritDoc}
     */
    public String generateUrl(AbstractRemotingBase remoteBase) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://");
        sb.append(getServiceHost());
        sb.append(":");
        sb.append(getServicePort());
        sb.append("/");
        sb.append(getContextPath());
        sb.append("/");
        sb.append(remoteBase.getServiceName());
        return sb.toString();
    }
}