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

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * This class exports a JasperPrint to a PDF file by popping up a dialog file.
 * The compilation and filling of the report is done in the report generator.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @see ch.elca.el4j.demos.jasper.gui.reporting.ReportGenerator
 *
 * @author Fabian Reichlin (FRE)
 */
public class ReportSaver extends JFileChooser {

	/**
	 * Version number needed by serialization.
	 */
	public static final long serialVersionUID = 1L;
	
	/**
	 * JasperPrint document.
	 */
	private JasperPrint m_jasperPrint;

	/**
	 * Default constructor.
	 */
	public ReportSaver() {
		
		super();
	}
	
	/**
	 * Shows a "Save File" dialog. This method stores a JasperReport
	 * as a PDF file.
	 * 
	 * @param parent The graphical component of the dialog's parent.
	 * @return The return value of the dialog.
	 * 
	 * @see ReportViewer
	 */
	@Override
	public int showSaveDialog(Component parent) {
		
		FileFilter filter = new FileFilter() {
			
			public boolean accept(File file) {
				String fileName = file.getName();
				return fileName.endsWith(".pdf") || file.isDirectory();
			}
			public String getDescription() {
				return "PDF Report Files (*.pdf)";
			}
		};
		this.addChoosableFileFilter(filter);
		
		int retVal = super.showSaveDialog(parent);
		if (retVal == APPROVE_OPTION) {
			File file = getSelectedFile();
			String fileName = file.toString();
			
			if (!fileName.endsWith("pdf")) {
				fileName = fileName + ".pdf";
			}
			
			ReportGenerator generator = new ReportGenerator();
			m_jasperPrint = generator.getJasperPrint();
			
			try {
				JasperExportManager.exportReportToPdfFile(
					m_jasperPrint, fileName);
			} catch (JRException e) {
				e.printStackTrace();
			}
		}
		return retVal;
	}
}