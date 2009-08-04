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

import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.bushe.swing.event.annotation.AnnotationProcessor;

import ch.elca.el4j.services.gui.swing.wrapper.AbstractWrapperFactory;

/**
 * This class represents a frame in a MDI environment.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class InternalApplicationFrame implements ApplicationFrame {
	/**
	 * The actual internal frame.
	 */
	private JInternalFrame m_internalFrame;
	
	/**
	 * @param internalFrame    the internal frame
	 */
	public InternalApplicationFrame(JInternalFrame internalFrame) {
		m_internalFrame = internalFrame;
	}
	
	/** {@inheritDoc} */
	public JComponent getContent() {
		return (JComponent) m_internalFrame.getContentPane();
	}
	
	/** {@inheritDoc} */
	public Object getFrame() {
		return m_internalFrame;
	}
	
	/** {@inheritDoc} */
	public void setFrame(Object frame) {
		m_internalFrame = (JInternalFrame) frame;
	}
	
	/** {@inheritDoc} */
	public void setName(String name) {
		m_internalFrame.setName(name);
	}
	
	/** {@inheritDoc} */
	public void setContent(JComponent component) {
		m_internalFrame.setContentPane(component);
		
		if (component instanceof ApplicationFrameAware) {
			ApplicationFrameAware awareComponent = (ApplicationFrameAware) component;
			awareComponent.setApplicationFrame(this);
		}
		
		m_internalFrame.setClosable(true);
		m_internalFrame.setResizable(true);
		m_internalFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		m_internalFrame.pack();
	}
	
	/** {@inheritDoc} */
	public void setMinimizable(boolean minimizable) {
		m_internalFrame.setIconifiable(minimizable);
	}
	
	/** {@inheritDoc} */
	public void setMaximizable(boolean maximizable) {
		m_internalFrame.setMaximizable(maximizable);
	}
	
	/** {@inheritDoc} */
	public void setClosable(boolean closable) {
		m_internalFrame.setClosable(closable);
	}
	
	/** {@inheritDoc} */
	public void setTitle(String title) {
		m_internalFrame.setTitle(title);
	}
	
	/** {@inheritDoc} */
	public void setMinimized(boolean minimized) {
		try {
			m_internalFrame.setIcon(minimized);
		} catch (PropertyVetoException e) {
			return;
		}
	}
	
	/** {@inheritDoc} */
	public void setMaximized(boolean maximized) {
		try {
			m_internalFrame.setMaximum(maximized);
		} catch (PropertyVetoException e) {
			return;
		}
	}
	
	/** {@inheritDoc} */
	public void show() {
		// register all event subscribers
		AnnotationProcessor.process(getContent());
		
		m_internalFrame.show();
	}
	
	/** {@inheritDoc} */
	public void setSelected(boolean selected) {
		try {
			m_internalFrame.setSelected(selected);
		} catch (PropertyVetoException e) {
			return;
		}
	}
	
	/** {@inheritDoc} */
	public void close() {
		if (getContent() != null) {
			// unregister all event subscribers
			AnnotationProcessor.unsubscribe(getContent());
		}
		
		AbstractWrapperFactory.removeWrapper(getContent());
		m_internalFrame.dispose();
	}
}
