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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;



/**
 * Base class for bean fiel resolving. Provides common methods.
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
public abstract class AbstractResolver implements Resolver {

	/** Stores the classpath to check files against. */
	protected Set<String> m_classpath;
	
	/**
	 * Set up a resolver.
	 * @param classpath The classpath to check valid files against.
	 */
	public AbstractResolver(URL[] classpath) {
		m_classpath = new HashSet<String>();
		for (URL u : classpath) {
			m_classpath.add(strip(u.toString()));
		}
	}

	/**
	 * Strip the protocol from a string to produce a standard form.
	 * @param source The string.
	 * @return The string with any leading "`getProtocol()`:" removed.
	 */
	protected String strip(String source) {
		String pre = getProtocol() + ":";
		if (source.startsWith(pre)) {
			return source.replaceFirst(pre, "");
		} else {
			return source;
		}
	}
	
	/**
	 * Create a subdirectory in root of the given name if it does not exist
	 * and ensure it is valid.
	 * @param name The name of the directory.
	 * @param root The root of the output directories.
	 * @throws IOException If something is wrong with the file system.
	 * @return A {@link File} object pointing to the directory.
	 */
	protected File createDir(String name, File root) throws IOException {
		File f = new File(root, name);
		if (!f.exists()) {
			f.mkdir();
		}
		if (!f.isDirectory()) {
			throw new IOException("File " + name + " in target is not a "
				+ "directory.");
		}
		return f;
	}
	
	/**
	 * Copy a file from source to dest.
	 * @param source The source.
	 * @param dest The destination. It is overwritten if it exists.
	 * @throws IOException If something goes wrong.
	 */
	protected void copyFile(File source, File dest) throws IOException {
		/* ensures that file exists and is empty */
		emptyFile(dest);
		
		BufferedReader in = new BufferedReader(new FileReader(source));
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		String line;
		while ((line = in.readLine()) != null) {
			out.write(line + "\n");
		}
		in.close();
		out.close();
	}
	
	/**
	 * Ensures a file exists and is empty.
	 * @param dest The destination file.
	 * @throws IOException
	 */
	protected void emptyFile(File dest) throws IOException {
		if (dest.exists()) {
			if (!dest.delete()) {
				throw new IOException("Cannot remove existing file " + dest);
			}
		}
		
		if (!dest.createNewFile()) {
			throw new IOException("Cannot create file " + dest);
		}
	}
}
