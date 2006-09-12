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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.PathMatcher;
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
     * Comma separated list of include patterns to lookup directories where to
     * find source files.
     * 
     * @parameter expression="${aggregate.sourceDirectoryIncludePatterns}"
     * @required
     */
    protected String sourceDirectoryIncludePatterns;
    
    /**
     * Comma separated list of exclude patterns to lookup directories where to
     * find source files.
     * 
     * @parameter expression="${aggregate.sourceDirectoryExcludePatterns}"
     */
    protected String sourceDirectoryExcludePatterns;

    /**
     * Comma separated list of include patterns to lookup source files.
     * 
     * @parameter expression="${aggregate.sourceFileIncludePatterns}"
     * @required
     */
    protected String sourceFileIncludePatterns;

    /**
     * Comma separated list of exclude patterns to lookup source files.
     * 
     * @parameter expression="${aggregate.sourceFileExcludePatterns}"
     */
    protected String sourceFileExcludePatterns;

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
     * Is the canonical path of the root source dir.
     */
    protected String m_rootSourceDirectoryString;
    
    /**
     * Are the dir include patterns.
     */
    protected String[] m_dirIncludePatterns;

    /**
     * Are the dir exclude patterns.
     */
    protected String[] m_dirExcludePatterns;
    
    /**
     * Are the file include patterns.
     */
    protected String[] m_fileIncludePatterns;

    /**
     * Are the file exclude patterns.
     */
    protected String[] m_fileExcludePatterns;

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
        
        init();

        PathMatchingResourcePatternResolver resolver 
            = new PathMatchingResourcePatternResolver(
                new FileSystemResourceLoader());
        PathMatcher pathMatcher = new AntPathMatcher();
        resolver.setPathMatcher(pathMatcher);
        
        List<File> sourceDirectories = resolveSourceDirectories(resolver);
        
        if (sourceDirectories.size() <= 0) {
            log.warn("No source directories found with given source "
                + "directory patterns '" + sourceDirectoryIncludePatterns 
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
        
        resolveSourceFiles(resolver, sourceDirectories);
    }

    /**
     * Resolves the source directories.
     * 
     * @param resolver Is the resolver used for dir lookup.
     * @return Returns the list of source dirs.
     * @throws MojoExecutionException On any execution problem.
     */
    protected List<File> resolveSourceDirectories(
        PathMatchingResourcePatternResolver resolver)
        throws MojoExecutionException {
        
        Log log = getLog();
    
        PathMatcher pathMatcher = resolver.getPathMatcher();
        List<File> sourceDirectories = new ArrayList<File>();
        if (sourceDirectoryIncludePatterns != null) {
            for (String dirPattern : m_dirIncludePatterns) {
                try {
                    Resource[] resources = resolver.getResources(
                        m_rootSourceDirectoryString + "/" + dirPattern);
                    for (Resource resource : resources) {
                        File dir = resource.getFile();
                        if (dir.isDirectory()) {
                            // Check if the given source dir should be excluded.
                            boolean matchesExclude = false;
                            if (m_dirExcludePatterns != null) {
                                String dirString = StringUtils.replace(
                                    dir.getCanonicalPath(), 
                                    File.separator, "/");
                                for (int i = 0; !matchesExclude 
                                    && i < m_dirExcludePatterns.length; i++) {
                                    matchesExclude = pathMatcher.match(
                                        m_dirExcludePatterns[i], dirString);
                                }
                            }
                            if (matchesExclude) {
                                // Skip current source dir.
                                continue;
                            }
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
        return sourceDirectories;
    }

    /**
     * Resolves the source directories.
     * 
     * @param resolver Is the resolver used for file lookup.
     * @param sourceDirectories Are the src dirs where to lookup files.
     * @throws MojoExecutionException On any execution problem.
     */
    protected void resolveSourceFiles(
        PathMatchingResourcePatternResolver resolver, 
        List<File> sourceDirectories) throws MojoExecutionException {
        
        Log log = getLog();
        
        PathMatcher pathMatcher = resolver.getPathMatcher();
        
        for (File sourceDirectory : sourceDirectories) {
            String sourceDirectoryString;
            try {
                sourceDirectoryString = StringUtils.replace(
                    sourceDirectory.getCanonicalPath(), File.separator, "/");
                int sourceDirectoryLength = sourceDirectoryString.length();
                for (String filePattern : m_fileIncludePatterns) {
                    int fileCounter = 0;
                    Resource[] resources = resolver.getResources(
                        sourceDirectoryString + "/" + filePattern);
                    for (Resource resource : resources) {
                        File sourceFile = resource.getFile();
                        String sourceFileString = StringUtils.replace(
                            sourceFile.getCanonicalPath(), File.separator, "/");
                        
                        // Check if the current resource is inside the source
                        // dir.
                        if (!sourceFileString.startsWith(
                            sourceDirectoryString)) {
                            throw new MojoExecutionException("Looked up file '" 
                                + sourceFileString 
                                + "' is not from source dir '" 
                                + m_rootSourceDirectoryString + "'!");
                        }
                        
                        // Check if file should be excluded.
                        boolean matchesExclude = false;
                        if (m_fileExcludePatterns != null) {
                            for (int i = 0; !matchesExclude 
                                && i < m_fileExcludePatterns.length; i++) {
                                matchesExclude = pathMatcher.match(
                                    m_fileExcludePatterns[i], sourceFileString);
                            }
                        }
                        if (matchesExclude) {
                            // Skip current resource.
                            continue;
                        }

                        String relativeFilePath 
                            = sourceFileString.substring(sourceDirectoryLength);
                        while (relativeFilePath.startsWith("/")) {
                            relativeFilePath = relativeFilePath.substring(1);
                        }

                        copyFile(sourceFile, relativeFilePath);
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

    /**
     * Copies the given source file with the given relative file path.
     * 
     * @param sourceFile Is the source file to copy.
     * @param relativeFilePath Is the relative path to copy.
     * @return Returns the copied file.
     * @throws MojoExecutionException On any execution exception.
     * @throws IOException On any copy problems. 
     */
    protected File copyFile(File sourceFile, String relativeFilePath)
        throws MojoExecutionException, IOException {
        File targetFile 
            = new File(targetDirectory, relativeFilePath);
        if (!allowOverwrite && targetFile.exists()) {
            throw new MojoExecutionException(
                "Overwrite not allowed! Target file '" 
                + targetFile.getAbsolutePath() 
                + "' does already exists.");
        }

        // Create target directories if necessary.
        File targetFileDir = targetFile.getParentFile();
        if (targetFileDir != null) {
            targetFileDir.mkdirs();
        }

        // Copy file.
        FileCopyUtils.copy(sourceFile, targetFile);
        
        return targetFile;
    }
    
    /**
     * Initializes the mojo.
     * 
     * @throws MojoExecutionException On any problem while execution.
     */
    protected void init() throws MojoExecutionException {
        initPaths();
        initPatterns();
    }

    /**
     * Initializes the the paths.
     * 
     * @throws MojoExecutionException On any problem while execution.
     */
    protected void initPaths() throws MojoExecutionException {
        // rootSourceDirectory checks
        Assert.isTrue(rootSourceDirectory != null 
            && rootSourceDirectory.isDirectory() 
            && rootSourceDirectory.canRead(), 
            "Given root source directory must be a readable directory!");
        try {
            m_rootSourceDirectoryString 
                = rootSourceDirectory.getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Could not get canonical path for rootSourceDirectory.");
        }
        
        // targetDirectory checks
        Assert.notNull(targetDirectory, "No target directory given!");
        targetDirectory.mkdirs();
    }

    /**
     * Initializes the patterns.
     * 
     * @throws MojoExecutionException On any problem while execution.
     */
    protected void initPatterns() throws MojoExecutionException {
        Log log = getLog();
        
        // pattern checks
        if (!StringUtils.hasText(sourceDirectoryIncludePatterns)) {
            sourceDirectoryIncludePatterns = null;
        }
        if (!StringUtils.hasText(sourceDirectoryExcludePatterns)) {
            sourceDirectoryExcludePatterns = null;
        }
        if (!StringUtils.hasText(sourceFileExcludePatterns)) {
            sourceFileExcludePatterns = null;
        }
        
        // sourceFilePatterns checks
        Assert.hasText(sourceFileIncludePatterns, 
            "Minimum one source file pattern must be given!");
        
        // Initializing source dir include patterns array
        if (sourceDirectoryIncludePatterns != null) {
            m_dirIncludePatterns = sourceDirectoryIncludePatterns.split(",");
            log.info("Following source dir include patterns are present: " 
                + sourceDirectoryIncludePatterns);
        } else {
            log.info("No source dir include patterns are present.");
        }
        
        // Initializing source dir exclude patterns array
        if (sourceDirectoryExcludePatterns != null) {
            m_dirExcludePatterns = sourceDirectoryExcludePatterns.split(",");
            log.info("Following source dir exclude patterns are present: " 
                + sourceDirectoryExcludePatterns);
        } else {
            log.info("No source dir exclude patterns are present.");
        }

        // Initializing source file include patterns array
        if (sourceFileIncludePatterns != null) {
            m_fileIncludePatterns = sourceFileIncludePatterns.split(",");
            log.info("Following source file include patterns are present: " 
                + sourceFileIncludePatterns);
        } else {
            log.info("No source file include patterns are present.");
        }
        
        // Initializing source file exclude patterns array
        if (sourceFileExcludePatterns != null) {
            m_fileExcludePatterns = sourceFileExcludePatterns.split(",");
            log.info("Following source file exclude patterns are present: " 
                + sourceFileExcludePatterns);
        } else {
            log.info("No source file exclude patterns are present.");
        }
    }
}
