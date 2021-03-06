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
package ch.elca.el4j.demos.gui;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;

import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * Base class for main forms.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 * @author Stefan Wismer (SWI)
 */
public class MainFormActions {

	/**
	 * The GUI application of the main app.
	 */
	private GUIApplication app;
	
	/**
	 * Default constructor.
	 * @param app The GUI application of the main app.
	 */
	public MainFormActions(GUIApplication app) {
		this.app = app;
	}
	
	/**
	 * Show the ResourceInjectionDemoForm.
	 */
	@Action
	public void showDemo1() {
		app.show("resourceInjectionDemoForm");
	}
	
	/**
	 * Show the CancelableDemoForm.
	 */
	@Action
	public void showDemo2() {
		app.show("cancelableDemoForm");
	}

	/**
	 * Show the Master/Detail DemoForm.
	 */
	@Action
	public void showDemo3() {
		app.show("masterDetailDemoForm");
	}
	
	/**
	 * Show the BindingDemoForm.
	 */
	@Action
	public void showDemo4() {
		app.show("bindingDemoForm");
	}
	
	/**
	 * Show the EventBusDemoForm.
	 */
	@Action
	public void showDemo5() {
		app.show("eventBusDemoForm");
	}
	
	/**
	 * Show the SeearchForm.
	 */
	@Action
	public void showSearch() {
		app.show("searchForm");
	}
	
	/**
	 * Show the RefDB demo.
	 */
	@Action
	public void showRefDB() {
		app.show("refDBDemoForm");
	}
	

	/**
	 * CookSwing XML Demo.
	 */
	@Action
	public void showDemo6() {
		app.show("xmlDemoForm");
	}
	
	/**
	 * EhCache Demo.
	 */
	@Action
	public void showCachingDemo() {
		app.show("cachingDemoForm");
	}
	
	/**
	 * Send an example event.
	 */
	@Action
	public void sendExampleEvent() {
		EventBus.publish(new ExampleEvent("I'm an Example Event!"));
	}
	
	/**
	 * Throw an example exception.
	 */
	@Action
	public void throwException() {
		throw new IllegalArgumentException();
	}
	

	/**
	 * Show the about dialog.
	 */
	@Action
	public void about() {
		app.show("aboutDialog");
	}
	
	/**
	 * A help only for admins (for demo purpose only).
	 */
	@Action
	public void help() {
		app.show("helpDialog");
	}
	
}
