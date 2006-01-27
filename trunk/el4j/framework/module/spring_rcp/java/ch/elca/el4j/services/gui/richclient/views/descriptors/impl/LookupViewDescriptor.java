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
package ch.elca.el4j.services.gui.richclient.views.descriptors.impl;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.support.ShowViewCommand;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.AbstractGroupPageComponentDescriptor;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * View descriptor that looks up view by using the application context this bean
 * has been instantiated with.
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
public class LookupViewDescriptor extends AbstractGroupPageComponentDescriptor
    implements ViewDescriptor, LayoutDescriptor {
    
    /**
     * Is the bean name of the view to describe. This bean must be defined as
     * prototype.
     */
    private String m_viewPrototypeBeanName;
    
    /**
     * @return Returns the viewPrototypeBeanName.
     */
    public final String getViewPrototypeBeanName() {
        return m_viewPrototypeBeanName;
    }

    /**
     * @param viewPrototypeBeanName The viewPrototypeBeanName to set.
     */
    public final void setViewPrototypeBeanName(String viewPrototypeBeanName) {
        m_viewPrototypeBeanName = viewPrototypeBeanName;
    }

    /**
     * {@inheritDoc}
     */
    public ActionCommand createShowViewCommand(final ApplicationWindow window) {
        return new ShowViewCommand(this, window);
    }

    /**
     * {@inheritDoc}
     */
    public CommandButtonLabelInfo getShowViewCommandLabel() {
        return getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public PageComponent createPageComponent() {
        return createView();
    }

    /**
     * @return Returns the created view.
     */
    protected View createView() {
        View view = (View) getApplicationContext().getBean(
            getViewPrototypeBeanName(), View.class);
        view.setDescriptor(this);
        return view;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getViewPrototypeBeanName(), "viewPrototypeBeanName", this);
        
        if (getApplicationContext() == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "This class with bean name '" + getBeanName() 
                + "' must be created by using an application context.");
        }
        if (!(getApplicationContext().containsBean(
            getViewPrototypeBeanName()))) {
            CoreNotificationHelper.notifyMisconfiguration(
                "There is no bean with name '" + getBeanName() + "'.");
        }
        if (getApplicationContext().isSingleton(
            getViewPrototypeBeanName())) {
            CoreNotificationHelper.notifyMisconfiguration(
                "View bean with name '" + getBeanName() 
                + "' must not be singleton.");
        }
        if (!View.class.isAssignableFrom(getApplicationContext().getType(
            getViewPrototypeBeanName()))) {
            CoreNotificationHelper.notifyMisconfiguration(
                "View bean with name '" + getBeanName() 
                + "' must implement interface '" + View.class.getName() + "'.");
        }
    }
}
