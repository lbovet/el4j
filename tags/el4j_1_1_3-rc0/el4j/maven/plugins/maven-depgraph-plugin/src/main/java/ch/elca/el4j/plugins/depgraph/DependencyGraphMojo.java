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
package ch.elca.el4j.plugins.depgraph;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Can be used to draw a dependency graph from the project, the mojo is executed 
 * in. It traverses all dependencies and creates a graph using Graphviz. It 
 * draws a dependency graph just for your project.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Philippe Jacot (PJA)
 * @goal depgraph
 * @requiresDependencyResolution compile|test
 */
public class DependencyGraphMojo extends AbstractDependencyGraphMojo {    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        initOutput();
        
        DependencyGraph resultGraph = new DependencyGraph();
        resultGraph.setName(m_project.getName());
        
        processProject(m_project, resultGraph);
        
        project(resultGraph);
   
    }
}
