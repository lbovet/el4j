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
package ch.elca.el4j.plugins.database;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * This class is ...
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public abstract class AbstractDatabaseHolder {

    /**
     * Logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractDatabaseHolder.class);
    
    /**
     * Classloader with all project dependend classes.
     */
    private URLClassLoader m_classloader;

    /**
     * Path matcher to find sql files.
     */
    private PathMatchingResourcePatternResolver m_resolver;
    
    /**
     * URLs of dependency artifacts.
     */
    private List<URL> m_dependencyURLs;
    
    /**
     * URLs of project artifacts.
     */
    private List<URL> m_projectURLs;
    
    /**
     * Constructor.
     * @param repository Maven repository for artifacts
     * @param project Maven project we're working on
     */
    protected AbstractDatabaseHolder(ArtifactRepository repository, 
        MavenProject project) {
        
        m_dependencyURLs = getDependencyURLs(repository, project);
        m_projectURLs = getProjectUrls(repository, project);
        createEnrichedClassloader(m_dependencyURLs, m_projectURLs);
    }
    
    /**
     * @return The PathMatcher
     */
    public PathMatchingResourcePatternResolver getResolver() {
        return m_resolver;
    } 
    
    /**
     * @return The URLs of project artifacts
     */
    protected List<URL> getProjectURLs() {
        return m_projectURLs;
    }
    
    /**
     * @return The classloader
     */
    protected URLClassLoader getClassloader() {
        return m_classloader;
    }
    
    /**
     * Collects and returns list of project urls. This includes the normal jar
     * as well as the {project-name}-tests.jar file in case we work on a test
     * project.
     * 
     * @param repo The artifact repository.
     * @param project The projects we're working on.
     * @return List of project's jar URLs
     * @throws MalformedURLException
     */
    private ArrayList<URL> getProjectUrls(ArtifactRepository repo, 
            MavenProject project) {
        String path;
        ArrayList<URL> urls = new ArrayList<URL>();      
        try {
            // Construct URL for /target directory of project (where we will 
            // find the jar files taken for the test phase).
            path = "/" + project.getBasedir().getAbsolutePath() + "/"
                + "target";
            URL url = new URL("file", "", path + "/");

            // Create own classloader and path matcher for /target directory
            URLClassLoader projectClasspath = URLClassLoader
                .newInstance(new URL[] {url}, Thread.currentThread()
                    .getContextClassLoader());
            PathMatchingResourcePatternResolver projectResolver 
                = new PathMatchingResourcePatternResolver(projectClasspath);

            // Look for .jar files in target directory and add them to
            // projectUrls
            Resource[] res = projectResolver.getResources("classpath*:*.jar");

            for (Resource r : res) {
                // Relies on the naming convention that the jar files in the
                // target directories are *-sources.jar, *-test-sources.jar,
                // *-tests.jar and *.jar (where we only need the latter two)
                if (r.getFilename().endsWith("-sources.jar")) {
                    s_logger
                        .info("Adding resource to classpath: " + r.getURL());
                    urls.add(r.getURL());
                }
            }
        } catch (IOException e) {
            throw new DatabaseHolderException(e);
        }
        return urls;
    }
    
    /**
     * Collects and returns list of urls of all dependencies of this project. 
     * 
     * @param repo The artifact repository.
     * @param project The projects we're working on.
     * @return List of dependencies jar URLs
     * @throws MalformedURLException
     */
    private List<URL> getDependencyURLs(ArtifactRepository repo, 
            MavenProject project) {
        List<URL> urls = new ArrayList<URL>();
        // Iterate through test dependencies, because it contains more resources
        for (Object obj : project.getTestArtifacts()) {
            Artifact artifact = (Artifact) obj;
            URL url = constructURL(repo.getBasedir(), repo.pathOf(artifact));
            s_logger.info("Adding resource to classpath: " + url);
            urls.add(url);
        }
        return urls;
    }
    
    /**
     * Add all project dependencies as well as project specific resources
     * to actual classpath and generate PathResolver.
     * 
     * @param urls Urls from dependencies to include into classpath.
     * @param projectURLs URLs from project to include.
     * @throws IOException
     */
    private void createEnrichedClassloader(List<URL> urls, 
        List<URL> projectURLs) {
        urls.addAll(projectURLs);
        // Set thread's classloader as parent classloader
        m_classloader = URLClassLoader.newInstance(urls.toArray(new URL[1]),
            Thread.currentThread().getContextClassLoader());
        m_resolver = new PathMatchingResourcePatternResolver(m_classloader);
    }
    
    /**
     * Constructs a URL from the given Base directory and the file path.
     * 
     * @param baseDir Base directory of maven repository.
     * @param filePath Path of file we want URL for
     * @return URL of this file
     * @throws MalformedURLException
     */
    private URL constructURL(String baseDir, String filePath) {
        String path = baseDir + "/" + filePath;
        try {
            File file = new File(path);
            // check if file exist to avoid Exception
            if (file.exists()) {
                return file.toURL();
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            throw new DatabaseHolderException(e);
        }
    }   
}
