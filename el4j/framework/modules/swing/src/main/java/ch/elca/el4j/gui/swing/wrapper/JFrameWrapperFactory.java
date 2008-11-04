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
package ch.elca.el4j.gui.swing.wrapper;

import javax.swing.JComponent;
import javax.swing.JFrame;

import ch.elca.el4j.gui.swing.frames.ExternalApplicationFrame;

/**
 * This class wraps components into {@link JFrame}s.
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
public class JFrameWrapperFactory extends AbstractWrapperFactory<ExternalApplicationFrame> {
	
	/**
	 * The abstract factory.
	 */
	private static AbstractWrapperFactory<ExternalApplicationFrame> s_factory = null;
	
	/**
	 * Wraps a GUI component into a {@link JFrame}.
	 *
	 * @param component    the component to wrap
	 * @return             the wrapper
	 */
	public static ExternalApplicationFrame wrap(JComponent component) {
		if (s_factory == null) {
			s_factory = new JFrameWrapperFactory();
		}
		return s_factory.wrapComponent(component);
	}
	
	/** {@inheritDoc} */
	@Override
	protected ExternalApplicationFrame createApplicationFrame(String name, String title, JComponent component) {
		ExternalApplicationFrame frame = new ExternalApplicationFrame(new JFrame());
		frame.setName(name);
		frame.setTitle(title);
		frame.setContent(component);
		return frame;
	}
}
