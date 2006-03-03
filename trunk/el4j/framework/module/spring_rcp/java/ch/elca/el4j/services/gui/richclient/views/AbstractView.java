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
package ch.elca.el4j.services.gui.richclient.views;

import javax.swing.JComponent;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.ViewDescriptor;

import ch.elca.el4j.services.gui.event.RefreshEvent;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;
import ch.elca.el4j.services.gui.richclient.utils.ApplicationListenerUtils;
import ch.elca.el4j.services.search.events.QueryObjectEvent;

/**
 * Base class for views.
 *
 * <b>ATTENTION:</b> This class has the same name in Spring RCP. The idea is 
 * that the people from Spring RCP will change their class in a next release
 * so we do not have to serve a separate class in the future.
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
public abstract class AbstractView 
    extends org.springframework.richclient.application.support.AbstractView 
    implements InitializingBean, BeanNameAware, ApplicationEventPublisherAware, 
        ApplicationListener, LayoutDescriptor, GroupDescriptor {
    /**
     * Is the control of this view.
     */
    private JComponent m_control;

    /**
     * Is the name of this view.
     */
    private String m_beanName;
    
    /**
     * Is the application event publisher for this view.
     */
    private ApplicationEventPublisher m_applicationEventPublisher;

    /**
     * @return Returns the beanName.
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
    public final void setApplicationEventPublisher(
        ApplicationEventPublisher applicationEventPublisher) {
        m_applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * @return Returns the application event publisher.
     */
    public final ApplicationEventPublisher getApplicationEventPublisher() {
        return m_applicationEventPublisher;
    }

    /**
     * {@inheritDoc}
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof QueryObjectEvent) {
            onQueryObjectEvent((QueryObjectEvent) event);
        } else if (event instanceof RefreshEvent) {
            onRefreshEvent((RefreshEvent) event);
        }
    }

    /**
     * Will be invoked if a <code>QueryObjectEvent</code> has been received.
     * Per default nothing will be made.
     * 
     * @param event Is the query object event.
     */
    protected void onQueryObjectEvent(QueryObjectEvent event) { }
    
    /**
     * Will be invoked if a <code>RefreshEvent</code> has been received.
     * Per default nothing will be made.
     * 
     * @param event Is the refresh event.
     */
    protected void onRefreshEvent(RefreshEvent event) { }

    /**
     * Checks if the query object event is coming from a neighbour from the 
     * same application window.
     * 
     * @param event Is the query object event to check.
     * @return Returns <code>true</code> if the given event is coming from
     *         the same application window as the current.
     */
    protected boolean isQueryObjectComingFromNeighbour(
        QueryObjectEvent event) {
        boolean result = false;
        if (event != null && isControlCreated()) {
            Object sourceObject = event.getSource();
            if (sourceObject instanceof PageComponent) {
                PageComponent pageComponent = (PageComponent) sourceObject;
                int windowNumberNeighBour 
                    = pageComponent.getContext().getWindow().getNumber();
                int windowNumberCurrent = getContext().getWindow().getNumber();
                result = windowNumberNeighBour == windowNumberCurrent;
            }
        }
        return result;
    }
    
    /**
     * @return Returns the control.
     */
    protected final JComponent getCreatedControl() {
        return m_control;
    }

    /**
     * @param control The control to set.
     */
    protected final void setCreatedControl(JComponent control) {
        m_control = control;
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentOpened() {
        super.componentOpened();
        ApplicationListenerUtils.registerApplicationListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void componentClosed() {
        ApplicationListenerUtils.unregisterApplicationListener(this);
        super.componentClosed();
    }

    /**
     * {@inheritDoc}
     */
    public Object getPreferredPositionArgument() {
        Object result = null;
        PageComponentDescriptor descriptor = getDescriptor();
        if (descriptor instanceof LayoutDescriptor) {
            LayoutDescriptor layoutDescriptor = (LayoutDescriptor) descriptor;
            result = layoutDescriptor.getPreferredPositionArgument();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getPreferredPositionIndex() {
        Integer result = null;
        PageComponentDescriptor descriptor = getDescriptor();
        if (descriptor instanceof LayoutDescriptor) {
            LayoutDescriptor layoutDescriptor = (LayoutDescriptor) descriptor;
            result = layoutDescriptor.getPreferredPositionIndex();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getPreferredGroup() {
        String result = null;
        PageComponentDescriptor descriptor = getDescriptor();
        if (descriptor instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor = (GroupDescriptor) descriptor;
            result = groupDescriptor.getPreferredGroup();
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getConfiguredGroup() {
        String result = null;
        PageComponentDescriptor descriptor = getDescriptor();
        if (descriptor instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor = (GroupDescriptor) descriptor;
            result = groupDescriptor.getConfiguredGroup();
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setConfiguredGroup(String group) {
        PageComponentDescriptor descriptor = getDescriptor();
        if (descriptor instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor = (GroupDescriptor) descriptor;
            groupDescriptor.setConfiguredGroup(group);
        }
    }
    
    /**
     * Requests page to set this view as active page component.
     */
    protected void requestActivePageComponent() {
        getActiveWindow().getPage().showView((ViewDescriptor) getDescriptor());
    }
}
