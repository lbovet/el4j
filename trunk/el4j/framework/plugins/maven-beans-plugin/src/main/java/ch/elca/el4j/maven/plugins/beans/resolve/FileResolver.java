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
package ch.elca.el4j.maven.plugins.beans.resolve;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Resolver for plain file: entries.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class FileResolver extends AbstractResolver {

	/**
	 * Set up a file resolver.
	 * @param classpath The classpath to check valid files against.
	 */
	public FileResolver(URL[] classpath) {
		super(classpath);
	}

	/** {@inheritDoc} */
	public String getProtocol() {
		return "file";
	}

	/** {@inheritDoc} */
	public boolean accept(String file) {
		String f = strip(file);
		for (String entry : m_classpath) {
			if (f.startsWith(entry)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	public void copy(String file, File target) throws IOException {
		File destDir = createDir("files", target);
		File source = new File(strip(file));
		File dest = new File(destDir, source.getName());
		copyFile(source, dest);
	}
}
