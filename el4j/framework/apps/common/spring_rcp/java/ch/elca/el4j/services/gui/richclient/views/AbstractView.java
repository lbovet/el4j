/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.richclient.application.Application;

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
        ApplicationListener {
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
     * {@inheritDoc}
     * 
     * Creates control only once.
     */
    protected JComponent createControl() {
        JComponent control = getCreatedControl();
        if (control == null) {
            control = createControlOnce();
            setCreatedControl(control);
        }
        return control;
    }
    
    /**
     * Is used that control is created only once. If method 
     * <code>createControl</code> is not overridden then this method must be 
     * overridden!
     * 
     * @return Returns the created control.
     */
    protected JComponent createControlOnce() {
        return null;
    }
    
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
        }
    }
    
    /**
     * Will be invoked if a <code>QueryObjectEvent</code> has been received.
     * Per default nothing will be made.
     * 
     * @param event Is the query object event.
     */
    protected void onQueryObjectEvent(QueryObjectEvent event) {
        
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
     * @return Returns the application event multicaster if it is available,
     *         otherwise <code>null</code>. 
     */
    protected ApplicationEventMulticaster getApplicationEventMulticaster() {
        ApplicationContext appContext 
            = Application.services().getApplicationContext();
        try {
            ApplicationEventMulticaster eventMulticaster 
                = (ApplicationEventMulticaster) appContext.getBean(
                    AbstractApplicationContext
                        .APPLICATION_EVENT_MULTICASTER_BEAN_NAME);
            return eventMulticaster;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Registers current view for application events.
     * 
     * @return Returns <code>true</code> if current view could be successfully
     *         registered.
     */
    protected boolean registerOnApplicationEventMulticaster() {
        boolean success = false;
        ApplicationEventMulticaster eventMulticaster 
            = getApplicationEventMulticaster();
        if (eventMulticaster != null) {
            eventMulticaster.addApplicationListener(this);
            success = true;
        }
        return success;
    }
    
    /**
     * Unregisters current view for application events.
     * 
     * @return Returns <code>true</code> if current view could be successfully
     *         unregistered.
     */
    protected boolean unregisterOnApplicationEventMulticaster() {
        boolean success = false;
        ApplicationEventMulticaster eventMulticaster 
            = getApplicationEventMulticaster();
        if (eventMulticaster != null) {
            eventMulticaster.removeApplicationListener(this);
            success = true;
        }
        return success;
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentOpened() {
        super.componentOpened();
        registerOnApplicationEventMulticaster();
    }

    /**
     * {@inheritDoc}
     */
    public void componentClosed() {
        unregisterOnApplicationEventMulticaster();
        super.componentClosed();
    }
}
