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
package ch.elca.el4j.services.daemonmanager.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ch.elca.el4j.services.daemonmanager.DaemonFactory;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Default implementation for a daemon factory.
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
public class DaemonFactoryImpl implements DaemonFactory, InitializingBean, 
    BeanNameAware, ApplicationContextAware {
    /**
     * Is the bean name of the daemons to create.
     */
    private String m_daemonBeanName;
    
    /**
     * Is the number of daemons to create. Default is set to one.
     */
    private int m_numberOfDaemons = 1;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * Is the application context this bean was created with.
     */
    private ApplicationContext m_applicationContext;

    /**
     * {@inheritDoc}
     */
    public List getDaemons() {
        List daemons = new ArrayList();
        for (int i = 1; i <= getNumberOfDaemons(); i++) {
            AbstractDaemon newDaemon = (AbstractDaemon) getApplicationContext()
                .getBean(getDaemonBeanName());
            newDaemon.setIdentification(
                newDaemon.getIdentification() + " #" + i);
            daemons.add(newDaemon);
        }
        return daemons;
    }

    /**
     * @return Returns the bean name of the daemons to create.
     */
    public String getDaemonBeanName() {
        return m_daemonBeanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDaemonBeanName(String daemonBeanName) {
        m_daemonBeanName = daemonBeanName;
    }
    
    /**
     * @return Returns the number of daemons to create.
     */
    public int getNumberOfDaemons() {
        return m_numberOfDaemons;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNumberOfDaemons(int numberOfDaemons) {
        m_numberOfDaemons = numberOfDaemons;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (getApplicationContext() == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Bean must be loaded with an application context.");
        }
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getDaemonBeanName(), "daemonBeanName", this);
        if (getNumberOfDaemons() < 1) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Property 'numberOfDaemons' of daemon factory '"
                + getBeanName() + "' must be greater than zero.");
        }
        if (!(getApplicationContext().containsBean(getDaemonBeanName()))) {
            CoreNotificationHelper.notifyMisconfiguration(
                "There is no daemon bean with name '" 
                + getDaemonBeanName() + "'.");
        }
        if (getApplicationContext().isSingleton(getDaemonBeanName())) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Daemon bean with name '" + getDaemonBeanName() 
                + "' must not be singleton.");
        }
        if (!AbstractDaemon.class.isAssignableFrom(
            getApplicationContext().getType(getDaemonBeanName()))) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Daemon bean with name '" + getDaemonBeanName() 
                + "' must extend class '" 
                + AbstractDaemon.class.getName() + "'.");
        }
    }

    /**
     * @return Returns the application context this bean was created with.
     */
    public final ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setApplicationContext(
        ApplicationContext applicationContext) throws BeansException {
        m_applicationContext = applicationContext;
    }

    /**
     * @return Returns the name of this bean.
     */
    public final String getBeanName() {
        return m_beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setBeanName(String beanName) {
        m_beanName = beanName;
    }
}
