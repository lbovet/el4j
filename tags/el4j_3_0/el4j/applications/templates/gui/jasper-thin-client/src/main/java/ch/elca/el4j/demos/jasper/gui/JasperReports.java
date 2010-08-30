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

import java.util.Arrays;
import java.util.List;

import javax.swing.JMenuBar;

import org.jdesktop.application.Action;

import ch.elca.el4j.demos.gui.extension.AbstractGUIExtension;
import ch.elca.el4j.demos.jasper.gui.reporting.ReportSaver;
import ch.elca.el4j.demos.jasper.gui.reporting.ReportViewer;

/**
 * Sample application extension that extends Thin Client by adding a reporting section.
 *
 * See also associated JasperReports.properties file that contains resources.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 */
public class JasperReports extends AbstractGUIExtension {
	
	
	/** {@inheritDoc} */
	public void extendMenuBar(JMenuBar menubar) {
		extendMenuBarDefault(menubar, "reportMenu");
	}
	
	/** {@inheritDoc} */
	public List<String> getActions() {
		return Arrays.asList("showReportViewer", "showReportSaver");
	}
	
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
		saver.showSaveDialog(application.getMainFrame());
	}
}
