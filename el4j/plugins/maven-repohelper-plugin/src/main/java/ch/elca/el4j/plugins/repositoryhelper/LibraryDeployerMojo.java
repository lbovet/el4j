/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.plugins.repositoryhelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Commandline;
import org.springframework.util.StringUtils;


/**
 * Maven mojo to deploy multiple libraries (jars and sources) in the given
 * repository.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * 
 * @goal deploy-libraries
 * @requiresProject true
 */
public class LibraryDeployerMojo extends AbstractLibraryAdderMojo {
    // Checkstyle: MemberName off
    /**
     * Directory where to deploy the libraries.
     * 
     * @parameter expression="${repositoryDirectory}"
     */
    protected File repositoryDirectory;
    
    /**
     * Url where to deploy the libraries.
     * 
     * @parameter expression="${repositoryUrl}"
     */
    protected URL repositoryUrl;
    
    /**
     * Id of the repository.
     * 
     * @parameter expression="${repositoryId}"
     */
    protected String repositoryId;
    
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    //Checkstyle: MemberName on
    
    /**
     * Is the repository url in string form.
     */
    private String m_repositoryUrlString;

    /**
     * {@inheritDoc}
     */
    protected void init() throws MojoExecutionException {
        super.init();
    
        /**
         * Get the repository url.
         */
        if (!StringUtils.hasText(repositoryId)) {
            throw new MojoExecutionException("Please set the following "
                + "properties: repositoryId or (repositoryId and "
                + "(repositoryDirectory or repositoryUrl))");
        }
        
        if (repositoryUrl != null) {
            m_repositoryUrlString = repositoryUrl.toString();
        } else if (repositoryDirectory != null) {
            checkIfWritableDirectory(repositoryDirectory, 
                "Repository");
            try {
                m_repositoryUrlString = repositoryDirectory.toURL().toString();
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        } else {
            repositoryId = repositoryId.trim();
        }
        
        /**
         * Set url to the configured one from the distribution mgt repository
         * if the repository id is the same.
         */
        if (m_repositoryUrlString == null) {
            DistributionManagement dm = project.getDistributionManagement();
            if (dm != null) {
                DeploymentRepository dr = dm.getRepository();
                if (dr != null && repositoryId.equals(dr.getId())) {
                    m_repositoryUrlString = dr.getUrl();
                }
            }
        }
        if (m_repositoryUrlString == null) {
            throw new MojoExecutionException("Repository url/directory could "
                + "not be found by using the repository id. Please further set "
                + "property repositoryDirectory or repositoryUrl.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void modifyCommandLine(MavenDependency dependency, 
        Commandline cmd) {
        cmd.createArgument().setValue("deploy:deploy-file");
        cmd.createArgument().setValue("-DgroupId=" + dependency.getGroupId());
        cmd.createArgument().setValue("-DartifactId=" 
            + dependency.getArtifactId());
        cmd.createArgument().setValue("-Dversion=" + dependency.getVersion());
        cmd.createArgument().setValue("-Dpackaging=jar");
        cmd.createArgument().setValue("-Dfile=" + dependency.getLibraryPath());
        
        if (m_repositoryUrlString != null) {
            cmd.createArgument().setValue("-Durl=" + m_repositoryUrlString);
        }
        if (StringUtils.hasText(repositoryId)) {
            cmd.createArgument().setValue("-DrepositoryId=" + repositoryId);
        }
        
        String classifier = dependency.getClassifier();
        if (StringUtils.hasText(classifier)) {
            cmd.createArgument().setValue("-Dclassifier=" + classifier);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getActionVerb() {
        return "deploy";
    }
}
