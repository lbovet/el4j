/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

/**
 * This class provides basic functionality to handle module dependency related operations.
 * 
 * It should be simply copied from maven-util, because java-doc annotations are not recognized if this
 * file is located in a separate jar (namely maven-util). See also http://jira.codehaus.org/browse/MNG-3042
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @deprecated do not use this class directly. Copy it to your maven plagin (see comment above).
 *
 * @author Stefan Wismer (SWI)
 */
@Deprecated
public abstract class AbstractDependencyAwareMojo extends AbstractMojo {
	// Checkstyle: MemberName off
	/**
	 * The maven project from where this plugin is called.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * Local maven repository.
	 *
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;
	
	/**
	 * @component
	 */
	private ArtifactMetadataSource artifactMetadataSource;
	
	/**
	 * @component
	 */
	private ArtifactCollector collector;
	
	/**
	 * @component
	 */
	private ArtifactFactory artifactFactory;
	
	/**
	 * @component
	 */
	private ArtifactResolver artifactResolver;
	
	/**
	 * The dependency tree builder to use.
	 *
	 * @component
	 * @required
	 * @readonly
	 */
	private DependencyTreeBuilder m_dependencyTreeBuilder;
	// Checkstyle: MemberName on
	
	/**
	 * The Dependency Graph Walker.
	 */
	private DepGraphWalker m_graphWalker;
	
	/**
	 * The resource loader.
	 */
	private ResourceLoader m_resourceLoader;
	
	/**
	 * @return The Maven artifact repository
	 */
	protected ArtifactRepository getRepository() {
		return localRepository;
	}

	/**
	 * @return The maven project
	 */
	protected MavenProject getProject() {
		return project;
	}
	
	/**
	 * Builds a tree of dependencies for the current Maven project.
	 * 
	 * @param mavenProject    the maven project to build the dependency tree for
	 * @param filter          the artifact filter to use
	 * @return                the dependency tree root node of the specified Maven project
	 * @throws                DependencyTreeBuilderException
	 *                            if the dependency tree cannot be resolved
	 */
	protected DependencyNode buildDependencyTree(MavenProject mavenProject,
		ArtifactFilter filter) throws DependencyTreeBuilderException {
		return m_dependencyTreeBuilder.buildDependencyTree(mavenProject,
			
			localRepository, artifactFactory, artifactMetadataSource,
			filter, collector);
	}
	
	/**
	 * Get the resource loader.
	 * 
	 * @param mostSpecificResourceLast    Indicates whether the most specific resource should be the last resource
	 *                                    in the fetched resource array.
	 * @param includeTestResources        Whether test resources should be include
	 * @return    the resource loader.
	 */
	protected ResourceLoader getResourceLoader(boolean mostSpecificResourceLast, boolean includeTestResources) {
		return getResourceLoader(mostSpecificResourceLast, true, includeTestResources);
	}
	
	/**
	 * Get the resource loader (module aware version).
	 * 
	 * @param mostSpecificModuleLast           Indicates whether the most specific module should be the last resource
	 *                                         in the fetched resource array.
	 * @param orderModuleResourcesAscending    <code>true</code> if resources inside modules must be sorted ascending
	 * @param includeTestResources             Whether test resources should be include
	 * @return    the resource loader.
	 */
	protected ResourceLoader getResourceLoader(boolean mostSpecificModuleLast, boolean orderModuleResourcesAscending,
		boolean includeTestResources) {
		if (m_resourceLoader == null || m_resourceLoader.isMostSpecificModuleLast() != mostSpecificModuleLast
			|| m_resourceLoader.isOrderModuleResourcesAscending() != orderModuleResourcesAscending) {
			m_resourceLoader = new ResourceLoader(
				localRepository, project, getGraphWalker(), mostSpecificModuleLast, orderModuleResourcesAscending,
				includeTestResources);
		}
		return m_resourceLoader;
	}
	
	/**
	 * @return The Dependency GraphWalker
	 */
	protected DepGraphWalker getGraphWalker() {
		if (m_graphWalker == null) {
			m_graphWalker = new DepGraphWalker(localRepository, project,
				artifactResolver, collector, artifactMetadataSource, artifactFactory);
		}
		return m_graphWalker;
	}
}
