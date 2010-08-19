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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is a simple helper class that takes a list of formatted paths and
 * writes them into the .springBeans file. 
 * Additionally it can set the springNature in the .projects file of eclipse.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */

public final class SpringNatureForcer {
	
	/**
	 * The logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(SpringNatureForcer.class);
	
	/**
	 * Hidden constructor.
	 */
	private SpringNatureForcer() { }
	
	/**
	 * Forces SpringNature (in the .project file that is checked by eclipse). -> adds a tag in the nature tag of the
	 * (xml) .project file
	 * 
	 * @param baseDirectory    the project base directory
	 */
	public static void forceSpringNature(File baseDirectory) {
		String content = null;
		File dotProject = null;

		try {
			dotProject = new File(baseDirectory, ".project");
			content = FileUtils.readFileToString(dotProject, null);
		} catch (IOException e) {
			s_logger.debug("Failed to retrieve the Eclipse .project file");

		}

		if (content.indexOf("<nature>org.springframework.ide.eclipse.core.springnature</nature>") < 0) {
			s_logger.debug("Add spring nature to the eclipse .project file");

			Xpp3Dom dom = null;

			try {
				dom = Xpp3DomBuilder.build(new FileReader(dotProject));
			} catch (XmlPullParserException e) {
				s_logger.debug("Failed to add missing tomcat nature to the eclipse .project file");
			} catch (FileNotFoundException e) {
				s_logger.debug("Failed to retrieve the Eclipse .project file");
			} catch (IOException e) {
				s_logger.debug("Failed to retrieve the Eclipse .project file");

			}
			Xpp3Dom nature = new Xpp3Dom("nature");
			nature.setValue("org.springframework.ide.eclipse.core.springnature");
			dom.getChild("natures").addChild(nature);
			FileWriter writer = null;
			
			try {
				writer = new FileWriter(dotProject);
				Xpp3DomWriter.write(writer, dom);
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {	
				s_logger.debug("Failed to write eclipse project file.");
			}
		}
		
	}
}
