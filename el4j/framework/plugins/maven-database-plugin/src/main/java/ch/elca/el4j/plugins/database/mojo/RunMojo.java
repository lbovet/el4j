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
 * This class is a database mojo for the generic 'run' statement which executes
 * all sql files matching the given prefix. 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @goal run
 * @author Stefan Wismer (SWI)
 */
public class RunMojo extends AbstractDBExecutionMojo {
    /**
     * File prefix of all sql files that should be executed.
     * 
     * @parameter expression="${filePrefix}"
     */
    protected String filePrefix;
    
    /**
     * Order in which sql files should be executed. <code>true</code> means 
     * top-down, <code>false</code> buttom-up.
     * 
     * @parameter expression="${reverse}"
     *            default-value="false"
     */
    protected Boolean reverse;
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            executeAction(filePrefix, reverse, false);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
