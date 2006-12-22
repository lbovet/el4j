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
package ch.elca.el4j.plugins.database.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.plugins.database.AbstractDBMojo;

/**
 * This class is a database mojo for the 'silentDrop' statement. 
 * It executes the same statement as the drop mojo, but ignores exceptions. 
 * It's intended to be used as a "safe" drop before a the create mojo, e.g.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @goal silentDrop
 * @author David Stefan (DST)
 */
public class SilentDropMojo extends AbstractDBMojo {

    /**
     * Action this mojo is implementing and identifier sql files have to start
     * with.
     */
    private static final String ACTION = "drop";
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            executeAction(ACTION);
            // Checkstyle: EmptyBlock off
        } catch (Exception e) {
            // ignore exceptions
        }
        // Checkstyle: EmptyBlock on
    }
}
