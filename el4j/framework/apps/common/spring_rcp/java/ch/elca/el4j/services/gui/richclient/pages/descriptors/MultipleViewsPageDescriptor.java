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
package ch.elca.el4j.services.gui.richclient.pages.descriptors;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.Icon;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.ViewDescriptor;

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
public class MultipleViewsPageDescriptor implements PageDescriptor,
    BeanNameAware, InitializingBean {
    
    /**
     * Are the view descriptors for the center position.
     */
    private ViewDescriptor[] m_viewDescriptorsCenter = null;

    /**
     * Are the view descriptors for the right position.
     */
    private ViewDescriptor[] m_viewDescriptorsRight = null;

    /**
     * Are the view descriptors for the left position.
     */
    private ViewDescriptor[] m_viewDescriptorsLeft = null;

    /**
     * Are the view descriptors for the top position.
     */
    private ViewDescriptor[] m_viewDescriptorsTop = null;

    /**
     * Are the view descriptors for the bottom position.
     */
    private ViewDescriptor[] m_viewDescriptorsBottom = null;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * Returns the first view descriptor that can be found by going through the
     * following order of properties, or <code>null</code> if no view
     * descriptor exists.
     * <ol>
     *     <li>viewDescriptorsCenter</li>
     *     <li>viewDescriptorsLeft</li>
     *     <li>viewDescriptorsTop</li>
     *     <li>viewDescriptorsRight</li>
     *     <li>viewDescriptorsBottom</li>
     * </ol>
     * 
     * @return Returns the main view descriptor of this page.
     */
    protected ViewDescriptor getMainViewDescriptor() {
        ViewDescriptor viewDesc 
            = getFirstViewDescriptor(getViewDescriptorsCenter());
        if (viewDesc == null) {
            viewDesc = getFirstViewDescriptor(getViewDescriptorsLeft());
            if (viewDesc == null) {
                viewDesc = getFirstViewDescriptor(getViewDescriptorsTop());
                if (viewDesc == null) {
                    viewDesc = getFirstViewDescriptor(
                        getViewDescriptorsRight());
                    if (viewDesc == null) {
                        viewDesc = getFirstViewDescriptor(
                            getViewDescriptorsBottom());
                    }
                }
            }
        }
        return viewDesc;
    }
    
    /**
     * @param viewDescs Are the view descriptions.
     * @return Returns the first view descriptor of the given array or 
     *         <code>null</code> if there is no view descriptor.
     */
    protected ViewDescriptor getFirstViewDescriptor(
        ViewDescriptor[] viewDescs) {
        return viewDescs != null && viewDescs.length > 0 ? viewDescs[0] : null;
    }

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
        
        layoutViews(pageLayout, getViewDescriptorsCenter(), 
            BorderLayout.CENTER);
        layoutViews(pageLayout, getViewDescriptorsLeft(), 
            BorderLayout.WEST);
        layoutViews(pageLayout, getViewDescriptorsTop(), 
            BorderLayout.NORTH);
        layoutViews(pageLayout, getViewDescriptorsRight(), 
            BorderLayout.EAST);
        layoutViews(pageLayout, getViewDescriptorsBottom(), 
            BorderLayout.SOUTH);
    }

    /**
     * Lays the given view out by using given page layout builder.
     * 
     * @param pageLayout Is the layout builder.
     * @param viewDescs Are the views to layout.
     * @param positionArgument Is the position argument for the given views.
     */
    protected void layoutViews(
        ch.elca.el4j.services.gui.richclient.pages.PageLayoutBuilder pageLayout,
        ViewDescriptor[] viewDescs, Object positionArgument) {
        if (viewDescs != null) {
            for (int i = 0; i < viewDescs.length; i++) {
                ViewDescriptor viewDesc = viewDescs[i];
                pageLayout.addView(viewDesc.getId(), positionArgument);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return getMainViewDescriptor().getId();
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return getMainViewDescriptor().getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public String getCaption() {
        return getMainViewDescriptor().getCaption();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return getMainViewDescriptor().getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return getMainViewDescriptor().getImage();
    }

    /**
     * {@inheritDoc}
     */
    public Icon getIcon() {
        return getMainViewDescriptor().getIcon();
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
     * @return Returns the viewDescriptorsBottom.
     */
    public final ViewDescriptor[] getViewDescriptorsBottom() {
        return m_viewDescriptorsBottom;
    }

    /**
     * @param viewDescriptorsBottom The viewDescriptorsBottom to set.
     */
    public final void setViewDescriptorsBottom(
        ViewDescriptor[] viewDescriptorsBottom) {
        m_viewDescriptorsBottom = viewDescriptorsBottom;
    }

    /**
     * @return Returns the viewDescriptorsCenter.
     */
    public final ViewDescriptor[] getViewDescriptorsCenter() {
        return m_viewDescriptorsCenter;
    }

    /**
     * @param viewDescriptorsCenter The viewDescriptorsCenter to set.
     */
    public final void setViewDescriptorsCenter(
        ViewDescriptor[] viewDescriptorsCenter) {
        m_viewDescriptorsCenter = viewDescriptorsCenter;
    }

    /**
     * @return Returns the viewDescriptorsLeft.
     */
    public final ViewDescriptor[] getViewDescriptorsLeft() {
        return m_viewDescriptorsLeft;
    }

    /**
     * @param viewDescriptorsLeft The viewDescriptorsLeft to set.
     */
    public final void setViewDescriptorsLeft(
        ViewDescriptor[] viewDescriptorsLeft) {
        m_viewDescriptorsLeft = viewDescriptorsLeft;
    }

    /**
     * @return Returns the viewDescriptorsRight.
     */
    public final ViewDescriptor[] getViewDescriptorsRight() {
        return m_viewDescriptorsRight;
    }

    /**
     * @param viewDescriptorsRight The viewDescriptorsRight to set.
     */
    public final void setViewDescriptorsRight(
        ViewDescriptor[] viewDescriptorsRight) {
        m_viewDescriptorsRight = viewDescriptorsRight;
    }

    /**
     * @return Returns the viewDescriptorsTop.
     */
    public final ViewDescriptor[] getViewDescriptorsTop() {
        return m_viewDescriptorsTop;
    }

    /**
     * @param viewDescriptorsTop The viewDescriptorsTop to set.
     */
    public final void setViewDescriptorsTop(
        ViewDescriptor[] viewDescriptorsTop) {
        m_viewDescriptorsTop = viewDescriptorsTop;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (getMainViewDescriptor() == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Bean with name '" + getBeanName() + "' must have minimum "
                + "one of the following properties with minimum "
                + "one view descriptor: viewDescriptorsCenter, "
                + "viewDescriptorsLeft, viewDescriptorsTop, "
                + "viewDescriptorsRight or viewDescriptorsBottom.");
        }
    }
}
