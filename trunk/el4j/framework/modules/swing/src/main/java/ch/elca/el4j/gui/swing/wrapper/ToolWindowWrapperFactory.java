/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.gui.swing.wrapper;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.noos.xing.mydoggy.ToolWindowAnchor;

import ch.elca.el4j.gui.swing.frames.ToolWindowTabApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ToolWindowTabConfiguration;

public class ToolWindowWrapperFactory extends AbstractWrapperFactory<ToolWindowTabApplicationFrame> {
	/**
	 * The abstract factory.
	 */
	private static AbstractWrapperFactory<ToolWindowTabApplicationFrame> s_factory = null;
	
	/**
	 * Wraps a GUI component into a {@link JFrame}.
	 *
	 * @param component    the component to wrap
	 * @return             the wrapper
	 */
	public static ToolWindowTabApplicationFrame wrap(JComponent component) {
		if (s_factory == null) {
			s_factory = new ToolWindowWrapperFactory();
		}
		return s_factory.wrapComponent(component);
	}
	
	/** {@inheritDoc} */
	@Override
	protected ToolWindowTabApplicationFrame createApplicationFrame(String name, String title, JComponent component) {
		/*DockingApplication application = (DockingApplication) GUIApplication.getInstance();
		
		final ToolWindowManager toolWindowManager = application.getToolWindowManager();
		
		String id = "__tmp";
		ToolWindow toolWindow = toolWindowManager.getToolWindow(id);
		if (toolWindow == null) {
			toolWindow = toolWindowManager.registerToolWindow(
				id, id, null, component, ToolWindowAnchor.BOTTOM);
			
			DockedTypeDescriptor typeDescriptor = (DockedTypeDescriptor)
				toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
			
			// default behavior: close tool window when clicking on X
			typeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
				public void onHideButtonClick(ToolWindow toolWindow) {
					toolWindowManager.unregisterToolWindow(toolWindow.getId());
				}
			});
		}
		
		// Register the tool.
		ToolWindow tool = toolWindowManager.registerToolWindow(
			name, title, null,
			component, ToolWindowAnchor.BOTTOM);

		tool.setAvailable(false);
		
		DockedTypeDescriptor typeDescriptor = (DockedTypeDescriptor) tool.getTypeDescriptor(ToolWindowType.DOCKED);
		
		// default behavior: close tool window when clicking on X
		typeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
			public void onHideButtonClick(ToolWindow toolWindow) {
				toolWindowManager.unregisterToolWindow(toolWindow.getId());
			}
		});
		
		ToolWindowTab toolWindowTab = toolWindow.addToolWindowTab(tool);
		toolWindow.removeToolWindowTab(toolWindowTab);
		
		toolWindowManager.unregisterToolWindow(id);*/
		
		ToolWindowTabConfiguration configuration = new ToolWindowTabConfiguration(
			name, title, null, component, ToolWindowAnchor.BOTTOM);
				
		return new ToolWindowTabApplicationFrame(configuration);
	}
}
