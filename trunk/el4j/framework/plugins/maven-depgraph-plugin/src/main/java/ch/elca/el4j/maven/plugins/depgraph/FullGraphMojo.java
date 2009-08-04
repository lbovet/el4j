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
package ch.elca.el4j.maven.plugins.depgraph;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import ch.elca.el4j.maven.depgraph.DependencyGraph;

/**
 * Can be used to draw a dependency graph from the project, the mojo is executed
 * in. It traverses all dependencies and creates a graph using Graphviz. It draws
 * a graph for all the modules as they are interconnected. Same as depgraph for a simple POM, but for a POM with
 * submodules, generates a combined dependency graph incorporating all modules.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philippe Jacot (PJA)
 * @goal fullgraph
 * @aggregator
 * @requiresDependencyResolution compile|test
 */
public class FullGraphMojo extends AbstractDependencyGraphMojo {
		
	/**
	 * @parameter default-value="${reactorProjects}"
	 * @required
	 * @readonly
	 */
	private List<MavenProject> m_reactorProjects;

	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {
		super.execute();
		
		DependencyGraph resultGraph = new DependencyGraph();
		resultGraph.setName(getProject().getName());
		
		for (MavenProject prj : m_reactorProjects) {
			processProject(prj, resultGraph);
		}
		
		project(resultGraph);
	}
}
