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
package ch.elca.el4j.plugins.database.holder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;
import ch.elca.el4j.core.io.support.OrderedPathMatchingResourcePatternResolver;
import ch.elca.el4j.plugins.database.DepGraphWalker;

/**
 *
 * This class is the abstract Base for all DatabaseHolders.
 * It contains the enriched classloader as well as the Path matcher needed
 * in its subclasses.
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
	private ListResourcePatternResolverDecorator m_resolver;
	
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
	 * @param walker The Dependency GraphWalker
	 */
	protected AbstractDatabaseHolder(ArtifactRepository repository,
		MavenProject project, DepGraphWalker walker) {
		
		m_dependencyURLs = walker.getDependencyURLs();
		Collections.reverse(m_dependencyURLs);
		m_projectURLs = getProjectUrls(repository, project);
		createEnrichedClassloader(m_dependencyURLs, m_projectURLs);
	}
	
	/**
	 * Get resources from the classloader.
	 * @param path Path of the resources to get
	 * @return Array of resources
	 */
	public Resource[] getResources(String path) {
		try {
			return m_resolver.getResources(path);
		} catch (IOException e) {
			throw new DatabaseHolderException(e);
		}
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
	 * Collects and returns list of project resource urls.
	 *
	 * @param repo The artifact repository.
	 * @param project The projects we're working on.
	 * @return List of project's jar URLs
	 */
	private ArrayList<URL> getProjectUrls(ArtifactRepository repo,
			MavenProject project) {
		ArrayList<URL> urls = new ArrayList<URL>();

		try {
			urls.add(new URL("file", "", "/"
				+ project.getBuild().getOutputDirectory() + "/"));
			urls.add(new URL("file", "", "/"
				+ project.getBuild().getTestOutputDirectory() + "/"));

			
		} catch (MalformedURLException e) {
			s_logger.error("Malformed resource URL: " + e);
			throw new DatabaseHolderException(e);
		}

		return urls;
	}
	
	/**
	 * Add all project dependencies as well as project specific resources to
	 * actual classpath and generate PathResolver.
	 *
	 * @param urls
	 *            Urls from dependencies to include into classpath.
	 * @param projectURLs
	 *            URLs from project to include.
	 */
	private void createEnrichedClassloader(List<URL> urls,
		List<URL> projectURLs) {
		projectURLs.addAll(urls);
		
		// Set thread's classloader as parent classloader
		m_classloader = URLClassLoader.newInstance(
			projectURLs.toArray(new URL[0]));
		
		m_resolver = new ListResourcePatternResolverDecorator(
			new ManifestOrderedConfigLocationProvider(),
			new OrderedPathMatchingResourcePatternResolver(m_classloader));
		m_resolver.setMostSpecificResourceLast(false);
		m_resolver.setMergeWithOuterResources(true);
		
	}
	
}
