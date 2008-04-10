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
import java.awt.Color;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * A GUI to inspect all defined classes of a project.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ClassInspector {

    /**
     * The finder that holds the classes.
     */
    private DuplicateClassFinder m_finder;
    
    public ClassInspector(DuplicateClassFinder finder) {
        m_finder = finder;
    }

    public JPanel getInspector() {
        return new Inspector();
    }
    
    /**
     * The inspector GUI.
     */
    class Inspector extends JPanel {
        
        /**
         * Displays the found classes in a tree view.
         */
        private classTree tree;
            
        /**
         * The information pane.
         */
        private JTextPane info;
        
        public Inspector() {
            tree = new classTree();
            populateTree();
            
            setLayout(new BorderLayout());
            info = new JTextPane();
            info.setEditable(false);
            info.setEditorKit(new HTMLEditorKit());
            info.setText("<html><pre>\n\n\n\n");
            JScrollPane infoPane = new JScrollPane(info);
            add(infoPane, BorderLayout.SOUTH);
            
            JScrollPane pane = new JScrollPane(tree);
            tree.addTreeSelectionListener( new TreeSelectionListener() {
                
                /**
                 * Update the info area.
                 * {@inheritDoc}
                 */
                public void valueChanged(TreeSelectionEvent e) {
                    Node node = (Node) tree.getLastSelectedPathComponent();
                    if (node == null) {
                        info.setText("");
                        return;
                    }
                    String path = node.getFullPath();
                    List<String> locations = node.locations;
                    
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
                    info.setText(text);
                }
            });
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
                tree.addNode(pkg, name, m_finder.getLocations(currentClass));
            }
        }
        
        class classTree extends JTree {
            
            public classTree() {
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
        String name;
        List <String> locations;
        boolean isWarning = false;
        
        /**
         * Creates a new node.
         * @param name The name of this node. For a package a.b, it is "b" - 
         * for a class a.b.C it is "C".
         * @param locations <code>null</code> if this is a package, otherwise
         * the locations of this class. 
         */
        Node(String name, List<String> locations) {
            super(name);
            this.name = name;
            this.locations = locations;
        }
        
        /**
         * If a child of this name exists, return it. If not,
         * create one and return it.
         * @param name The name of the child.
         * @returns A child of this name.
         */
        Node walkToChild(String name) {
            if (getChildCount() == 0) {
                return createChild(name);
            }
            Enumeration<Node> children = children();
            while (children.hasMoreElements()) {
                Node child = children.nextElement();
                if (child.name.equals(name)) return child;
            }
            return createChild(name);
        }
        
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
            if (!isWarning) {
                isWarning = true;
                if (getParent() != null) {
                    Node parent = (Node) getParent();
                    parent.setWarning();
                }
            }

        }
        
        /** {@inheritDoc} */
        public String toString() {
            if (isWarning) {
                return "<html><font color=\"#FF0000\">" + name;
            } else {
                return name;
            }
        }
        
        /**
         * @return The fully qualified name of this package/class.
         */
        public String getFullPath() {
            String path = name;
            Node current = this;
            while (current.getParent() != null) {
                current = (Node) current.getParent();
                if (!current.name.equals("(root)")) {
                    path = current.name + "." + path;
                }
            }
            return path;
        }

    }
    
}
