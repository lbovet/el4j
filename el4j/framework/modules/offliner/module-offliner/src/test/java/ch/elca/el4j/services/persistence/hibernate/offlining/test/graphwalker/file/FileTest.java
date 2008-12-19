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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeVisitor;

import junit.framework.TestCase;

/**
 * Example of using graphwalker for copying files recursively. This is an analogon
 * of synchronizing two databases.
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
@Component
public class FileTest extends TestCase {

	/** A "filesystem". */
	private Map<String, TestFile> m_files;
	
	/** 
	 * Set up.
	 */
	public void setUp() {
		m_files = new HashMap<String, TestFile>();
		
		// Source files to copy.
		create(new String[] {
			"/", "/home/", "/home/text/", "/home/text/one.txt", "/home/text/two.txt",
			"/home/images/", "/home/images/el4j.gif"
		});
		
		// Target directory, and existing files that cause conflicts.
		create(new String[] {
			"/user/", "/user/text/", "/user/text/one.txt"
		});
	}
	
	/**
	 * Copy /home/ to /user/.
	 */
	public void testCopy() {
		List<String> filesToCopy;
		TreeListVisitor treeVisitor = new TreeListVisitor();
		GraphWalker treeWalker = new GraphWalker(treeVisitor, FileUtilities.fileWrapperChild());
		treeWalker.run(m_files.get("/home/"));
		filesToCopy = treeVisitor.getAllNames();
		GraphWalker copyWalker = new GraphWalker(new CopyVisitor("/home/", "/user/"),
			FileUtilities.fileWrapperParent());
		
		List<String> errors = new LinkedList<String>();
		for (String file : filesToCopy) {
			Object[] newErrors = copyWalker.run(m_files.get(file));
			for (Object err : newErrors) {
				errors.add((String) err);
			}
		}
		
		// Make sure it all worked.
		verify(errors);
	}
	
	/**
	 * Make sure everything worked.
	 * @param errors The errors we got.
	 */
	private void verify(List<String> errors) {
		Set<String> expectedFiles = new HashSet<String>();
		expectedFiles.addAll(Arrays.asList(new String[] {
			"/", "/home/", "/home/text/", "/home/text/one.txt", "/home/text/two.txt",
			"/home/images/", "/home/images/el4j.gif",
			"/user/", "/user/text/", "/user/text/one.txt",
			"/user/text/two.txt"
		}));
		assertEquals(expectedFiles, m_files.keySet());
		
		Set<String> expectedErrors = new HashSet<String>();
		expectedErrors.addAll(Arrays.asList(new String[] {
			"Error copying file /home/images/ : Error creating driectory images.",
			"The file or directory /home/images/el4j.gif was not copied because its directory couldn't be created.",
			"File /user/text/one.txt already exists."
		}));
		Set<String> actualErrors = new HashSet<String>();
		actualErrors.addAll(errors);
		assertEquals(expectedErrors, actualErrors);
	}
	
	
	/**
	 * Create files from names.
	 * @param names The names.
	 */
	private void create(String[] names) {
		for (String name : names) {
			TestFile file;
			if (name.equals("/")) {
				file = TestFile.createRoot();
			} else {
				boolean isDir = name.endsWith("/");
				Pattern p = Pattern.compile("(^/?.*/)([^/]+/?$)");
				Matcher m = p.matcher(name);
				if (!m.matches()) {
					throw new IllegalArgumentException();
				}
				String parent = m.group(1);
				if (parent.equals("")) {
					parent = "/";
				}
				String fileName = m.group(2);
				TestFile parentFile = m_files.get(parent);
				if (isDir) {
					file = TestFile.createDirectory(fileName, parentFile);
				} else {
					file = TestFile.createFile(fileName, parentFile);
				}
			}
			m_files.put(file.getPath(), file);
		}
	}
	
	/**
	 * List all files in a directory.
	 */
	static class TreeListVisitor implements NodeVisitor {

		/** The found files. Order is preserved. */
		private Set<String> m_allFiles = new LinkedHashSet<String>();
		
		/** {@inheritDoc} */
		public java.lang.Object markError(java.lang.Object node,
			java.lang.Object cause) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc} */
		public NodeState preVisit(java.lang.Object node) {
			if (node instanceof TestFile) {
				TestFile file = (TestFile) node;
				if (m_allFiles.contains(file.getPath())) {
					// Seen this before.
					return NodeState.PROCESSED;
				} else {
					return NodeState.UNSEEN;
				}
			} else {
				throw new IllegalArgumentException();
			}
			
		}

		/** {@inheritDoc} */
		public void visit(java.lang.Object node) throws NodeException {
			if (node instanceof TestFile) {
				TestFile file = (TestFile) node;
				m_allFiles.add(file.getPath());
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		/**
		 * @return The found filenames.
		 */
		public List<String> getAllNames() {
			List<String> names = new ArrayList<String>(m_allFiles.size());
			names.addAll(m_allFiles);
			return names;
		}
	}
	
	/**
	 * Copy files from source to dest using relative paths and throwing "ERROR"
	 * if a file exists. 
	 */
	class CopyVisitor implements NodeVisitor {

		/** The source directory. */
		private final String m_sourceDir;
		
		/** The target directory. */
		private final String m_destDir;
		
		/**
		 * The copied files/dirs.
		 */
		private Set<String> m_copied;
		 
		/**
		 * The failed files and dirs (to prevent retries across operations).
		 */
		private Set<String> m_failed;
		
		/**
		 * @param sourceDir The source directory.
		 * @param destDir The target directory.
		 */
		public CopyVisitor(String sourceDir, String destDir) {
			m_sourceDir = sourceDir;
			m_destDir = destDir;
			m_copied = new HashSet<String>();
			m_failed = new HashSet<String>();
		}

		/** {@inheritDoc} */
		public Object markError(Object node, Object cause) {
			// A file cannot be copied because its directory can't be made.
			String path = ((TestFile) node).getPath();
			m_failed.add(path);
			return "The file or directory " + path + " was not "
				+ "copied because its directory couldn't be created.";
		}

		/** {@inheritDoc} */
		public NodeState preVisit(Object node) {
			String path = ((TestFile) node).getPath();
			if (!path.startsWith(m_sourceDir)) {
				// We've gone up outside our source directory.
				return NodeState.PROCESSED;
			}
			
			NodeState color;
			if (m_failed.contains(path)) {
				color = NodeState.ERROR;
			} else if (m_copied.contains(path)) {
				color = NodeState.PROCESSED;
			} else {
				color = NodeState.UNSEEN;
			}
			return color;
		}

		/** {@inheritDoc} */
		public void visit(Object node) throws NodeException {
			// Do the real work - try to copy the file.
			String path = ((TestFile) node).getPath();
			String relative = path.substring(m_sourceDir.length());
			String newLocation = m_destDir + relative;

			if (path.equals(m_sourceDir)) {
				return;
			}
			
			// Does it exist? if so and it's a dir, ignore. If it's a file, error.
			if (m_files.containsKey(newLocation)) {
				TestFile file = m_files.get(newLocation);
				if (file.isDirectory()) {
					m_copied.add(path);
					return;
				} else {
					m_failed.add(path);
					throw new NodeException("File " + newLocation + " already exists.");
				}
			}
			
			try {
				copy((TestFile) node, newLocation);
				m_copied.add(path);
			} catch (IllegalArgumentException ex) {
				m_failed.add(path);
				throw new NodeException("Error copying file " + path + " : " + ex.getMessage());
			}
		}
	}
	
	/**
	 * "Copy a file".
	 * @param file The file.
	 * @param newLocation The target.
	 */
	private void copy(TestFile file, String newLocation) {
		Pattern p = Pattern.compile("(.*/)([^/]+/?)$");
		Matcher m = p.matcher(newLocation);
		if (!m.matches()) {
			throw new RuntimeException("Failed to match " + newLocation);
		}
		String newParent = m.group(1);
		String newName = m.group(2);
		
		TestFile parentFile = m_files.get(newParent);
		if (parentFile == null) {
			throw new RuntimeException("Target directory does not exist.");
		}
		// For testing purposes.
		if (newLocation.endsWith("images/")) {
			// oops!
			throw new IllegalArgumentException("Error creating driectory images.");
		}
		TestFile newFile = file.isDirectory() 
			? TestFile.createDirectory(newName, parentFile)
			: TestFile.createFile(newName, parentFile);
		m_files.put(newLocation, newFile);
	}
}
