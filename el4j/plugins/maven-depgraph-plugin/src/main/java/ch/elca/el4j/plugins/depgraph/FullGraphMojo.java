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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Mojo to config files in the .settings directory. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/plugins/maven-checkclipse-helper-plugin/src/main/java/ch/elca/el4j/plugins/checkclipsehelper/CheckclipseConfigSettingsMojo.java
 * $", "$Revision$", "$Date: 2006-08-09 15:46:40 +0200 (Mi, 09 Aug 2006)
 * $", "$Author$" );</script>
 * 
 * @author Philippe Jacot (PJA)
 * @goal fullgraph
 * @aggregator 
 * @requiresDependencyResolution compile|test
 */
public class FullGraphMojo extends AbstractDependencyGraphMojo {
    /**
     * The default file extension.
     */
    public static final String DEFAULT_EXTENSION = "png";
        
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
        initOutput();
        
        DependencyGraph resultGraph = new DependencyGraph();
        resultGraph.setName(m_project.getName());
        
        for (MavenProject prj : m_reactorProjects) {
            processProject(prj, resultGraph);
        }
        
        if (resultGraph.getArtifacts().size() == 0) {
            getLog().error(
                "There were no Artifacts resolved. "
                    + "Maybe there's a problem with a user supplied filter.");
            throw new MojoExecutionException("No artifacts resolved");
        }       

        getProjector().project(resultGraph);   
    }
}
