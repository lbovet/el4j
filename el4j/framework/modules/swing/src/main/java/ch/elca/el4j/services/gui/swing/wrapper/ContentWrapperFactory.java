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
package ch.elca.el4j.services.gui.swing.wrapper;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.noos.xing.mydoggy.ContentManager;

import ch.elca.el4j.services.gui.swing.DockingApplication;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.frames.ContentApplicationFrame;
import ch.elca.el4j.services.gui.swing.frames.ContentConfiguration;

public class ContentWrapperFactory extends AbstractWrapperFactory<ContentApplicationFrame> {
	/**
	 * The abstract factory.
	 */
	private static AbstractWrapperFactory<ContentApplicationFrame> s_factory = null;
	
	/**
	 * Wraps a GUI component into a {@link JFrame}.
	 *
	 * @param component    the component to wrap
	 * @return             the wrapper
	 */
	public static ContentApplicationFrame wrap(JComponent component) {
		if (s_factory == null) {
			s_factory = new ContentWrapperFactory();
		}
		return s_factory.wrapComponent(component);
	}
	
	/** {@inheritDoc} */
	@Override
	protected ContentApplicationFrame createApplicationFrame(String name, String title, JComponent component) {
		DockingApplication application = (DockingApplication) GUIApplication.getInstance();
		
		ContentManager contentManager = application.getToolWindowManager().getContentManager();
		
		// Does a content already have this name? (names must be unique)
		String uniqueName = title;
		if (contentManager.getContent(uniqueName) != null) {
			int index = 1;
			while (contentManager.getContent(uniqueName + " (" + index + ")") != null) {
				index++;
			}
			uniqueName = uniqueName + " (" + index + ")";
		}
		
		ContentConfiguration contentDescriptor = new ContentConfiguration(uniqueName, title, null, component);
		
		return new ContentApplicationFrame(contentDescriptor);
	}
}
