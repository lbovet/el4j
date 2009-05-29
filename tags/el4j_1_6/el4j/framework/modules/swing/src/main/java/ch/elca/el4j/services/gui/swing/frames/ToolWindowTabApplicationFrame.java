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
package ch.elca.el4j.services.gui.swing.frames;

import javax.swing.JComponent;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;

import ch.elca.el4j.services.gui.swing.wrapper.AbstractWrapperFactory;

/**
 * This class represents a tool window tab in a docking environment.
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
public class ToolWindowTabApplicationFrame implements ApplicationFrame {
	/**
	 * The representation of a frame in a docking environment.
	 */
	protected ToolWindowTab m_toolWindowTab;
	
	/**
	 * A descriptor of the content which allows to use the frame (in a limited way) before it's visible.
	 */
	protected ToolWindowTabConfiguration m_configuration;
	
	/**
	 * @param configuration    A descriptor of the content which allows to use the frame (in a limited way)
	 *                         before it's visible.
	 */
	public ToolWindowTabApplicationFrame(ToolWindowTabConfiguration configuration) {
		m_configuration = configuration;
	}
	
	/** {@inheritDoc} */
	public JComponent getContent() {
		if (m_toolWindowTab == null) {
			return m_configuration.getComponent();
		} else {
			return (JComponent) m_toolWindowTab.getComponent();
		}
	}
	
	/** {@inheritDoc} */
	public void setContent(JComponent component) {
		checkFrame();
		m_toolWindowTab.setComponent(component);
		
		if (component instanceof ApplicationFrameAware) {
			ApplicationFrameAware awareComponent = (ApplicationFrameAware) component;
			awareComponent.setApplicationFrame(this);
		}
	}
	
	/** {@inheritDoc} */
	public Object getFrame() {
		if (m_toolWindowTab != null && m_toolWindowTab.getDockableManager() == null) {
			m_toolWindowTab = null;
		}
		return m_toolWindowTab;
	}
	
	/** {@inheritDoc} */
	public void setFrame(Object frame) {
		m_toolWindowTab = (ToolWindowTab) frame;
	}
	
	/** {@inheritDoc} */
	public void setName(String name) {
		if (m_toolWindowTab == null) {
			m_configuration.setId(name);
		}
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setTitle(String title) {
		if (m_toolWindowTab == null) {
			m_configuration.setTitle(title);
		} else {
			m_toolWindowTab.setTitle(title);
		}
	}
	
	/** {@inheritDoc} */
	public void setMinimizable(boolean minimizable) {
		checkFrame();
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setMaximizable(boolean maximizable) {
		checkFrame();
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setClosable(boolean closable) {
		checkFrame();
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setMinimized(boolean minimized) {
		checkFrame();
		m_toolWindowTab.setMinimized(minimized);
	}
	
	/** {@inheritDoc} */
	public void setMaximized(boolean maximized) {
		checkFrame();
		m_toolWindowTab.setMaximized(maximized);
	}
	
	/** {@inheritDoc} */
	public void show() {
		// register all event subscribers
		AnnotationProcessor.process(getContent());

		// not supported
	}
	
	/** {@inheritDoc} */
	public void setSelected(boolean selected) {
		checkFrame();
		m_toolWindowTab.setSelected(selected);
	}
	
	/** {@inheritDoc} */
	public void close() {
		checkFrame();
		if (getContent() != null) {
			// unregister all event subscribers
			AnnotationProcessor.unsubscribe(getContent());
		}
		AbstractWrapperFactory.removeWrapper(getContent());
		
		m_toolWindowTab.getDockableManager().removeToolWindowTab(m_toolWindowTab);
		m_toolWindowTab = null;
	}
	
	/**
	 * @return    the configuration of the tool window
	 */
	public ToolWindowTabConfiguration getConfiguration() {
		return m_configuration;
	}
	
	/**
	 * @param configuration    the configuration of the tool window
	 */
	public void setConfiguration(ToolWindowTabConfiguration configuration) {
		m_configuration = configuration;
	}
	
	/**
	 * Check if frame is already set.
	 */
	private void checkFrame() {
		if (m_toolWindowTab == null) {
			throw new IllegalStateException("Frame is not yet set.");
		}
	}
}
