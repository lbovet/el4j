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
package ch.elca.el4j.services.gui.richclient.views;

import java.awt.Component;

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
import org.springframework.util.Assert;

import ch.elca.el4j.services.gui.event.RefreshEvent;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.ExtendedPageComponentDescriptor;
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
 *   ("$URL$",
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
     * Is the component which had as last the focus.
     */
    private Component m_lastFocusedComponent;
    
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
    public void componentFocusGained() {
        super.componentFocusGained();
        firePropertyChange("pageComponentSelected", false, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentFocusLost() {
        firePropertyChange("pageComponentSelected", true, false);        
        super.componentFocusLost();
    }

    /**
     * @return Returns the lastFocusedComponent.
     */
    protected final Component getLastFocusedComponent() {
        return m_lastFocusedComponent;
    }

    /**
     * @param lastFocusedComponent Is the lastFocusedComponent to set.
     */
    protected final void setLastFocusedComponent(
        Component lastFocusedComponent) {
        m_lastFocusedComponent = lastFocusedComponent;
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
        getContext().getPage().showView((ViewDescriptor) getDescriptor());
    }
    
    /**
     * @return Returns the extended page component descriptor of this page
     *         component.
     */
    public final ExtendedPageComponentDescriptor getExtendedDescriptor() {
        PageComponentDescriptor descriptor = getDescriptor();
        Assert.isInstanceOf(ExtendedPageComponentDescriptor.class, descriptor);
        return (ExtendedPageComponentDescriptor) descriptor;
    }
    
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    protected void firePropertyChange(String propertyName, boolean oldValue, 
        boolean newValue) {
        getExtendedDescriptor().firePropertyChange(
            propertyName, oldValue, newValue);
    }
    
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    protected void firePropertyChange(String propertyName, int oldValue, 
        int newValue) {
        getExtendedDescriptor().firePropertyChange(
            propertyName, oldValue, newValue);
    }
    
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, 
        Object newValue) {
        getExtendedDescriptor().firePropertyChange(
            propertyName, oldValue, newValue);
    }
}
