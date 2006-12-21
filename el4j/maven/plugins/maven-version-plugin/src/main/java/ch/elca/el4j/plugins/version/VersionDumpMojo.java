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
package ch.elca.el4j.plugins.version;

// Checkstyle: MemberName off

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

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
 * @goal dump
 * @aggregator 
 */
public  class VersionDumpMojo extends AbstractMojo {
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
     * @parameter expression="${version.outFile}"
     */
    private File outFile;
    
    /**
     * Filter the group.
     * @parameter expression="${version.excludeGroup}"
     */
    private String excludeGroup = "";

    /**
     * The directory to write to.
     * 
     * @parameter expression ="${version.outDir}"
     */
    private File outDir;

    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository m_localRepository;
    
    /**
     * Metadata Manager.
     * 
     * @component
     */
    private RepositoryMetadataManager m_metadataManager;
        
    /**
     * @parameter default-value="${reactorProjects}"
     * @required
     * @readonly
     */
    private List<MavenProject> m_reactorProjects;
        
    /**
     * 
     */
    private WriteBuffer m_out;   
    
    /**
     * Process a project.
     * @param project Project to process.
     */
    private void processProject(MavenProject project) {
        m_out.start();
        
        m_out.writeLine(project.getId());
        m_out.indent();
        m_out.start();
        boolean pluginWritten = false;
        m_out.writeLine("Plugins:");
        m_out.indent();
        for (Artifact a : (Collection<Artifact>) project.getPluginArtifacts()) {
            pluginWritten = printArtifact
            (a, project.getRemoteArtifactRepositories()) || pluginWritten;
        }
        m_out.unindent();
        if (pluginWritten) {
            m_out.commit();
        } else {
            m_out.rollback();
        }
        
        m_out.start();
        boolean dependencyWritten = false;
        m_out.writeLine("Dependencies:");
        m_out.indent();
        for (Artifact a : (Collection<Artifact>) project.getDependencyArtifacts()) {
            dependencyWritten = printArtifact
            (a, project.getRemoteArtifactRepositories()) || dependencyWritten;
        }
        m_out.unindent();
        if (dependencyWritten) {
            m_out.commit();
        } else {
            m_out.rollback();
        }
        
        m_out.unindent();
        m_out.writeLine("");
        
        if (pluginWritten || dependencyWritten) {
            m_out.commit();
        } else {
            m_out.rollback();
        }


    }
    
    /**
     * Print an artifact.
     * @param a Artifact to print
     * @param remoteRepositories Remote repositories to consider
     * @return If an artifact was written
     */
    private boolean printArtifact(Artifact a,
        List<ArtifactRepository> remoteRepositories) {
        if (StringUtils.isNotEmpty(excludeGroup)) {
            if (a.getGroupId().startsWith(excludeGroup)) {
                return false;
            }
        }
        
        String version = a.getVersion();
        
        if (a.isSnapshot()) {
            SnapshotArtifactRepositoryMetadata sarm 
                = new SnapshotArtifactRepositoryMetadata(a);
            try {
                m_metadataManager.resolve(sarm, remoteRepositories
                    , m_localRepository);
            } catch (RepositoryMetadataResolutionException e) {
                getLog().warn("Unable to resolve Metadata for " + a.getId());
            }
            Snapshot s = sarm.getMetadata().getVersioning().getSnapshot();
            if (s.getTimestamp() != null || s.getBuildNumber() != 0) {
                version += " (Timestamp " + s.getTimestamp();
                version += ", Build " + s.getBuildNumber() + ")";
            }
            
        }
        
        m_out.writeLine(a.getArtifactId() + ": " + version);
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        if (outFile == null) {
            outFile = new File("version_dump.txt");
        }
        
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to create " 
                    + outFile.getAbsolutePath(), e);
            }
        }
        
        try {
            m_out = new WriteBuffer(new PrintStream(outFile));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Unable to write to " 
                + outFile.getAbsolutePath(), e);
        }
        
        
        if (m_reactorProjects != null) {
            for (MavenProject project : m_reactorProjects) {
                processProject(project);
            }
        } else {
            processProject(m_project);
        }
        
        try {
            m_out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
}
