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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

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
 * @author Philippe Jacot (PJA)
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
     * @parameter expression="${depgraph.edgeLabel}" default-value="false"
     */
    private boolean edgeLabel;

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

    private DepGraphResolutionListener listener;

    /**
     * Initialize the output directory/file.
     * 
     * @throws MojoExecutionException
     */
    protected void initOutput() throws MojoExecutionException {
        if (outFile == null) {
            outFile = new File(m_project.getName() + "." + DEFAULT_EXTENSION);
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
        Artifact projectArtifact = project.getArtifact();

        RegexArtifactFilter filter = new RegexArtifactFilter(artifactFilter,
            groupFilter, versionFilter);

        project.getDependencyArtifacts();

        listener = new DepGraphResolutionListener(graph, filter);

        try {

            m_artifactResolver.resolve(projectArtifact, project
                .getRemoteArtifactRepositories(), m_localRepository);

            m_collector.collect(project.getDependencyArtifacts(), project
                .getArtifact(), m_localRepository, project
                .getRemoteArtifactRepositories(), m_artifactMetadataSource,
                filter, Collections.singletonList(listener));

        } catch (AbstractArtifactResolutionException e) {
            s_log.error("Error resolving artifacts", e);
        }

    }
    
    protected Set<Artifact> getDependentArtifacts()
    {
        return listener.getDependentArtifacts();
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
    protected void project(DependencyGraph graph) 
        throws MojoExecutionException {

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

        if (graph.getArtifacts().size() == 0) {
            s_log.error(
                "There were no Artifacts resolved. "
                    + "Maybe there's a problem with a user supplied filter.");
            throw new MojoExecutionException("No artifacts resolved");
        }

        getProjector().project(graph);
    }

    /**
     * Get the projector.
     * 
     * @return The projector
     */
    protected DepGraphProjector getProjector() {
        // TODO: Implement properly so the actual projector used can be inejcted

        if (m_projector == null) {
            s_log.info(
                "Writing dependency graph to " + outFile.getAbsolutePath());

            GraphvizProjector projector = new GraphvizProjector(getOutFile(), edgeLabel);
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
}
