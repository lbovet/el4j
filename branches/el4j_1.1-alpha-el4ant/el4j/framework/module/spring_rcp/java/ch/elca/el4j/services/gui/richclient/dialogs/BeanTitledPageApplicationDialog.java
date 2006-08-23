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
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.AbstractEditorBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.action.ExecutorAction;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;

/**
 * Application dialog with title used for beans.
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
public class BeanTitledPageApplicationDialog
    extends TitledPageApplicationDialog implements ExecutorDisplayable {
    /**
     * Flag to indicate that this dialog has been configured.
     */
    protected boolean m_configured = false;
    
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
        setEnabled(getDialogPage().isPageComplete());
    }

    /**
     * {@inheritDoc}
     */
    protected boolean onFinish() {
        boolean actionCompleted = false;
        ExecutorAction action = getExecutorAction();
        try {
            actionCompleted = action.onFinishOrConfirm();
        } catch (Exception e) {
            actionCompleted 
                = action.onFinishOrConfirmException(e);
        }
        return actionCompleted;
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
     */
    public void configure(AbstractBeanExecutor executor) {
        Assert.isInstanceOf(AbstractEditorBeanExecutor.class, executor);
        
        AbstractEditorBeanExecutor propertiesBeanExecutor 
            = (AbstractEditorBeanExecutor) executor;
        setExecutorAction(propertiesBeanExecutor);
        setDialogPage(propertiesBeanExecutor.getDialogPage());
        
        BeanPresenter beanPresenter = propertiesBeanExecutor.getBeanPresenter();
        if (beanPresenter instanceof PageComponent) {
            PageComponent pageComponent = (PageComponent) beanPresenter;
            setParent(pageComponent.getContext().getWindow().getControl());
        }
        
        // Set the dialog title.
        String title 
            = MessageUtils.getMessage(propertiesBeanExecutor.getId(), "title");
        if (!StringUtils.hasText(title)) {
            title = getDialogPage().getTitle();
        }
        setTitle(title);
        m_configured = true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isConfigured() {
        return m_configured;
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
