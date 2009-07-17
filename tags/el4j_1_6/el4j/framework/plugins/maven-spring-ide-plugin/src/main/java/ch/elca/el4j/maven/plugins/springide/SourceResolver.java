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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 * 
 * This class is a helper class that will look for a file that defines the Module Application Context.
 * First it will check if it finds a .xml file, as this would be the preferred way to define the Module
 * Application Context in a web project, afterwards it will check through all .java files in the source
 * alphabetically and return the first one that contains // $$ BEANS INCLUDE $$
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Daniel Thomas (DTH)
 */

public class SourceResolver {

	/**
	 * Search for a file in the sourcefiles that contains the module application context.
	 * 
	 * @param baseDir
	 *            are the paths to the sourcefiles
	 * @return File of first found document that contains $$ BEANS EXCLUDE
	 * @throws FileNotFoundException
	 */
	public static File getSourceFile(File baseDir) {
		File returnFile = null;
		List<File> javaFiles = null;

		javaFiles = getAllRelevantFiles(baseDir);

		/*
		 * check if there is any .java file that contains // $$ BEANS INCLUDE $$, and return it
		 */

		javaFiles = sortJavaFilesAlpabetically(javaFiles);
		
		for (File javaFile : javaFiles) {
			Scanner scan = null;
			try {
				scan = new Scanner(javaFile);

				while (scan.hasNextLine()) {
					if (scan.nextLine().contains("$$ BEANS INCLUDE $$")) {
						return javaFile;
					}
				}
				
			} catch (Exception e) {
				/* nothing to be done */
			} finally {
				if (scan != null) {
					scan.close();
				}
			}
		}

		return returnFile;
	}

	/**
	 * Simple seclection sort on the names of the files in the javaFiles List.
	 */
	private static List<File> sortJavaFilesAlpabetically(List<File> javaFiles) {
		File[] javaFilesAsArray = (File[]) javaFiles.toArray(new File[1]);
		Arrays.sort(javaFilesAsArray);
		return Arrays.asList(javaFilesAsArray);

	}

	/**
	 * Populates the Lists javaFiles and xmlFiles with the corresponding Files it found in the sourceDirectory. Checks
	 * recursevely through folders.
	 * 
	 * @param sourceDirectory
	 *            folder to start with
	 */

	private static List<File> getAllRelevantFiles(File sourceDirectory) {
		String[] extensions = {"java"};
		return (List<File>) FileUtils.listFiles(sourceDirectory, extensions, true);

	}

}
