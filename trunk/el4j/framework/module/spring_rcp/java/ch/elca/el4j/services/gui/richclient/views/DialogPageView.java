/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.views;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.SpringLayoutUtils;
import org.springframework.util.Assert;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.AbstractPropertiesBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.action.ExecutorAction;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.pages.ExtendedApplicationPage;
import ch.elca.el4j.services.gui.richclient.utils.ComponentUtils;

/**
 * View built by using a dialog page. This is used to display a dialog page not
 * in a <code>JDialog</code> but in a view.
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
public class DialogPageView extends AbstractView
    implements ExecutorDisplayable, Messagable, PropertyChangeListener {
    /**
     * Is the id for the save command.
     */
    public static final String SAVE_COMMAND_ID = "saveCommand";

    /**
     * Is the id for the revert command.
     */
    public static final String REVERT_COMMAND_ID = "revertCommand";

    /**
     * Flag to indicate that this view has been configured.
     */
    protected boolean m_configured = false;
    
    /**
     * Is the save command.
     */
    protected ActionCommand m_saveCommand;
    
    /**
     * Is the revert command.
     */
    protected ActionCommand m_revertCommand;
    
    /**
     * Is the control on top to show the state of entered data. 
     */
    private TitlePane m_statePane;

    /**
     * Is the used dialog page.
     */
    private DialogPage m_dialogPage;

    /**
     * Is the execution action for this dialog.
     */
    private ExecutorAction m_executorAction;
    
    /**
     * Is the panel where the search components do take place.
     */
    private JPanel m_control;
    
    /**
     * @return Returns the state pane.
     */
    protected final TitlePane getStatePane() {
        if (m_statePane == null) {
            m_statePane = createDefaultStatePane();
        }
        return m_statePane;
    }

    /**
     * @return Returns a newly created default state pane.
     */
    protected TitlePane createDefaultStatePane() {
        return new TitlePane();
    }

    /**
     * @param statePane Is the state pane to set.
     */
    protected final void setStatePane(TitlePane statePane) {
        m_statePane = statePane;
    }

    /**
     * @param title Is the title to set in the state pane.
     */
    protected void setStatePaneTitle(String title) {
        getStatePane().setTitle(title);
    }

    /**
     * @param image Is the image to set in the state pane.
     */
    protected void setStatePaneImage(Image image) {
        getStatePane().setImage(image);
    }

    /**
     * @return Returns the message of the state pane.
     */
    public Message getMessage() {
        return getStatePane().getMessage();
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        TitlePane statePane = getStatePane();
        if (message == null || message.isEmpty()) {
            Message defaultMessage = null;
            DialogPage dialogPage = getDialogPage();
            if (dialogPage != null) {
                defaultMessage = new Message(dialogPage.getDescription());
            }
            if (defaultMessage == null) {
                defaultMessage = Message.EMPTY_MESSAGE;
            }
            statePane.setMessage(defaultMessage);
        } else {
            statePane.setMessage(message);
        }
    }
    
    /**
     * Uses the default message.
     */
    public void setDefaultMessage() {
        setMessage(null);
    }
    
    /**
     * {@inheritDoc}
     */
    protected JComponent createControl() {
        m_control = getComponentFactory().createPanel(new SpringLayout());
        m_control.add(getStatePaneComponent());
        Component dialogPageControl = getDialogPageComponent();
        ComponentUtils.addFocusListenerRecursivly(dialogPageControl, 
            new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                Component focusedComponent = e.getComponent();
                setLastFocusedComponent(focusedComponent);
            }
        });
        m_control.add(dialogPageControl);
        m_control.add(getButtonComponent());
        SpringLayoutUtils.makeCompactGrid(m_control, 
            m_control.getComponentCount(), 1, 0, 0, 0, 0);
        return m_control;
    }
    
    /**
     * @return Returns the component of the state pane.
     */
    protected JComponent getStatePaneComponent() {
        JPanel c = getComponentFactory().createPanel(new SpringLayout());
        setDefaultMessage();
        c.add(getStatePane().getControl());
        c.add(new JSeparator());
        c.setMaximumSize(c.getMinimumSize());
        SpringLayoutUtils.makeCompactGrid(
            c, c.getComponentCount(), 1, 0, 0, 0, 0);
        return c;
    }

    /**
     * @return Returns the component of the dialog page.
     */
    protected JComponent getDialogPageComponent() {
        DialogPage dialogPage = getDialogPage();
        Assert.notNull(dialogPage);
        JComponent c = dialogPage.getControl();
        c.setMaximumSize(c.getMinimumSize());
        GuiStandardUtils.attachDialogBorder(c);
        return c;
    }

    /**
     * @return Returns the button control element.
     */
    protected JComponent getButtonComponent() {
        m_saveCommand = new ActionCommand(getSaveCommandId()) {
            /**
             * {@inheritDoc}
             */
            protected void doExecuteCommand() {
                save();
            }
        };
        
        m_revertCommand = new ActionCommand(getRevertCommandId()) {
            /**
             * {@inheritDoc}
             */
            protected void doExecuteCommand() {
                revert();
            }
        };

        CommandGroup commandGroup = CommandGroup.createCommandGroup(null, 
            new AbstractCommand[] {m_saveCommand, m_revertCommand});
        JComponent buttonBar = commandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }
    
    /**
     * @return Returns the id for the save command.
     */
    protected String getSaveCommandId() {
        return SAVE_COMMAND_ID;
    }
    
    /**
     * @return Returns the id for the revert command.
     */
    protected String getRevertCommandId() {
        return REVERT_COMMAND_ID;
    }
    
    /**
     * Calls the executor action first.
     * 
     * {@inheritDoc}
     */
    public void componentOpened() {
        ExecutorAction action = getExecutorAction();
        action.onAboutToShow();
        super.componentOpened();
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentFocusGained() {
        super.componentFocusGained();
        if (isControlCreated()) {
            Component c = getLastFocusedComponent();
            if (c != null) {
                c.requestFocusInWindow();
            }
        }
    }
    
    /**
     * View is asked to save the current bean.
     */
    public void save() {
        if (isControlCreated()) {
            ExecutorAction action = getExecutorAction();
            try {
                action.onFinishOrConfirm();
            } catch (Exception e) {
                if (action.onFinishOrConfirmException(e)) {
                    ApplicationPage applicationPage = getContext().getPage();
                    if (applicationPage instanceof ExtendedApplicationPage) {
                        ExtendedApplicationPage extendedApplicationPage
                            = (ExtendedApplicationPage) applicationPage;
                        extendedApplicationPage.close(this);
                    }
                }
            }
        }
    }

    /**
     * View is asked to revert current bean.
     */
    public void revert() {
        ExecutorAction action = getExecutorAction();
        action.onRevertOrCancel();
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception { }

    /**
     * {@inheritDoc}
     */
    public void configure(AbstractBeanExecutor executor) {
        Assert.isInstanceOf(AbstractPropertiesBeanExecutor.class, executor);
        
        AbstractPropertiesBeanExecutor propertiesBeanExecutor 
            = (AbstractPropertiesBeanExecutor) executor;
        setExecutorAction(propertiesBeanExecutor);
        setDialogPage(propertiesBeanExecutor.getDialogPage());
        
        m_configured = true;
    }

    /**
     * @return Returns the dialogPage.
     */
    public final DialogPage getDialogPage() {
        return m_dialogPage;
    }

    /**
     * @param dialogPage Is the dialogPage to set.
     */
    public final void setDialogPage(DialogPage dialogPage) {
        m_dialogPage = dialogPage;
        if (dialogPage != null) {
            dialogPage.addPropertyChangeListener(this);
            updateStatePane();
        }
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
    public boolean isConfigured() {
        return m_configured;
    }

    /**
     * {@inheritDoc}
     */
    public void showDisplayable() { }

    /**
     * {@inheritDoc}
     */
    public Component getMainComponent() {
        return getControl();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getStatePane().addPropertyChangeListener(listener);
        super.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String propertyName, 
        PropertyChangeListener listener) {
        getStatePane().addPropertyChangeListener(propertyName, listener);
        super.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getStatePane().removePropertyChangeListener(listener);
        super.removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String propertyName, 
        PropertyChangeListener listener) {
        getStatePane().removePropertyChangeListener(propertyName, listener);
        super.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
//        if (Messagable.MESSAGE_PROPERTY.equals(evt.getPropertyName())) {
//            updateStatePane();
//        }
        if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(evt.getPropertyName())) {
            DialogPage dialogPage = getDialogPage();
            setEnabled(dialogPage.isPageComplete());
        } else {
            updateStatePane();
        }
    }

    /**
     * Will be called to update the state pane.
     *
     */
    protected void updateStatePane() {
        DialogPage dialogPage = getDialogPage();
        setStatePaneTitle(dialogPage.getTitle());
        setStatePaneImage(dialogPage.getImage());
        setMessage(dialogPage.getMessage());
    }
    
    /**
     * @param enabled Is the state this view must have.
     */
    protected void setEnabled(boolean enabled) {
        if (isControlCreated()) {
            m_saveCommand.setEnabled(enabled);
        }
    }

    /**
     * @return Returns <code>true</code> if the this view is enabled.
     */
    public boolean isEnabled() {
        if (isControlCreated()) {
            return m_saveCommand.isEnabled();
        } else {
            return false;
        }
    }
}
