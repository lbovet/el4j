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
package ch.elca.el4j.plugins.duplicatefinder;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import ch.elca.el4j.util.maven.DuplicateClassFinder;

/**
 * Plugin to run duplicatefinder as part of the build lifecycle.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 * @phase test
 */
public abstract class AbstractDuplicateFinderMojo extends AbstractMojo {

    /**
     * The project we are dealing with.
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject m_project;
    
    /** 
     * The duplicate finder element.
     */
    protected DuplicateClassFinder m_finder;
    
    // Checkstyle: MemberName off
    
    /**
     * Whether to fail the build if duplicates are found.
     * @parameter default-value="false"
     */
    protected boolean duplicateIsFail;

    // Checkstyle: MemberName on
    
    /**
     * Set up the finder.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @SuppressWarnings("unchecked")
    protected void setUp() throws MojoExecutionException, MojoFailureException {
        m_finder = new DuplicateClassFinder();
        List<String> cp;
        try {
            cp = m_project.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Dependency resolution", e);
        }
        for (String element : cp) {
            try {
                m_finder.addUrl(new File(element).toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(
                    "Malformed URL: " + element, e);
            }
        }
    }
}
