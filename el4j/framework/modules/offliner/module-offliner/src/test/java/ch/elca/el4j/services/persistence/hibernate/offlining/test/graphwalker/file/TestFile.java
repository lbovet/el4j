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
package ch.elca.el4j.services.persistence.hibernate.offlining.test.graphwalker.file;

import java.util.HashSet;
import java.util.Set;

/**
 * A test class representing a "file system object".
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
public final class TestFile {

	/** The parent. Must be a directory or <code>null</code> for a root. */
	private TestFile m_parent;
	
	/** Whether this is a directory. */
	private boolean m_isDirectory;
	
	/** The file name. */
	private String m_name;
	
	/** In a directory, the set of all contained files. */ 
	private Set<TestFile> m_files;
	
	/** Internal use only. */
	private TestFile() { }
	
	/**
	 * Get the parent.
	 * @return The parent.
	 */
	public TestFile getParent() {
		return m_parent;
	}

	/**
	 * @return Whether this file is a directory.
	 */
	public boolean isDirectory() {
		return m_isDirectory;
	}

	/**
	 * Get the name.
	 * @return The name.
	 */
	public String getName() {
		return m_name + (m_isDirectory ? "/" : "");
	}

	/**
	 * Setter for name.
	 * @param name The new name to set.
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * Get the files.
	 * @return The files.
	 */
	public Set<TestFile> getFiles() {
		if (!m_isDirectory) {
			return new HashSet<TestFile>();
		}
		Set<TestFile> files = new HashSet<TestFile>();
		files.addAll(m_files);
		return files;
	}
	
	/**
	 * Create a new root. The name will be "/".
	 * @return The root.
	 */
	public static TestFile createRoot() {
		TestFile root = new TestFile();
		root.m_isDirectory = true;
		root.m_files = new HashSet<TestFile>();
		root.m_name = "";
		root.m_parent = null;
		return root;
	}
	
	/**
	 * Create a directory.
	 * @param name The name.
	 * @param parent The parent.
	 * @return The directory.
	 */
	public static TestFile createDirectory(String name, TestFile parent) {
		if (!parent.isDirectory()) {
			throw new IllegalArgumentException("Parent must be a directory.");
		}
		String dirName = name;
		if (dirName.endsWith("/")) {
			dirName = dirName.substring(0, dirName.length() - 1);
		}
		if (dirName.contains("/")) {
			throw new IllegalArgumentException("Slash (/) not allowed in names.");
		}
		if (dirName.equals("")) {
			throw new IllegalArgumentException("Empty name not allowed.");
		}
		
		TestFile dir = new TestFile();
		dir.m_name = dirName;
		dir.m_isDirectory = true;
		dir.m_files = new HashSet<TestFile>();
		dir.m_parent = parent;
		parent.m_files.add(dir);
		return dir;
	}
	
	/**
	 * Create a file.
	 * @param name The file name.
	 * @param parent The parent.
	 * @return The file.
	 */
	public static TestFile createFile(String name, TestFile parent) {
		if (!parent.isDirectory()) {
			throw new IllegalArgumentException("Parent must be a directory.");
		}
		if (name.contains("/")) {
			throw new IllegalArgumentException("Slash (/) not allowed in names.");
		}
		if (name.equals("")) {
			throw new IllegalArgumentException("Empty name not allowed.");
		}

		TestFile file = new TestFile();
		file.m_isDirectory = false;
		file.m_name = name;
		file.m_parent = parent;
		parent.m_files.add(file);
		return file;
	}

	/**
	 * @return The full path and name.
	 */
	public String getPath() {
		String path = "";
		if (m_name.equals("")) {
			return "/";
		}
		TestFile current = this;
		while (!current.m_name.equals("")) {
			path = "/" + current.m_name + path; 
			current = current.m_parent;
		}
		if (m_isDirectory) {
			path += "/";
		}
		return path;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof TestFile) {
			TestFile other = (TestFile) obj;
			return getPath().equals(other.getPath());
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getPath();
	}
}
