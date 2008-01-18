package ch.elca.el4j.maven.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Plugin to execute maven commands recursive on dependent projects.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author chd
 * @goal execute
 */
public class MavenRecursiveMojo extends AbstractMojo {

    /**
     * Name of bootstrap-xml-file.
     */
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
     * Set to true to create a bootstrap-file.
     * 
     * @parameter expression="${mvn.rec.bootstrap}" default-value="false"
     */
    private boolean bootstrap;
    
    /**
     * Set the behaviour in case of failure. Set to true to get fail-fast
     * behaviour, otherwise fail-at-end behaviour is activated (default).
     * 
     * @parameter expression="${mvn.rec.ff}" default-value="false"
     */
    private boolean failFast;

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
            getLog().error("[MavenRec] No commands defined!");
            System.exit(1);
        }

        if (!isAllowedPackaging(m_project.getPackaging())) {
            getLog().error("[MavenRec] Wrong packaging type: " + m_project.getPackaging());
            System.exit(2);
        }
        
        /*
         * Print out flags
         */
        printSettings();

        /*
         * get projectId-to-ProjectData-mapping
         */
        Map<String, ProjectData> projectIdToDataMap = getBootstrapMap();

        /*
         * Create sorted list of directories where the shell-command should be
         * executed for the given project
         */
        List<ProjectData> executionProjectList = createExecutionProjectList(projectIdToDataMap);

        /*
         * Print out a list of projects to execute
         */
        printExecutionPlan(executionProjectList);
        
        /*
         * Execute the provided shell-command within the directories of all
         * ProjectDatas in the list
         */
        executeCommandForProjects(executionProjectList);
        
        /*
         * Print out a summary list
         */
        printSummary(executionProjectList);
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

    /**
     * Returns a mapping of projectId-Strings to their ProjectData-objects.
     * The map is either loaded from an existing bootstrap.xml-file or created
     * by scanning the directory structure. 
     * 
     * @return a mapping of projectId-Strings to ProjectData-objects
     */
    private Map<String, ProjectData> getBootstrapMap() {
        File rootDir = getRootDir(m_project);
        File targetDir = new File(rootDir, "target");
        Map<String, ProjectData> bootstrapMap = null;

        if (!bootstrap) {
            // load already existing bootstrap-map from file
            try {
                bootstrapMap = loadBootstrapMap(targetDir);
            } catch (Exception e) {
                bootstrapMap = null;
                getLog().debug(
                    "[MavenRec] Failed loading bootstrap-file: "
                        + e.getMessage());
            }
        }

        if (bootstrapMap == null) {
            try {
                // Create a bootstrap map
                bootstrapMap = createBootstrapMap(rootDir);
                // store bootstrap map to file
                storeBootstrapMap(bootstrapMap, targetDir);
            } catch (IOException e) {
                getLog().warn(e);
            }
        }

        // DEBUG: write map to log
        for (String projectId : bootstrapMap.keySet()) {
            ProjectData projectData = bootstrapMap.get(projectId);
            getLog().debug("Project found: ".concat(projectData.getProjectId()));
            for (String dependencyId : projectData.getDependencies()) {
                getLog().debug(" - " + dependencyId);
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
            .debug(
                "[MavenRec] Create new boostrap-map from rootdir "
                    + rootDir.getAbsolutePath());
        /*
         * Retrieve set of ProjectData-objects of local projects (with packaging
         * "jar" or "war")
         */
        getLog().info("[MavenRec] Scanning for projects...");
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
        // create target-dir if it doesn't exist
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File bootstrapXml = new File(dir, BOOTSTRAP_FILENAME);
        getLog().debug(
            "[MavenRec] Store bootstrap-map into "
                + bootstrapXml.getAbsolutePath());

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

        getLog().debug(
            "[MavenRec] Load bootstrap-map from "
                + bootstrapXml.getAbsolutePath());

        Document bootstrapDom = BootstrapFileHandler
            .loadDomFromXml(bootstrapXml);
        Map<String, ProjectData> bootstrapMap = BootstrapFileHandler
            .createBootstrapMapFromDom(bootstrapDom);

        return bootstrapMap;
    }

    /**
     * Creates a sorted list of ProjectData-objects for which the shell-command
     * shall be executed. The position of the ProjectDatas in the list
     * corresponds to the order in which they must be processed.
     * 
     * @param projectIdToDataMap
     *            mapping of projectIds to ProjectData-objects.
     * @return list of ProjectDatas for which to execute the shell-command.
     */
    private List<ProjectData> createExecutionProjectList(
        Map<String, ProjectData> projectIdToDataMap) {
        List<ProjectData> projectList = new ArrayList<ProjectData>();

        String startProjectId = ProjectData.createProjectIdString(m_project
            .getGroupId(), m_project.getArtifactId(), m_project.getVersion());

        postOrderTraverseDependencyGraph(startProjectId, projectIdToDataMap,
            projectList);

        return projectList;
    }

    /**
     * Traverses the dependent project of the specified project in postorder.
     * Adds the ProjectData of the visited projects to the ProjectData-list.
     * 
     * @param projectId
     *            the project whose dependencies are to traverse.
     * @param bootstrapMap
     *            the map containing the projectId to ProjectData-mapping.
     * @param projectList
     *            list where to add the ProjectDatas of the visited projects.
     */
    private void postOrderTraverseDependencyGraph(String projectId,
        Map<String, ProjectData> bootstrapMap, List<ProjectData> projectList) {

        ProjectData projectData = bootstrapMap.get(projectId);
        // traverse all dependent projects
        for (String dependentProjectId : projectData.getDependencies()) {
            postOrderTraverseDependencyGraph(dependentProjectId, bootstrapMap,
                projectList);
        }
        /*
         * add the ProjectData of current project after the ProjectDatas of all
         * dependent projects. Before a project can be processed, all its
         * dependent projects have to be already processed at that moment.
         */
        // avoid listing a ProjectData more than once
        if (!projectList.contains(projectData)) {
            projectList.add(projectData);
        }
    }

    /**
     * Execute the provided shell-command within the directories of the Projects
     * in the list. Sets execution result (isSuccessful) and duration of
     * execution in each project.
     * 
     * @param executionProjectList
     *            list of Projects for which to execute the shell-command.
     */
    private void executeCommandForProjects(List<ProjectData> executionProjectList) {
        
        for (ProjectData project : executionProjectList) {
            File dir = project.getDirectory();
            getLog()
                .debug(
                    "[MavenRec] Working Directory: \"" + dir.getAbsolutePath()
                        + "\"");
            
            // timer variables
            long start = 0;
            long end = 0;
            
            try {
                Commandline cl = new Commandline();
                cl.setWorkingDirectory(dir);
                cl.setExecutable("mvn");
                cl.createArg().setLine(command);

                BufferedLogConsumer blg = new BufferedLogConsumer(
                    getLog());
                int result = -1;

                getLog().info(
                    "[MavenRec] Executing \"" + cl.toString() + "\" in \""
                        + project.getName() + "\"...");
                // start timer
                start = System.currentTimeMillis();
                // execute command
                result = CommandLineUtils.executeCommandLine(cl, blg, blg);

                String output = blg.toString();
                if (result != 0 || !output.contains("BUILD SUCCESSFUL")) {
                    throw new CommandLineException("BUILD ERROR");
                }
                
                project.setExecutionState(ProjectData.SUCCESS);
                
            } catch (CommandLineException e) {
                getLog().warn(
                    "[MavenRec] Failed to execute command in \""
                        + project.getName() + "\": " + e.getMessage());
                project.setExecutionState(ProjectData.FAILED);
                
                // end execution if fail-fast behaviour is activated
                if (failFast) {
                    return;
                }
            }
            finally {
                // stop timer
                end = System.currentTimeMillis();
                project.setDuration(end - start);
            }
        }
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
                    "Project \"" + projectData.getProjectId()
                        + "\" is defined twice!");
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
        projectData.setName(project.getName());
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

    /**
     * Print flag settings to debug-log.
     */
    private void printSettings() {
        getLog().debug("Force project scanning: " + (bootstrap ? "ON" : "OFF"));
        getLog().debug(
            "Termination behaviour: "
                + (failFast ? "fail-fast" : "fail-at-end"));
        getLog().debug("Maven command(s): " + command);
    }

    /**
     * Print an overview of the projects for which the command will be executed.
     * 
     * @param executionProjectList
     *            list of projects for which the command will be executed.
     */
    private void printExecutionPlan(List<ProjectData> executionProjectList) {
        // Checkstyle: MagicNumber off
        getLog().info(StringUtils.repeat("-", 72));
        getLog().info("MavenRec Execution Order:");
        getLog().info(StringUtils.repeat("-", 72));

        for (ProjectData projectData : executionProjectList) {
            getLog().info("  " + projectData.getName());
        }
        
        getLog().info(StringUtils.repeat("-", 72));
        // Checkstyle: MagicNumber on
    }

    /**
     * Print a summary of the projects for which the command has been executed.
     * Prints execution status and execution duration.
     * 
     * @param executionProjectList
     *            list of projects for which the command has been executed.
     */
    private void printSummary(List<ProjectData> executionProjectList) {
        // Checkstyle: MagicNumber off
        getLog().info(StringUtils.repeat("-", 72));
        getLog().info("MavenRec Execution Summary:");
        getLog().info(StringUtils.repeat("-", 72));

        for (ProjectData projectData : executionProjectList) {
            StringBuffer line = new StringBuffer(StringUtils.rightPad(
                projectData.getName().concat(" "), 55, "."));
            line.append(" ");
            Double duration = (Double.parseDouble(new Long(projectData
                .getDuration()).toString()) / 1000);
            line.append(projectData.getStateDescription());
            line.append(" [").append(String.format("%.3f", duration)).append(
                "s]");
            getLog().info(line.toString());
        }
        
        getLog().info(StringUtils.repeat("-", 72));
        // Checkstyle: MagicNumber on
    }
}
