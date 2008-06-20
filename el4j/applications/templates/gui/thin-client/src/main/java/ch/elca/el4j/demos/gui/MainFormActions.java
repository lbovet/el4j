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
import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * Base class for main forms.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Fabian Reichlin (FRE)
 * @author Stefan Wismer (SWI)
 */
public class MainFormActions {

	/**
	 * The GUI application of the main app.
	 */
	private GUIApplication m_app;
	
	/**
	 * Default constructor.
	 * @param app The GUI application of the main app.
	 */
	public MainFormActions(GUIApplication app) {
		m_app = app;
	}
	
	@Action
	public void showDemo1() {
		m_app.show("ResourceInjectionDemoForm");
	}
	
	@Action
	public void showDemo2() {
		m_app.show("CancelableDemoForm");
	}

	@Action
	public void showDemo3() {
		m_app.show("MasterDetailDemoForm");
	}
	
	@Action
	public void showDemo4() {
		m_app.show("BindingDemoForm");
	}
	
	@Action
	public void showDemo5() {
		m_app.show("EventBusDemoForm");
	}
	
	@Action
	public void showSearch() {
		m_app.show("SearchDialog");
	}
	
	@Action
	public void showRefDB() {
		m_app.show("RefDBDemoForm");
	}
	
	@Action
	public void sendExampleEvent() {
		EventBus.publish(new ExampleEvent("I'm an Example Event!"));
	}
	
	/**
	 * Show the about dialog.
	 */
	@Action
	public void about() {
		m_app.show("AboutDialog");
	}
}
