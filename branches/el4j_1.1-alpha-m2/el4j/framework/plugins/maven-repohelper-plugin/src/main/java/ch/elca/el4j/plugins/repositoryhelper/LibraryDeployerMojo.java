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
import java.net.MalformedURLException;
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
 * Maven mojo to deploy multiple libraries (jars and sources) in the given
 * repository path.
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
 */
public class LibraryDeployerMojo extends AbstractMojo {
    /**
     * Directory where the libraries to deloy are.
     * 
     * @parameter expression="${libraryDirectory}"
     *            property="libraryDirectory"
     * @required
     */
    private File m_libraryDirectory;
    
    /**
     * Directory where to deploy the libraries.
     * 
     * @parameter expression="${repositoryDirectory}"
     *            property="repositoryDirectory"
     * @required
     */
    private File m_repositoryDirectory;
    
    /**
     * Extension of jar files.
     * 
     * @parameter expression="${jarExtension}" default-value=".jar"
     *            property="jarExtension"
     * @required
     */
    private String m_jarExtension;
    
    /**
     * Extension of source files.
     * 
     * @parameter expression="${sourceExtension}" default-value="-src.zip"
     *            property="sourceExtension"
     * @required
     */
    private String m_sourceExtension;
    
    /**
     * Pattern to lookup jar files.
     * 
     * @parameter expression="${jarLookupPattern}" default-value="**\/*.jar"
     *            property="jarLookupPattern"
     * @required
     */
    private String m_jarLookupPattern;
    
    /**
     * Pattern to lookup source files.
     * 
     * @parameter expression="${sourceLookupPattern}" 
     *            default-value="**\/*-src.zip"
     *            property="sourceLookupPattern"
     * @required
     */    
    private String m_sourceLookupPattern;
    
    /**
     * Flag to indicate if the deploy process should stop on problem.
     * 
     * @parameter expression="${stopOnProblem}" default-value=true
     *            property="stopOnProblem"
     */
    private boolean m_stopOnProblem;
    
    
    /**
     * Flag to indicate if the execute process should only check the given data.
     * 
     * @parameter expression="${justCheckDirectories}" default-value=false
     *            property="justCheckDirectories"
     */
    private boolean m_justCheckDirectories;

    /**
     * Is the dir where this mojo is executed.
     * 
     * @parameter expression="${basedir}"
     *            property="basedir"
     * @required
     * @readonly
     */
    private File m_basedir;
        
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        /**
         * Check the basedir for correctness.
         */
        String basedirString = null;
        try {
            basedirString = getBasedir().getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Could not get canonical basedir path.", e);
        }
        
        /**
         * Check the given directories and save the canonical path from them
         * for later use.
         */
        String libraryDirectoryCanonical 
            = checkIfWritableDirectory(getLibraryDirectory(), "Library");
        checkIfWritableDirectory(getRepositoryDirectory(), "Repository");
        String repositoryUrl = getRepositoryUrl();
        
        /**
         * Lookup files by using the given patterns.
         */
        List<File> jarList 
            = getFiles(getLibraryDirectory(), getJarLookupPattern());
        List<File> sourceList 
            = getFiles(getLibraryDirectory(), getSourceLookupPattern());
        
        /**
         * Translate normal filenames into maven dependencies.
         */
        List<MavenDependency> jarDepList = getDependencies(
            jarList, libraryDirectoryCanonical, getJarExtension(), "");
        List<MavenDependency> sourceDepList = getDependencies(sourceList, 
            libraryDirectoryCanonical, getSourceExtension(), "sources");
        
        if (isJustCheckDirectories()) {
            getLog().info("Directory check successfully terminated.");
            getLog().info("Property 'justCheckDirectories' set to true, "
                + "so no library will be deployed.");
            return;
        }
        
        /**
         * Deploy libraries and sources.
         */
        deployLibraries(jarDepList, basedirString, repositoryUrl);
        deployLibraries(sourceDepList, basedirString, repositoryUrl);
    }
    
    /**
     * Deploys the given list of maven dependencies.
     * 
     * @param dependencyList
     *            Is the list of dependencies to deploy.
     * @param basedirString
     *            Is the dir where to run the deploy command.
     * @param repositoryUrl
     *            Is the url of the repository where to deploy the dependencies.
     * @throws MojoExecutionException
     *             If deploying failed.
     */
    protected void deployLibraries(List<MavenDependency> dependencyList, 
        String basedirString, String repositoryUrl) 
        throws MojoExecutionException {
        Assert.notEmpty(dependencyList);
        Assert.hasText(basedirString);
        Assert.hasText(repositoryUrl);
        Commandline cmd = new Commandline();
        cmd.setWorkingDirectory(basedirString);
        cmd.setExecutable("mvn");
        
        for (MavenDependency dependency : dependencyList) {
            deployLibrary(dependency, cmd, repositoryUrl);
        }
    }
    
    /**
     * Deploys the given maven dependency.
     * 
     * @param dependency
     *            Is the dependency to deploy.
     * @param cmd
     *            Is the command used to execute the deploy command.
     * @param repositoryUrl
     *            Is the place where to deploy the libraries.
     * @throws MojoExecutionException
     *             If deploying failed.
     */
    protected void deployLibrary(MavenDependency dependency, 
        Commandline cmd, String repositoryUrl) throws MojoExecutionException {
        Assert.notNull(dependency);
        Assert.hasText(dependency.getGroupId());
        Assert.hasText(dependency.getArtifactId());
        Assert.hasText(dependency.getVersion());
        Assert.hasText(dependency.getLibraryPath());
        Assert.notNull(cmd);
        Assert.hasText(repositoryUrl);

        /**
         * Add arguments to commandline.
         */
        cmd.clearArgs();
        cmd.createArgument().setValue("deploy:deploy-file");
        cmd.createArgument().setValue("-DgroupId=" + dependency.getGroupId());
        cmd.createArgument().setValue("-DartifactId=" 
            + dependency.getArtifactId());
        cmd.createArgument().setValue("-Dversion=" + dependency.getVersion());
        cmd.createArgument().setValue("-Dpackaging=jar");
        cmd.createArgument().setValue("-Dfile=" + dependency.getLibraryPath());
        cmd.createArgument().setValue("-DrepositoryId=repository");
        cmd.createArgument().setValue("-Durl=" + repositoryUrl);
        String classifier = dependency.getClassifier();
        if (StringUtils.hasText(classifier)) {
            cmd.createArgument().setValue("-Dclassifier=" + classifier);
        }
        
        /**
         * Execute commandline.
         */
        int result = -1;
        String output;
        StringBufferConsumer sbc = new StringBufferConsumer();
        try {
            result = CommandLineUtils.executeCommandLine(
                cmd, sbc, sbc);
        } catch (CommandLineException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            output = sbc.toString();
            getLog().info(output);
        }
        if (result != 0 || !output.contains("BUILD SUCCESSFUL")) {
            throw new MojoExecutionException(
                "It was not possible to deploy file with path '" 
                + dependency.getLibraryPath() 
                + "'. For more information see above.");
        } else {
            getLog().info("Library '" + dependency.getLibraryPath() 
                + "' could be successfully deployed.");
        }
    }
    
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
        if (isStopOnProblem()) {
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

    /**
     * @return Returns the basedir.
     */
    public final File getBasedir() {
        return m_basedir;
    }

    /**
     * @param basedir Is the basedir to set.
     */
    public final void setBasedir(File basedir) {
        m_basedir = basedir;
    }

    /**
     * @return Returns the jarExtension.
     */
    public final String getJarExtension() {
        return m_jarExtension;
    }

    /**
     * @param jarExtension Is the jarExtension to set.
     */
    public final void setJarExtension(String jarExtension) {
        m_jarExtension = jarExtension;
    }

    /**
     * @return Returns the jarLookupPattern.
     */
    public final String getJarLookupPattern() {
        return m_jarLookupPattern;
    }

    /**
     * @param jarLookupPattern Is the jarLookupPattern to set.
     */
    public final void setJarLookupPattern(String jarLookupPattern) {
        m_jarLookupPattern = jarLookupPattern;
    }

    /**
     * @return Returns the libraryDirectory.
     */
    public final File getLibraryDirectory() {
        return m_libraryDirectory;
    }

    /**
     * @param libraryDirectory Is the libraryDirectory to set.
     */
    public final void setLibraryDirectory(File libraryDirectory) {
        m_libraryDirectory = libraryDirectory;
    }

    /**
     * @return Returns the repositoryDirectory.
     */
    public final File getRepositoryDirectory() {
        return m_repositoryDirectory;
    }
    
    /**
     * @return Returns the repository directory as URL in string form.
     * @throws MojoExecutionException If there is a problem creating the URL.
     */
    protected String getRepositoryUrl() throws MojoExecutionException {
        try {
            return getRepositoryDirectory().toURL().toString();
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * @param repositoryDirectory Is the repositoryDirectory to set.
     */
    public final void setRepositoryDirectory(File repositoryDirectory) {
        m_repositoryDirectory = repositoryDirectory;
    }

    /**
     * @return Returns the sourceExtension.
     */
    public final String getSourceExtension() {
        return m_sourceExtension;
    }

    /**
     * @param sourceExtension Is the sourceExtension to set.
     */
    public final void setSourceExtension(String sourceExtension) {
        m_sourceExtension = sourceExtension;
    }

    /**
     * @return Returns the sourceLookupPattern.
     */
    public final String getSourceLookupPattern() {
        return m_sourceLookupPattern;
    }

    /**
     * @param sourceLookupPattern Is the sourceLookupPattern to set.
     */
    public final void setSourceLookupPattern(String sourceLookupPattern) {
        m_sourceLookupPattern = sourceLookupPattern;
    }

    /**
     * @return Returns the stopOnProblem.
     */
    public final boolean isStopOnProblem() {
        return m_stopOnProblem;
    }

    /**
     * @param stopOnProblem Is the stopOnProblem to set.
     */
    public final void setStopOnProblem(boolean stopOnProblem) {
        m_stopOnProblem = stopOnProblem;
    }

    /**
     * @return Returns the justCheckDirectories.
     */
    public final boolean isJustCheckDirectories() {
        return m_justCheckDirectories;
    }

    /**
     * @param justCheckDirectories Is the justCheckDirectories to set.
     */
    public final void setJustCheckDirectories(boolean justCheckDirectories) {
        m_justCheckDirectories = justCheckDirectories;
    }
}
