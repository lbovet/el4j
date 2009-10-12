/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.io.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * This class extends PathMatchingResourcePatternResolver in the way that
 * it sorts the files in a folder alphabetically.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class OrderedPathMatchingResourcePatternResolver extends
	PathMatchingResourcePatternResolver {
	
	/**
	 * Is order ascending?
	 */
	protected boolean m_ascending = true;
	
	/**
	 * The resource comparator.
	 */
	protected final Comparator<Resource> m_resourceComparator = new Comparator<Resource>() {
		public int compare(Resource f1, Resource f2) {
			if (m_ascending) {
				return f1.getFilename().compareTo(f2.getFilename());
			} else {
				return -f1.getFilename().compareTo(f2.getFilename());
			}
		}
	};
	
	/**
	 * Create a PathMatchingResourcePatternResolver using a DefaultResourceLoader
	 * that sorts the files in a folder alphabetically.
	 * <p>ClassLoader access will happen via the thread context class loader.
	 * @see org.springframework.core.io.DefaultResourceLoader
	 */
	public OrderedPathMatchingResourcePatternResolver() {
		super();
	}
	
	/**
	 * Create a new PathMatchingResourcePatternResolver with a DefaultResourceLoader
	 * that sorts the files in a folder alphabetically.
	 * @param classLoader the ClassLoader to load classpath resources with,
	 * or <code>null</code> for using the thread context class loader
	 * @see org.springframework.core.io.DefaultResourceLoader
	 */
	public OrderedPathMatchingResourcePatternResolver(ClassLoader classLoader) {
		super(classLoader);
	}
	
	/**
	 * Create a new PathMatchingResourcePatternResolver that sorts the files in a folder alphabetically.
	 * <p>ClassLoader access will happen via the thread context class loader.
	 * @param resourceLoader the ResourceLoader to load root directories and
	 * actual resources with
	 */
	public OrderedPathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}
	
	/**
	 * @return    whether order is ascending or not
	 */
	public boolean isAscending() {
		return m_ascending;
	}

	/**
	 * @param ascending    is order ascending?
	 */
	public void setAscending(boolean ascending) {
		m_ascending = ascending;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected Set doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
		Set<FileSystemResource> matchingResources = super.doFindPathMatchingFileResources(rootDirResource, subPattern);
		List<FileSystemResource> list = new ArrayList<FileSystemResource>(matchingResources);
		Collections.sort(list, m_resourceComparator);
		return new LinkedHashSet<Resource>(list);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected Set doFindPathMatchingJarResources(Resource rootDirResource, String subPattern) throws IOException {
		Set<Resource> matchingJarResources = super.doFindPathMatchingJarResources(rootDirResource, subPattern);
		List<Resource> list = new ArrayList<Resource>(matchingJarResources);
		Collections.sort(list, m_resourceComparator);
		return new LinkedHashSet<Resource>(list);
	}
}
