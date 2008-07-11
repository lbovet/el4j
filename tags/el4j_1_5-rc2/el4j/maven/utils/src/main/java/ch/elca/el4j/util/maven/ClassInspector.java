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
package ch.elca.el4j.util.maven;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A GUI to inspect all defined classes of a project.
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
public class ClassInspector {

	/**
	 * The finder that holds the classes.
	 */
	private DuplicateClassFinder m_finder;
	
	/**
	 * Constructor - creates an inspector from a finder
	 * that must have completed its search already.
	 * @param finder The finder to use.
	 */
	public ClassInspector(DuplicateClassFinder finder) {
		m_finder = finder;
	}

	/**
	 * @return A JPanel containing the class inspector.
	 */
	public JPanel getInspector() {
		return new Inspector();
	}

	// Checkstyle: MagicNumber off
	
	/**
	 * Show the inspector in a new frame.
	 */
	public void show() {
		JPanel inspectorPanel = getInspector();
		JFrame frame = new JFrame("Class inspector");
		frame.getContentPane().add(inspectorPanel);
		frame.setSize(600, 400);
		frame.setVisible(true);
		while (frame.isVisible()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.");
			}
		}
	}
	
	// Checkstyle: MagicNumber on
	
	/**
	 * The inspector GUI.
	 */
	class Inspector extends JPanel {
		
		/**
		 * Displays the found classes in a tree view.
		 */
		private ClassTree m_tree;
			
		/**
		 * The information pane.
		 */
		private JTextPane m_info;
		
		/**
		 * Default constructor. Initialize the tree and its elements.
		 */
		public Inspector() {
			m_tree = new ClassTree();
			populateTree();
			
			setLayout(new BorderLayout());
			m_info = new JTextPane();
			m_info.setEditable(false);
			m_info.setEditorKit(new HTMLEditorKit());
			m_info.setText("<html><pre>\n\n\n\n");
			JScrollPane infoPane = new JScrollPane(m_info);
			add(infoPane, BorderLayout.SOUTH);
			
			JScrollPane pane = new JScrollPane(m_tree);
			m_tree.addTreeSelectionListener(new ClassTreeSelectionListener());
			add(pane, BorderLayout.CENTER);
		}
		
		/**
		 * Load classes from finder into tree.
		 */
		private void populateTree() {
			Iterator<String> i = m_finder.iterator();
			while (i.hasNext()) {
				String currentClass = i.next();
				String pkg;
				String name;
				if (!currentClass.contains(".")) {
					// Top-level
					pkg = "";
					name = currentClass;
				} else {
					int split = currentClass.lastIndexOf(".");
					pkg = currentClass.substring(0, split);
					name = currentClass.substring(split + 1);
				}
				m_tree.addNode(pkg, name, m_finder.getLocations(currentClass));
			}
		}
		
		/**
		 * Handles clicking on a tree node (updates the detail view).
		 */
		class ClassTreeSelectionListener implements TreeSelectionListener {
			
			/** {@inheritDoc} */
			public void valueChanged(TreeSelectionEvent e) {
				Node node = (Node) m_tree.getLastSelectedPathComponent();
				if (node == null) {
					m_info.setText("");
					return;
				}
				String path = node.getFullPath();
				List<String> locations = node.m_locations;
				
				String text = "<html><pre>Name: " + path + "\n";
				if (locations == null) {
					text += "Type: package";
				} else {
					text += "Type: class\n";
					if (locations.size() == 1) {
						text += "Location: " + locations.get(0);
					} else {
						text += "<font color=\"#FF0000\">"
							+ "<b>Duplicated!</b> Locations:\n";
						for (String loc : locations) {
							text += loc + "\n";
						}
					}
				}
				m_info.setText(text);
			}
		}
		
		
		/**
		 * The tree object that handles displaying the classes.
		 */
		class ClassTree extends JTree {
			
			/**
			 * Default constructor.
			 */
			public ClassTree() {
				super(new Node("(root)", null));
			}
			
			
			/**
			 * Adds a node.
			 * @param pkg The package name.
			 * @param name The class name.
			 * @param locations The locations.
			 */
			public void addNode(String pkg, String name,
				List<String> locations) {

				// Get the root node and walk down until the node is found,
				// creating nodes along the way.
				Node current = (Node) getModel().getRoot();
				
				if (!pkg.equals("")) {
					String[] path = pkg.split("\\.");
					for (String element : path) {
						current = current.walkToChild(element);
					}
				}
				
				// We are now at the right package.
				Node thisNode = new Node(name, locations);
				current.add(thisNode);
				
				// If multiple locations exist, make a warning node.
				if (locations != null && locations.size() > 1) {
					thisNode.setWarning();
				}
			}
		}
	}
	
	/**
	 * Tree node information.
	 */
	class Node extends DefaultMutableTreeNode {
		/** The name of this class/package. */
		String m_name;
		
		/** The locations this class is defined in;
		 * <code>null</code> for packages.
		 */
		List <String> m_locations;
 
		/**
		 * Whether this is a "warning" node -
		 * either a duplicated class or a parent of one.
		 */
		boolean m_isWarning = false;
		
		/**
		 * Creates a new node.
		 * @param name The name of this node. For a package a.b, it is "b" -
		 * for a class a.b.C it is "C".
		 * @param locations <code>null</code> if this is a package, otherwise
		 * the locations of this class.
		 */
		Node(String name, List<String> locations) {
			super(name);
			this.m_name = name;
			this.m_locations = locations;
		}
		
		/**
		 * If a child of this name exists, return it. If not,
		 * create one and return it.
		 * @param name The name of the child.
		 * @return A child of this name.
		 */
		@SuppressWarnings("unchecked")
		Node walkToChild(String name) {
			/* Handled by case below.
			if (getChildCount() == 0) {
				return createChild(name);
			}
			*/
			Enumeration<Node> children = children();
			while (children.hasMoreElements()) {
				Node child = children.nextElement();
				if (child.m_name.equals(name)) {
					return child;
				}
			}
			return createChild(name);
		}
		
		/**
		 * @param name The name.
		 * @return A new child node with this name.
		 */
		private Node createChild(String name) {
			Node child = new Node(name, null);
			add(child);
			return child;
		}
		
		/**
		 * Sets this node to be a "warning" node (contains duplicates).
		 * Recurses to parent nodes.
		 */
		void setWarning() {
			// If warning is already set, do notihng -
			// in particular don't recurse.
			if (!m_isWarning) {
				m_isWarning = true;
				if (getParent() != null) {
					Node parent = (Node) getParent();
					parent.setWarning();
				}
			}

		}
		
		/** {@inheritDoc} */
		public String toString() {
			if (m_isWarning) {
				return "<html><font color=\"#FF0000\">" + m_name;
			} else {
				return m_name;
			}
		}
		
		/**
		 * @return The fully qualified name of this package/class.
		 */
		public String getFullPath() {
			String path = m_name;
			Node current = this;
			while (current.getParent() != null) {
				current = (Node) current.getParent();
				if (!current.m_name.equals("(root)")) {
					path = current.m_name + "." + path;
				}
			}
			return path;
		}

	}
	
}
