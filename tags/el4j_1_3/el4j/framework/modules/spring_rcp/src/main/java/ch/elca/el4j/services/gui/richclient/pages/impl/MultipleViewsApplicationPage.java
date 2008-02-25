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
package ch.elca.el4j.services.gui.richclient.pages.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.DefaultViewContext;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.pagecomponents.GroupPageComponent;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupPageComponentDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.panes.ControlablePageComponentPane;
import ch.elca.el4j.services.gui.richclient.pages.ExtendedApplicationPage;
import ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Application page that can have multiple views.
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
public class MultipleViewsApplicationPage extends AbstractApplicationPage 
    implements PageLayoutBuilder, ExtendedApplicationPage {
    
    /**
     * Are the group page components. The group name is used as key.
     */
    protected final Map<String, GroupPageComponent> m_groupPageComponents 
        = Collections.synchronizedMap(
            new HashMap<String, GroupPageComponent>()
        );
    
    /**
     * Are the page component descriptor groups. The group name is used as key.
     */
    protected final 
    Map<String, GroupPageComponentDescriptor> m_groupPageComponentDescriptors 
        = Collections.synchronizedMap(
            new HashMap<String, GroupPageComponentDescriptor>()
        );

    /**
     * {@inheritDoc}
     * 
     * Returns the control of this page.
     */
    public JComponent getControl() {
        GroupPageComponent mainComponent = getMainGroupPageComponent();
        if (mainComponent == null) {
            getPageDescriptor().buildInitialLayout(this);
            mainComponent = getMainGroupPageComponent();
            if (mainComponent == null) {
                CoreNotificationHelper.notifyMisconfiguration("There is no main"
                    + " group page component, also after building the initial"
                    + " layout of the page.");
            }
            setActiveComponent();
        }
        return mainComponent.getControl();
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
    protected final GroupPageComponent getGroupPageComponent(String groupName) {
        return m_groupPageComponents.get(
            StringUtils.hasLength(groupName)
            ? groupName : GroupDescriptor.DEFAULT_GROUP
        );
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
            m_groupPageComponents.put(GroupDescriptor.DEFAULT_GROUP, 
                groupPageComponent);
        }
    }
    
    
    /**
     * @return Returns the main group page component descriptor.
     */
    protected final GroupPageComponentDescriptor 
    getMainGroupPageComponentDescriptor() {
        return getGroupPageComponentDescriptor(null);
    }
    
    /**
     * @param mainGroupPageComponentDescriptor
     *            Is the main group descriptor to set.
     */
    protected final void setMainGroupPageComponentDescriptor(
        GroupPageComponentDescriptor mainGroupPageComponentDescriptor) {
        setGroupPageComponentDescriptor(null, mainGroupPageComponentDescriptor);
    }
    
    /**
     * @param groupName Is the name of the descriptor to return.
     * @return Returns the group descriptor with the given group name.
     */
    protected final GroupPageComponentDescriptor 
    getGroupPageComponentDescriptor(String groupName) {
        return m_groupPageComponentDescriptors.get(
            StringUtils.hasLength(groupName)
            ? groupName
            : GroupDescriptor.DEFAULT_GROUP
        );        
    }
    
    /**
     * @param groupName
     *            Is the name of the group to set.
     * @param groupPageComponentDescriptor
     *            Is the group descriptor to set for the given group name.
     */
    protected final void setGroupPageComponentDescriptor(
        String groupName, 
        GroupPageComponentDescriptor groupPageComponentDescriptor) {
        if (StringUtils.hasLength(groupName)) {
            m_groupPageComponentDescriptors.put(groupName, 
                groupPageComponentDescriptor);
        } else {
            m_groupPageComponentDescriptors.put(GroupDescriptor.DEFAULT_GROUP, 
                groupPageComponentDescriptor);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean giveFocusTo(PageComponent pageComponent) {
        GroupPageComponent targetGroup = findTargetGroup(pageComponent);
        if (targetGroup == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "No group page component found for page component with id "
                    + pageComponent.getId() + ".");
        }
        
        /**
         * Return immediately if the given page component is the same as the 
         * target group.
         */
        if (targetGroup == pageComponent) {
            return false;
        }
        
        /**
         * Remove the page component from group page components other than the 
         * target group page component.
         */
        synchronized (m_groupPageComponents) {
            for (GroupPageComponent group : m_groupPageComponents.values()) {
                if (group != targetGroup 
                    && group.containsPageComponent(pageComponent)) {
                    group.removePageComponent(pageComponent);
                }
            }
        }
        
        /**
         * Add the page component to the target group if it is not already on
         * it.
         */
        if (!targetGroup.containsPageComponent(pageComponent)) {
            targetGroup.addPageComponent(pageComponent);
        }
        
        /**
         * Focus target group page component too.
         */
        giveFocusTo(targetGroup);
        
        return targetGroup.getControl().requestFocusInWindow();
    }
    
    /**
     * Method to find the group page component where the given page component 
     * belongs to.
     * 
     * @param pageComponent Is the page component to lookup a group for.
     * @return Returns the found group page component.
     */
    protected GroupPageComponent findTargetGroup(PageComponent pageComponent) {
        GroupPageComponent targetGroup = null;
        // If page component is a group descriptor group can be extracted.
        if (pageComponent instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor
                = (GroupDescriptor) pageComponent;
            
            // Take the configured group if it exists.
            String groupName = groupDescriptor.getConfiguredGroup();
            if (StringUtils.hasLength(groupName)) {
                targetGroup = getGroupPageComponent(groupName);
            }
            // Otherwise take the preferred group.
            if (targetGroup == null) {
                groupName = groupDescriptor.getPreferredGroup();
                targetGroup = getGroupPageComponent(groupName);
            }
        }
        // Return the main group if no specific group could be found.
        if (targetGroup == null) {
            targetGroup = getMainGroupPageComponent();
        }
        return targetGroup;
    }
    
    /**
     * Method to find the group page component descriptor where the given page
     * component descriptor belongs to.
     * 
     * @param pageComponentDescriptor
     *            Is the page component descriptor to lookup a group descriptor
     *            for.
     * @return Returns the found group page component descriptor.
     */
    protected GroupPageComponentDescriptor findTargetGroupDescriptor(
        PageComponentDescriptor pageComponentDescriptor) {
        GroupPageComponentDescriptor targetGroupDescriptor = null;
        // If page component descriptor is a group descriptor, group can be 
        // extracted.
        if (pageComponentDescriptor instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor
                = (GroupDescriptor) pageComponentDescriptor;
            
            // Take the configured group if it exists.
            String groupName = groupDescriptor.getConfiguredGroup();
            if (StringUtils.hasLength(groupName)) {
                targetGroupDescriptor 
                    = getGroupPageComponentDescriptor(groupName);
            }
            // Otherwise take the preferred group.
            if (targetGroupDescriptor == null) {
                groupName = groupDescriptor.getPreferredGroup();
                targetGroupDescriptor 
                    = getGroupPageComponentDescriptor(groupName);
            }
        }
        // Return the main group if no specific group descriptor could be found.
        if (targetGroupDescriptor == null) {
            targetGroupDescriptor = getMainGroupPageComponentDescriptor();
        }
        return targetGroupDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    protected PageComponent createPageComponent(
        PageComponentDescriptor pageComponentDescriptor) {
        PageComponent pageComponent;
        boolean isGroupComponentDesciptor 
            = pageComponentDescriptor instanceof GroupPageComponentDescriptor;
        if (isGroupComponentDesciptor) {
            GroupPageComponentDescriptor groupPageComponentDescriptor 
                = (GroupPageComponentDescriptor) pageComponentDescriptor;
            
            /**
             * Create the group page component if it was not already created.
             */
            String groupName = groupPageComponentDescriptor.getId();
            pageComponent = getGroupPageComponent(groupName);
            if (pageComponent == null) {
                GroupPageComponent groupPageComponent = (GroupPageComponent)
                    internalCreatePageComponent(groupPageComponentDescriptor);
                setGroupPageComponent(groupName, groupPageComponent);
                pageComponent = groupPageComponent;
            }
            
            /**
             * Find the parent group page component descriptor for the given
             * group page component descriptor.
             */
            GroupPageComponentDescriptor parentGroupPageComponentDescriptor 
                = findTargetGroupDescriptor(groupPageComponentDescriptor);
            if (parentGroupPageComponentDescriptor == null) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "No parent group descriptor could be found for the group "
                    + "page component descriptor with id " 
                    + groupPageComponentDescriptor.getId() + ".");
            }
            /**
             * Only create the parent group page component if the parent group
             * page component descriptor is not the same as the given group page
             * component descriptor.
             */
            if (parentGroupPageComponentDescriptor 
                != groupPageComponentDescriptor) {
                createPageComponent(parentGroupPageComponentDescriptor);
            }
            
        } else {
            /**
             * Get the group page component descriptor for the given page
             * component descriptor.
             */
            GroupPageComponentDescriptor groupPageComponentDescriptor 
                = findTargetGroupDescriptor(pageComponentDescriptor);
            if (groupPageComponentDescriptor == null) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "No group descriptor could be found for the "
                    + "page component descriptor with id " 
                    + pageComponentDescriptor.getId() + ".");
            }
            
            /**
             * Create the group page component.
             */
            createPageComponent(groupPageComponentDescriptor);
            
            /**
             * Create the page component for the given page component
             * descriptor.
             */
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
            if (pageComponent.getContext() == null) {
                pageComponent.setContext(new DefaultViewContext(
                    this, new ControlablePageComponentPane(
                        pageComponent, this)));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }
    
    /**
     * @param viewDescriptor Is the view descriptor to add to this page.
     */
    public void addView(ViewDescriptor viewDescriptor) {
        showView(viewDescriptor);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addPageComponentDescriptor(
        PageComponentDescriptor pageComponentDescriptor) {
        List<PageComponentDescriptor> leafDescriptors 
            = new ArrayList<PageComponentDescriptor>();
        initializeDescriptorsAndCollectLeafDescriptors(
            null, pageComponentDescriptor, leafDescriptors);
        Iterator<PageComponentDescriptor> it = leafDescriptors.iterator();
        while (it.hasNext()) {
            PageComponentDescriptor leafDescriptor 
                = it.next();
            if (leafDescriptor instanceof ViewDescriptor) {
                ViewDescriptor viewDescriptor = (ViewDescriptor) leafDescriptor;
                addView(viewDescriptor);
            } else {
                String leafDescriptorId = leafDescriptor.getId();
                addView(leafDescriptorId);
            }
        }
    }
    
    /**
     * Method to initialize the given page component descriptor recursivly and
     * collect all leaf page component descriptor ids.
     * 
     * @param parentPageComponentDescriptor
     *            Is the parent page component descriptor of the given page
     *            component descriptor.
     * @param pageComponentDescriptor
     *            Is the page component descriptor to handle.
     * @param leafDescriptors
     *            Is the list where to put all found page component descriptors
     *            that are not a group page component descriptor.
     */
    protected void initializeDescriptorsAndCollectLeafDescriptors(
        GroupPageComponentDescriptor parentPageComponentDescriptor,
        PageComponentDescriptor pageComponentDescriptor, 
        final List<PageComponentDescriptor> leafDescriptors) {
        
        Reject.ifNull(pageComponentDescriptor);
        Reject.ifNull(leafDescriptors);
        
        setInitialGroup(
            parentPageComponentDescriptor, pageComponentDescriptor);
        
        if (pageComponentDescriptor instanceof GroupPageComponentDescriptor) {
            GroupPageComponentDescriptor groupPageComponentDescriptor
                = (GroupPageComponentDescriptor) pageComponentDescriptor;
            
            /**
             * Register group page component descriptor. This can be done only 
             * once per group name.
             */
            String groupName = groupPageComponentDescriptor.getId();
            if (getGroupPageComponentDescriptor(groupName) == null) {
                setGroupPageComponentDescriptor(groupName, 
                    groupPageComponentDescriptor);
            } else {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Group name '" + groupName + "' must only be used once.");
            }
            
            /**
             * Invoke the current method for all child page components of the 
             * group page component descriptor.
             */
            PageComponentDescriptor[] descriptors 
                = groupPageComponentDescriptor.getPageComponentDescriptors();
            if (descriptors != null) {
                for (int i = 0; i < descriptors.length; i++) {
                    PageComponentDescriptor childPageComponentDescriptor 
                        = descriptors[i];
                    initializeDescriptorsAndCollectLeafDescriptors(
                        groupPageComponentDescriptor, 
                        childPageComponentDescriptor, leafDescriptors);
                }
            }
        } else {
            /**
             * Add the non group page component descriptor to the leaf desriptor
             * list.
             */
            leafDescriptors.add(pageComponentDescriptor);
        }
    }

    /**
     * Set the configured group of the child page component descriptor if it is
     * a group descriptor and the group page component descriptor is not null.
     * 
     * @param groupPageComponentDescriptor
     *            Is the group where the child must be in.
     * @param childPageComponentDescriptor
     *            Is the child where the group name must be set.
     */
    protected void setInitialGroup(
        GroupPageComponentDescriptor groupPageComponentDescriptor, 
        PageComponentDescriptor childPageComponentDescriptor) {
        if (groupPageComponentDescriptor != null 
            && childPageComponentDescriptor instanceof GroupDescriptor) {
            GroupDescriptor groupDescriptor
                = (GroupDescriptor) childPageComponentDescriptor;
            String configuredGroupName = groupPageComponentDescriptor.getId();
            groupDescriptor.setConfiguredGroup(configuredGroupName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void close(PageComponent pageComponent) {
        GroupPageComponent parentPageComponent
            = getParentPageComponent(pageComponent);
        if (parentPageComponent != null) {
            parentPageComponent.removePageComponent(pageComponent);
        }
        super.close(pageComponent);
        
        if (parentPageComponent != null
            && parentPageComponent.getNumberOfPageComponents() <= 0) {
            close(parentPageComponent);
        }
    }
    
    /**
     * @param pageComponent
     *            Is the page component that is looking for its parent.
     * @return Returns the parent page component or <code>null</code> if there
     *         is no.
     */
    protected GroupPageComponent getParentPageComponent(
        PageComponent pageComponent) {
        GroupPageComponent result = null;
        synchronized (m_groupPageComponents) {
            for (GroupPageComponent group : m_groupPageComponents.values()) {
                if (group.containsPageComponent(pageComponent)) {
                    result = group;
                }
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setActiveComponent(PageComponent pageComponent) {
        super.setActiveComponent(pageComponent);
    }

    /** {@inheritDoc} */
    public void setDescriptor(PageDescriptor descriptor) {
        // TODO Auto-generated method stub
        // don't know what goes in here, leaving it for MZE
        // -- AMS
        assert false;
    }
}
