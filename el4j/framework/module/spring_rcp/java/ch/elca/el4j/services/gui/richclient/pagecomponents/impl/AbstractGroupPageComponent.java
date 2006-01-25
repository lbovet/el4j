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
package ch.elca.el4j.services.gui.richclient.pagecomponents.impl;

import java.awt.Image;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.factory.AbstractControlFactory;

import ch.elca.el4j.services.gui.richclient.pagecomponents.GroupPageComponent;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.PageComponentDescriptorGroup;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract class for a page component group.
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
public abstract class AbstractGroupPageComponent extends AbstractControlFactory
    implements GroupPageComponent {
    
    /**
     * Is the context of this group component.
     */
    private PageComponentContext m_context; 
    
    /**
     * Is the descriptor for this group descriptor.
     */
    private PageComponentDescriptorGroup m_pageComponentDescriptorGroup;

    /**
     * {@inheritDoc}
     */
    public PageComponentContext getContext() {
        return m_context;
    }

    /**
     * {@inheritDoc}
     */
    public void setContext(PageComponentContext context) {
        m_context = context;
    }

    /**
     * {@inheritDoc}
     */
    public void componentOpened() { }

    /**
     * {@inheritDoc}
     */
    public void componentFocusGained() { }

    /**
     * {@inheritDoc}
     */
    public void componentFocusLost() { }

    /**
     * {@inheritDoc}
     */
    public void componentClosed() { }

    /**
     * {@inheritDoc}
     */
    public void dispose() { }

    /**
     * {@inheritDoc}
     */
    public void setDescriptor(PageComponentDescriptor pageComponentDescriptor) {
        if (pageComponentDescriptor == null || !(pageComponentDescriptor 
            instanceof PageComponentDescriptorGroup)) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Descriptor must be of type " 
                + PageComponentDescriptorGroup.class.getName() 
                + ". Given descriptor is of type " 
                + pageComponentDescriptor.getClass().getName() + ".");
        }
        m_pageComponentDescriptorGroup 
            = (PageComponentDescriptorGroup) pageComponentDescriptor;
    }
    
    /**
     * @return Returns the page component descriptor group.
     */
    public PageComponentDescriptorGroup getPageComponentDescriptorGroup() {
        return m_pageComponentDescriptorGroup;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return m_pageComponentDescriptorGroup.getId();
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        m_pageComponentDescriptorGroup.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        m_pageComponentDescriptorGroup.addPropertyChangeListener(
            propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(
        PropertyChangeListener listener) {
        m_pageComponentDescriptorGroup.removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        m_pageComponentDescriptorGroup.removePropertyChangeListener(
            propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return m_pageComponentDescriptorGroup.getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public String getCaption() {
        return m_pageComponentDescriptorGroup.getCaption();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return m_pageComponentDescriptorGroup.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return m_pageComponentDescriptorGroup.getImage();
    }

    /**
     * {@inheritDoc}
     */
    public Icon getIcon() {
        return m_pageComponentDescriptorGroup.getIcon();
    }

    /**
     * {@inheritDoc}
     */
    public Object getPreferredPositionArgument() {
        return m_pageComponentDescriptorGroup.getPreferredPositionArgument();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getPreferredPositionIndex() {
        return m_pageComponentDescriptorGroup.getPreferredPositionIndex();
    }

    /**
     * {@inheritDoc}
     */
    public String getPreferredGroup() {
        return m_pageComponentDescriptorGroup.getPreferredGroup();
    }

    /**
     * {@inheritDoc}
     */
    public JComponent addPageComponent(PageComponent pageComponent) {
        JComponent addedComponent = null;
        if (isControlCreated()) {
            JComponent groupControl = getControl();
            JComponent childControl;
            PageComponentContext childContext = pageComponent.getContext();
            if (childContext != null) {
                childControl = childContext.getPane().getControl();
            } else {
                childControl = pageComponent.getControl();
            }
            
            Object positionArgument = null;
            Integer positionIndex = null;

            if (pageComponent instanceof LayoutDescriptor) {
                LayoutDescriptor childLayoutDescriptor 
                    = (LayoutDescriptor) pageComponent;
                positionArgument 
                    = childLayoutDescriptor.getPreferredPositionArgument();
                positionIndex 
                    = childLayoutDescriptor.getPreferredPositionIndex();
            }

            if (positionArgument != null && positionIndex != null) {
                groupControl.add(childControl, positionArgument, 
                    positionIndex.intValue());
            } else if (positionArgument != null) {
                groupControl.add(childControl, positionArgument);
            } else if (positionIndex != null) {
                groupControl.add(childControl, 
                    positionIndex.intValue());
            } else {
                groupControl.add(childControl);
            }
            
            groupControl.validate();
            groupControl.repaint();
            
            addedComponent = childControl;
        }
        return addedComponent;
    }

    /**
     * {@inheritDoc}
     */
    public JComponent removePageComponent(PageComponent pageComponent) {
        JComponent removedComponent = null;
        if (isControlCreated()) {
            JComponent groupControl = getControl();
            JComponent childControl;
            
            PageComponentContext childContext = pageComponent.getContext();
            if (childContext != null) {
                childControl = childContext.getPane().getControl();
            } else {
                childControl = pageComponent.getControl();
            }
            groupControl.remove(childControl);
            
            groupControl.validate();
            groupControl.repaint();

            removedComponent = childControl;
        }
        return removedComponent;
    }
}
