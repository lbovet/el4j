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
package ch.elca.el4j.services.gui.richclient.pages.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.closure.support.AbstractConstraint;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentListener;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.support.SharedCommandTargeter;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.Assert;

import ch.elca.el4j.services.gui.richclient.utils.Services;

/**
 * Abstract class for an application page.
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
 * @author Peter De Bruycker
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractApplicationPage implements ApplicationPage {
    /**
     * Event listener helper for page components.
     */
    private final EventListenerListHelper m_pageComponentListeners 
        = new EventListenerListHelper(PageComponentListener.class);

    /**
     * Registry to get all view descriptors from application context.
     */
    private final ViewDescriptorRegistry m_viewDescriptorRegistry 
        = Services.get(ViewDescriptorRegistry.class);

    /**
     * List of page components for this page.
     */
    private final Set<PageComponent> m_pageComponents 
        = new LinkedHashSet<PageComponent>();

    /**
     * Is the currently active page component.
     */
    private PageComponent m_activeComponent;

    /**
     * Retargets shared commands pending to the active page component.
     */
    private SharedCommandTargeter m_sharedCommandTargeter;

    /**
     * Is the page descriptor for this page.
     */
    private PageDescriptor m_descriptor;

    /**
     * Is the application window where this page belongs to.
     */
    private ApplicationWindow m_window;

    /**
     * @param viewDescriptorId
     *            Is the view descriptor id to find the page component for.
     * @return Returns the found page component.
     */
    protected PageComponent findPageComponent(final String viewDescriptorId) {
        AbstractConstraint constraint = new AbstractConstraint() {

            public boolean test(Object arg) {
                if (arg instanceof View) {
                    return ((View) arg).getId().equals(viewDescriptorId);
                }
                return false;
            }
        };
        return (PageComponent) constraint.findFirst(m_pageComponents);
    }

    /**
     * {@inheritDoc}
     */
    public void addPageComponentListener(PageComponentListener listener) {
        m_pageComponentListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePageComponentListener(PageComponentListener listener) {
        m_pageComponentListeners.remove(listener);
    }

    /**
     * @param viewDescriptorId Is the view descriptor to fetch.
     * @return Returns the fetched view descriptor.
     */
    protected ViewDescriptor getViewDescriptor(String viewDescriptorId) {
        return m_viewDescriptorRegistry.getViewDescriptor(viewDescriptorId);
    }

    /**
     * {@inheritDoc}
     */
    public PageComponent getActiveComponent() {
        return m_activeComponent;
    }

    /**
     * Sets the first page component as active component.
     */
    protected void setActiveComponent() {
        if (m_pageComponents.size() > 0) {
            setActiveComponent(
                m_pageComponents.iterator().next());
        }
    }

    /**
     * @param pageComponent Is the page component to set active.
     */
    protected void setActiveComponent(PageComponent pageComponent) {
        // if pageComponent is already active, don't do anything
        if (this.m_activeComponent == pageComponent) {
            return;
        }

        if (this.m_activeComponent != null) {
            fireFocusLost(this.m_activeComponent);
        }
        giveFocusTo(pageComponent);
        this.m_activeComponent = pageComponent;
        fireFocusGained(this.m_activeComponent);
    }

    /**
     * @param component Is the page component to send an open event for.
     */
    protected void fireOpened(PageComponent component) {
        component.componentOpened();
        m_pageComponentListeners.fire("componentOpened", component);
    }

    /**
     * @param component Is the page component to send a closed event for.
     */
    protected void fireClosed(PageComponent component) {
        component.componentClosed();
        m_pageComponentListeners.fire("componentClosed", component);
    }

    /**
     * @param component Is the page component to send a focus gained event for.
     */
    protected void fireFocusGained(PageComponent component) {
        component.componentFocusGained();
        m_pageComponentListeners.fire("componentFocusGained", component);
    }

    /**
     * @param component Is the page component to send a focus lost event for.
     */
    protected void fireFocusLost(PageComponent component) {
        component.componentFocusLost();
        m_pageComponentListeners.fire("componentFocusLost", component);
    }

    /**
     * @param pageComponent Is the page component to give the focus.
     * @return Returns <code>true</code> if focus could be successfully given.
     */
    protected abstract boolean giveFocusTo(PageComponent pageComponent);

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return m_descriptor.getId();
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationWindow getWindow() {
        return m_window;
    }

    /**
     * @param pageComponent Is the page component to close.
     */
    protected void close(PageComponent pageComponent) {
        if (pageComponent == m_activeComponent) {
            fireFocusLost(pageComponent);
            m_activeComponent = null;
        }
        m_pageComponents.remove(pageComponent);
        pageComponent.dispose();
        fireClosed(pageComponent);
        if (m_activeComponent == null) {
            setActiveComponent();
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Closes all page components.
     */
    public boolean close() {
        Set<PageComponent> snapshot
            = new HashSet<PageComponent>(m_pageComponents);
        for (PageComponent component : snapshot) {
            close(component);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void openEditor(Object editorInput) {
        // todo
    }

    /**
     * {@inheritDoc}
     */
    public boolean closeAllEditors() {
        // todo
        return true;
    }

    /**
     * @param pageComponent Is the page component to add.
     */
    protected void addPageComponent(PageComponent pageComponent) {
        m_pageComponents.add(pageComponent);
        fireOpened(pageComponent);
    }

    /**
     * Creates a page component for the given page component descriptor.
     * 
     * @param descriptor
     *            Is the page component descriptor to create a page component
     *            of.
     * @return Returns the created page component.
     */
    protected abstract PageComponent createPageComponent(
        PageComponentDescriptor descriptor);

    /**
     * {@inheritDoc}
     * 
     * Shows the view with the given id.
     */
    public void showView(String viewDescriptorId) {
        showView(getViewDescriptor(viewDescriptorId));
    }

    /**
     * {@inheritDoc}
     * 
     * Shows the given view.
     */
    public void showView(ViewDescriptor viewDescriptor) {
        PageComponent component = findPageComponent(viewDescriptor.getId());
        if (component == null) {
            component = createPageComponent(viewDescriptor);

            addPageComponent(component);
        }
        setActiveComponent(component);
    }

    /**
     * @return Returns all page components in a set.
     */
    public Set<PageComponent> getPageComponents() {
        return Collections.unmodifiableSet(m_pageComponents);
    }

    /**
     * @param window Is the application window to set.
     */
    public final void setApplicationWindow(ApplicationWindow window) {
        Assert.notNull(window, "The containing window is required");
        Assert.state(this.m_window == null, "Page window already set: "
            + "it should only be set once, during initialization");
        this.m_window = window;
        m_sharedCommandTargeter = new SharedCommandTargeter(window);
        addPageComponentListener(m_sharedCommandTargeter);
    }

    /**
     * @param descriptor Is the page descriptor to set.
     */
    public final void setPageDescriptor(PageDescriptor descriptor) {
        Assert.notNull(descriptor, "The page's descriptor is required");
        Assert.state(this.m_descriptor == null, "Page descriptor already set: "
            + "it should only be set once, during initialization");
        this.m_descriptor = descriptor;
    }

    /**
     * @return Returns the page descriptor.
     */
    protected PageDescriptor getPageDescriptor() {
        return m_descriptor;
    }
}
