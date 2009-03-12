/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.maven;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;

import ch.elca.el4j.maven.depgraph.DepGraphArtifact;
import ch.elca.el4j.maven.depgraph.DepGraphResolutionListener;
import ch.elca.el4j.maven.depgraph.DependencyGraph;
import ch.elca.el4j.maven.depgraph.RegexArtifactFilter;

/**
 *
 * This class is goes through the project and all its dependency and creates
 * a graph out of it with help of the Dependency Graph Plugin.
 * Next, it takes this graph and transforms it into a
 * Directed Acyclic Graph (DAG) and performs a topological sort on it.
 *
 * The reason for this cumbersome way to get the Dependency URLs is that if
 * you simply take <code>project.getDependencyArtifacts</code> the order in
 * which you state the dependencies in the pom.xml file would matter.
 * This could lead to nasty sideeffects that we want to prevent.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class DepGraphWalker {
	/**
	 * The Intermediate Dependency Graph as used by the DepGraph plugin.
	 */
	private DependencyGraph m_graph;

	/**
	 * The Directed Acyclic Graph of the dependencies.
	 */
	private DAG m_dag;

	/**
	 * The Maven Project.
	 */
	private MavenProject m_project;

	/**
	 * The Maven Artifact Repository.
	 */
	private ArtifactRepository m_repo;

	/**
	 * The Maven Artifact Resolver.
	 */
	private ArtifactResolver m_resolver;

	/**
	 * The Maven Artifact Factory.
	 */
	private ArtifactFactory m_factory;

	/**
	 * Set of visited DepGraphArtifacts.
	 */
	private Set<DepGraphArtifact> m_visitedDepGraphArtifacts = new HashSet<DepGraphArtifact>();;

	/**
	 * Constructor.
	 *
	 * @param repo
	 *            The artifact repository.
	 * @param project
	 *            The projects we're working on
	 * @param resolver
	 *            The maven artifact resolver
	 * @param collector
	 *            The maven artifact collector
	 * @param metaSrc
	 *            The maven artifact meta source
	 * @param factory
	 *            The maven artifact factory
	 */
	public DepGraphWalker(ArtifactRepository repo, MavenProject project,
		ArtifactResolver resolver, ArtifactCollector collector,
		ArtifactMetadataSource metaSrc, ArtifactFactory factory) {

		m_dag = new DAG();
		m_project = project;
		m_repo = repo;
		m_factory = factory;
		m_resolver = resolver;

		// Create an intermediate dependency graph
		m_graph = new DependencyGraph();
		m_graph.setName(project.getName());
		createGraph(m_graph, resolver, collector, metaSrc);
	}



	/**
	 * Collects and returns list of artifact-objects of all dependencies of
	 * this project,
	 * topologically sorted.
	 *
	 * @author Frank Bitzer
	 *
	 * @return List of dependencies as Artifact
	 */
	@SuppressWarnings("unchecked")
	public List<Artifact> getDependencyArtifacts() {
		// Get the root artifact from the created dependency m_graph
		DepGraphArtifact rootArtifact
			= m_graph.getArtifact(
				m_project.getArtifactId(),
				m_project.getGroupId(),
				m_project.getVersion(),
				m_project.getArtifact().getScope(),
				m_project.getArtifact().getType(),
				m_project.getArtifact().getClassifier());

		// Create a DAG from the dependency m_graph and sort it.
		createDAG(rootArtifact);
		List<String> list = TopologicalSorter.sort(m_dag);
		// Remove root artifact as it is not a dependency
		list.remove(rootArtifact.getQualifiedName());

		List<Artifact> result = resolveArtifacts(
			m_resolver, m_factory, m_graph, list);

		return result;
	}

	/**
	 * Collects and returns list of urls of all dependencies of this project,
	 * topologically sorted.
	 *
	 * @return List of dependencies jar URLs
	 */
	@SuppressWarnings("unchecked")
	public List<URL> getDependencyURLs() {
		// Get the root artifact from the created dependency m_graph
		DepGraphArtifact rootArtifact
			= m_graph.getArtifact(
				m_project.getArtifactId(),
				m_project.getGroupId(),
				m_project.getVersion(),
				m_project.getArtifact().getScope(),
				m_project.getArtifact().getType(),
				m_project.getArtifact().getClassifier());

		// Create a DAG from the dependency m_graph and sort it.
		createDAG(rootArtifact);
		List<String> list = TopologicalSorter.sort(m_dag);
		// Remove root artifact as it is not a dependency
		list.remove(rootArtifact.getQualifiedName());

		return resolveArtifactsToURL(m_resolver, m_factory, m_graph, list);
	}


	/**
	 * Resolves the sorted DepGraphArtifacts back to (Maven) Artifacts and
	 * finally URLs.
	 *
	 * @param resolver
	 *            The maven artifact resolver
	 * @param factory
	 *            The maven artifact factory
	 * @param graph
	 *            The intermediate dependency graph
	 * @param sorted
	 *            Topologically sorted list of all dependencies
	 * @return List of the URLs of the topologically sorted dependencies
	 */
	private List<URL> resolveArtifactsToURL(ArtifactResolver resolver,
		ArtifactFactory factory, DependencyGraph graph, List<String> sorted) {

		String[] parts;
		List<URL> artifactURLs = new ArrayList<URL>();
		for (String item : sorted) {
			parts = item.split(":");
			DepGraphArtifact art = graph.getArtifact(parts[3], parts[0],
				parts[1], "", "", parts[2]);

			Artifact artifact = factory.createDependencyArtifact(
				art.getGroupId(),
				art.getArtifactId(),
				VersionRange.createFromVersion(art.getVersion()),
				art.getType(),
				art.getClassifier(),
				art.getScope());


			try {
				resolver.resolve(artifact, m_project
					.getRemoteArtifactRepositories(), m_repo);
				artifactURLs.add(artifact.getFile().toURL());
			} catch (Exception e) {
				throw new DepGraphWalkerException(e);
			}
		}
		return artifactURLs;
	}



	/**
	 * Resolves the sorted DepGraphArtifacts back to (Maven) Artifacts
	 * and returns a list of them.
	 *
	 * @author Frank Bitzer (FBI)
	 *
	 * @param resolver
	 *            The maven artifact resolver
	 * @param factory
	 *            The maven artifact factory
	 * @param graph
	 *            The intermediate dependency graph
	 * @param sorted
	 *            Topologically sorted list of all dependencies
	 * @return List of the Artifacts of the topologically sorted dependencies
	 */
	private List<Artifact> resolveArtifacts(ArtifactResolver resolver,
		ArtifactFactory factory, DependencyGraph graph, List<String> sorted) {

		String[] parts;
		List<Artifact> artifacts = new ArrayList<Artifact>();
		for (String item : sorted) {
			parts = item.split(":");
			DepGraphArtifact art = graph.getArtifact(parts[3], parts[0],
				parts[1], "", "", parts[2]);

			Artifact artifact = factory.createDependencyArtifact(
				art.getGroupId(),
				art.getArtifactId(),
				VersionRange.createFromVersion(art.getVersion()),
				art.getType(),
				art.getClassifier(),
				art.getScope());

			try {
				resolver.resolve(artifact, m_project
					.getRemoteArtifactRepositories(), m_repo);
				artifacts.add(artifact);
			} catch (Exception e) {
				throw new DepGraphWalkerException(e);
			}
		}
		return artifacts;
	}




	/**
	 * Creates the Intermediate dependency graph with help of the Dependency
	 * Graph plugin.
	 *
	 * @param graph
	 *            The Intermediate graph to create
	 * @param resolver
	 *            The maven artifact resolver
	 * @param collector
	 *            The maven artifact collector
	 * @param metadataSource
	 *            The maven artifact meta source
	 */
	private void createGraph(DependencyGraph graph, ArtifactResolver resolver,
		ArtifactCollector collector, ArtifactMetadataSource metadataSource) {

		RegexArtifactFilter filter = new RegexArtifactFilter("", "", "");

		DepGraphResolutionListener listener = new DepGraphResolutionListener(
			graph);
		try {
			/* Although this was used in the Dependency Graph Plugin,
			 * it causes an exception here, because the project's artifact
			 * usually isn't deployed in the local repository by the time
			 * this method is called (which is usuall in the pre- and
			 * post-integration phase)
			 */

			// resolver.resolve(m_project.getArtifact(), m_project
			// .getRemoteArtifactRepositories(), m_repo);

			collector.collect(m_project.getDependencyArtifacts(), m_project
				.getArtifact(), m_repo, m_project
				.getRemoteArtifactRepositories(), metadataSource,
				filter, Collections.singletonList(listener));

		} catch (AbstractArtifactResolutionException e) {
			throw new DepGraphWalkerException(e);
		}

	}

	/**
	 * Goes recursively through Intermediate dependency graph and creates a DAG
	 * out of it, where Artifacts denote Vertices and Dependencies between them
	 * edges.
	 *
	 * @param artifact
	 *            The root (i.e. project) artifact
	 */
	private void createDAG(DepGraphArtifact artifact) {
		if (m_visitedDepGraphArtifacts.contains(artifact)) {
			return;
		}
		m_visitedDepGraphArtifacts.add(artifact);
		m_dag.addVertex(artifact.getQualifiedName());
		for (DepGraphArtifact dep : artifact.getDependencies()) {
			createDAG(dep);
			try {
				m_dag.addEdge(artifact.getQualifiedName(), dep
					.getQualifiedName());
			} catch (CycleDetectedException e) {
				throw new DepGraphWalkerException(e);
			}
		}
	}
}
