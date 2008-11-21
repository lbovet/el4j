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
package ch.elca.el4j.plugins.coberturaruntime;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Goal to clean the cobertura data directory.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * 
 * @goal clean
 * @requiresProject true
 */
public class CoberturaClean extends AbstractCoberturaMojo {
	/**
	 * Executes the plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (coberturaDataDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(coberturaDataDirectory);
			} catch (IOException e) {
				new MojoExecutionException("The given cobertura data directory '" + coberturaDataDirectory.getName()
					+ "' could not be removed!", e);
			}
		}
	}
}
