/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupPageComponentDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.impl.JPanelGroupPageComponent;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Groups multiple page component descriptors.
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
public class DefaultGroupPageComponentDescriptor 
    extends AbstractGroupPageComponentDescriptor
    implements GroupPageComponentDescriptor {
    
    /**
     * Are the page component descriptors.
     */
    private PageComponentDescriptor[] m_pageComponentDescriptors;
    
    /**
     * Is the layout manager for this view group. Default is a grid layout
     * with one column.
     */
    private LayoutManager m_layoutManager = new GridLayout(0, 1);
    
    /**
     * @return Returns the pageComponentDescriptors.
     */
    public final PageComponentDescriptor[] getPageComponentDescriptors() {
        return m_pageComponentDescriptors;
    }

    /**
     * @param pageComponentDescriptors The pageComponentDescriptors to set.
     */
    public final void setPageComponentDescriptors(
        PageComponentDescriptor[] pageComponentDescriptors) {
        m_pageComponentDescriptors = pageComponentDescriptors;
    }

    /**
     * {@inheritDoc}
     */
    public final LayoutManager getLayoutManager() {
        return m_layoutManager;
    }
    
    /**
     * @param layoutManager The layoutManager to set.
     */
    public final void setLayoutManager(LayoutManager layoutManager) {
        m_layoutManager = layoutManager;
    }

    /**
     * {@inheritDoc}
     */
    public PageComponent createPageComponent() {
        JPanelGroupPageComponent panelGroupPageComponent 
            = new JPanelGroupPageComponent();
        panelGroupPageComponent.setDescriptor(this);
        return panelGroupPageComponent;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getLayoutManager(), "layoutManager", this);
        PageComponentDescriptor[] pageComponentDescriptors 
            = getPageComponentDescriptors();
        if (pageComponentDescriptors == null) {
            pageComponentDescriptors = new PageComponentDescriptor[0];
        }
    }
}
