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

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.DefaultViewContext;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.pagecomponents.GroupPageComponent;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.PageComponentDescriptorGroup;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.DefaultPageComponentDescriptorGroup;
import ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Application page that can have multiple views.
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
public class MultipleViewsApplicationPage extends AbstractApplicationPage 
    implements PageLayoutBuilder {
    /**
     * Are the top page component descriptors for this page.
     */
    private final List m_pageComponentDescriptors = new ArrayList();
    
    /**
     * Are the group page components. The group name is used as key.
     */
    private final Map m_groupPageComponents = new HashMap();
    
    /**
     * Are the page component descriptor groups. The group name is used as key.
     */
    private final Map m_groupDescriptors = new HashMap();

    /**
     * Layout manager for the page control.
     */
    private LayoutManager m_layoutManager = new BorderLayout();

    /**
     * {@inheritDoc}
     * 
     * Returns the control of this page.
     */
    public JComponent getControl() {
        GroupPageComponent mainGroupPageComponent = getMainGroupPageComponent();
        if (mainGroupPageComponent == null) {
            mainGroupPageComponent = createMainGroupPageComponent();
            setMainGroupPageComponent(mainGroupPageComponent);
            getPageDescriptor().buildInitialLayout(this);
            setActiveComponent();
        }
        return mainGroupPageComponent.getControl();
    }
    
    /**
     * @return Returns the created main group page component.
     */
    protected GroupPageComponent createMainGroupPageComponent() {
        DefaultPageComponentDescriptorGroup groupDescriptor 
            = new DefaultPageComponentDescriptorGroup();
        groupDescriptor.setLayoutManager(getLayoutManager());
        groupDescriptor.setPreferredGroup(LayoutDescriptor.DEFAULT_GROUP);
        return (GroupPageComponent) groupDescriptor.createPageComponent();
    }
    
    
    
    /**
     * @return Returns the main group page component.
     */
    protected final GroupPageComponent getMainGroupPageComponent() {
        return getGroupPageComponent(null);
    }
    
    /**
     * @param mainGroupPageComponent Is the main group page component to set.
     */
    protected final void setMainGroupPageComponent(
        GroupPageComponent mainGroupPageComponent) {
        setGroupPageComponent(null, mainGroupPageComponent);
    }
    
    /**
     * @param groupName Is the name of the page component to return.
     * @return Returns the group page component with the given group name.
     */
    protected final GroupPageComponent getGroupPageComponent(
        String groupName) {
        GroupPageComponent groupPageComponent;
        if (StringUtils.hasLength(groupName)) {
            groupPageComponent = (GroupPageComponent) 
                m_groupPageComponents.get(groupName);
        } else {
            groupPageComponent = (GroupPageComponent) 
                m_groupPageComponents.get(LayoutDescriptor.DEFAULT_GROUP);
        }
        return groupPageComponent;
    }
    
    /**
     * @param groupName
     *            Is the name of the group to set.
     * @param groupPageComponent
     *            Is the group page component to set for the given group name.
     */
    protected final void setGroupPageComponent(
        String groupName, GroupPageComponent groupPageComponent) {
        if (StringUtils.hasLength(groupName)) {
            m_groupPageComponents.put(groupName, groupPageComponent);
        } else {
            m_groupPageComponents.put(LayoutDescriptor.DEFAULT_GROUP, 
                groupPageComponent);
        }
    }

    
    
    /**
     * @return Returns the main group descriptor.
     */
    protected final PageComponentDescriptorGroup getMainGroupDescriptor() {
        return getGroupDescriptor(null);
    }
    
    /**
     * @param mainGroupDescriptor Is the main group descriptor to set.
     */
    protected final void setMainGroupDescriptor(
        PageComponentDescriptorGroup mainGroupDescriptor) {
        setGroupDescriptor(null, mainGroupDescriptor);
    }
    
    /**
     * @param groupName Is the name of the descriptor to return.
     * @return Returns the group descriptor with the given group name.
     */
    protected final PageComponentDescriptorGroup getGroupDescriptor(
        String groupName) {
        PageComponentDescriptorGroup groupDescriptor;
        if (StringUtils.hasLength(groupName)) {
            groupDescriptor = (PageComponentDescriptorGroup) 
                m_groupDescriptors.get(groupName);
        } else {
            groupDescriptor = (PageComponentDescriptorGroup) 
                m_groupDescriptors.get(LayoutDescriptor.DEFAULT_GROUP);
        }
        return groupDescriptor;
    }
    
    /**
     * @param groupName
     *            Is the name of the group to set.
     * @param groupDescriptor
     *            Is the group descriptor to set for the given group name.
     */
    protected final void setGroupDescriptor(
        String groupName, PageComponentDescriptorGroup groupDescriptor) {
        if (StringUtils.hasLength(groupName)) {
            m_groupDescriptors.put(groupName, groupDescriptor);
        } else {
            m_groupDescriptors.put(LayoutDescriptor.DEFAULT_GROUP, 
                groupDescriptor);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected boolean giveFocusTo(PageComponent pageComponent) {
        String groupName = null;
        if (pageComponent instanceof LayoutDescriptor) {
            LayoutDescriptor layoutDescriptor
                = (LayoutDescriptor) pageComponent;
            groupName = layoutDescriptor.getPreferredGroup();
        }
        
        GroupPageComponent groupPageComponent 
            = getGroupPageComponent(groupName);
        
        if (groupPageComponent == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "No group page component found for group '" + groupName + "'.");
        }
        
        groupPageComponent.addPageComponent(pageComponent);
        // TODO implement
        
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    protected PageComponent createPageComponent(
        PageComponentDescriptor pageComponentDescriptor) {
        PageComponent pageComponent;
        boolean isGroupComponentDesciptor 
            = pageComponentDescriptor instanceof PageComponentDescriptorGroup;
        if (isGroupComponentDesciptor) {
            PageComponentDescriptorGroup groupDescriptor 
                = (PageComponentDescriptorGroup) pageComponentDescriptor;
            String groupName = groupDescriptor.getPreferredGroup();
            pageComponent = getGroupPageComponent(groupName);
            if (pageComponent == null) {
                GroupPageComponent groupPageComponent = (GroupPageComponent)
                    internalCreatePageComponent(groupDescriptor);
                setGroupPageComponent(groupName, groupPageComponent);
                pageComponent = groupPageComponent;
            }
        } else {
            if (pageComponentDescriptor instanceof LayoutDescriptor) {
                LayoutDescriptor layoutDescriptor 
                    = (LayoutDescriptor) pageComponentDescriptor;
                String groupName = layoutDescriptor.getPreferredGroup();
                PageComponentDescriptorGroup groupDescriptor 
                    = getGroupDescriptor(groupName);
                if (groupDescriptor != null) {
                    createPageComponent(groupDescriptor);
                } else {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "No page component descriptor group available for group"
                        + "with name " + groupName + ".");
                }
            }
            pageComponent 
                = internalCreatePageComponent(pageComponentDescriptor);
        }
        return pageComponent;
    }
    
    /**
     * Internal method to create a page component by using its descriptor.
     * 
     * @param pageComponentDescriptor
     *            Is the descriptor to create a page component.
     * @return Returns the create page component.
     */
    protected PageComponent internalCreatePageComponent(
        PageComponentDescriptor pageComponentDescriptor) {
        PageComponent pageComponent 
            = pageComponentDescriptor.createPageComponent();
        configurePageComponent(pageComponent);

        /**
         * Trigger the createControl method of the PageComponent, so if a
         * PageComponentListener is added in the createControl method, the
         * componentOpened event is received.
         */
        pageComponent.getControl();
        
        return pageComponent;
    }
    
    /**
     * Configures the given page component.
     * 
     * @param pageComponent Is the page component to configure.
     */
    protected void configurePageComponent(PageComponent pageComponent) {
        Reject.ifNull(pageComponent);
        if (pageComponent instanceof View) {
            pageComponent.setContext(new DefaultViewContext(
                this, new PageComponentPane(pageComponent)));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }

    /**
     * {@inheritDoc}
     */
    public void addPageComponentDescriptor(
        PageComponentDescriptor pageComponentDescriptor) {
        m_pageComponentDescriptors.add(pageComponentDescriptor);
        List viewDescriptorIds = new ArrayList();
        collectAllViews(pageComponentDescriptor, viewDescriptorIds);
        Iterator it = viewDescriptorIds.iterator();
        while (it.hasNext()) {
            String viewDescriptorId = (String) it.next();
            addView(viewDescriptorId);
        }
    }
    
    /**
     * Method to collect all views of a page component descriptor. Currently
     * only <code>PageComponentDescriptorGroup</code> and
     * <code>ViewDescriptor</code> classes are allowed. Subclass this method
     * for other types.
     * 
     * @param pageComponentDescriptor
     *            Is the page component descriptor to handle.
     * @param viewDescriptorIds
     *            Is the list where to put all found view descriptor ids.
     */
    protected void collectAllViews(
        PageComponentDescriptor pageComponentDescriptor, 
        List viewDescriptorIds) {
        Reject.ifNull(pageComponentDescriptor);
        if (pageComponentDescriptor instanceof PageComponentDescriptorGroup) {
            PageComponentDescriptorGroup groupDescriptor
                = (PageComponentDescriptorGroup) pageComponentDescriptor;
            
            String groupName = groupDescriptor.getPreferredGroup();
            if (getGroupDescriptor(groupName) == null) {
                setGroupDescriptor(groupName, groupDescriptor);
            } else {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Group name '" + groupName + "' must only be used once.");
            }
            
            PageComponentDescriptor[] descriptors 
                = groupDescriptor.getPageComponentDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                PageComponentDescriptor descriptor = descriptors[i];
                collectAllViews(descriptor, viewDescriptorIds);
            }
        } else if (pageComponentDescriptor instanceof ViewDescriptor) {
            String viewDescriptorId = pageComponentDescriptor.getId();
            viewDescriptorIds.add(viewDescriptorId);
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Unsupported type of page component descriptor '" 
                + pageComponentDescriptor.getClass().getName() + "'.");
        }
    }

    /**
     * @return Returns the layout manager for this page.
     */
    public final LayoutManager getLayoutManager() {
        return m_layoutManager;
    }

    /**
     * @param layoutManager Is the layout manager to set.
     */
    public final void setLayoutManager(LayoutManager layoutManager) {
        m_layoutManager = layoutManager;
    }
}
