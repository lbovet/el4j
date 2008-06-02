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

package ch.elca.el4j.util.logging;

import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class is used to load a freely choosable log4j xml configuration file.
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
public class Log4jXmlLoader implements InitializingBean, BeanNameAware {
    /**
     * This is the location of the log4j configuration file.
     */
    private String m_configLocation;

    /**
     * This is the name of the bean.
     */
    private String m_beanName;

    /**
     * @return Returns the configLocation.
     */
    public String getConfigLocation() {
        return m_configLocation;
    }

    /**
     * @param configLocation
     *            The configLocation to set.
     */
    public void setConfigLocation(String configLocation) {
        Reject.ifEmpty(configLocation,
                "Config location of log4j loader must not be empty!");
        this.m_configLocation = configLocation.trim();
        System.setProperty("log4j.configuration", configLocation);
        URL url = Log4jXmlLoader.class.getClassLoader().getResource(
                m_configLocation);
        DOMConfigurator.configure(url);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasText(getConfigLocation())) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                    "configLocation", this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * @return Returns the beanName.
     */
    public String getBeanName() {
        return m_beanName;
    }
}