package ch.elca.el4j.maven.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author chd
 * @goal execute
 */
public class MavenRecursiveMojo extends AbstractMojo {

    private static final String BOOTSTRAP_FILENAME = "mvn_rec_bootstrap.xml";

    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject m_project;

    /**
     * Artifact resolver, needed to download source jars for inclusion in
     * classpath.
     * 
     * @component
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * The project's artifact metadata source, used to resolve transitive
     * dependencies.
     * 
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * The collection of remote artifact repositories.
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteRepositories;

    /**
     * Set to true to create a bootstrap-file.
     * 
     * @parameter expression="${mvn.rec.bootstrap}" default-value="false"
     */
    private boolean bootstrap;

    /**
     * The shell-command to execute in each directory of all dependent projects
     * before executing in the directory of the current project.
     * 
     * @parameter expression="${mvn.rec.cmd}"
     * @required
     */
    private String command;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (command == null || command.trim().equals("")) {
            getLog().error("No commands defined!");
            System.exit(1);
        }

        if (!isAllowedPackaging(m_project.getPackaging())) {
            getLog().error("Wrong packaging type: " + m_project.getPackaging());
            System.exit(2);
        }

        /*
         * get projectId-to-ProjectData-mapping
         */
        Map<String, ProjectData> bootstrapMap = getBootstrapMap();

        // write map to log
        for (String projectId : bootstrapMap.keySet()) {
            ProjectData projectData = bootstrapMap.get(projectId);
            getLog().info("Project found: ".concat(projectData.getProjectId()));
            for (String dependencyId : projectData.getDependencies()) {
                getLog().info(" - " + dependencyId);
            }
        }

        /*
         * Create sorted list of directories where the shell-command should be
         * executed for the given project
         */
        List<File> executionDirectories = createExecutionDirectoryList(bootstrapMap);

        /*
         * Execute the provided shell-command within all directories in the list
         */
        List<File> successDirs = executeCommandInDirectories(executionDirectories);
        
        /*
         * Printout list of directories where command execution failed
         */
        executionDirectories.removeAll(successDirs);
        if (executionDirectories.size() > 0) {
            getLog().info(
                "[MavenRec] Execution failed in the following directories:");
            getLog().info(executionDirectories.toString());
        } else {
            getLog().info("");
            getLog().info("[MavenRec] Successful done!");
            getLog().info("");
        }
    }

    /**
     * Returns true if the specified packaging-type is allowed to execute this
     * plugin on. Only "jar" and "war" packagings are allowed.
     * 
     * @param packaging
     *            the packaging-type of the project.
     * @return true if the specified packaging-type is allowed, otherwise false.
     */
    private boolean isAllowedPackaging(String packaging) {
        List<String> allowedPackaging = new ArrayList<String>();
        allowedPackaging.add("jar");
        allowedPackaging.add("war");

        return allowedPackaging.contains(packaging);
    }

    private Map<String, ProjectData> getBootstrapMap() {
        File rootDir = getRootDir(m_project);
        Map<String, ProjectData> bootstrapMap = null;

        if (!bootstrap) {
            // load already existing bootstrap-map from file
            try {
                bootstrapMap = loadBootstrapMap(rootDir);
            } catch (Exception e) {
                bootstrapMap = null;
                getLog()
                    .warn("Error loading bootstrap-file: " + e.getMessage());
            }
        }

        if (bootstrapMap == null) {
            // Create a bootstrap map
            bootstrapMap = createBootstrapMap(rootDir);
            // store bootstrap map to file
            try {
                storeBootstrapMap(bootstrapMap, rootDir);
            } catch (IOException e) {
                getLog().warn(e);
            }
        }

        return bootstrapMap;
    }

    /**
     * Create a bootstrap map by inspecting recursively any subfolders of the
     * specified root-directory.
     * 
     * @param rootDir
     *            the directory where to start the recursive search for local
     *            projects.
     * @return the bootstrap-map mapping projectIds to ProjectData-objects
     */
    private Map<String, ProjectData> createBootstrapMap(File rootDir) {
        getLog()
            .info(
                "Create new boostrap-map from rootdir "
                    + rootDir.getAbsolutePath());
        /*
         * Retrieve set of ProjectData-objects of local projects (with packaging
         * "jar" or "war")
         */
        Map<String, ProjectData> localProjectMap = new HashMap<String, ProjectData>();
        searchLocalProjects(rootDir, localProjectMap);

        /*
         * Remove dependencies to non-local projects
         */
        removeDependenciesToNonLocalProjects(localProjectMap);

        return localProjectMap;
    }

    /**
     * Store the specified bootstrap-map into a XML-file located at the
     * specified directory.
     * 
     * @param bootstrapMap
     *            the map to store
     * @param dir
     *            the directory where to store the xml.
     * @throws IOException
     *             if there's any problem writing.
     */
    private void storeBootstrapMap(Map<String, ProjectData> bootstrapMap,
        File dir) throws IOException {
        File bootstrapXml = new File(dir, BOOTSTRAP_FILENAME);
        getLog().info(
            "Store bootstrap-map into " + bootstrapXml.getAbsolutePath());

        Document bootstrapDom = BootstrapFileHandler
            .createDomFromBoostrapMap(bootstrapMap);
        BootstrapFileHandler.storeDomToXml(bootstrapDom, bootstrapXml);
    }

    /**
     * Load the bootstrap-map from the xml-file in the specified directory.
     * 
     * @param dir
     *            the directory where the xml to load the map from is located.
     * @return the bootstrap-map loaded from the xml-file.
     * @throws JDOMException
     *             when errors occur in parsing
     * @throws IOException
     *             when an I/O error prevents a document from being fully parsed
     */
    private Map<String, ProjectData> loadBootstrapMap(File dir)
        throws JDOMException, IOException {
        File bootstrapXml = new File(dir, BOOTSTRAP_FILENAME);

        getLog().info(
            "Load bootstrap-map from " + bootstrapXml.getAbsolutePath());

        Document bootstrapDom = BootstrapFileHandler
            .loadDomFromXml(bootstrapXml);
        Map<String, ProjectData> bootstrapMap = BootstrapFileHandler
            .createBootstrapMapFromDom(bootstrapDom);

        return bootstrapMap;
    }

    /**
     * Creates a sorted list of directory-paths where the execute the
     * shell-command. The position of the directories in the list corresponds to
     * the order in which they have to be processed.
     * 
     * @param bootstrapMap
     *            mapping of projectIds to ProjectData-objects.
     * @return list of directories where to execute the shell-command.
     */
    private List<File> createExecutionDirectoryList(
        Map<String, ProjectData> bootstrapMap) {
        List<File> directoryList = new ArrayList<File>();

        String startProjectId = ProjectData.createProjectIdString(m_project
            .getGroupId(), m_project.getArtifactId(), m_project.getVersion());

        postOrderTraverseDependencyGraph(startProjectId, bootstrapMap,
            directoryList);

        return directoryList;
    }

    /**
     * Traverse the dependent project of the specified project in postorder. Add
     * the directory of the visited projects to the directory-list.
     * 
     * @param projectId
     *            the project whose dependencies are to traverse.
     * @param bootstrapMap
     *            the map containing the projectId to ProjectData-mapping.
     * @param directoryList
     *            list where to add the directories of the visited projects.
     */
    private void postOrderTraverseDependencyGraph(String projectId,
        Map<String, ProjectData> bootstrapMap, List<File> directoryList) {

        ProjectData projectData = bootstrapMap.get(projectId);
        // traverse all dependent projects
        for (String dependentProjectId : projectData.getDependencies()) {
            postOrderTraverseDependencyGraph(dependentProjectId, bootstrapMap,
                directoryList);
        }
        /*
         * add the directory of current project after the directories of all
         * dependent projects. Before a project can be processed, all its
         * dependent projects have to be up to date at that moment.
         */
        // avoid listing a directory more than once
        if (!directoryList.contains(projectData.getDirectory())) {
            directoryList.add(projectData.getDirectory());
        }
    }

    /**
     * Execute the provided shell-command within the directories in the list.
     * 
     * @param executionDirectories
     *            list of directories where to execute the shell-command.
     * @return List of directories where command has been executed successfully.
     */
    private List<File> executeCommandInDirectories(List<File> executionDirectories) {

        List<File> successfulExecuted = new ArrayList<File>();
        
        for (File dir : executionDirectories) {
            getLog().info(
                "[MavenRec] Execute command \"mvn " + command + "\" in <"
                    + dir.getAbsolutePath() + ">.");
            try {
                Commandline cl = new Commandline();
                cl.setWorkingDirectory(dir);
                cl.setExecutable("mvn");
                cl.createArg().setLine(command);

                StringStreamConsumer systemOut = new CommandLineUtils.StringStreamConsumer() {
                    @Override
                    public void consumeLine(String line) {
                        System.out.println(line);
                    }
                };

                CommandLineUtils.executeCommandLine(cl, systemOut, systemOut);

                successfulExecuted.add(dir);
            } catch (CommandLineException e) {
                getLog().warn(
                    "Command could not be executed in " + dir.getAbsolutePath()
                        + "!\n" + e.getMessage());
            }
        }
        
        return successfulExecuted;
    }

    /**
     * Returns the directory of the topmost pom of the specified project within
     * the same directory structure. Moves up the path where the pom of the
     * specified project is located until either:
     * <ul>
     * <li>the parent pom is reached or</li>
     * <li>the parent pom is not within the same directory tree as the current
     * project</li>
     * </ul>
     * The parent pom has to be in the parent directory of the current pom.
     * 
     * @param project
     *            MavenProject to get the root directory for.
     * @return File describing the root directory.
     */
    public File getRootDir(MavenProject project) {

        if (project.getParent() == null) {
            return project.getFile().getParentFile();
        }

        File filesystemParentPath = project.getFile().getParentFile()
            .getParentFile();
        File modelParentPath = project.getParent().getFile().getParentFile();

        if (filesystemParentPath.equals(modelParentPath)) {
            return getRootDir(project.getParent());
        } else {
            return filesystemParentPath;
        }
    }

    /**
     * Searches recursively in the specified directory and in all subdirectories
     * for pom-files which define local jar/war-projects. Creates a
     * ProjectData-object and puts them into the specified map. If this
     * directory does not contain a pom-file, subdirectories are ignored.
     * 
     * @param dir
     *            the directory where to start the recursive search.
     * @param localProjectMap
     *            the map where to put the created ProjectData-objects into.
     */
    private void searchLocalProjects(File dir,
        Map<String, ProjectData> localProjectMap) {
        String pomFileName = "pom.xml";

        // process current dir
        File pom = new File(dir, pomFileName);
        ProjectData projectData = null;
        
        // ignore dir and its sub-dirs if no pom exists
        if (!pom.isFile()) {
            return;
        }
        
        try {
            projectData = parsePom(pom);
        } catch (Exception e) {
            // skip current pom if an exception occurs and print warn
            // message
            getLog().warn(e);
            projectData = null;
        }

        if (projectData != null
            && isAllowedPackaging(projectData.getPackaging())) {
            ProjectData before = localProjectMap.put(
                projectData.getProjectId(), projectData);
            if (before != null) {
                // log warn message if there was already a project with same id
                getLog().warn(
                    "Project <" + projectData.getProjectId()
                        + "> is defined twice!");
            }
        }

        // recursive call for each subdir
        File[] subDirs = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file != null && file.isDirectory());
            }
        });

        for (File subDir : subDirs) {
            searchLocalProjects(subDir, localProjectMap);
        }
    }

    /**
     * Gets a ProjectData-object from the specified pom using
     * MavenProjectBuilder.
     * 
     * @param pom
     *            the pom to parse and retrieve ProjectData from.
     * @return ProjectData retrieved from the specified pom.
     * @throws Exception
     *             if pom can not be parsed.
     */
    @SuppressWarnings("unchecked")
    private ProjectData parsePom(File pom) throws Exception {
        MavenProject project;
        ProjectData projectData = null;

        /*
         * Use reflection to retrieve the projectBuilder from
         * artifactMetadataSource
         */
        MavenProjectBuilder projectBuilder = null;
        Class<? extends ArtifactMetadataSource> metadata = artifactMetadataSource
            .getClass();
        Field field = metadata.getDeclaredField("mavenProjectBuilder");
        field.setAccessible(true);
        projectBuilder = (MavenProjectBuilder) field
            .get(artifactMetadataSource);

        /*
         * Create default ProfileManager
         */
        DefaultPlexusContainer plexusContainer = new DefaultPlexusContainer();
        plexusContainer.initialize();
        DefaultProfileManager profileManager = new DefaultProfileManager(
            plexusContainer, new Properties());

        // Build MavenProject from pom.xml
        project = projectBuilder.build(pom, localRepository, profileManager);

        /*
         * Retrieve project-data
         */
        projectData = new ProjectData();
        projectData.setPom(pom);
        projectData.setArtifactId(project.getArtifactId());
        projectData.setVersion(project.getVersion());
        projectData.setPackaging(project.getPackaging());
        // if no groupId is set, get it from parent artifact
        if (project.getGroupId() != null) {
            projectData.setGroupId(project.getGroupId());
        } else {
            if (project.getParent() != null
                && project.getParent().getGroupId() != null) {
                projectData.setGroupId(project.getParent().getGroupId());
            }
        }
        List<Dependency> dependencies = project.getDependencies();
        for (Dependency dependency : dependencies) {
            // ignore scope "system"
            if (!"system".equalsIgnoreCase(dependency.getScope())) {
                projectData.addDependency(dependency.getGroupId(), dependency
                    .getArtifactId(), dependency.getVersion());
            }
        }

        return projectData;
    }

    /**
     * Remove dependencies of the ProjectData-objects in the specified map,
     * which do not reference to a local project. Only dependencies are kept
     * which reference to a project, that is contained by the specified map.
     * 
     * @param localProjectMap
     *            the map to get the ProjectData-objects from.
     */
    private void removeDependenciesToNonLocalProjects(
        Map<String, ProjectData> localProjectMap) {
        for (String projectId : localProjectMap.keySet()) {
            ProjectData projectData = localProjectMap.get(projectId);

            List<String> newDependencyList = new ArrayList<String>();

            for (String dependencyId : projectData.getDependencies()) {
                if (localProjectMap.containsKey(dependencyId)) {
                    /*
                     * add only dependencies to projects which are located in
                     * the local filesystem (i.e. are contained in the map)
                     */
                    newDependencyList.add(dependencyId);
                }
            }
            projectData.setDependencies(newDependencyList);
        }
    }
}
