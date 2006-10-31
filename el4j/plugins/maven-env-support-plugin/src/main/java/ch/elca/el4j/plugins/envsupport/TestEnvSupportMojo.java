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
package ch.elca.el4j.plugins.envsupport;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Test environment support plugin. Filters the test resources of given env dir
 * and saves the generate test resources in a special dir. 
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
 * @goal testResources
 * @phase generate-test-resources
 * @requiresProject true
 */
public class TestEnvSupportMojo extends AbstractEnvSupportMojo {
    // Checkstyle: MemberName off
    /**
     * The test output directory into which to copy the env resources.
     * 
     * @parameter expression="${envsupport.testOutputDirectory}" 
     *            default-value="${project.build.directory}/env-test"
     * @required
     */
    private File testOutputDirectory;

    /**
     * The test resource directory where to transfer files.
     * 
     * @parameter expression="${envsupport.testResourceDirectory}" 
     *            default-value="src/test/env"
     * @required
     */
    private File testResourceDirectory;
    
    /**
     * The global test resource directory where to transfer files.
     * 
     * @parameter expression="${envsupport.globalTestResourceDirectory}"
     */
    private File globalTestResourceDirectory;
    
    /**
     * Flag to indicate if the global test resource dir should be used.
     * 
     * @parameter expression="${envsupport.useGlobalTestResourceDirectory}"
     *            default-value="false"
     */
    private boolean useGlobalTestResourceDirectory;
    // Checkstyle: MemberName on
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        if (useGlobalTestResourceDirectory) {
            copyResourcesFiltered(globalTestResourceDirectory, 
                testOutputDirectory, "globalTestResources");
        } else {
            copyResourcesFiltered(testResourceDirectory, testOutputDirectory,
                "testResources");
        }
    }
}
