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
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.apache.commons.lang.ArrayUtils;
import org.bushe.swing.event.EventBus;

import ch.elca.el4j.gui.swing.frames.ApplicationFrame;
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
	 * @param component    the panel to show as MDI child window
	 */
	@Override
	public void show(JComponent component) {
		show(JInteralFrameWrapperFactory.wrap(component));
	}

	
	/** {@inheritDoc} */
	@Override
	public void show(ApplicationFrame frame) {
		JInternalFrame jiframe = (JInternalFrame) frame.getFrame();
		
		// (resources are already injected in frame)
		/*if (jiframe.getClass().getClassLoader() != null) {
			ApplicationContext appContext = Application.getInstance().getContext();
			ResourceMap map = appContext.getResourceMap(jiframe.getClass());
			
			// inject values from properties file
			map.injectComponents(jiframe);
		}*/
		
		if (!ArrayUtils.contains(getDesktopPane().getComponents(), jiframe)) {
			jiframe.addInternalFrameListener(new ListenerToEvent());
		
			getDesktopPane().add(jiframe, JLayeredPane.DEFAULT_LAYER);
			super.show(frame);
		}
		
		frame.setSelected(true);
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
