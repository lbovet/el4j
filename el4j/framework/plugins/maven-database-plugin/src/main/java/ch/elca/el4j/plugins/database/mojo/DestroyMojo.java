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

import ch.elca.el4j.plugins.database.AbstractDBExecutionMojo;


/**
 * This class is a database mojo for the 'destroy' statement. 
 * It deletes all database tables.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @goal destroy
 * @author Stefan Wismer (SWI)
 */
public class DestroyMojo extends AbstractDBExecutionMojo {

    /**
     * Action this mojo is implementing and identifier sql files have to start
     * with.
     */
    private static final String ACTION = "destroy";
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            executeAction(ACTION, true, false);
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

}
