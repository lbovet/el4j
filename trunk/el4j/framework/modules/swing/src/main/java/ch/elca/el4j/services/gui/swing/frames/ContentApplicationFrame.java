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
import org.noos.xing.mydoggy.Content;

import ch.elca.el4j.services.gui.swing.wrapper.AbstractWrapperFactory;

/**
 * This class represents a frame in a docking environment.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class ContentApplicationFrame implements ApplicationFrame {
	/**
	 * The representation of a frame in a docking environment.
	 */
	protected Content m_content;
	
	/**
	 * A descriptor of the content which allows to use the frame (in a limited way) before it's visible.
	 */
	protected ContentConfiguration m_contentConfiguration;
	
	/**
	 * @param contentDescriptor    A descriptor of the content which allows to use the frame (in a limited way)
	 *                             before it's visible.
	 */
	public ContentApplicationFrame(ContentConfiguration contentDescriptor) {
		m_contentConfiguration = contentDescriptor;
	}
	
	/** {@inheritDoc} */
	public JComponent getContent() {
		if (m_content == null) {
			return m_contentConfiguration.getComponent();
		} else {
			return (JComponent) m_content.getComponent();
		}
	}
	
	/** {@inheritDoc} */
	public void setContent(JComponent component) {
		checkFrame();
		m_content.setComponent(component);
		
		if (component instanceof ApplicationFrameAware) {
			ApplicationFrameAware awareComponent = (ApplicationFrameAware) component;
			awareComponent.setApplicationFrame(this);
		}
	}
	
	/** {@inheritDoc} */
	public Object getFrame() {
		if (m_content != null && m_content.getDockableManager() == null) {
			m_content = null;
		}
		return m_content;
	}
	
	/** {@inheritDoc} */
	public void setFrame(Object frame) {
		m_content = (Content) frame;
	}
	
	/** {@inheritDoc} */
	public void setName(String name) {
		if (m_content == null) {
			m_contentConfiguration.setId(name);
		}
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setTitle(String title) {
		if (m_content == null) {
			m_contentConfiguration.setTitle(title);
		} else {
			m_content.setTitle(title);
		}
	}
	
	/** {@inheritDoc} */
	public void setMinimizable(boolean minimizable) {
		checkFrame();
		m_content.getContentUI().setMinimizable(minimizable);
	}
	
	/** {@inheritDoc} */
	public void setMaximizable(boolean maximizable) {
		checkFrame();
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setClosable(boolean closable) {
		checkFrame();
		m_content.getContentUI().setCloseable(closable);
	}
	
	/** {@inheritDoc} */
	public void setMinimized(boolean minimized) {
		checkFrame();
		m_content.setMinimized(minimized);
	}
	
	/** {@inheritDoc} */
	public void setMaximized(boolean maximized) {
		checkFrame();
		m_content.setMaximized(maximized);
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
		m_content.setSelected(selected);
	}
	
	/** {@inheritDoc} */
	public void close() {
		checkFrame();
		if (getContent() != null) {
			// unregister all event subscribers
			AnnotationProcessor.unsubscribe(getContent());
		}
		AbstractWrapperFactory.removeWrapper(getContent());
		
		m_content.getDockableManager().removeContent(m_content);
		m_content = null;
	}
	
	/**
	 * @return    the configuration of the content
	 */
	public ContentConfiguration getConfiguration() {
		return m_contentConfiguration;
	}

	/**
	 * @param configuration    the configuration of the content
	 */
	public void setConfiguration(ContentConfiguration configuration) {
		m_contentConfiguration = configuration;
	}
	
	/**
	 * Check if frame is already set.
	 */
	private void checkFrame() {
		if (m_content == null) {
			throw new IllegalStateException("Frame is not yet set.");
		}
	}
}
