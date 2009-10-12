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

/**
 * This interface represents a generic frame that contains a component.
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
public interface ApplicationFrame {
	/**
	 * @param component    the content of this frame
	 */
	public void setContent(JComponent component);
	
	/**
	 * @return    the content of this frame
	 */
	public JComponent getContent();
	
	/**
	 * @return    the actual frame
	 */
	public Object getFrame();
	
	/**
	 * @param frame    the actual frame
	 */
	public void setFrame(Object frame);
	
	/**
	 * @param name    the name of the frame. This must be unique.
	 */
	public void setName(String name);
	
	/**
	 * @param title    the title of the frame
	 */
	public void setTitle(String title);
	
	/**
	 * @param minimizable    is frame minimizable
	 */
	public void setMinimizable(boolean minimizable);
	
	/**
	 * @param maximizable    is frame maximizable
	 */
	public void setMaximizable(boolean maximizable);
	
	/**
	 * @param closable    is frame closable
	 */
	public void setClosable(boolean closable);
	
	/**
	 * @param minimized    <code>true</code> if frame should get minimized
	 */
	public void setMinimized(boolean minimized);
	
	/**
	 * @param maximized    <code>true</code> if frame should get maximized
	 */
	public void setMaximized(boolean maximized);
	
	/**
	 * Show this frame (if supported).
	 */
	public void show();
	
	/**
	 * @param selected    is frame selected?
	 */
	public void setSelected(boolean selected);
	
	/**
	 * Close this frame.
	 */
	public void close();
}
