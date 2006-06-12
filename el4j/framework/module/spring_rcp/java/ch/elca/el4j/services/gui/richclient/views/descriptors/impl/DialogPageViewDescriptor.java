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
package ch.elca.el4j.services.gui.richclient.views.descriptors.impl;

import java.awt.Component;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.support.ShowViewCommand;
import org.springframework.util.Assert;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.AbstractGroupPageComponentDescriptor;
import ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.views.DialogPageView;

/**
 * View descriptor for dialog pages. This special descriptor is made
 * for only one view.
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
public class DialogPageViewDescriptor extends AbstractViewDescriptor 
                                      implements ExecutorDisplayable {
    /**
     * Is the default of this descriptor.
     */
    public static final String DEFAULT_GROUP = "dialogGroup";
    
    /**
     * Flag to indicate that this descriptor has been configured.
     */
    protected boolean m_configured = false;
    
    /**
     * Is the page where this descriptor is used.
     */
    protected ApplicationPage m_applicationPage;
    
    /**
     * Is the view this descriptor belongs to.
     */
    private DialogPageView m_dialogPageView;

    /**
     * Constructor.
     */
    public DialogPageViewDescriptor() {
        setPreferredGroup(DEFAULT_GROUP);
    }
    
    /**
     * {@inheritDoc}
     */
    public PageComponent createPageComponent() {
        return getDialogPageView();
    }
    
    /**
     * @return Returns the singleton dialog page view.
     */
    public DialogPageView getDialogPageView() {
        if (m_dialogPageView == null) {
            initDialogPageView();
        }
        return m_dialogPageView;
    }
    
    /**
     * @param dialogPageView Is the dialog page view to set.
     */
    public void setDialogPageView(DialogPageView dialogPageView) {
        m_dialogPageView = dialogPageView;
    }
    
    /**
     * Initializes the dialog page view of this descriptor.
     */
    protected void initDialogPageView() {
        m_dialogPageView = new DialogPageView();
        m_dialogPageView.setDescriptor(this);
    }

    /**
     * {@inheritDoc}
     */
    public void configure(AbstractBeanExecutor executor) {
        // Set the id for this descriptor.
        String id = executor.getId();
        setId(id);
        getObjectConfigurer().configure(this, id);
        
        // Ask view to configure.
        DialogPageView dialogPageView = getDialogPageView();
        dialogPageView.configure(executor);

        // Register this descriptor on page where the given executor is defined.
        BeanPresenter beanPresenter = executor.getBeanPresenter();
        Assert.isInstanceOf(PageComponent.class, beanPresenter);
        PageComponent pageComponent = (PageComponent) beanPresenter;
        m_applicationPage = pageComponent.getContext().getPage();
        Assert.isInstanceOf(PageLayoutBuilder.class, m_applicationPage);
        PageLayoutBuilder pageLayoutBuilder
            = (PageLayoutBuilder) m_applicationPage;
        pageLayoutBuilder.addPageComponentDescriptor(this);
        
        m_configured = true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConfigured() {
        return m_configured && getDialogPageView().isConfigured();
    }

    /**
     * {@inheritDoc}
     */
    public void showDisplayable() {
        getDialogPageView().showDisplayable();
        Assert.notNull(m_applicationPage);
        m_applicationPage.showView(this);
    }

    /**
     * {@inheritDoc}
     */
    public Component getMainComponent() {
        return getDialogPageView().getMainComponent();
    }
}

