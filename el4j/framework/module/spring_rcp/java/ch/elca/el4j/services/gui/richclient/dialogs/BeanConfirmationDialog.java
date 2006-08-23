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
package ch.elca.el4j.services.gui.richclient.dialogs;

import java.awt.Component;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.util.Assert;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.AbstractConfirmBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.action.ExecutorAction;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.i18n.MessageProvider;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;
import ch.elca.el4j.util.dom.reflect.EntityType;

/**
 * Confirmation dialog used for beans.
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
public class BeanConfirmationDialog extends ConfirmationDialog
    implements ExecutorDisplayable {
    /**
     * Flag to indicate if the yes button should be used as default. Otherwise
     * the no button is set as default.
     */
    private boolean m_yesDefault = true;

    /**
     * Is the execution action for this dialog.
     */
    private ExecutorAction m_executorAction;
    
    /**
     * {@inheritDoc}
     */
    protected void onAboutToShow() {
        ExecutorAction action = getExecutorAction();
        action.onAboutToShow();
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onConfirm() {
        ExecutorAction action = getExecutorAction();
        try {
            action.onFinishOrConfirm();
        } catch (Exception e) {
            action.onFinishOrConfirmException(e);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onFinishException(Exception e) {
        ExecutorAction action = getExecutorAction();
        action.onFinishOrConfirmException(e);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onCancel() {
        ExecutorAction action = getExecutorAction();
        action.onRevertOrCancel();
        super.onCancel();
    }
    
    /**
     * @return Returns the executorAction.
     */
    public final ExecutorAction getExecutorAction() {
        return m_executorAction;
    }

    /**
     * @param executorAction Is the executorAction to set.
     */
    public final void setExecutorAction(ExecutorAction executorAction) {
        m_executorAction = executorAction;
    }
    
    /**
     * {@inheritDoc}
     * 
     * If property <code>yesDefault</code> is <code>true</code>, the yes button
     * will be used as default, if <code>false</code> the no button.
     */
    protected void registerDefaultCommand() {
        if (isControlCreated()) {
            if (isYesDefault()) {
                getFinishCommand().setDefaultButtonIn(getDialog());
            } else {
                getCancelCommand().setDefaultButtonIn(getDialog());
            }
        }
    }

    /**
     * @return Returns the yesDefault.
     */
    public final boolean isYesDefault() {
        return m_yesDefault;
    }

    /**
     * @param yesDefault The yesDefault to set.
     */
    public final void setYesDefault(boolean yesDefault) {
        m_yesDefault = yesDefault;
    }
    
    /**
     * {@inheritDoc}
     */
    @ImplementationAssumption(
        "Super types of entity types are not entity types themselves.")
    public void configure(AbstractBeanExecutor executor) {
        Assert.isInstanceOf(AbstractConfirmBeanExecutor.class, executor);
        
        AbstractConfirmBeanExecutor confirmBeanExecutor
            = (AbstractConfirmBeanExecutor) executor;
        
        BeanPresenter beanPresenter = confirmBeanExecutor.getBeanPresenter();
        Object[] beans = beanPresenter.getSelectedBeans();
        Reject.ifNull(beans, 
            "Can not configure dialog without any selected bean!");
        
        // Sets the executor action.
        setExecutorAction(confirmBeanExecutor);
        
        // Set the parent component.
        if (beanPresenter instanceof PageComponent) {
            PageComponent pageComponent = (PageComponent) beanPresenter;
            setParent(pageComponent.getContext().getWindow().getControl());
        }
        
        // Sets the title and confirmation message on this dialog.        
        MessageProvider.Fetcher msgs 
            = MessageProvider.instance().forConfirmation(
                confirmBeanExecutor.getId(),
                EntityType.get(beans[0].getClass()),
                beans.length
            );
        setConfirmationMessage(msgs.get("message"));
        setTitle(msgs.get("title"));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConfigured() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void showDisplayable() {
        showDialog();
    }

    /**
     * {@inheritDoc}
     */
    public Component getMainComponent() {
        return getDialog();
    }
}
