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
package ch.elca.el4j.maven.plugins.springide;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import ch.elca.el4j.maven.plugins.springide.SpringIDEMojo.LogCallback;

/**
 * 
 * This class is a simple helper class that takes a list of formatted paths and
 * writes them into the .springBeans file. 
 * Additionally it can set the springNature in the .projects file of eclipse.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "DTH"
 * );</script>
 *
 * @author Daniel Thomas (DTH)
 */

public class SpringNatureForcer {
	
	/**
	 * Lets the class use the logging functionality of the Mojo.
	 */
	private static LogCallback m_logger;
	
	
	/**
	 * Forces SpringNature (in the .project file that is checked by eclipse). -> adds a tag in the nature tag of the
	 * (xml) .project file
	 */
	public static void forceSpringNature(File baseDirectory) {
		try {
			File dotProject = new File(baseDirectory, ".project");
			String content = FileUtils.readFileToString(dotProject, null);
			if (content.indexOf("<nature>org.springframework.ide.eclipse.core.springnature</nature>") < 0) {
				m_logger.log("Add spring nature to the eclipse .project file");
				try {
					Xpp3Dom dom = Xpp3DomBuilder.build(new FileReader(dotProject));
					Xpp3Dom nature = new Xpp3Dom("nature");
					nature.setValue("org.springframework.ide.eclipse.core.springnature");
					dom.getChild("natures").addChild(nature);
					FileWriter writer = new FileWriter(dotProject);
					Xpp3DomWriter.write(writer, dom);
					writer.close();
				} catch (Exception e) {
					m_logger.log("Failed to add missing tomcat nature to the eclipse .project file");
				}
			}
		} catch (IOException e) {
			m_logger.log("Failed to retrieve the Eclipse .project file");
		}
	}
	/**
	 * 
	 * Here one can set a logger for debug output.
	 * 
	 * @param log is a logger as one would get in the main class through getLog().
	 */

	static void setLogger(LogCallback log) {
		m_logger = log;
	}
	
}
