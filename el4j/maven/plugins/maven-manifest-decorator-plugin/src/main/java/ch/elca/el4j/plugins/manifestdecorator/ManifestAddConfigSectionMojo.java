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
package ch.elca.el4j.plugins.manifestdecorator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * Prepares data for the special config section inside the manifest.
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
 * @goal manifest-prepare-config-section
 * @phase package
 * @requiresProject true
 */
public class ManifestAddConfigSectionMojo extends AbstractMojo {
    // Checkstyle: MemberName off
    
    /**
     * Comma separated includes for the files list.
     *
     * @parameter expression="${fileListIncludes}" default-value="**\/*"
     * @required
     */
    protected String fileListIncludes;

    /**
     * Comma separated excludes for the files list.
     *
     * @parameter expression="${fileListExcludes}" default-value="**\/*.class"
     * @required
     */
    protected String fileListExcludes;
    
    /**
     * The prefix of each property which will be set.
     *
     * @parameter expression="${propertyNamePrefix}" default-value="el4j-config"
     * @required
     */
    protected String propertyNamePrefix;
    
    /**
     * Separator for string lists.
     *
     * @parameter expression="${separator}" default-value=","
     * @required
     */
    protected String separator;

    /**
     * Packaging name for normal jars.
     *
     * @parameter expression="${packagingNameJar}" default-value="jar"
     * @required
     */
    protected String packagingNameJar;
    
    /**
     * Packaging name for test jars.
     *
     * @parameter expression="${packagingNameTestJar}" default-value="test-jar"
     * @required
     */
    protected String packagingNameTestJar;
    
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
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        String projectPackaging = project.getPackaging();
        if (!StringUtils.hasText(projectPackaging) 
            || projectPackaging.contains("pom")) {
            getLog().info("No manifest config section property creation for "
                + "pom project.");
            return;
        }
        
        /**
         * Create the id for the current module.
         */
        String manifestModule = project.getGroupId() + ":" 
            + project.getArtifactId() + ":" + packagingNameJar;
        String manifestTestModule = project.getGroupId() + ":" 
            + project.getArtifactId() + ":" + packagingNameTestJar;
        
        /**
         * Get runtime dependency list for manifest.
         */
        List<Dependency> deps = project.getRuntimeDependencies();
        String manifestDependencies = getDependencyList(deps, false);
        List<Dependency> testDeps = project.getTestDependencies();
        String manifestTestDependencies = getDependencyList(testDeps, false);
        
        logDependencies (deps, testDeps);
        
        // Prepend dependency to own (main) module jar.
        manifestTestDependencies 
            = manifestModule + separator + manifestTestDependencies;
        
        /**
         * Get resource file list for manifest.
         */
        Build build = project.getBuild();
        String outputDirectoryString = null;
        String testOutputDirectoryString = null;
        if (build != null) {
            outputDirectoryString = build.getOutputDirectory();
            testOutputDirectoryString = build.getTestOutputDirectory();
        }
        String manifestFiles 
            = findFileResources(outputDirectoryString, "output");
        String manifestTestFiles 
            = findFileResources(testOutputDirectoryString, "testOutput");
        
        /**
         * Write the generated properties into project properties.
         */
        Properties projectProperties = project.getProperties();
        projectProperties.setProperty(
            propertyNamePrefix + ".module", manifestModule);
        projectProperties.setProperty(
            propertyNamePrefix + ".testmodule", manifestTestModule);
        projectProperties.setProperty(
            propertyNamePrefix + ".files", manifestFiles);
        projectProperties.setProperty(
            propertyNamePrefix + ".testfiles", manifestTestFiles);
        projectProperties.setProperty(
            propertyNamePrefix + ".dependencies", manifestDependencies);
        projectProperties.setProperty(
            propertyNamePrefix + ".testdependencies", manifestTestDependencies);
    }

    /**
     * log dependencies more intelligently
     * @param deps	the dependencies for normal executions
     * @param testDeps  the dependencies for tests
     */
    private void logDependencies(List<Dependency> deps,
            List<Dependency> testDeps) {
        if (deps == null) {
            deps = new ArrayList<Dependency>();
        }
        if (testDeps == null) {
            testDeps = new ArrayList<Dependency>();
        }       
        
        List<Dependency> onlyInNormal = calculateDependencyOnlyInFirstList(
                deps, testDeps);
        
        List<Dependency> onlyInTests = calculateDependencyOnlyInFirstList(
                testDeps, deps);
        
      String manifestDependencies = getDependencyList(deps, true);
        
      getLog().info("Project " + project.getGroupId() + ":" 
      + project.getArtifactId() + " has the following " 
      + deps.size() + " runtime dependencies: " 
      + manifestDependencies);
      
      getLog().info("Delta for tests:    only in tests: "+ getDependencyList(onlyInTests, true) + 
              "  | only in normal execution: "+ getDependencyList(onlyInNormal, true));
        
    }

    private List<Dependency> calculateDependencyOnlyInFirstList(
            List<Dependency> deps, List<Dependency> testDeps) {
        List<Dependency> onlyInNormal = new ArrayList<Dependency>();
        for (Dependency d : deps) {
            boolean found = false;
            for (Dependency tDep : testDeps) {
                if ((d.getArtifactId().equals(tDep.getArtifactId())) && 
                    (d.getGroupId().equals(tDep.getGroupId())) && 
                    (d.getVersion().equals(tDep.getVersion())) && 
                    (d.getType().equals(tDep.getType()))) {
                   found = true;
                   break;
                }
            }
            
            if (!found) {
                onlyInNormal.add(d);
            }
        }
        return onlyInNormal;
    }

    /**
     * @param deps Are the dependencies to fit into a single string.
     * @param showVersion shall we add the version for each dependency
     *                     (used for debugging)
     * @return Returns the dependencies as a string.
     */
    protected String getDependencyList(List<Dependency> deps, boolean showVersion) {
        Assert.notNull(deps);
        String manifestDependencies = "";
        if (!CollectionUtils.isEmpty(deps)) {
            StringBuffer sb = new StringBuffer();
            int manifestDependencyCount = 0;
            for (Dependency dependency : deps) {
                String artifactId = dependency.getArtifactId();
                String groupId = dependency.getGroupId();
                String type = dependency.getType();
                String dependencyString 
                    = groupId + ":" + artifactId + ":" + type;
                
                if (showVersion) {
                    dependencyString += ":" + dependency.getVersion();
                }
                
                if (manifestDependencyCount > 0) {
                    sb.append(separator);
                }
                sb.append(dependencyString);
                manifestDependencyCount++;
                
            }
            manifestDependencies = sb.toString();
            
        }
        return manifestDependencies;
    }

    /**
     * @param fileResourceDirectoryString Is the resource directiory as string.
     * @param directoryName Is the name of the given directory.
     * @return Returns a list of file resources as string. 
     */
    protected String findFileResources(String fileResourceDirectoryString,
        String directoryName) {
        Assert.hasText(directoryName);
        String resourceFiles = "";
        if (StringUtils.hasText(fileResourceDirectoryString)) {
            File fileResourceDirectory = new File(fileResourceDirectoryString);
            if (fileResourceDirectory.exists() 
                && fileResourceDirectory.isDirectory() 
                && fileResourceDirectory.canRead()) {
                try {
                    List<String> resourceFileList = FileUtils.getFileNames(
                        fileResourceDirectory, fileListIncludes, 
                        fileListExcludes, false, true);
                    int resourceFileCount = 0;
                    StringBuffer sb = new StringBuffer();
                    for (String resource : resourceFileList) {
                        if (resourceFileCount > 0) {
                            sb.append(separator);
                        }
                        resource = resource.replace("\\", "/");
                        sb.append(resource);
                        resourceFileCount++;
                    }
                    resourceFiles = sb.toString();
                    getLog().info("Project " + project.getGroupId() + ":" 
                        + project.getArtifactId() 
                        + " has following resource files in " + directoryName 
                        + " directory '" 
                        + fileResourceDirectoryString + "': " + resourceFiles);
                } catch (IOException e) {
                    getLog().info("Project " + project.getGroupId() + ":" 
                        + project.getArtifactId() 
                        + " made trouble while reading " + directoryName 
                        + " directory '" 
                        + fileResourceDirectoryString + "'.", e);
                }
                
            } else {
                getLog().info("Project " + project.getGroupId() + ":" 
                    + project.getArtifactId() 
                    + " has no readable " + directoryName + " directory '" 
                    + fileResourceDirectoryString + "'.");
            }
        } else {
            getLog().info("Project " + project.getGroupId() + ":" 
                + project.getArtifactId() + " has no " + directoryName 
                + " directory.");
        }
        return resourceFiles;
    }
}
