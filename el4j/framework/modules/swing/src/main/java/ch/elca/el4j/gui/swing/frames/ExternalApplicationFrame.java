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
package ch.elca.el4j.gui.swing.frames;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.bushe.swing.event.annotation.AnnotationProcessor;

import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;

/**
 *  This class represents a frame in a SDI environment.
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
public class ExternalApplicationFrame implements ApplicationFrame {

	/**
	 * The actual internal frame.
	 */
	private JFrame m_frame;
	
	/**
	 * @param frame    the frame
	 */
	public ExternalApplicationFrame(JFrame frame) {
		m_frame = frame;
	}
	
	/** {@inheritDoc} */
	public JComponent getContent() {
		return (JComponent) m_frame.getContentPane().getComponent(0);
	}
	
	/** {@inheritDoc} */
	public Object getFrame() {
		return m_frame;
	}
	
	/** {@inheritDoc} */
	public void setFrame(Object frame) {
		m_frame = (JFrame) frame;
	}
	
	/** {@inheritDoc} */
	public void setName(String name) {
		m_frame.setName(name);
	}
	
	/** {@inheritDoc} */
	public void setContent(JComponent component) {
		m_frame.setContentPane(component);
		
		if (component instanceof ApplicationFrameAware) {
			ApplicationFrameAware awareComponent = (ApplicationFrameAware) component;
			awareComponent.setApplicationFrame(this);
		}
		
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		m_frame.pack();
	}
	
	/** {@inheritDoc} */
	public void setMinimizable(boolean minimizable) {
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setMaximizable(boolean maximizable) {
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setClosable(boolean closable) {
		// not supported
	}
	
	/** {@inheritDoc} */
	public void setTitle(String title) {
		m_frame.setTitle(title);
	}
	
	/** {@inheritDoc} */
	public void setMinimized(boolean minimized) {
		if (minimized) {
			m_frame.setExtendedState(m_frame.getExtendedState() | JFrame.ICONIFIED);
		} else {
			m_frame.setExtendedState(JFrame.NORMAL);
		}
	}
	
	/** {@inheritDoc} */
	public void setMaximized(boolean maximized) {
		if (maximized) {
			m_frame.setExtendedState(m_frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		} else {
			m_frame.setExtendedState(JFrame.NORMAL);
		}
	}
	
	/** {@inheritDoc} */
	public void show() {
		// register all event subscribers
		AnnotationProcessor.process(getContent());
		
		m_frame.setVisible(true);
	}
	
	/** {@inheritDoc} */
	public void setSelected(boolean selected) {
		// not supported
	}
	
	/** {@inheritDoc} */
	public void close() {
		if (getContent() != null) {
			// unregister all event subscribers
			AnnotationProcessor.unsubscribe(getContent());
		}
		AbstractWrapperFactory.removeWrapper(getContent());
		m_frame.dispose();
	}
}
