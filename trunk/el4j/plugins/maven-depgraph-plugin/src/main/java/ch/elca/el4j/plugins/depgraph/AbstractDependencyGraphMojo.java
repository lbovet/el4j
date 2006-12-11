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
 * @requiresDependencyResolution compile|runtime|test
 */
public abstract class AbstractDependencyGraphMojo extends AbstractMojo {
    /**
     * The default file extension.
     */
    public static final String DEFAULT_EXTENSION = "png";

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
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository m_localRepository;

    /**
     * @parameter expression="${depgraph.dotFile}"
     */
    private File dotFile;

    /**
     * @component
     */
    private ArtifactResolver m_artifactResolver;

    /**
     * @component
     */
    private ArtifactMetadataSource m_artifactMetadataSource;

    /**
     * The projector used to project the graph. //@component //@required
     */
    private DepGraphProjector m_projector;

    /**
     * Collector.
     * 
     * @component
     */
    private ArtifactCollector m_collector;

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
                    getLog().error(
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
            getLog().error(
                "Unable to write to \"" + outFile.getAbsolutePath() + "\"");
            throw new MojoExecutionException("Out File not writeable");

        } else if (!outFile.exists()) {
            boolean createPossible = false;
            try {
                createPossible = outFile.createNewFile();
            } catch (IOException e) {
                getLog().error("Unable to create " + outFile.getAbsolutePath(),
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

        DepGraphResolutionListener listener = new DepGraphResolutionListener(
            graph, filter);
        listener.setLog(getLog());

        try {

            m_artifactResolver.resolve(projectArtifact, m_project
                .getRemoteArtifactRepositories(), m_localRepository);

            m_collector.collect(project.getDependencyArtifacts(), project
                .getArtifact(), m_localRepository, project
                .getRemoteArtifactRepositories(), m_artifactMetadataSource,
                filter, Collections.singletonList(listener));

        } catch (AbstractArtifactResolutionException e) {
            getLog().error("Error resolving artifacts", e);
        }

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
     * Get the projector.
     * 
     * @return The projector
     */
    protected DepGraphProjector getProjector() {
        // TODO: Implement properly so the actual projector used can be inejcted

        if (m_projector == null) {
            getLog().info(
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
                getLog().error("Unable to create dot file.", e);
            }

            m_projector = projector;
        }
        return m_projector;
    }
}
