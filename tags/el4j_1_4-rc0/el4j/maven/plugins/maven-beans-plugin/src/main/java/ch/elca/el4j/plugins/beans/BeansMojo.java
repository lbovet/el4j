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
package ch.elca.el4j.plugins.beans;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import ch.elca.el4j.plugins.beans.resolve.ResolverManager;

/**
 * The mojo to extract beans from a project.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 * 
 * @goal beans
 * @requiresDependencyResolution runtime
 */
public class BeansMojo extends AbstractMojo {

    /**
     * The maven project - used for runtime classpath resolution.
     * @parameter expression="${project}"
     */
    private MavenProject m_project;

    // Checkstyle: MemberName off
    
    /**
     * The file to read configuration information from.
     * @parameter
     * @required
     */
    private String sourceFile;
    
    // Checkstyle: MemberName on
    
    /** {@inheritDoc} */
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        URL[] classpath = constructClasspath();
        
        ConfigurationExtractor ex = new ConfigurationExtractor(sourceFile);
        
        BeanPathResolver resolver = new BeanPathResolver();
        String[] files = resolver.resolve(
            ex.getInclusive(), ex.getExclusive(), classpath);

        ResolverManager mgr = new ResolverManager(classpath);
        
        String outputDir = m_project.getBasedir().getAbsolutePath();
        
        File beanDirectory = new File(outputDir, "beans");
        
        beanDirectory.mkdir();
        if (!beanDirectory.exists() || !beanDirectory.isDirectory()) {
            throw new MojoFailureException("Failed to create beans directory.");
        }
        
        for (String file : files) {
            try {
                mgr.copy(file, beanDirectory);
            } catch (IOException e) {
                
                throw new MojoFailureException("IO exception copying files. "
                    + e.toString());
            }
        }
    }
    

    
    /**
     * Constructs an URL[] representing the runtime classpath.
     * @return The urls.
     */
    private URL[] constructClasspath() {
        List<?> list;
        List<URL> classpath = new LinkedList<URL>();
        try {
            list = m_project.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            // Can't happen - dependency resolution required.
            throw new Error(e);
        }
        for (Object o : list) {
            String s = o.toString();
            try {
                classpath.add(new File(s).toURL());
            } catch (MalformedURLException e) {
                // Shouldn't really happen.
                throw new RuntimeException(e);
            }
        }
        return classpath.toArray(new URL[0]);
    }
}
