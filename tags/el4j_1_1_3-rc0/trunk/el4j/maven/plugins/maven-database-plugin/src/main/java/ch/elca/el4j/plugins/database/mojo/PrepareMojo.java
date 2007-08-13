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
import ch.elca.el4j.plugins.database.util.derby.DerbyNetworkServerStarter;


/**
 * This class is a convenience mojo that includes the 'start', 
 * 'silentDrop' and 'create' mojo.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/maven/plugins/maven-database-plugin/src/main/java/ch/elca/el4j/plugins/database/mojo/PrepareDatabaseMojo.java $",
 *    "$Revision: 1896 $",
 *    "$Date: 2007-08-03 10:39:34 +0200 (Fr, 03 Aug 2007) $",
 *    "$Author: swismer $"
 * );</script>
 * 
 * @goal prepare
 * @author David Stefan (DST)
 */
public class PrepareMojo extends AbstractDBExecutionMojo {

    /**
     * Delay to wait for Derby Network Server.
     */
    private static final int DELAY = 500;
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // Start Derby Network Server if necessary, but do not wait, because
            // we know that execution will continue
            if (needStartup()) {
                getLog().info("Starting database...");
                DerbyNetworkServerStarter.setHomeDir(getDerbyLocation());
                DerbyNetworkServerStarter.startNetworkServer();
            }
            Thread.sleep(DELAY);
            getLog().info("Executing silent drop");
            // Execute a silent drop
            try {
                executeAction("drop", true, true);
                // Checkstyle: EmptyBlock off
            } catch (Exception e) {
                // Skip Exception
            }
            // Checkstyle: EmptyBlock on
            getLog().info("Executing create");
            // Create tables
            executeAction("create", false, false);
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }
}