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

import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.demos.model.ServiceBroker;
import ch.elca.el4j.services.gui.swing.GUIApplication;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * This class is the base class for generating a jasper report. By instantiating
 * this class, a new jasper report will be generated.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 */
public class ReportGenerator {

	/**
	 * The reference service of cached references.
	 */
	private ReferenceService service;
	
	/**
	 * JasperPrint document.
	 */
	private JasperPrint jasperPrint;
	
	/**
	 * Default constructor. Loads the model (database data) and
	 * generates the report.
	 */
	public ReportGenerator() {
	
		loadModel();
		generateReport();
	}
	
	/**
	 * Returns the compiled and filled jasper report.
	 * 
	 * @return The filled JasperPrint.
	 */
	public JasperPrint getJasperPrint() {
		
		return jasperPrint;
	}
	
	/**
	 * Loads the model (database data) and stores it locally.
	 */
	private void loadModel() {
		
		GUIApplication app = GUIApplication.getInstance();
		ServiceBroker.setApplicationContext(
			app.getSpringContext());
		service = ServiceBroker.getReferenceService();
	}
	
	/**
	 * Compiles and fills the jasper report.
	 */
	private void generateReport() {
		
		if (service.getAllReferences().isEmpty()) {
			generateSampleReferences();
		}
		
		JasperReport jasperReport;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("ReportTitle", "RefDB Sample Report");
		try {
			jasperReport = JasperCompileManager.compileReport(
				"src/main/resources/jasper/RefDB.jrxml");
			jasperPrint = JasperFillManager.fillReport(
				jasperReport, parameters,
				new JRBeanCollectionDataSource(service.getAllReferences()));
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sample link generator in case RefDB is empty.
	 */
	private void generateSampleReferences() {
		
		Link link = new Link();
		link.setName("Boing Boing");
		link.setDescription("A directory of wonderful things.");
		link.setUrl("http://www.boingboing.net/");
		service.saveReference(link);
		
		Link link2 = new Link();
		link2.setName("Mac Rumors");
		link2.setDescription("Apple Mac Rumors and News You Care About.");
		link2.setUrl("http://www.macrumors.com/");
		service.saveReference(link2);
	}
}
