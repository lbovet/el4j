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

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract bean executor.
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
public abstract class AbstractBeanExecutor extends AbstractActionCommandExecutor
    implements InitializingBean, BeanNameAware {
    /**
     * Is the id of this executor action.
     */
    private String m_id;
    
    /**
     * Is the id of the command.
     */
    private String m_commandId;

    /**
     * Is the presenter where this executor is used.
     */
    private BeanPresenter m_beanPresenter;
    
    /**
     * Name of this bean.
     */
    private String m_beanName;

    /**
     * Will be invoked if the executor should update its state.
     */
    public abstract void updateState();
    
    /**
     * The schema is used to specify displayed messages.
     * 
     * @return Returns the schema of this executor. 
     */
    public abstract String getSchema();
    
    /**
     * @return Returns the id.
     */
    public final String getId() {
        return m_id;
    }

    /**
     * @param id Is the id to set.
     */
    public final void setId(String id) {
        m_id = id;
    }
    
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
     * @return Returns the beanPresenter.
     */
    public final BeanPresenter getBeanPresenter() {
        return m_beanPresenter;
    }

    /**
     * @param beanPresenter The beanPresenter to set.
     */
    public final void setBeanPresenter(BeanPresenter beanPresenter) {
        m_beanPresenter = beanPresenter;
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
        if (!StringUtils.hasText(getId())) {
            setId(beanName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getCommandId(), "commandId", this);
    }
}
