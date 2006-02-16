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
package ch.elca.el4j.services.gui.richclient.pages.descriptors;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.DefaultGroupPageComponentDescriptor;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Page descriptor for multiple views.
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
public class MultipleViewsPageDescriptor extends LabeledObjectSupport
    implements PageDescriptor, BeanNameAware, InitializingBean {
    
    /**
     * Are the page component descriptors.
     */
    private PageComponentDescriptor[] m_pageComponentDescriptors = null;

    /**
     * Layout manager to layout views on page.
     */
    private LayoutManager m_layoutManager = new BorderLayout();

    /**
     * Is the id of this page.
     */
    private String m_id;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * {@inheritDoc}
     * 
     * The given <code>PageLayourBuilder</code> from Spring RCP must be casted
     * to the enhanced one of EL4J.
     */
    public void buildInitialLayout(PageLayoutBuilder oldPageLayoutBuilder) {
        ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder pageLayout
            = (ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder)
                oldPageLayoutBuilder;
        
        DefaultGroupPageComponentDescriptor mainGroupPageComponentDescriptor 
            = new DefaultGroupPageComponentDescriptor();
        if (m_layoutManager != null) {
            mainGroupPageComponentDescriptor.setLayoutManager(m_layoutManager);
        }
        mainGroupPageComponentDescriptor.setPageComponentDescriptors(
            m_pageComponentDescriptors);
        mainGroupPageComponentDescriptor.setId(GroupDescriptor.DEFAULT_GROUP);
        
        try {
            mainGroupPageComponentDescriptor.afterPropertiesSet();
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Could not create properly the root group page component "
                + "descriptor. Have a look at the appended exception.", e);
        }
        
        pageLayout.addPageComponentDescriptor(mainGroupPageComponentDescriptor);
    }

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
     * @return Returns the layoutManager.
     */
    public final LayoutManager getLayoutManager() {
        return m_layoutManager;
    }

    /**
     * <code>BorderLayout</code> is the default layout manager.
     * 
     * @param layoutManager The layoutManager to set.
     */
    public final void setLayoutManager(LayoutManager layoutManager) {
        m_layoutManager = layoutManager;
    }

    /**
     * {@inheritDoc}
     * 
     * If property id is not set the bean name will be taken.
     */
    public String getId() {
        return StringUtils.hasText(m_id) ? m_id : getBeanName();
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    public final void setBeanName(String beanName) {
        m_beanName = beanName;
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
    public void afterPropertiesSet() throws Exception {
        if (m_pageComponentDescriptors == null 
            || m_pageComponentDescriptors.length <= 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Bean with name '" + getBeanName() + "' must have minimum "
                + "one page component descriptor.");
        }
    }
}
