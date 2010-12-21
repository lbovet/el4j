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
package ch.elca.el4j.services.gui.swing.wrapper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.services.gui.swing.frames.ApplicationFrame;

/**
 * This abstract class helps wrapping components into a container like
 * {@link JInternalFrame} or {@link JFrame}. As the module Swing supports
 * both MDI and docking, this has been introduced to use a single component
 * in both approaches. Depending on the base class of the application
 * ({@link MDIApplication} or {@link DockingApplication}), forms provided
 * as {@link JComponent}s are automatically wrapped into the right "container"
 * (see {@link GUIApplication#show(JComponent)}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @param <T>    the type of wrapper
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractWrapperFactory<T extends ApplicationFrame> {
	/**
	 * A mapping between wrapped component and wrapper.
	 */
	protected static Map<JComponent, WeakReference<ApplicationFrame>> s_componentToWrapper
		= new HashMap<JComponent, WeakReference<ApplicationFrame>>();
	
	/**
	 * Wraps a GUI component.
	 *
	 * @param component    the component to wrap
	 * @return             the wrapper
	 */
	@SuppressWarnings("unchecked")
	protected T wrapComponent(JComponent component) {
		// check if component has already a wrapper
		if (s_componentToWrapper.get(component) != null) {
			if (s_componentToWrapper.get(component).get() != null) {
				return (T) s_componentToWrapper.get(component).get();
			}
		}
		ApplicationContext appContext = Application.getInstance().getContext();
		ResourceMap map = appContext.getResourceMap(component.getClass());
		
		String name = map.getString("name");
		if (name == null) {
			name = component.getClass().getName();
		}
		
		String title = map.getString("title");
		if (title == null) {
			title = name;
		}
		
		T wrapper = createApplicationFrame(name, title, component);
		
		
		// inject values from properties file
		component.setName(name);
		map.injectComponents(component);
		
		s_componentToWrapper.put(component, new WeakReference<ApplicationFrame>(wrapper));
		
		return wrapper;
	}
	

	/**
	 * @return    the concrete wrapper
	 */
	protected abstract T createApplicationFrame(String name, String title, JComponent component);
	
	/**
	 * @param component    a wrapped component
	 * @return             the corresponding wrapper
	 */
	public static ApplicationFrame getFrame(JComponent component) {
		if (s_componentToWrapper.get(component) == null) {
			return null;
		} else {
			return s_componentToWrapper.get(component).get();
		}
	}
	
	/**
	 * Remove the wrapper from a component.
	 * @param component    the component to remove the wrapper
	 */
	public static void removeWrapper(JComponent component) {
		s_componentToWrapper.remove(component);
	}
}
