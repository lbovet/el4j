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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * Abstract cobertura mojo.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractCoberturaMojo extends AbstractMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Is <code>true</code> if the test files should be instrumented too. Default is <code>false</code>.
	 * 
	 * @parameter expression="${cobertura-runtime.includeTestFiles}" default-value="false"
	 * @required
	 */
	protected boolean includeTestFiles;
	
	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	/**
	 * Is the filename of the file where cobertura will report to.
	 * 
	 * @parameter expression="${cobertura-runtime.dataFilename}"
	 * @required
	 * @readonly
	 */
	protected String coberturaDataFilename;
	
	/**
	 * Is the directory of the file where cobertura will report to.
	 * 
	 * @parameter expression="${cobertura-runtime.dataDirectory}"
	 * @required
	 * @readonly
	 */
	protected File coberturaDataDirectory;

	/**
	 * Name of the source collector directory.
	 *
	 * @parameter expression="${cobertura-runtime.sourceCollectorDirectoryName}"
	 * @required
	 * @readonly
	 */
	protected String sourceCollectorDirectoryName;
	
	//Checkstyle: MemberName on
}
