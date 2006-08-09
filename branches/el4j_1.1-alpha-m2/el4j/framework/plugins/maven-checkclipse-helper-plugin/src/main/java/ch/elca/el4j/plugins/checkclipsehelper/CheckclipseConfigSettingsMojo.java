/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.plugins.checkclipsehelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * Mojo to config files in the .settings directory.
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
 * @goal checkclipse-config-settings
 * @requiresProject true
 */
public class CheckclipseConfigSettingsMojo extends AbstractMojo {
    // Checkstyle: MemberName off
    
    /**
     * Path to checkclipse config file.
     * 
     * @parameter expression="${checkclipsehelper.settingsConfigFilePath}"
     *            default-value=".settings/de.mvmsoft.checkclipse.prefs"
     * @required
     */
    protected File settingsConfigFilePath;
    
    /**
     * Flag to enable checkclipse.
     * 
     * @parameter expression="${checkclipsehelper.enableCheckclipse}"
     *            default-value="true"
     * @required
     */
    protected boolean enableCheckclipse;
    
    /**
     * Flag to use the project classloader to execute checkstyle checks.
     * 
     * @parameter expression="${checkclipsehelper.useProjectClassloader}"
     *            default-value="true"
     * @required
     */
    protected boolean useProjectClassloader;
    
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
     * Util to save property files.
     */
    protected final PropertiesPersister m_persister 
        = new DefaultPropertiesPersister();
    
    /** 
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        String projectPackaging = project.getPackaging();
        if (!StringUtils.hasText(projectPackaging) 
            || projectPackaging.contains("pom")) {
            getLog().info("No checkclipse settings config for pom project.");
            return;
        }
        
        if (settingsConfigFilePath.isDirectory()) {
            throw new MojoExecutionException("Given file path " 
                + settingsConfigFilePath.getAbsolutePath() 
                + " is a directory!");
        }
        getLog().info(
            "Writing file " + settingsConfigFilePath.getAbsolutePath());
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enableCheckclipse));
        props.setProperty("projectclassloader", 
            Boolean.toString(useProjectClassloader));
        
        try {
            m_persister.store(props, 
                new FileOutputStream(settingsConfigFilePath),
                "Checkclipse settings config file");
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Can not write file to given file path " 
                + settingsConfigFilePath.getAbsolutePath(), e);
        }
    }
}
