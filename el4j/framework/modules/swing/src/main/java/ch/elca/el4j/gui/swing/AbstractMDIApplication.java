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

import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.gui.swing.wrapper.JInteralFrameWrapper;
import ch.elca.el4j.gui.swing.wrapper.JInteralFrameWrapperFactory;

/**
 * Parent class for new MDI applications using an XML GUI description.
 * Programmatically written GUI should use {@link MDIApplication}.
 *
 *  Additional features:
 *   * allows adding internal frames (for the Documents of MDI) to the application
 *      Internal frames can (optionally) minimize themselves
 *       { @link JInternalFrame#setIconifiable(boolean) }
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 * @author Philipp Oser (POS)
 */
public abstract class AbstractMDIApplication extends GUIApplication {

	/**
	 * @return    the desktop pane of this MDIApplication
	 */
	protected abstract JDesktopPane getDesktopPane();
	
	/**
	 * Adds an internal frame to the MDI application.
	 * @param frame     the frame to add
	 * @see #show(JInternalFrame,int)
	 */
	public void showInternalFrame(JComponent frame) {
		if (frame instanceof JInternalFrame) {
			show((JInternalFrame) frame, JLayeredPane.DEFAULT_LAYER);
			try {
				((JInternalFrame) frame).setSelected(true);
			} catch (PropertyVetoException e) {
				// ignore
			}
		}
	}
	
	/**
	 * @param component    the panel to show as MDI child window
	 */
	@Override
	public void show(JComponent component) {
		if (JPanel.class.isAssignableFrom(component.getClass())) {
			JPanel panel = (JPanel) component;
			JInteralFrameWrapper wrapper
				= JInteralFrameWrapperFactory.wrap(panel);
			showInternalFrame(wrapper);
		} else {
			super.show(component);
		}
	}

	/**
	 * Adds an internal frame to the MDI application
	 *   In particular: keeps track of the frame, adds listeners
	 *    to frame events and ensures properties of the frame
	 *    are stored persistently.
	 * @param frame the internal frame to add
	 * @param index the position at which to insert the
	 *          component, or -1 to append the component to the end
	 */
	public void show(JInternalFrame frame, int index) {
		ApplicationContext appContext = Application.getInstance().getContext();
		ResourceMap map = appContext.getResourceMap(frame.getClass());
		
		frame.addInternalFrameListener(new ListenerToEvent());

		// inject values from properties file
		map.injectComponents(frame);

		getDesktopPane().add(frame, index);
		frame.show();
	}
	
	/**
	 * Helper that listens to events of the internal frames.
	 */
	protected final class ListenerToEvent implements InternalFrameListener {
		/** {@inheritDoc} */
		public void internalFrameClosing(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameClosed(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameOpened(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameIconified(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameDeiconified(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameActivated(InternalFrameEvent e) {
			EventBus.publish(e);
		}

		/** {@inheritDoc} */
		public void internalFrameDeactivated(InternalFrameEvent e) {
			EventBus.publish(e);
		}
	}

}
