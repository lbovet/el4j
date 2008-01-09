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

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

/**
 * Can be used to draw a dependency graph from the project, the mojo is executed
 * in. It traverses all dependencies and creates a graph using Graphviz. It
 * draws a dependency graph just for your project. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/maven/plugins/maven-depgraph-plugin/src/main/java/ch/elca/el4j/plugins/depgraph/RecursiveDependencyGraphMojo.java
 * $", "$Revision$", "$Date: 2007-07-03 10:50:47 +0200 (Di, 03 Jul 2007)
 * $", "$Author$" );</script>
 * 
 * @author Claude Humard (CHD)
 * @goal rec
 * @requiresDependencyResolution compile|test
 * @deprecated Does not work any longer. Is no longer required. Otherwise, it
 *             must be re-engineered to comply with new
 *             DependencyGraphMojo-behaviour (using DependencyTreeBuilder etc.)
 */
@Deprecated
public class RecursiveDependencyGraphMojo extends AbstractDependencyGraphMojo {

    private Set<Artifact> dependentArtifacts = new HashSet<Artifact>();
    private Set<Artifact> processedArtifacts = new HashSet<Artifact>();

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        initOutput();

        // initialize the resulting graph
        DependencyGraph resultGraph = new DependencyGraph();
        resultGraph.setName(m_project.getName());

        // retrieve projectBuilder-instance
        MavenProjectBuilder projectBuilder = getProjectBuilder();
        // initialize FIFO queue containing the projects to process
        Queue<MavenProject> projectQueue = createQueue();
        projectQueue.add(m_project);

        // process all projects in queue
        while (!projectQueue.isEmpty()) {
            // get next project from FIFO queue
            MavenProject project = projectQueue.remove();

            /*
             * process the current project
             */
            processProject(project, resultGraph);

            // add processed artifact to list 
            processedArtifacts.add(project.getArtifact());
            // get list of dependencies of current project
            dependentArtifacts = new HashSet();
            // already processed artifacts should not be processed again
            dependentArtifacts.removeAll(processedArtifacts);

            // build MavenProjects from dependency artifacts
            for (Artifact dependentArtifact : dependentArtifacts) {
                try {
                    /*
                     * because artifacts do not contain dependency-informations, it is necessary
                     * to build a MavenProject from Repository
                     */
                    MavenProject dependentProject = projectBuilder
                        .buildFromRepository(dependentArtifact, m_project
                            .getRemoteArtifactRepositories(), m_localRepository);
                    /*
                     * Set same remote repositories as in current project
                     */
                    dependentProject.setRemoteArtifactRepositories(m_project
                        .getRemoteArtifactRepositories());
                    /*
                     * because the dependencies are contained in the project-model but not in as
                     * dependency-artifacts directly in the MavenProject, they have to be transformed
                     * from Dependency to Artifacts and added manually to the MavenProject.
                     */
                    List<Dependency> dependencies = dependentProject.getModel()
                        .getDependencies();
                    HashSet<Artifact> dependencyArtifacts = new HashSet<Artifact>();
                    for (Dependency dependency : dependencies) {
                        DefaultArtifactHandler handler = new DefaultArtifactHandler(
                            dependency.getType());
                        Artifact dependencyArtifact = new DefaultArtifact(
                            dependency.getGroupId(),
                            dependency.getArtifactId(), VersionRange
                                .createFromVersion(dependency.getVersion()),
                            dependency.getScope(), dependency.getType(),
                            dependency.getClassifier(), handler);
                        dependencyArtifacts.add(dependencyArtifact);
                    }
                    dependentProject
                        .setDependencyArtifacts(dependencyArtifacts);

                    // finally the dependent mavenProject can be added to the Queue
                    projectQueue.add(dependentProject);
                } catch (ProjectBuildingException e) {
                    getLog().warn(e);
                }
            }
        }

        // print graph
        project(resultGraph);

    }

    /**
     * @return a FIFO-queue to store MavenProjects
     */
    private Queue<MavenProject> createQueue() {
        Comparator<MavenProject> comparator = new Comparator<MavenProject>() {

            public int compare(MavenProject o1, MavenProject o2) {
                return 0;
            }
        };
        Queue<MavenProject> projectQueue = new PriorityQueue<MavenProject>(10, comparator);
        return projectQueue;
    }

    /**
     * Get ProjectBuilder using reflection from artifactMetadataSource.
     * 
     * @return instance of ProjectBuilder
     * @throws MojoExecutionException
     */
    private MavenProjectBuilder getProjectBuilder()
        throws MojoExecutionException {
        /*
         * Use reflection to retrieve the projectBuilder from
         * artifactMetadataSource
         */
        MavenProjectBuilder projectBuilder = null;
        try {
            Class<? extends ArtifactMetadataSource> metadata = m_artifactMetadataSource
                .getClass();
            Field field = metadata.getDeclaredField("mavenProjectBuilder");
            field.setAccessible(true);
            projectBuilder = (MavenProjectBuilder) field
                .get(m_artifactMetadataSource);
        } catch (Exception e) {
            throw new MojoExecutionException(
                "Could not get MavenProjectBuilder.", e);
        }
        return projectBuilder;
    }
}
