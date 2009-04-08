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
package ch.elca.el4j.maven.plugins.coberturaruntime;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.coberturaruntime.CoberturaRuntimeControllerImpl;

/**
 * Goal to create the cobertura report as via the JMX MBean.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * 
 * @goal report
 * @requiresProject true
 */
public class CoberturaReport extends AbstractCoberturaMojo {
	//Checkstyle: MemberName off
	
	/**
	 * If every report should be in a separate directory.
	 *
	 * @parameter expression="${cobertura-runtime.keepReports}"
	 * @required
	 * @readonly
	 */
	protected String keepReports;
	
	//Checkstyle: MemberName on
	
	/**
	 * Executes the plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		System.setProperty(CoberturaRuntimeControllerImpl.COBERTURA_RUNTIME_DATA_DIRECTORY,
			coberturaDataDirectory.getAbsolutePath());
		System.setProperty(CoberturaRuntimeControllerImpl.COBERTURA_RUNTIME_DATA_FILENAME,
			coberturaDataFilename);
		System.setProperty(CoberturaRuntimeControllerImpl.COBERTURA_RUNTIME_SRC_COL_DIR_NAME,
			sourceCollectorDirectoryName);
		System.setProperty(CoberturaRuntimeControllerImpl.COBERTURA_RUNTIME_KEEP_REPORTS,
			keepReports);
		CoberturaRuntimeControllerImpl impl = new CoberturaRuntimeControllerImpl(true);
		String reportDirectory = impl.generateReport();
		getLog().info("Report generated in directory '" + reportDirectory + "'.");
	}
}
