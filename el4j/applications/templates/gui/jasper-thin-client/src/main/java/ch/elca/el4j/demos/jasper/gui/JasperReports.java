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
package ch.elca.el4j.demos.jasper.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.jdesktop.application.Action;

import ch.elca.el4j.demos.gui.GUIExtension;
import ch.elca.el4j.demos.jasper.gui.reporting.ReportSaver;
import ch.elca.el4j.demos.jasper.gui.reporting.ReportViewer;
import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * Sample application extension that extends Thin Client by adding a reporting section.
 *
 * See also associated JasperReports.properties file that contains resources.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Fabian Reichlin (FRE)
 */
public class JasperReports implements GUIExtension {
	/**
	 * The main application which gets extended.
	 */
	GUIApplication m_application;
	
	/** {@inheritDoc} */
	public void setApplication(GUIApplication application) {
		m_application = application;
	}
	
	/** {@inheritDoc} */
	public void extendMenuBar(JMenuBar menubar) {
		String[] reportMenuActionNames
			= {"showReportViewer", "showReportSaver"};
		
		JMenu menu = new JMenu();
		menu.setName("reportMenu");
		
		for (String actionName : reportMenuActionNames) {
			if (actionName.equals("---")) {
				menu.add(new JSeparator());
			} else {
				JMenuItem menuItem = new JMenuItem();
				menuItem.setAction(m_application.getAction(actionName));
				//menuItem.setIcon(null);
				menu.add(menuItem);
			}
		}
		
		menubar.add(menu, menubar.getComponentCount() - 2);
	}
	
	/** {@inheritDoc} */
	public void extendToolBar(JToolBar menubar) { }
	
	/**
	 * Shows the report viewer.
	 */
	@Action
	public void showReportViewer() {
		ReportViewer viewer = new ReportViewer();
		viewer.showViewer();
	}
	
	/**
	 * Shows a save dialog to store the report as PDF.
	 */
	@Action
	public void showReportSaver() {
		ReportSaver saver = new ReportSaver();
		saver.showSaveDialog(m_application.getMainFrame());
	}
}
