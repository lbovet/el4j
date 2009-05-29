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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

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
	 * The logger.
	 */
	private static final Log s_logger = LogFactory.getLog(
		OrderedPathMatchingResourcePatternResolver.class);
	
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
	
	/** {@inheritDoc} */
	@Override
	protected void doRetrieveMatchingFiles(String fullPattern, File dir,
		Set result) throws IOException {
		
		if (s_logger.isDebugEnabled()) {
			s_logger.debug("Searching directory [" + dir.getAbsolutePath() +
					"] for files matching pattern [" + fullPattern + "]");
		}
		File[] dirContents = dir.listFiles();
		
		// this is new {
		Arrays.sort(dirContents, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return f1.getName().compareTo(f2.getName());
			}
		});
		// } this is new
		
		if (dirContents == null) {
			throw new IOException("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
		}
		for (int i = 0; i < dirContents.length; i++) {
			File content = dirContents[i];
			String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
			if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
				doRetrieveMatchingFiles(fullPattern, content, result);
			}
			if (getPathMatcher().match(fullPattern, currPath)) {
				result.add(content);
			}
		}
	}

}
