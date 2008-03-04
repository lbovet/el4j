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
package ch.elca.el4j.gui.swing;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.gui.swing.wrapper.JFrameWrapper;
import ch.elca.el4j.gui.swing.wrapper.JFrameWrapperFactory;

/**
 * Parent class for new Docking applications.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class DockingApplication extends GUIApplication {
    /**
     * The tool window manager.
     */
    protected ToolWindowManager m_toolWindowManager = null;
    
    /**
     * Creates the tool window manager if necessary.
     */
    protected void createToolWindowManager() {
        if (m_toolWindowManager == null) {
            // Create a new instance of MyDoggyToolWindowManager
            m_toolWindowManager = new MyDoggyToolWindowManager(getMainFrame());
        }
    }
    
    /**
     * @param beanName    the Spring bean name
     * @param anchor      the anchor of the docked window
     */
    @SuppressWarnings("unchecked")
    public void show(String beanName, ToolWindowAnchor anchor)
        throws NoSuchBeanDefinitionException {
        
        if (!m_springContext.containsBean(beanName)) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        show((JPanel) m_springContext.getBean(beanName), anchor);
    }
    
    /** {@inheritDoc} */
    @Override
    public void show(JComponent component) {
        if (JPanel.class.isAssignableFrom(component.getClass())) {
            JPanel panel = (JPanel) component;
            JFrameWrapper wrapper = JFrameWrapperFactory.wrap(panel);
            addContent(wrapper);
        } else {
            super.show(component);
        }
    }
    
    /**
     * @param component   the component to show
     * @param anchor      the anchor of the docked window
     */
    @SuppressWarnings("unchecked")
    public void show(JComponent component, ToolWindowAnchor anchor) {
        if (JPanel.class.isAssignableFrom(component.getClass())) {
            JPanel panel = (JPanel) component;
            JFrameWrapper wrapper = JFrameWrapperFactory.wrap(panel);
            createToolWindow(wrapper, anchor);
        } else {
            super.show(component);
        }
    }
    
    /**
     * @param content    the content of the tool window to be created
     * @param anchor     the anchor of the docked window
     * @return           the created tool window
     */
    protected ToolWindow createToolWindow(JFrame content,
        ToolWindowAnchor anchor) {
        
        if (m_toolWindowManager.getToolWindow(content.getName()) != null) {
            m_toolWindowManager.unregisterToolWindow(
                m_toolWindowManager.getToolWindow(content.getName()).getId());
        }
        // Register the tool.
        ToolWindow tool = m_toolWindowManager.registerToolWindow(
            content.getName(), content.getTitle(), null,
            content.getContentPane(), anchor);

        tool.setAvailable(true);
        
        DockedTypeDescriptor dockedTypeDescriptor
            = (DockedTypeDescriptor) tool.getTypeDescriptor(
                ToolWindowType.DOCKED);
        
        // default behavior: close tool window when clicking on X
        dockedTypeDescriptor
            .setToolWindowActionHandler(new ToolWindowActionHandler() {

                public void onHideButtonClick(ToolWindow toolWindow) {
                    m_toolWindowManager
                        .unregisterToolWindow(toolWindow.getId());
                }
            });

        return tool;
    }
    
    /**
     * @param content    the content of the content window to be created
     * @return           the created content window
     */
    protected Content addContent(JFrame content) {
        ContentManager contentManager = m_toolWindowManager.getContentManager();
        
        // remove if content already exists
        if (contentManager.getContent(content) != null) {
            contentManager.removeContent(
                contentManager.getContent(content));
        }
        return contentManager.addContent(content.toString(), content.getTitle(),
            null, content.getContentPane());
    }
}
