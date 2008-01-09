package ch.elca.el4j.maven.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectData {

    private File pom = null;

    private String groupId = null;

    private String artifactId = null;

    private String version = null;

    private String packaging = null;

    private List<String> dependencies = new ArrayList<String>();

    private List<ProjectData> dependentProjects = new ArrayList<ProjectData>();

    public ProjectData() {

    }

    public ProjectData(String groupId, String artifactId, String version) {
        this(null, groupId, artifactId, version, null);
    }

    public ProjectData(File pom, String groupId, String artifactId,
        String version, String packaging) {
        this.pom = pom;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packaging = packaging;
    }

    /**
     * @return the pom
     */
    public File getPom() {
        return pom;
    }

    /**
     * @param pom
     *            the pom to set
     */
    public void setPom(File pom) {
        this.pom = pom;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return avoidNull(groupId);
    }

    /**
     * @param groupId
     *            the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return avoidNull(artifactId);
    }

    /**
     * @param artifactId
     *            the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return avoidNull(version);
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the packaging
     */
    public String getPackaging() {
        return avoidNull(packaging);
    }

    /**
     * @param packaging
     *            the packaging to set
     */
    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    /**
     * Trims the specified string and returns it. Returns empty string if
     * specified string is null.
     * 
     * @param str
     *            the string to trim
     * @return trimmed, specified string or empty string, if specified string is
     *         null.
     */
    private String avoidNull(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * @return a string identifying this project.
     */
    public String getProjectId() {
        return createProjectIdString(getGroupId(), getArtifactId(),
            getVersion());
    }

    /**
     * Add a dependency to this project.
     * 
     * @param groupId
     *            groupId of dependency
     * @param artifactId
     *            artifactId of dependency
     * @param version
     *            version of dependency
     */
    public void addDependency(String groupId, String artifactId, String version) {
        addDependency(createProjectIdString(groupId, artifactId, version));
    }

    /**
     * Add a dependency to this project.
     * 
     * @param projectId
     *            String identifying the referenced project.
     */
    public void addDependency(String projectId) {
        dependencies.add(projectId);
    }

    /**
     * @param dependencies
     *            the dependencies to set
     */
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * @return list containing id-strings identifying dependencies of this
     *         project.
     */
    public List<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<String>();
        }
        return dependencies;
    }

    /**
     * Removes the specified dependencyId from this projectData-object.
     * 
     * @param dependencyId
     *            the dependencyId to remove from this projectData.
     * @return true if this list contained the specified dependencyId.
     */
    public boolean removeDependency(String dependencyId) {
        return dependencies.remove(dependencyId);
    }

    /**
     * Add ProjectData of a dependent project.
     * 
     * @param projectData
     *            data of a dependent project.
     */
    @Deprecated
    public void addDependentProject(ProjectData projectData) {
        dependentProjects.add(projectData);
    }

    /**
     * @return list with data of dependent projects.
     */
    @Deprecated
    public List<ProjectData> getDependentProjects() {
        return dependentProjects;
    }

    /**
     * Creates a project-id-string using the specified attributes.
     * 
     * @param groupId
     *            groupId of project
     * @param artifactId
     *            artifactId of project
     * @param version
     *            version of project
     * @return id-string string identifying a project.
     */
    public static String createProjectIdString(String groupId,
        String artifactId, String version) {
        StringBuffer dependency = new StringBuffer();
        dependency = dependency.append(groupId).append(":").append(artifactId)
            .append(":").append(version);
        return dependency.toString();
    }

    /**
     * @return File describing the directory where the pom of this project is
     *         located.
     */
    public File getDirectory() {
        File dir = null;

        if (getPom() != null) {
            dir = getPom().getParentFile();
        }

        if (dir == null) {
            return new File("");
        } else {
            return dir;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getProjectId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((artifactId == null) ? 0 : artifactId.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProjectData other = (ProjectData) obj;
        if (artifactId == null) {
            if (other.artifactId != null)
                return false;
        } else if (!artifactId.equals(other.artifactId))
            return false;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }
}
