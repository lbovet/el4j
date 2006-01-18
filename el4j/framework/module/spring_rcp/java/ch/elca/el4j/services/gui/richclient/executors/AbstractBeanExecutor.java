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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

import ch.elca.el4j.services.gui.richclient.views.AbstractBeanView;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract bean executor.
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
public abstract class AbstractBeanExecutor extends AbstractActionCommandExecutor
    implements InitializingBean, BeanNameAware, ApplicationContextAware {
    
    /**
     * Is the id of the command.
     */
    private String m_commandId;

    /**
     * Is the view where this executor is used.
     */
    private AbstractBeanView m_beanView;

    /**
     * Name of this bean.
     */
    private String m_beanName;

    /**
     * Is the application context this bean was created with.
     */
    private ApplicationContext m_applicationContext;

    /**
     * Will be invoked if the executor should update its state.
     */
    public abstract void updateState();
    
    /**
     * @return Returns the commandId.
     */
    public String getCommandId() {
        return m_commandId;
    }

    /**
     * @param commandId The commandId to set.
     */
    public void setCommandId(String commandId) {
        m_commandId = commandId;
    }

    /**
     * @return Returns the beanView.
     */
    public final AbstractBeanView getBeanView() {
        return m_beanView;
    }

    /**
     * @param beanView The beanView to set.
     */
    public final void setBeanView(AbstractBeanView beanView) {
        m_beanView = beanView;
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

    /**
     * {@inheritDoc}
     */
    public final void setApplicationContext(
        ApplicationContext applicationContext) 
        throws BeansException {
        m_applicationContext = applicationContext;
    }
    
    /**
     * @return Returns the application context this bean was made with.
     */
    public final ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getCommandId(), "commandId", this);
    }
}
