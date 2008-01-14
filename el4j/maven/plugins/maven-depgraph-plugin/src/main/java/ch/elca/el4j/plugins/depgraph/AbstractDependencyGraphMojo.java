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

// Checkstyle: MemberName off

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.dependency.tree.filter.ArtifactDependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.FilteringDependencyNodeVisitor;

/**
 * 
 * A starting point for DepGraph Mojos.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA), Claude Humard (CHD)
 * @requiresDependencyResolution test
 */
public abstract class AbstractDependencyGraphMojo extends AbstractMojo {   
    /**
     * The default file extension.
     */
    public static final String DEFAULT_EXTENSION = "png";

    /**
     * Logger.
     */
    private static Log s_log = LogFactory
        .getLog(AbstractDependencyGraphMojo.class);
    
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject m_project;

    /**
     * The file to write to.
     * 
     * @parameter expression="${depgraph.outFile}"
     */
    private File outFile;
    
    /**
     * The output file extension.
     * 
     * @parameter expression="${depgraph.ext}"
     */
    private String outFileExtension;

    /**
     * The directory to write to.
     * 
     * @parameter expression ="${depgraph.outDir}"
     */
    private File outDir;

    /**
     * A filter for the artifact name.
     * 
     * @parameter expression="${depgraph.artifactFilter}"
     */
    private String artifactFilter;

    /**
     * A filter for the group name.
     * 
     * @parameter expression="${depgraph.groupFilter}"
     */
    private String groupFilter;

    /**
     * A filter for the version.
     * 
     * @parameter expression="${depgraph.versionFilter}"
     */
    private String versionFilter;

    /**
     * Filter all empty artifacts.
     * 
     * @parameter expression="${depgraph.filterEmptyArtifacts}"
     */
    private boolean filterEmptyArtifacts;

    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    protected ArtifactRepository m_localRepository;

    /**
     * The *.dot file to write to. Will be a temporary file if unset.
     * 
     * @parameter expression="${depgraph.dotFile}"
     */
    private File dotFile;

    /**
     * Whether to label the edges with name of dependency-scope.
     * 
     * @parameter expression="${depgraph.drawScope}" default-value="true"
     */
    private boolean drawScope;

    /**
     * Whether to draw omitted artifacts.
     * 
     * @parameter expression="${depgraph.drawOmitted}" default-value="false"
     */
    private boolean drawOmitted;

    /**
     * @component
     */
    private ArtifactResolver m_artifactResolver;

    /**
     * The MetaDataSource used by the collector.
     * 
     * @component
     */
    protected ArtifactMetadataSource m_artifactMetadataSource;

    /**
     * The projector used to project the graph.
     */
    private DepGraphProjector m_projector;

    /**
     * The ArtifactCollector used to resolve the dependencies.
     * 
     * @component
     */
    private ArtifactCollector m_collector;
    
    /**
     * The dependency tree builder to use.
     * 
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * The artifact factory to use.
     * 
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * Initialize the output directory/file.
     * 
     * @throws MojoExecutionException
     */
    protected void initOutput() throws MojoExecutionException {
        if (outFile == null) {
            outFile = new File(m_project.getName() + "." + getExtension());
        }

        // First, check if there was an output directory given
        if (outDir == null) {
            // No output dir is given, therefore
            // the given filename is used alone
            outFile = outFile.getAbsoluteFile();
        } else {
            if (!(outDir.exists() && outDir.isDirectory())) {
                // Try to create the directory
                if (!outDir.mkdirs()) {
                    s_log.error(
                        "Unable to create Directory "
                            + outDir.getAbsolutePath());
                    throw new MojoExecutionException(
                        "Could not create Directory");
                }
            }

            // Use the outDir and only the name of the file
            outFile = new File(outDir, outFile.getName());
        }

        if (outFile.exists() && !outFile.canWrite()) {
            s_log.error(
                "Unable to write to \"" + outFile.getAbsolutePath() + "\"");
            throw new MojoExecutionException("Out File not writeable");

        } else if (!outFile.exists()) {
            boolean createPossible = false;
            try {
                createPossible = outFile.createNewFile();
            } catch (IOException e) {
                s_log.error("Unable to create " + outFile.getAbsolutePath(),
                    e);
            }

            if (!createPossible) {
                throw new MojoExecutionException("Unable to create out file");
            }
        }
    }
    
    /**
     * Returns the extension of the output file. This can be set from outside
     * via the depgraph.ext property. It has to be a file extensions supported
     * by the used projector.
     * 
     * @return the extension of the output file.
     */
    private String getExtension() {
        if (outFileExtension != null) {
            return outFileExtension;
        } else {
            return DEFAULT_EXTENSION;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        initOutput();
    }

    /**
     * Process the given project and save result in graph.
     * 
     * @param project
     *            Project to process
     * @param graph
     *            Graph to store results
     */
    protected void processProject(MavenProject project, DependencyGraph graph) {
        
        graph.setDrawScope(drawScope);

        RegexArtifactFilter filter = new RegexArtifactFilter(artifactFilter,
            groupFilter, versionFilter);

        DependencyNode rootNode = null;
        try {
            rootNode = dependencyTreeBuilder.buildDependencyTree(project,
                m_localRepository, artifactFactory, m_artifactMetadataSource,
                filter, m_collector);
        } catch (DependencyTreeBuilderException e) {
            s_log.error("Error resolving artifacts", e);
        }
        
        DependencyNode filteredNodes = applyDependencyFilter(rootNode, filter);

        s_log.debug(filteredNodes.toNodeString());

        transformDependencyNodeToGraph(filteredNodes, graph);
    }

    /**
     * Get the file to write to.
     * 
     * @return Out File
     */
    protected File getOutFile() {
        return outFile;
    }

    /**
     * Project the given graph.
     * 
     * @param graph
     *            The graph to project
     * @throws MojoExecutionException
     */
    protected void project(DependencyGraph graph) throws MojoExecutionException {

        /*
         * Remove all DepGraphArtifacts, which do not contain any dependencies
         * and are not referenced by any artifact
         */
        removeUnusedArtifacts(graph);

        if (graph.getArtifacts().size() == 0) {
            s_log.error("There were no Artifacts resolved. "
                + "Maybe there's a problem with a user supplied filter.");
            throw new MojoExecutionException("No artifacts resolved");
        }

        getProjector().project(graph);
    }

    /**
     * Removes all DepGraphArtifacts from DependencyGraph, which do not contain
     * any dependency AND which are not referenced by any other DepGraphArtifact
     * (both conditions must be met to be removed).
     * 
     * @param graph
     *            DependencyGraph to remove unused DepGraphArtifacts from.
     */
    @SuppressWarnings("unchecked")
    private void removeUnusedArtifacts(DependencyGraph graph) {
        if (filterEmptyArtifacts) {
            // Create a list artifacts someone is depending on
            Map<String, DepGraphArtifact> dependencies = new HashMap();
            List<DepGraphArtifact> dependants = graph.getArtifacts();

            for (DepGraphArtifact a : dependants) {
                for (DepGraphArtifact dep : a.getDependencies()) {
                    if (!dependencies.containsKey(dep.getQualifiedName())) {
                        dependencies.put(dep.getQualifiedName(), dep);
                    }
                }
            }

            for (DepGraphArtifact a : dependants) {
                if (a.getDependencies().size() == 0
                    && !dependencies.containsKey(a.getQualifiedName())) {
                    graph.removeArtifact(a);
                }
            }
        }
    }

    /**
     * Get the projector.
     * 
     * @return The projector
     */
    protected DepGraphProjector getProjector() {
        // TODO: Implement properly so the actual projector used can be injected

        if (m_projector == null) {
            s_log.info(
                "Writing dependency graph to " + outFile.getAbsolutePath());

            GraphvizProjector projector = new GraphvizProjector(getOutFile());
            try {
                if (dotFile != null) {
                    if (outDir != null) {
                        dotFile = new File(outDir, dotFile.getName());
                    } else {
                        dotFile = dotFile.getAbsoluteFile();
                    }

                    if ((dotFile.exists() && dotFile.canWrite())
                        || (!dotFile.exists() && dotFile.createNewFile())) {
                        projector.setDotFile(dotFile);
                    }
                }
            } catch (IOException e) {
                s_log.error("Unable to create dot file.", e);
            }

            m_projector = projector;
        }
        return m_projector;
    }

    /**
     * Transform the DependencyNode-tree into the DependencyGraph. Creates
     * DepGraphArtifacts for each DependencyNode and stores them in the
     * DependencyGraph-object.
     * 
     * @param node
     *            root node containing tree of DependencyNodes.
     * @param graph
     *            DependencyGraph where to create DepGraphArtifacts.
     */
    @SuppressWarnings("unchecked")
    private void transformDependencyNodeToGraph(DependencyNode node,
        DependencyGraph graph) {
        DepGraphArtifact artifact = createDepGraphArtifact(node, graph);

        // do not draw children if artifact is omitted
        if (artifact == null || artifact.isOmitted()) {
            return;
        } else {
            for (DependencyNode child : (List<DependencyNode>) node
                .getChildren()) {
                DepGraphArtifact depGraphChild = createDepGraphArtifact(child,
                    graph);
                if (depGraphChild != null) {
                    try {
                        artifact.addDependency(depGraphChild);
                    } catch (IllegalArgumentException e) {
                        s_log.debug("Ignore dependency "
                            + depGraphChild.getArtifactId() + ". "
                            + artifact.getArtifactId()
                            + " already depends on it.");
                    }
                    transformDependencyNodeToGraph(child, graph);
                }
            }
        }
    }

    /**
     * Creates a DepGraphArtifact from the specified node and returns the
     * DepGraphArtifact. The DepGraphArtifact is stored in the specified
     * DependencyGraph. Returns null if the node is omitted and drawOmitted is
     * set to false. If the artifact already exists in the DependencyGraph, the
     * stored instance is returned.
     * 
     * @param node
     *            the node to create a DepGraphArtifact from
     * @param graph
     *            DependencyGraph where to store the created DepGraphArtifact
     * @return the created or stored DepGraphArtifact or null, if artifact is
     *         omitted.
     */
    private DepGraphArtifact createDepGraphArtifact(DependencyNode node,
        DependencyGraph graph) {
        Artifact a = node.getArtifact();
        boolean omitted = (node.getState() != DependencyNode.INCLUDED);

        if (!drawOmitted && omitted) {
            return null;
        }
        DepGraphArtifact artifact = graph.getArtifact(a.getArtifactId(), a
            .getGroupId(), a.getVersion(), a.getScope(), a.getType(), omitted);
        return artifact;
    }
    
    /**
     * Apply specified ArtifactFilter to the dependency tree with the specified rootNode on top.
     * The rootNode can not be filtered out to avoid several not connected subtrees.
     * The resulting dependency tree contains at least the rootNode and all dependent artifacts, which 
     * satisfy the specified filter. The children of a filtered out artifact are also traversed.
     * <code><pre>
     * Example 1
     * 
     * Input tree: A -> B -> C
     * Filter: "C"
     * Output tree: A -> C
     * </pre></code>
     * (Root node "A" is added anyway, "B" is filtered out, "C" and "D" satisfy filter and is added)
     * 
     * <code><pre>
     * Example 2
     * 
     * Input tree: A --> B -> D
     *               \-> C
     * Filter: "C | D"
     * Output tree: A --> D
     *                \-> C
     * </pre></code>
     * (This example shows why the root node has to be added anyway: if node "A" would be ignored, we
     * had two non-connecting trees with D and C as root nodes)
     * 
     * <code><pre>
     * Example 3
     * 
     * Input tree: A -> B --> D
     *                    \-> C
     * Filter: "C | D"
     * Output tree: A --> D
     *                \-> C
     * </pre></code>
     * 
     * @param rootNode
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    private DependencyNode applyDependencyFilter(DependencyNode rootNode,
        ArtifactFilter filter) {
        BuildingDependencyNodeVisitor buildingVisitor = new BuildingDependencyNodeVisitor();
        FilteringDependencyNodeVisitor filteringVisitor = new FilteringDependencyNodeVisitor(
            buildingVisitor, new ArtifactDependencyNodeFilter(filter));

        // force adding of rootNode to result tree, i.e. rootNode can not be filtered out
        buildingVisitor.visit(rootNode);

        // traverse children of rootNode using filteringVisitor
        for (Iterator iterator = rootNode.getChildren().iterator(); iterator
            .hasNext();) {
            DependencyNode child = (DependencyNode) iterator.next();

            child.accept(filteringVisitor);
        }

        // force endVisit of rootNode
        buildingVisitor.endVisit(rootNode);

        return buildingVisitor.getDependencyTree();
    }
}
