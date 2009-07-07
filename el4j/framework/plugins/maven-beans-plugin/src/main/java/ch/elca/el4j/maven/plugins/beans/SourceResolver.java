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
package ch.elca.el4j.maven.plugins.beans;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
 * @author your name (???)
 */

public class SourceResolver {
	/**
	 * Will be filled with all .java files in the baseDir.
	 */
	private List<File> javaFiles;
	/**
	 * Simple constructor that sets up the Lists which getAllRelevantFiles will populate.
	 */
	public SourceResolver() {
		javaFiles = new ArrayList<File>();
	}

	/**
	 * Search for a file in the sourcefiles that contains the module application context.
	 * 
	 * @param baseDir
	 *            are the paths to the sourcefiles
	 * @return File of first found document that contains $$ BEANS EXCLUDE
	 * @throws FileNotFoundException
	 */
	public File getSourceFile(File baseDir) throws FileNotFoundException {
		File returnFile = new File("");

		getAllRelevantFiles(baseDir);
		
		/*
		 *  check if there is any .java file that contains // $$ BEANS INCLUDE
		 * $$, and return it
		 */

		sortJavaFilesAlpabetically();

		for (File javaFile : javaFiles) {

			Scanner scan = new Scanner(javaFile);

			while (scan.hasNextLine()) {
				if (scan.nextLine().contains("$$ BEANS INCLUDE $$")) {
					return javaFile;
				}
			}

		}

		return returnFile;
	}

	/**
	 * Simple seclection sort on the names of the files in the javaFiles List.
	 */
	private void sortJavaFilesAlpabetically() {
		File temp = new File("");
		int placeToSwapTo = 0;
		for (int i = 0; i < javaFiles.size(); i++) {
			// find lexigraphically smallest filename in the rest of the list
			temp = javaFiles.get(i);
			for (int j = i + 1; j < javaFiles.size(); j++) {
				if (javaFiles.get(j).getName().compareTo(temp.getName()) <= 0) {
					temp = javaFiles.get(j);
					placeToSwapTo = j;
				}
			}
			// swap file at i and the one at placeToSwapTo
			javaFiles.set(placeToSwapTo, javaFiles.get(i));
			javaFiles.set(i, temp);

		}

	}

	/**
	 * Populates the Lists javaFiles and xmlFiles with the corresponding Files it found in the sourceDirectory. Checks
	 * recursevely through folders.
	 * 
	 * @param sourceDirectory
	 *            folder to start with
	 */

	private void getAllRelevantFiles(File sourceDirectory) {
		File[] sourceArray = sourceDirectory.listFiles();
		/* check every file in sourceDirectory */
		for (int i = 0; i < sourceArray.length; i++) {
			if (sourceArray[i].isDirectory()) {
				getAllRelevantFiles(sourceArray[i]);
			} else if (sourceArray[i].getAbsolutePath().contains(".java")) {
				javaFiles.add(sourceArray[i]);
			} 

		}

	}

}
