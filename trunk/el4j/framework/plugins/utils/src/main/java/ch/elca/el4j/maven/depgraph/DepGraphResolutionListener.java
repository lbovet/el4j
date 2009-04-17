package ch.elca.el4j.maven.depgraph;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 *
 * This class implements the ResolutionListener interface and saves the
 * dependency hierarchy. It Filters according to an artifact filter.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
public class DepGraphResolutionListener implements ResolutionListener {
	/**
	 * Number indicating that no filtering is done.
	 */
	private static final int NOT_FILTERING = -1;
	
	/**
	 * The graph.
	 */
	private DependencyGraph m_graph;
	
	/**
	 * Stack to keep track of the hierarchy.
	 */
	private Stack<Artifact> m_artifactStack = new Stack<Artifact>();
		
	/**
	 * Filter to select certain artifacts.
	 *
	 */
	private ArtifactFilter m_filter;
	
	/**
	 * Integer to keep track of how "deep" we are in a tree that's filtered.
	 */
	private int m_filterDepth = NOT_FILTERING;

	private Set<Artifact> dependentArtifacts;
	
	/**
	 * Default constructor, without a filter.
	 * @param graph The graph to write to
	 */
	public DepGraphResolutionListener(DependencyGraph graph) {
		this(graph, new RegexArtifactFilter("", "", ""));
	}
	
	/**
	 * Create a Resolution Listener that filters the artifacts.
	 * @param graph The graph to write to
	 * @param filter The filter to use
	 */
	public DepGraphResolutionListener(
		DependencyGraph graph, ArtifactFilter filter) {
		m_graph = graph;
		
		if (filter == null) {
			m_filter = new RegexArtifactFilter("", "", "");
		} else {
			m_filter = filter;
		}

		dependentArtifacts = new HashSet<Artifact>();
	}
	
	/**
	 * @return set of included dependency artifacts.
	 */
	public Set<Artifact> getDependentArtifacts() {
		return dependentArtifacts;
	}

	/**
	 * Get the dependency for artifact.
	 * @param artifact The artifact
	 * @return The dependency for artifact
	 */
	private DepGraphArtifact getArtifact(Artifact artifact) {
		return m_graph.getArtifact(artifact);
	}
	
	/**
	 * Get the graph this listener is working on.
	 * @return The graph
	 */
	public DependencyGraph getGraph() {
		return m_graph;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endProcessChildren(Artifact artifact) {
		if (!isFiltering()) {
			m_artifactStack.pop();
		} else {
			leaveFilteredChildren();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void includeArtifact(Artifact artifact) {
		if (!isFiltering()) {
			// Currently not in a tree that's filtered
			// Check if the given artifact should be filtered
			if (!m_filter.include(artifact)) {
				// This and all artifacts in this tree are ignored
				startFiltering();
				return;
			}
			
			// collect included dependencies
			dependentArtifacts.add(artifact);
			
			if (!m_artifactStack.isEmpty()) {
				DepGraphArtifact dependant = getArtifact(
					m_artifactStack.peek());
				try {
					dependant.addDependency(getArtifact(artifact));
				} catch (IllegalArgumentException e) {
					/*s_log.debug("Ignore dependency "
						+ getArtifact(artifact).getArtifactId() + ". "
						+ dependant.getArtifactId()
						+ " already depends on it.");*/
				}
			} else {
				// Create an artifact for this one
				getArtifact(artifact);
			}
		}
	}
	
	/**
	 * In a filtered tree.
	 * @return Whether were filtering at the moment
	 */
	private boolean isFiltering() {
		return m_filterDepth > -1;
	}
	
	/**
	 * Start filtering.
	 */
	private void startFiltering() {
		if (!isFiltering()) {
			m_filterDepth = 0;
		}
	}
	
	/**
	 * Go a level deeper in a filtered tree.
	 */
	private void enterFilteredChildren() {
		m_filterDepth++;
	}
	
	/**
	 * Leave the processing of children in a filtered tree.
	 */
	private void leaveFilteredChildren() {
		m_filterDepth--;
		if (m_filterDepth == 0) {
			// Stop filtering
			m_filterDepth = NOT_FILTERING;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void startProcessChildren(Artifact artifact) {
		if (!isFiltering()) {
			m_artifactStack.push(artifact);
		} else {
			enterFilteredChildren();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void manageArtifact(Artifact artifact, Artifact replacement) { }

	/**
	 * {@inheritDoc}
	 */
	public void omitForCycle(Artifact artifact) { }

	/**
	 * {@inheritDoc}
	 */
	public void omitForNearer(Artifact omitted, Artifact kept) { }

	/**
	 * {@inheritDoc}
	 */
	public void restrictRange(Artifact artifact, Artifact replacement,
		VersionRange newRange) { }

	/**
	 * {@inheritDoc}
	 */
	public void selectVersionFromRange(Artifact artifact) { }

	/**
	 * {@inheritDoc}
	 */
	public void testArtifact(Artifact node) { }

	/**
	 * {@inheritDoc}
	 */
	public void updateScope(Artifact artifact, String scope) { }

	/**
	 * {@inheritDoc}
	 */
	public void updateScopeCurrentPom(Artifact artifact, String scope) { }
}