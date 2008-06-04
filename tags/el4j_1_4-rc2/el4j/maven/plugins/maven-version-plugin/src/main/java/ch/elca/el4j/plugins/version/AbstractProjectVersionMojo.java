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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * A common superclass for version goals that center on a project.
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
public abstract class AbstractProjectVersionMojo extends AbstractVersionMojo {
    //  Checkstyle: MemberName off
    /**
     * Should the plugin list all versions?
     * 
     * @parameter expression="${version.listall}"
     */
    private boolean listAllVersions = false;
    //  Checkstyle: MemberName on
    
    /**
     * All overview created so far.
     */
    private Map<Artifact, ArtifactOverview> m_overviews 
        = new HashMap<Artifact, ArtifactOverview>();
    
    /**
     * 
     * This class presents an overview over an artifacts occurences.
     *
     */
    protected class ArtifactOverview {
        /**
         * A list of occurences of the given artifact.
         */
        private List<ArtifactOccurence> m_occurences 
            = new LinkedList<ArtifactOccurence>();

        /**
         * All available versions of this artifact.
         */
        private VersionResult m_versions;

        /**
         * The artifact.
         */
        private Artifact m_artifact;
        
        /**
         * Create an overview for an artifact.
         * @param artifact The artifact this overview is about
         */
        public ArtifactOverview(Artifact artifact) {
            if (artifact == null) {
                throw new NullPointerException("Artifact is null");
            }

            this.m_artifact = artifact;
        }

        /**
         * At another occurence for this artifact.
         * @param occurence {@link ArtifactOccurence} to add
         */
        @SuppressWarnings("unchecked")
        public void addOccurence(ArtifactOccurence occurence) {
            m_occurences.add(occurence);
            
            // Get the Versions for this occurence's project
            List<ArtifactRepository> remoteRepositories 
                = (List<ArtifactRepository>) occurence
                    .getProject().getRemoteArtifactRepositories();
            VersionResult versions = getAvailableVersions(
                m_artifact, remoteRepositories);
            
            // Check if already a version result is there an if so,
            // try to merge them
            if (m_versions != null) {
                // ArtifactVersion seems not to implement equals, 
                // therefore it's done by hand.
                List<ArtifactVersion> oldVersions = m_versions.getVersions();
                List<ArtifactVersion> newVersions = versions.getVersions();
                List<ArtifactVersion> additionalVersions 
                    = new LinkedList<ArtifactVersion>();
                                
                // Test elements against each other
                for (ArtifactVersion newVersion : newVersions) {
                    boolean found = false;
                inner:
                    for (ArtifactVersion oldVersion : oldVersions) {
                        if (oldVersion.compareTo(newVersion) == 0) {
                            // found this element
                            found = true;
                            break inner;
                        }
                    }
                    if (!found) {
                        additionalVersions.add(newVersion);
                        getLog().warn("Adding version " + newVersion
                            + " to available version for " 
                            + m_artifact.getId() 
                            + ". Maybe there are different repositories " 
                            + "available to the different projects.");
                    }
                }
                
                oldVersions.addAll(additionalVersions);
            } else {
                m_versions = versions;
            }
            
        }

        /**
         * Get the list of all occurences.
         * 
         * @return All occurences
         */
        public List<ArtifactOccurence> getOccurences() {
            return m_occurences;
        }
        
        /**
         * Get all available versions.
         * @return All available versions
         */
        public VersionResult getVersions() {
            return m_versions;
        }

        /**
         * Get the artifact described by this overview.
         * 
         * @return The artifact
         */
        public Artifact getArtifact() {
            return m_artifact;
        }
    }

    /**
     * 
     * This class describes the occurence of a artifact in a project.
     *
     */
    protected class ArtifactOccurence {
        /**
         * The type of the reference.
         */
        private int m_type;

        /**
         * Project in which the artifact occurs.
         */
        private MavenProject m_project;

        /**
         * Create a new Occurence in a project.
         * 
         * @param project Project the occurence appears
         * @param type Type of the occurense
         */
        public ArtifactOccurence(MavenProject project, int type) {
            if (project == null || type == 0) {
                throw new NullPointerException("Null value passed");
            }

            this.m_type = type;
            this.m_project = project;
        }

        /**
         * Get the type of the occurence.
         * 
         * @return The type
         */
        public int getType() {
            return m_type;
        }

        /**
         * Get the project the artifact appears in.
         * 
         * @return The project
         */
        public MavenProject getProject() {
            return m_project;
        }
    }
    
    /**
     * 
     * The type of References.
     * 
     * This actually should be an enum, but qdox used by the
     * maven-plugin-plugin hates enums (leads to a ParseException), so we do
     * it the ugly way.
     *
     */
    protected static final class ReferenceType {
        /** 
         * A dependency.
         */
        public static final int DEPENDENCY = 1;
        
        /**
         * A plugin.
         */
        public static final int PLUGIN = 2;
        
        /**
         * A dependency management.
         */
        public static final int DEPENDENCY_MANAGEMENT = 3;
        
        /**
         * A plugin management.
         */
        public static final int PLUGIN_MANAGEMENT = 4;
        
        /**
         * Default Constructor.
         *
         */
        private ReferenceType() { };
        
        /**
         * Get a name for the reference.
         * @param type Type of the reference
         * @return Name for the given type
         */
        public static String getName(int type) {
            String name = "";
            switch (type) {
                case DEPENDENCY:
                    name = "Dependency";
                    break;
                case PLUGIN:
                    name = "Plugin";
                    break;
                case DEPENDENCY_MANAGEMENT:
                    name = "DependecyManagement";
                    break;
                case PLUGIN_MANAGEMENT:
                    name = "PluginManagement";
                    break;
                default:
                    name = "Undefined";
            }
            return name;
        }
    }
    
    /**
     * Cpmvert a dependency to an artifact.
     * @param dependency Dependency to convert
     * @return An artifact
     */
    protected Artifact toArtifact(Dependency dependency) {
        return getArtifact(dependency.getArtifactId(), dependency.getGroupId(),
            dependency.getVersion(), dependency.getScope(), dependency
                .getType());
    }
    
    /**
     * Convert a plugin to an artifact.
     * @param plugin Plugin to convert
     * @return An artifact
     */
    protected Artifact toArtifact(Plugin plugin) {
        return getArtifact(plugin.getArtifactId(), plugin.getGroupId(), plugin
            .getVersion(), Artifact.SCOPE_RUNTIME, "maven-plugin");
    }
    
    /**
     * Print all caches overviews.
     *
     */
    protected void printArtifactOverviews() {
        for (ArtifactOverview overview : m_overviews.values()) {
            if (listAllVersions || overview.getVersions()
                .isNewerVersionAvailable()) {
                Log log = getLog();
                List<ArtifactVersion> versions;
                
                log.info("Artifact ID: " 
                    + overview.getArtifact().getArtifactId());
                log.info("Group ID: " + overview.getArtifact().getGroupId());
                log.info("Version: " + overview.getArtifact().getVersion());
                if (listAllVersions) {
                    log.info("\tAvailable Versions: ");
                    versions = overview.getVersions().getVersions();
                } else {
                    log.info("\tNewer Versions: ");
                    versions = overview.getVersions().getNewerVersions();
                }
    
                for (ArtifactVersion v : versions) {
                    log.info("\t\t" + v);
                }
                
                log.info("\tOccurences in:");
                for (ArtifactOccurence occurence : overview.getOccurences()) {
                    log.info("\t\t\"" 
                        + occurence.getProject().getName() 
                        + "\" as " 
                        + ReferenceType.getName(occurence.getType()));
                }
                log.info("");
            }
        }
    }

    /**
     * Create an overview and add the given artifact to it. 
     * 
     * @param artifact The artifact to create an overview to.
     * @return The created overview
     */
    protected ArtifactOverview createOverview(Artifact artifact) {
        if (m_overviews.containsKey(artifact)) {
            return m_overviews.get(artifact);
        } else {
            ArtifactOverview result = new ArtifactOverview(artifact);
            m_overviews.put(artifact, result);
            return result;
        }
    }
}
