/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.plugins.filecollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * Mojo to aggregate files in one file.
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
 * @goal aggregate-files
 */
public class AggregateFilesMojo extends AbstractMojo {
    // Checkstyle: MemberName off
    /**
     * Root directory where to begin source file lookup.
     * 
     * @parameter expression="${aggregate.rootSourceDirectory}"
     * @required
     */
    protected File rootSourceDirectory;
    
    /**
     * Comma separated list of patterns to lookup directories where to find 
     * source files.
     * 
     * @parameter expression="${aggregate.sourceDirectoryPatterns}"
     * @required
     */
    protected String sourceDirectoryPatterns;
    
    /**
     * Comma separated list of patterns to lookup source files.
     * 
     * @parameter expression="${aggregate.sourceFilePatterns}"
     * @required
     */
    protected String sourceFilePatterns;
    
    /**
     * Directory where to aggregate files.
     * 
     * @parameter expression="${aggregate.targetDirectory}"
     *            default-value="${project.build.directory}/aggregated-files"
     * @required
     */
    protected File targetDirectory;
    
    /**
     * Flag to indicate if it is allowed to overwrite existing files.
     * 
     * @parameter expression="${aggregate.allowOverwrite}"
     *            default-value="true"
     * @required
     */
    protected boolean allowOverwrite;
    
    /**
     * Flag to indicate if child projects should be visited.
     * 
     * @parameter expression="${aggregate.visitChildProjects}"
     *            default-value="false"
     * @required
     */
    protected boolean visitChildProjects;
    
    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;
    // Checkstyle: MemberName on
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        if (!visitChildProjects
            && project != null && !project.isExecutionRoot()) {
            log.info("Current project is a child project. "
                + "Execution will be skipped.");
            return;
        }
        
        // rootSourceDirectory checks
        Assert.isTrue(rootSourceDirectory != null 
            && rootSourceDirectory.isDirectory() 
            && rootSourceDirectory.canRead(), 
            "Given root source directory must be a readable directory!");
        String rootSourceDirectoryString = null;
        try {
            rootSourceDirectoryString = rootSourceDirectory.getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Could not get canonical path for rootSourceDirectory.");
        }
        
        // sourceDirectoryPatterns checks
        if (!StringUtils.hasText(sourceDirectoryPatterns)) {
            sourceDirectoryPatterns = null;
        }
        
        // sourceFilePatterns checks
        Assert.hasText(sourceFilePatterns, 
            "Minimum one source file pattern must be given!");
        
        // targetDirectory checks
        Assert.notNull(targetDirectory, "No target directory given!");
        targetDirectory.mkdirs();

        PathMatchingResourcePatternResolver resolver 
            = new PathMatchingResourcePatternResolver(
                new FileSystemResourceLoader());
        
        // Lookup source directories
        List<File> sourceDirectories = new ArrayList<File>();
        if (sourceDirectoryPatterns != null) {
            String[] dirPatterns = sourceDirectoryPatterns.split(",");
            for (String dirPattern : dirPatterns) {
                try {
                    Resource[] resources = resolver.getResources(
                        rootSourceDirectoryString + "/" + dirPattern);
                    for (Resource resource : resources) {
                        File dir = resource.getFile();
                        if (dir.isDirectory()) {
                            sourceDirectories.add(dir);
                        }
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                        "Exception occured while source dir lookup.", e);
                }
            }
        } else {
            log.warn("No source directory patterns found. Will take the root "
                + "source directory as the only one source directory.");
            sourceDirectories.add(rootSourceDirectory);
        }
        
        if (sourceDirectories.size() <= 0) {
            log.warn("No source directories found with given source "
                + "directory patterns '" + sourceDirectoryPatterns 
                + "'. File aggregation will be skipped.");
            return;
        }
        
        if (log.isInfoEnabled()) {
            log.info("Following " + sourceDirectories.size()
                + " source dir(s) could be found:");
            for (File sourceDirectory : sourceDirectories) {
                log.info(sourceDirectory.getAbsolutePath());
            }
        }
        
        String[] filePatterns = sourceFilePatterns.split(",");
        log.info("Following source file patterns are present: " 
            + sourceFilePatterns);
        for (File sourceDirectory : sourceDirectories) {
            String sourceDirectoryString;
            try {
                sourceDirectoryString = sourceDirectory.getCanonicalPath();
                int sourceDirectoryLength = sourceDirectoryString.length();
                for (String filePattern : filePatterns) {
                    int fileCounter = 0;
                    Resource[] resources = resolver.getResources(
                        sourceDirectoryString + "/" + filePattern);
                    for (Resource resource : resources) {
                        File sourceFile = resource.getFile();
                        String sourceFileString = sourceFile.getCanonicalPath();
                        if (!sourceFileString.startsWith(
                            sourceDirectoryString)) {
                            throw new MojoExecutionException("Looked up file '" 
                                + sourceFileString 
                                + "' is not from source dir '" 
                                + rootSourceDirectoryString + "'!");
                        }
                        String relativeFilePath 
                            = sourceFileString.substring(sourceDirectoryLength);
                        while (relativeFilePath.startsWith("/") 
                            || relativeFilePath.startsWith("\\")) {
                            relativeFilePath = relativeFilePath.substring(1);
                        }
                        File targetFile 
                            = new File(targetDirectory, relativeFilePath);
                        if (!allowOverwrite && targetFile.exists()) {
                            throw new MojoExecutionException(
                                "Overwrite not allowed! Target file '" 
                                + targetFile.getAbsolutePath() 
                                + "' does already exists.");
                        }
//                        log.info("Copy file '" + sourceFile.getAbsolutePath() 
//                            + "' to '" + targetFile.getAbsolutePath() + "'.");
                        
                        // Create target directories if necessary.
                        File targetFileDir = targetFile.getParentFile();
                        if (targetFileDir != null) {
                            targetFileDir.mkdirs();
                        }
                        
                        FileCopyUtils.copy(sourceFile, targetFile);
                        fileCounter++;
                    }
                    log.info(fileCounter + " file(s) copied from directory '" 
                        + sourceDirectoryString + "' by using pattern '" 
                        + filePattern + "'.");
                }
            } catch (IOException e) {
                throw new MojoExecutionException(
                    "Exception occured while lookup/copying source files.", e);
            }
        }
        

        
    }
}
