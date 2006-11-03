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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.PropertyUtils;
import org.apache.maven.plugin.resources.ReflectionProperties;
import org.apache.maven.plugin.resources.util.InterpolationFilterReader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.util.StringUtils;

/**
 * Abstract environment support plugin. Filters the resources of given env dir 
 * and saves the generate resources in a special dir. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractEnvSupportMojo extends AbstractMojo {
    /**
     * An empty string array.
     */
    public static final String[] EMPTY_STRING_ARRAY = {};
    
    /**
     * Default includes string array.
     */
    public static final String[] DEFAULT_INCLUDES = {"**/**"};
    
    // Checkstyle: MemberName off
    /**
     * The character encoding scheme to be applied.
     * 
     * @parameter
     */
    protected String encoding;
    
    /**
     * The list of additional key-value pairs aside from that of the System, and
     * that of the project, which would be used for the filtering.
     * 
     * @parameter expression="${project.build.filters}"
     */
    protected List<String> filters;
    
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    // Checkstyle: MemberName on
    
    /**
     * Properties used for filtering.
     */
    protected Properties m_filterProperties;

    /**
     * Copies the resources filtered.
     * 
     * @param resourceDir Is the source dir.
     * @param outputDir Is the dest dir.
     * @param resourceType Is the type of resources.
     * @return Returns <code>true</code> if copying was successful.
     * @throws MojoExecutionException On problem.
     */
    protected boolean copyResourcesFiltered(File resourceDir, File outputDir, 
        String resourceType) 
        throws MojoExecutionException {
        
        initializeFiltering();
    
        if (!resourceDir.exists() || !resourceDir.isDirectory()) {
            getLog().info("Env " + resourceType + " directory '" 
                + resourceDir.getAbsolutePath() + "' does not exists.");
            return false;
        } else {
            getLog().info("Will proceed env " + resourceType + " directory '" 
                + resourceDir.getAbsolutePath() + "'.");
        }
    
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new MojoExecutionException(
                "Cannot create env " + resourceType + " output directory: " 
                + outputDir);
        }
        
        if (StringUtils.hasText(encoding)) {
            getLog().info("Using encoding: '" + encoding
                    + "' to copy filtered " + resourceType + ".");
        } else {
            getLog().info("Using default encoding to copy filtered " 
                + resourceType + ".");
        }
    
        DirectoryScanner scanner = new DirectoryScanner();
    
        scanner.setBasedir(resourceDir);
        scanner.setIncludes(DEFAULT_INCLUDES);
        scanner.addDefaultExcludes();
        scanner.scan();
        String[] includedFiles = scanner.getIncludedFiles();
        
        for (String fileName : includedFiles) {
            File sourceFile = new File(resourceDir, fileName);
            File destinationFile = new File(outputDir, fileName);
    
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }
    
            try {
                copyFileFiltered(sourceFile, destinationFile);
            } catch (IOException e) {
                throw new MojoExecutionException(
                    "Error copying filtered env " + resourceType + ".", e);
            }
        }
        
        return true;
    }

    /**
     * Initializes the properties used for filtering.
     * 
     * @throws MojoExecutionException On problem.
     */
    protected void initializeFiltering() throws MojoExecutionException {
        m_filterProperties = new Properties();
    
        // System properties
        m_filterProperties.putAll(System.getProperties());
    
        // Project properties
        m_filterProperties.putAll(project.getProperties());
        
        // Properties from filter property files
        for (String filterFile : filters) {
            try {
                Properties properties = PropertyUtils.loadPropertyFile(
                    new File(filterFile), true, true);
                m_filterProperties.putAll(properties);
            } catch (IOException e) {
                throw new MojoExecutionException("Error loading property "
                    + "filter file '" + filterFile + "'", e);
            }
        }
    }

    /**
     * Copies the a env resource file filtered.
     * 
     * @param from
     *            Is the source file.
     * @param to
     *            Is the dest file.
     * @throws IOException
     *             On copy problem.
     */
    protected void copyFileFiltered(File from, File to) throws IOException {
        Reader fileReader = null;
        Writer fileWriter = null;
        try {
            if (!StringUtils.hasText(encoding)) {
                fileReader = new BufferedReader(new FileReader(from));
                fileWriter = new FileWriter(to);
            } else {
                FileInputStream instream = new FileInputStream(from);
                FileOutputStream outstream = new FileOutputStream(to);
                fileReader = new BufferedReader(
                    new InputStreamReader(instream, encoding));
                fileWriter = new OutputStreamWriter(outstream, encoding);
            }
    
            // support ${token}
            Reader reader = new InterpolationFilterReader(fileReader,
                m_filterProperties, "${", "}");
    
            // support @token@
            reader = new InterpolationFilterReader(reader,
                m_filterProperties, "@", "@");
    
            boolean isPropertiesFile = false;
    
            if (to.isFile() && to.getName().endsWith(".properties")) {
                isPropertiesFile = true;
            }
    
            reader = new InterpolationFilterReader(reader,
                new ReflectionProperties(
                    project, isPropertiesFile), "${", "}");
    
            IOUtil.copy(reader, fileWriter);
        } finally {
            IOUtil.close(fileReader);
            IOUtil.close(fileWriter);
        }
    }
}