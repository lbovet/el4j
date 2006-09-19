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
package ch.elca.el4j.plugins.repositoryhelper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Abstract mojo to add multiple libraries (jars and sources) to a given
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
 */
public abstract class AbstractLibraryAdderMojo extends AbstractMojo {
    // Checkstyle: MemberName off
    /**
     * Directory where the libraries to add are.
     * 
     * @parameter expression="${libraryDirectory}"
     * @required
     */
    protected File libraryDirectory;
    
    /**
     * Extension of jar files.
     * 
     * @parameter expression="${jarExtension}" default-value=".jar"
     * @required
     */
    protected String jarExtension;
    
    /**
     * Extension of source files.
     * 
     * @parameter expression="${sourceExtension}" default-value="-src.zip"
     * @required
     */
    protected String sourceExtension;
    
    /**
     * Pattern to lookup jar files.
     * 
     * @parameter expression="${jarLookupPattern}" default-value="**\/*.jar"
     * @required
     */
    protected String jarLookupPattern;
    
    /**
     * Pattern to lookup source files.
     * 
     * @parameter expression="${sourceLookupPattern}" 
     *            default-value="**\/*-src.zip"
     * @required
     */    
    protected String sourceLookupPattern;
    
    /**
     * Flag to indicate if the add process should stop on problem.
     * 
     * @parameter expression="${stopOnProblem}" default-value=true
     */
    protected boolean stopOnProblem;
    
    /**
     * Flag to indicate if the execute process should only check the given data.
     * 
     * @parameter expression="${justCheckDirectories}" default-value=false
     */
    protected boolean justCheckDirectories;

    /**
     * Is the dir where this mojo is executed.
     * 
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;
    //Checkstyle: MemberName on
    
    /**
     * The base dir in canonical format.
     */
    private String m_baseDirCanonical;
    
    /**
     * The library dir in canonical format.
     */
    private String m_libraryDirectoryCanonical;
        
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        /**
         * Initialize the mojo.
         */
        init();

        /**
         * Lookup files by using the given patterns.
         */
        List<File> jarList 
            = getFiles(libraryDirectory, jarLookupPattern);
        List<File> sourceList 
            = getFiles(libraryDirectory, sourceLookupPattern);
        
        /**
         * Translate normal filenames into maven dependencies.
         */
        List<MavenDependency> jarDepList = getDependencies(
            jarList, m_libraryDirectoryCanonical, jarExtension, "");
        List<MavenDependency> sourceDepList = getDependencies(sourceList, 
            m_libraryDirectoryCanonical, sourceExtension, "sources");
        
        if (justCheckDirectories) {
            getLog().info("Directory check successfully terminated.");
            getLog().info("Property 'justCheckDirectories' set to true, "
                + "so no library will be " + getActionVerb() + "ed.");
            return;
        }
        
        /**
         * Add libraries and sources.
         */
        addLibraries(jarDepList, m_baseDirCanonical);
        addLibraries(sourceDepList, m_baseDirCanonical);
    }
    
    /**
     * @return Returns the action verb used to print out specific messages.
     */
    protected abstract String getActionVerb();
    
    /**
     * Initializes the mojo.
     * 
     * @throws MojoExecutionException On any initalization problem.
     */
    protected void init() throws MojoExecutionException {
        /**
         * Check the basedir for correctness.
         */
        try {
            m_baseDirCanonical = basedir.getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Could not get canonical basedir path.", e);
        }
        
        /**
         * Check the given library directory and save the canonical path 
         * for later use.
         */
        m_libraryDirectoryCanonical 
            = checkIfWritableDirectory(libraryDirectory, "Library");
    }

    /**
     * Deploys the given list of maven dependencies.
     * 
     * @param dependencyList
     *            Is the list of dependencies to deploy.
     * @param basedirString
     *            Is the dir where to run the deploy command.
     * @throws MojoExecutionException
     *             If deploying failed.
     */
    protected void addLibraries(List<MavenDependency> dependencyList, 
        String basedirString) 
        throws MojoExecutionException {
        Assert.notNull(dependencyList);
        Assert.hasText(basedirString);
        Commandline cmd = new Commandline();
        cmd.setWorkingDirectory(basedirString);
        cmd.setExecutable("mvn");
        
        for (MavenDependency dependency : dependencyList) {
            addLibrary(dependency, cmd);
        }
    }
    
    /**
     * Add the given maven dependency.
     * 
     * @param dependency
     *            Is the dependency to deploy.
     * @param cmd
     *            Is the command used to execute the deploy command.
     * @throws MojoExecutionException
     *             If deploying failed.
     */
    protected void addLibrary(MavenDependency dependency, Commandline cmd) 
        throws MojoExecutionException {
        Assert.notNull(dependency);
        Assert.hasText(dependency.getGroupId());
        Assert.hasText(dependency.getArtifactId());
        Assert.hasText(dependency.getVersion());
        Assert.hasText(dependency.getLibraryPath());
        Assert.notNull(cmd);

        /**
         * Add arguments to commandline.
         */
        cmd.clearArgs();
        modifyCommandLine(dependency, cmd);
        
        /**
         * Execute commandline.
         */
        int result = -1;
        BufferedLoggerConsumer blg = new BufferedLoggerConsumer(getLog());
        try {
            result = CommandLineUtils.executeCommandLine(
                cmd, blg, blg);
        } catch (CommandLineException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        String output = blg.toString();
        if (result != 0 || output.contains("BUILD ERROR")) {
            throw new MojoExecutionException(
                "It was not possible to " + getActionVerb() 
                + " file with path '" 
                + dependency.getLibraryPath() 
                + "'. For more information see above.");
        } else {
            getLog().info("Library '" + dependency.getLibraryPath() 
                + "' could be successfully " + getActionVerb() + "ed.");
        }
    }

    /**
     * Modifies the given command line.
     * 
     * @param dependency Is the library to add.
     * @param cmd Is the command line used to add library.
     */
    protected abstract void modifyCommandLine(MavenDependency dependency, 
        Commandline cmd);
    
    /**
     * Checks if the given file is an existing, writable directory.
     * 
     * @param dir
     *            Is the given directory.
     * @param description
     *            Is the description word for the given directory.
     * @return Returns the canonical path of the given directory.
     * @throws MojoExecutionException
     *             If given file is not an existing, writable directory.
     */
    protected String checkIfWritableDirectory(File dir, 
        String description) throws MojoExecutionException {
        Assert.notNull(dir);
        Log logger = getLog();
        String path = null;
        try {
            path = dir.getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Could not get canonical path for directory " 
                + description + ".");
        }
        if (!dir.exists()) {
            throw new MojoExecutionException(
                description + " path '" + path + "' does not exists!");
        } else if (!dir.isDirectory()) {
            throw new MojoExecutionException(
                description + " path '" + path + "' must be a directory!");
        } else if (!dir.canWrite()) {
            throw new MojoExecutionException(
                description + " directory '" + path + "' is write protected!");
        } else {
            logger.debug(description + " directory is set to '" + path + "'.");
        }
        return path;
    }
    
    /**
     * @param baseDir
     *            Is the place where to start searching files.
     * @param pattern
     *            Is the pattern to search files.
     * @return Returns a list of files that match the given pattern and are in
     *         the given base directory.
     */
    protected List<File> getFiles(File baseDir, String pattern) {
        Assert.notNull(baseDir);
        Assert.hasText(pattern);
        Log logger = getLog();
        List<File> fileList = new LinkedList<File>();
        PathMatchingResourcePatternResolver resolver 
            = new PathMatchingResourcePatternResolver(
                new FileSystemResourceLoader());
        try {
            Resource[] resources = resolver.getResources(
                baseDir.getCanonicalPath() + "/" + pattern);
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    File file = resources[i].getFile();
                    fileList.add(file);
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Found following " + fileList.size() 
                    + " file(s) for pattern '" 
                    + pattern + "'.");
                for (File file : fileList) {
                    logger.debug("Found file: " + file.getCanonicalPath());
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }
        
        return fileList;
    }
    
    /**
     * @param fileList
     *            Is the file list.
     * @param basePath
     *            Is the place where files where found. Must be given to detect
     *            the group id of a dependency.
     * @param fileExtension
     *            Is the extension that the given file must have. The dot before
     *            the extension must be included in this string.
     * @param classifier
     *            Is the type of the dependency. For jars this is empty, for
     *            source it is <code>classifier</code>.
     * @return Reuturns the converted file list as a list of maven dependencies.
     * @throws MojoExecutionException On file problem.
     */
    protected List<MavenDependency> getDependencies(List<File> fileList, 
        String basePath, String fileExtension, String classifier)
        throws MojoExecutionException {
        Assert.notNull(fileList);
        Assert.hasText(basePath);
        Assert.hasText(fileExtension);
        
        Log logger = getLog();
        List<MavenDependency> dependencyList 
            = new LinkedList<MavenDependency>();
        
        for (File file : fileList) {
            String filenameWithExtension = file.getName();
            logger.debug(
                "Inspecting file with name '" + filenameWithExtension + "'.");
            
            String filePath = null;
            try {
                filePath = file.getCanonicalPath();
            } catch (IOException e) {
                reportFileProblem(
                    "Could not get canonical path of file with name '" 
                    + filenameWithExtension + "'.", e);
                continue;
            }
            
            if (!filenameWithExtension.endsWith(fileExtension)) {
                reportFileProblem("File with name '" + filenameWithExtension 
                    + "' ends not with given file extension " + fileExtension 
                    + ".", null);
                continue;
            }
            String filename = filenameWithExtension.substring(
                0, filenameWithExtension.length() - fileExtension.length());
            
            
            MavenDependency dependency 
                = extractAndSetVersionAndArtifactId(filename);
            
            if (dependency == null 
                || !StringUtils.hasText(dependency.getVersion())
                || !StringUtils.hasText(dependency.getArtifactId())) {
                reportFileProblem("Version or artifact id of given file with "
                    + "name '" + filenameWithExtension 
                    + "' could not be extracted.", null);
                continue;
            }
            
            extractAndSetGroupId(basePath, filenameWithExtension, 
                filePath, dependency);
            
            dependency.setClassifier(classifier);
            dependency.setLibraryPath(filePath);
            dependencyList.add(dependency);
        }
        
        logger.info(dependencyList.size() 
            + " dependencies are ready to be uploaded.");
        if (logger.isDebugEnabled()) {
            for (MavenDependency dependency : dependencyList) {
                logger.debug(dependency.toString());
            }
        }
        
        return dependencyList;
    }
    
    /**
     * Reports the file error. If flag <code>stopOnProblem</code> a
     * <code>MojoExecutionException</code> will be thrown, else a log message
     * will be published.
     * 
     * @param message
     *            Is the message to publish.
     * @param t
     *            Is cause for this problem.
     * @throws MojoExecutionException
     *             If flag <code>stopOnProblem</code> is set to
     *             <code>true</code>.
     */
    protected void reportFileProblem(String message, Throwable t) 
        throws MojoExecutionException {
        if (stopOnProblem) {
            throw new MojoExecutionException(
                message + " Deploy process terminated!", t);
        } else {
            getLog().error(message + " File will be skipped!", t);
        }
    }
    
    /**
     * Extracts the version out of the given filename. The artifact id is the
     * other part of the filename. See here the rules:
     * 
     * Looking for version in filename prefix. The rules are the following:
     * <ol>
     *     <li>
     *       Look for first occurrence of a diget right after a -.
     *       The text from this digit to the end of the filename
     *       without the extension is the version.
     *     </li>
     *     <li>
     *       Look for last occurrence of a -.
     *       The text after this charater to the end of the filename
     *       without the extension is the version.
     *     </li>
     * </ol>
     * The rest of the filename (minus the dash) is the artifact id.
     * 
     * @param filenamePrefix
     *            Is the name of the file without the extension (and without the
     *            dot).
     * @return Return the created maven dependency where the version and the
     *         artifact id are set. Returns <code>null</code> if the values
     *         could not be extracted.
     */
    protected MavenDependency extractAndSetVersionAndArtifactId(
        String filenamePrefix) {
        String version = null;
        int dashIndex = filenamePrefix.indexOf('-');
        while (version == null 
            && dashIndex > 0 
            && dashIndex + 1 < filenamePrefix.length()) {
            char firstVersionChar = filenamePrefix.charAt(dashIndex + 1);
            if (firstVersionChar >= '0' && firstVersionChar <= '9') {
                version = filenamePrefix.substring(dashIndex + 1);
            } else {
                dashIndex = filenamePrefix.indexOf('-', dashIndex + 1);
            }
        }
        if (version == null) {
            dashIndex = filenamePrefix.lastIndexOf('-');
            if (dashIndex > 0 && dashIndex + 1 < filenamePrefix.length()) {
                version = filenamePrefix.substring(dashIndex + 1);
            }
        }
        
        MavenDependency dependency = null;
        if (version != null) {
            dependency = new MavenDependency();
            dependency.setVersion(version);
            
            /**
             * The artifac id is the part before the dash.
             */
            String artifactId = filenamePrefix.substring(0, dashIndex);
            dependency.setArtifactId(artifactId);
        }
        return dependency;
    }

    /**
     * @param basePath
     *            Is the path where the file was found.
     * @param filenameWithExtension
     *            Is the full name of the dependency file.
     * @param filePath
     *            Is the full path to the dependency file.
     * @param dependency
     *            Is the place where to set the group id.
     */
    protected void extractAndSetGroupId(String basePath, 
        String filenameWithExtension, String filePath, 
        MavenDependency dependency) {
        Log logger = getLog();
        String groupId = null;
        if (filePath.startsWith(basePath)) {
            String tail = filePath.substring(basePath.length());
            if (tail.endsWith(filenameWithExtension)) {
                tail = tail.substring(
                    0, tail.length() - filenameWithExtension.length());
                tail = tail.replace('\\', '.');
                tail = tail.replace('/', '.');
                tail = tail.replace(' ', '-');
                while (tail.length() > 0 && tail.startsWith(".")) {
                    tail = tail.substring(1);
                }
                while (tail.length() > 0 && tail.endsWith(".")) {
                    tail = tail.substring(0, tail.length() - 1);
                }
                if (tail.length() > 0) {
                    groupId = tail;
                }
            }
        }
        if (groupId == null) {
            logger.warn("No group id found for file '" + filePath 
                + "'. Group id will be set to its artifact id '" 
                + dependency.getArtifactId() + "'.");
            groupId = dependency.getArtifactId();
        }
        dependency.setGroupId(groupId);
    }
}
