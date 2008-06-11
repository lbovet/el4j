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
package ch.elca.el4j.plugins.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Extracts configuration information (inclusive and exclusive configuration
 * locations) from a java or xml file.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhaed (DBD)
 */
public class ConfigurationExtractor {

	/** The reader that holds the locations for us. */
	private AbstractReader m_reader;
	
	/**
	 * Extract configuration from a source file.
	 * @param sourceFile The file to read from.
	 */
	public ConfigurationExtractor(File sourceFile) {
		
		if (!sourceFile.exists() || !sourceFile.canRead()) {
			throw new RuntimeException("Reading from source file impossible.");
		}
		
		BufferedReader r = getReader(sourceFile);
		
		if (sourceFile.toString().endsWith(".java")) {
			m_reader = new JavaReader();
		} else if (sourceFile.toString().endsWith(".xml")) {
			m_reader = new XmlReader();
		} else {
			throw new Error("Not yet implemented.");
		}
		m_reader.read(r);
	}
	
	/**
	 * Get a reader for the file, handle exceptions.
	 * @param sourceFile The file.
	 * @return A BufferedReader for the file.
	 */
	private BufferedReader getReader(File sourceFile) {
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(sourceFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return r;
	}
	
	/**
	 * @return Returns the inclusive configuration.
	 */
	public String[] getInclusive() {
		return m_reader.getInclusive();
	}

	/**
	 * @return Returns the exclusive configuration.
	 */
	public String[] getExclusive() {
		return m_reader.getExclusive();
	}
}
