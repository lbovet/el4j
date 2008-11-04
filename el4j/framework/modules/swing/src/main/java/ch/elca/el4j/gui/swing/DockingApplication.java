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
import javax.swing.JPanel;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ContentApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ContentConfiguration;
import ch.elca.el4j.gui.swing.frames.ToolWindowTabApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ToolWindowTabConfiguration;
import ch.elca.el4j.gui.swing.wrapper.ContentWrapperFactory;
import ch.elca.el4j.gui.swing.wrapper.ToolWindowWrapperFactory;

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
	 * @return    the current {@link ToolWindowManager}.
	 */
	public ToolWindowManager getToolWindowManager() {
		if (m_toolWindowManager == null) {
			// Create a new instance of MyDoggyToolWindowManager
			m_toolWindowManager = new MyDoggyToolWindowManager();
		}
		return m_toolWindowManager;
	}
	
	/** {@inheritDoc} */
	@Override
	public void show(JComponent component) {
		show(ContentWrapperFactory.wrap(component));
	}
	
	/** {@inheritDoc} */
	@Override
	public void show(ApplicationFrame frame) {
		if (frame instanceof ContentApplicationFrame) {
			showContent((ContentApplicationFrame) frame);
		}
	}
	
	/**
	 * @param beanName        the Spring bean name
	 * @param toolWindowId    the id of the toolWindow to add a new tab
	 * @param anchor          the anchor of the docked window
	 */
	public void show(String beanName, String toolWindowId, ToolWindowAnchor anchor)
		throws NoSuchBeanDefinitionException {
		
		if (!m_springContext.containsBean(beanName)) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		show((JPanel) m_springContext.getBean(beanName), toolWindowId, anchor);
	}
	
	/**
	 * @param component       the component to show
	 * @param toolWindowId    the id of the toolWindow to add a new tab
	 * @param anchor          the anchor of the docked window
	 */
	public void show(JComponent component, String toolWindowId, ToolWindowAnchor anchor) {
		ToolWindowTabApplicationFrame frame = ToolWindowWrapperFactory.wrap(component);
		frame.getConfiguration().setId(toolWindowId);
		frame.getConfiguration().setAnchor(anchor);
		showToolWindow(frame);
	}
	
	/**
	 * Show a frame as content.
	 * @param frame    the frame to show as content
	 */
	public void showContent(ContentApplicationFrame frame) {
		ContentManager contentManager = m_toolWindowManager.getContentManager();
		Content content = (Content) frame.getFrame();
		if (content == null) {
			ContentConfiguration config = frame.getConfiguration();
			content = contentManager.addContent(config.getId(), config.getTitle(),
				config.getIcon(), config.getComponent(), config.getToolTip(), config.getConstraints());
			// default settings
			content.getContentUI().setMinimizable(false);
			content.getContentUI().setDetachable(false);
			
			frame.setFrame(content);
			frame.setContent(config.getComponent());
		}
		
		super.show(frame);
		
		frame.setSelected(true);
	}
	
	/**
	 * Show a frame as toolbox.
	 * @param frame           the frame to show as toolbox
	 */
	public void showToolWindow(final ToolWindowTabApplicationFrame frame) {
		if (frame.getFrame() == null) {
			ToolWindowTab toolWindowTab = null;
			ToolWindowTabConfiguration config = frame.getConfiguration();
			ToolWindow toolWindow = m_toolWindowManager.getToolWindow(config.getId());
			if (toolWindow == null) {
				toolWindow = m_toolWindowManager.registerToolWindow(config.getId(), config.getTitle(),
					config.getIcon(), config.getComponent(), config.getAnchor());
				
				DockedTypeDescriptor typeDescriptor = (DockedTypeDescriptor)
					toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
				
				// default behavior: close tool window when clicking on X
				typeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
					public void onHideButtonClick(ToolWindow toolWindow) {
						frame.close();
						m_toolWindowManager.unregisterToolWindow(toolWindow.getId());
					}
				});
				toolWindowTab = toolWindow.getToolWindowTabs()[0];
			} else {
				toolWindowTab = toolWindow.addToolWindowTab(
					((ToolWindowTab) ToolWindowWrapperFactory.wrap(frame.getContent()).getFrame()).getOwner());
			}
			toolWindow.setAvailable(true);
			
			frame.setFrame(toolWindowTab);
		}
	}
}
