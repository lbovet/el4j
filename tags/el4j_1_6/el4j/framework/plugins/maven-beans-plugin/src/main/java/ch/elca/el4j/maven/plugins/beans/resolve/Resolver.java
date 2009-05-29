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

/**
 * Interface for resolving bean path entries against their type (file, jar ...).
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
public interface Resolver {

	/**
	 * @return The protocol this resolver works on. "" for the manager.
	 */
	String getProtocol();
	
	/**
	 * Check if a file is to be included in the resolved bean files.
	 * @param file The file name/path.
	 * @return Whether this file is included.
	 */
	boolean accept(String file);
	
	/**
	 * Copy a file to its proper place in the target directory.
	 * @param file The file to copy.
	 * @param target The target directory. It must exist.
	 * @throws IOException If soemthing goes wrong with file operations.
	 */
	void copy(String file, File target) throws IOException;
}
