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
package ch.elca.el4j.demos.jasper.gui.reporting;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * This class opens a viewer to display a JasperPrint. The compilation and
 * filling of the report is done in the report generator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see ch.elca.el4j.demos.jasper.gui.reporting.ReportGenerator
 * @author Fabian Reichlin (FRE)
 */
public class ReportViewer {

	/**
	 * JasperPrint document.
	 */
	private JasperPrint m_jasperPrint;
	
	/**
	 * Default constructor. Calls the report generator and stores
	 * the JasperPrint document locally.
	 */
	public ReportViewer() {
		
		ReportGenerator generator = new ReportGenerator();
		m_jasperPrint = generator.getJasperPrint();
	}
	
	/**
	 * Shows the jasper viewer to display the JasperPrint
	 * document.
	 */
	public void showViewer() {
		
		JasperViewer.viewReport(m_jasperPrint, false);
	}
}
