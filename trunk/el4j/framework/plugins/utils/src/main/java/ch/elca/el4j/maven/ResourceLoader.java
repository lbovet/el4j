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
package ch.elca.el4j.maven;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;
import ch.elca.el4j.core.io.support.OrderedPathMatchingResourcePatternResolver;

/**
 *
 * This class contains the enriched classloader as well as the Path matcher needed for resource loading.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Stefan (DST)
 * @author Stefan Wismer (SWI)
 */
public class ResourceLoader {

	/**
	 * Logger.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(ResourceLoader.class);
	
	/**
	 * The classloader.
	 */
	private URLClassLoader m_classLoader;

	/**
	 * Path matcher to find resources.
	 */
	private ListResourcePatternResolverDecorator m_resolver;
	
	/**
	 * Path matcher to find resources.
	 */
	private ListResourcePatternResolverDecorator m_projectResolver;
	
	/**
	 * Path matcher to find resources.
	 */
	private ListResourcePatternResolverDecorator m_dependenciesResolver;

	/**
	 * Is most specific resource last?
	 */
	private final boolean m_mostSpecificResourceLast;
	
	/**
	 * Create a resource loader.
	 * @param repository Maven repository for artifacts
	 * @param project Maven project we're working on
	 * @param walker The Dependency GraphWalker
	 * @param mostSpecificResourceLast    Indicates whether the most specific resource should be the last resource
	 *                                    in the fetched resource array.
	 * @param includeTestResources        Whether test resources should be include
	 */
	public ResourceLoader(ArtifactRepository repository,
		MavenProject project, DepGraphWalker walker, boolean mostSpecificResourceLast, boolean includeTestResources) {
		
		m_mostSpecificResourceLast = mostSpecificResourceLast;
		
		List<URL> projectUrls = getProjectUrls(repository, project, includeTestResources);
		List<URL> dependenciesUrls = walker.getDependencyURLs(includeTestResources ? "test" : "runtime");
		// make most specific first (as it has to be in classpaths)
		Collections.reverse(dependenciesUrls);
		List<URL> urls = new ArrayList<URL>();

		urls.addAll(projectUrls);
		urls.addAll(dependenciesUrls);
		
		m_projectResolver = createResolver(projectUrls);
		m_dependenciesResolver = createResolver(dependenciesUrls);
		m_resolver = createResolver(urls);
		
		m_classLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]));
	}
	
	/**
	 * @return    <code>true</code> if most specific resource is last
	 */
	public boolean isMostSpecificResourceLast() {
		return m_mostSpecificResourceLast;
	}

	
	/**
	 * Get resources in the project and its dependencies.
	 * @param path Path of the resources to get
	 * @return Array of resources
	 */
	public Resource[] getResources(String path) throws IOException {
		return m_resolver.getResources(path);
	}
	
	/**
	 * @return    the resource loader
	 */
	public ListResourcePatternResolverDecorator getResolver() {
		return m_resolver;
	}
	
	/**
	 * Get resources in the project but not in its dependencies.
	 * @param path Path of the resources to get
	 * @return Array of resources
	 */
	public Resource[] getProjectResources(String path) throws IOException {
		return m_projectResolver.getResources(path);
	}
	
	/**
	 * Get resources in the project's dependencies but not the project itself.
	 * @param path Path of the resources to get
	 * @return Array of resources
	 */
	public Resource[] getDependenciesResources(String path) throws IOException {
		return m_dependenciesResolver.getResources(path);
	}
	
	/**
	 * @return    the classloader for the project and its dependencies
	 */
	public URLClassLoader getClassLoader() {
		return m_classLoader;
	}
	
	/**
	 * Collects and returns list of project resource urls.
	 *
	 * @param repo The artifact repository.
	 * @param project The projects we're working on.
	 * @param includeTestResources Whether test resources should be include
	 * @return List of project's jar URLs
	 */
	private ArrayList<URL> getProjectUrls(ArtifactRepository repo,
			MavenProject project, boolean includeTestResources) {
		ArrayList<URL> urls = new ArrayList<URL>();

		try {
			if (includeTestResources) {
				urls.add(new URL("file", "", "/" + project.getBuild().getTestOutputDirectory() + "/"));
			}
			urls.add(new URL("file", "", "/" + project.getBuild().getOutputDirectory() + "/"));

			
		} catch (MalformedURLException e) {
			s_logger.error("Malformed resource URL: " + e);
		}

		return urls;
	}
	
	/**
	 * @param urls    the URLs that the resolver has to search through
	 * @return        a PathResolver
	 */
	private ListResourcePatternResolverDecorator createResolver(List<URL> urls) {
		
		// Set thread's classloader as parent classloader
		URLClassLoader classloader = URLClassLoader.newInstance(
			urls.toArray(new URL[0]));
		
		ListResourcePatternResolverDecorator resolver = new ListResourcePatternResolverDecorator(
			new ManifestOrderedConfigLocationProvider(),
			new OrderedPathMatchingResourcePatternResolver(classloader));
		resolver.setMostSpecificResourceLast(m_mostSpecificResourceLast);
		resolver.setMergeWithOuterResources(true);
		
		return resolver;
	}
}
