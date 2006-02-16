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

package ch.elca.el4j.services.monitoring.jmx;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Adaptation of the "MBeanServerFactoryBean" class. The MBean Server is
 * identified via name. If an MBean Server already exists, it will be returned,
 * otherwise, a new one will be created.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class MBeanServerFactoryBean implements FactoryBean,
    InitializingBean {

    /**
     * Hold the MBeanServer.
     */
    private MBeanServer m_server = null;

    /**
     * The default domain used by the MBeanServer.
     */
    private String m_defaultDomain = "defaultDomain";

    /**
     * Returns the default domain used by the MBeanServer.
     * 
     * @return the domain name
     */
    public String getDefaultDomain() {
        return m_defaultDomain;
    }

    /**
     * Set the default domain to be used by the MBeanServer. Must be set before
     * the MBeanServer is created, that is before afterPropertiesSet() is
     * called.
     * 
     * @param defaultDomain
     *            the domain name to use
     */
    public void setDefaultDomain(String defaultDomain) {
        this.m_defaultDomain = defaultDomain;
    }

    /**
     * Convenience method to retrieve the MBean Server without the need to cast.
     * 
     * @return the MBean Server
     */
    public MBeanServer getServer() {
        return m_server;
    }

    /**
     * Indicates the type of Object returned by this factory bean.
     * 
     * @return always MBeanServer
     */
    public Class getObjectType() {
        return MBeanServer.class;
    }

    /**
     * Returns the MBeanServer instance.
     * 
     * @return the MBeanServer instance
     */
    public Object getObject() {
        return this.m_server;
    }

    /**
     * Indicates that the MBeanServer returned by this method is a singleton.
     * 
     * @return always true
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Looks up or creates the MBeanServer instance.
     * 
     * @throws Exception
     *             in case the initialization was not successful
     */
    public void afterPropertiesSet() throws Exception {

        ArrayList al = MBeanServerFactory.findMBeanServer(null);

        int i = 0;
        boolean found = false;
        MBeanServer mBeanServer;

        // Look up if there is already an MBean Server for this domain.
        while (i < al.size() && !found) {
            mBeanServer = (MBeanServer) al.get(i);
            if (mBeanServer.getDefaultDomain().equals(getDefaultDomain())) {
                found = true;
                m_server = mBeanServer;
            }
            i++;
        }

        // If there is no MBean Server yet for this domain, then create one.
        if (!found) {
            m_server = MBeanServerFactory.createMBeanServer(getDefaultDomain());
        }
    }
}