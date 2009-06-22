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
package ch.elca.el4j.maven.plugins.beans.resolvers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Resolver for jar entries.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class JarResolver extends AbstractResolver {

	/**
	 * @param classpath The classpath.
	 */
	public JarResolver(URL[] classpath) {
		super(classpath);
	}

	/** {@inheritDoc} */
	public boolean accept(String file) {
		int pos = file.indexOf(".jar!") + ".jar".length();
		return m_classpath.contains(strip(file.substring(0, pos)));
	}

	/**
	 * Holds the path segments of a jar entry.
	 */
	class JarEntryPath {
		
		/** Path to the jar file. */
		String m_jarPath;
		
		/** Name of the jar file. */
		String m_jarName;
		
		/** Path to the file within the jar. */
		String m_filePath;
		
		/** Name of the file in the jar. */
		String m_fileName;
		
		/**
		 * Construct a JarEntryPath.
		 * @param file The string describing the file's absolute path.
		 */
		JarEntryPath(String file) {
			String regex = "(.*)/([^/]*\\.jar)!/?"
				+ "(.*)/([^/]*)";
			
			String s = strip(file).replace("\\", "/");
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(s);
			if (!m.matches()) {
				throw new RuntimeException("Invalid jar entry.");
			}
			
			// Checkstyle: MagicNumber off
			m_jarPath = m.group(1);
			m_jarName = m.group(2);
			m_filePath = m.group(3);
			m_fileName = m.group(4);
			//Checkstyle: MagicNumber on
		}
	}
	
	/** {@inheritDoc} */
	public String getProtocol() {
		return "jar";
	}

	/** {@inheritDoc} */
	@Override protected String strip(String source) {
		String s = super.strip(source);
		return s.replaceFirst("^file:", "");
	}

	/** {@inheritDoc} */
	public void copy(String file, File target) throws IOException {
		JarEntryPath p = new JarEntryPath(file);
		JarFile jarFile = new JarFile(p.m_jarPath + "/" + p.m_jarName);
		JarEntry entryFile = jarFile.getJarEntry(
			(p.m_filePath.equals("") ? ""
			: p.m_filePath + "/") + p.m_fileName);
		if (entryFile == null) {
			throw new IOException("Entry does not exist. " + file);
		}
		
		File destDir = createDir(p.m_jarName, target);
		
		File dest = new File(destDir, p.m_filePath.replace("/", "__")
			+ (p.m_filePath.equals("") ? "" : "__") + p.m_fileName);

		emptyFile(dest);
		
		// Can't use copyFile() as we don't have a File object as source.
		
		BufferedWriter w = new BufferedWriter(new FileWriter(dest));
		BufferedReader r = new BufferedReader(new InputStreamReader(
			jarFile.getInputStream(entryFile)));
		String line;
		while ((line = r.readLine()) != null) {
			w.write(line + "\n");
		}
		
		w.close();
		r.close();
		jarFile.close();
	}
	
}
