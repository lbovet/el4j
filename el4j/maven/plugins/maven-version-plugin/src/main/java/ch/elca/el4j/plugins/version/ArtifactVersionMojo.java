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

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * This class is ...
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 * @goal version
 */
public class ArtifactVersionMojo extends AbstractVersionMojo {
    
    /**
     * Artifact to find
     * @parameter expression="${version.artifactid}"
     * @ required
     */
    private String artifactId="";
    
    /**
     * Group of the Artifact to find
     * @parameter expression="${version.groupid}
     */
    private String groupId="";
    

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        
//        List metas;
////        try {
////            metas = discoverer.discoverMetadata(localRepository,"",Collections.EMPTY_LIST);
///*        } catch (DiscovererException e) {
//            // TODO Auto-generated catch block
//            throw new RuntimeException(e);
//        } */
//        
//        getLog().info("Trying to get versions of " + groupId + ":" + artifactId);
//        
//        Artifact a = factory.createArtifact(groupId,artifactId,null,null,"jar");
//   
//        ArtifactRepositoryMetadata meta = new ArtifactRepositoryMetadata(a);
        }

}
