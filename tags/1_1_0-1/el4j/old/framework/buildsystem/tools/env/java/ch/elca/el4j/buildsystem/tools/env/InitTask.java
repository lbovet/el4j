/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.buildsystem.tools.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import ch.elca.el4ant.model.Attribute;
import ch.elca.el4ant.model.ConfigurationEvent;
import ch.elca.el4ant.model.ConfigurationListener;
import ch.elca.el4ant.model.Module;
import ch.elca.el4ant.model.Plugin;
import ch.elca.el4ant.model.ProjectRepository;

/**
 * This task initializes the environment support, that gathers properties files
 * from a central properties file and makes them available to the project.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class InitTask extends Task implements ConfigurationListener {

    /** Name for the environment properties file location attribute. */
    public static final String ENV_LOCATION = "env.location";
    
    /** 
     * Name of the property holding the location where the merged env.properties
     * file is written to.
     */
    public static final String ENV_DIST_LOCATION = "env.dist.properties";
    
    /** Environment plugin's name. */
    public static final String ENV_PLUGIN = "env";
    
    /**
     * The key used to store the properties in the {@link ProjectRepository},
     * allowing other plugins to access them.
     */
    public static final String ENVIRONMENT = "environment";
    
    /** The environment module's name. */
    public static final String MODULE_ENV = "module-env";
    
    /** The name of the environment's marker plugin. */
    protected static final String ENV_ATTRIBUTE_NAME = "env.enable";
    
    /**
     * {@inheritDoc}
     */
    public void componentConfigured(ConfigurationEvent event) {
        if (!(event.getSource() instanceof Module)) {
            return;
        }
        
        Module source = (Module) event.getSource();
        for (Iterator iter = source.getModuleDependencies(); iter.hasNext();) {
            Module next = (Module) iter.next();
            if (MODULE_ENV.equals(next.getModuleName())) {
                Attribute attribute = new Attribute();
                attribute.setName(ENV_ATTRIBUTE_NAME);
                attribute.setValue("true");
                source.addAttribute(attribute);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void componentConfiguring(ConfigurationEvent event) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
        String envLocation = getAttributeValue(ENV_LOCATION);
        Properties props = loadProperties(envLocation);
        
        String[] locations = getPropertiesLocations(props);
        
        Properties mergedProperties = loadPlugins(locations);
        storeEnvProperties(mergedProperties);
        
        ProjectRepository pr = ProjectRepository.getInstance();
        pr.addConfigurationListener(this);
        
        // Add merged properties to the project ropository's user data which
        // allows other plugins to query them.
        pr.setUserData(ENVIRONMENT, mergedProperties);
    }

    /**
     * Loads the environment properties file that lists the real properties
     * files.
     * 
     * @param fileName
     *      The environment properties file name.
     *      
     * @return The properties loaded from the given file.
     */
    private Properties loadProperties(String fileName) {
        File file = ProjectRepository.getInstance().resolveFile(fileName);
        Properties p = new Properties();
        
        try {
            p.load(new FileInputStream(file));
        } catch (FileNotFoundException fnfe) {
            throw new BuildException("Properties file not found '"
                    + fileName + "'.", fnfe);
            
        } catch (IOException ioe) {
            throw new BuildException("Error while loading properties file '"
                    + fileName + "'.", ioe);
        }
        
        return p;
    }
    
    /**
     * Parses the properties file containing links to other properties files and
     * returns their locations as a string array.
     * 
     * @param p
     *      The properties to parse.
     *      
     * @return Returns a string array with the properties files' locations.
     */
    private String[] getPropertiesLocations(Properties p) {
        String[] result = new String[p.size()];
        int i = 0;
        for (Iterator iter = p.values().iterator(); iter.hasNext();) {
            result[i++] = (String) iter.next();
        }
        return result;
    }
    
    /**
     * Creates and registers a property generator that copies all properties
     * of a {@link Properties} object to the project properties.
     * 
     * @param props
     *      The properties to create a generator for.
     */
    private void createAndRegisterPropertyGenerator(Properties props) {
        PropertiesMergePropertyGenerator generator
            = new PropertiesMergePropertyGenerator();
        generator.setProperties(props);
        ProjectRepository.getInstance().addPropertyGenerator(generator);
    }
    
    /**
     * Loads plugins defined in properties files. Each location provided by the
     * string array references a properties file which contains two special
     * key-value pairs:
     * <ul>
     * <li>plugin=<i>plugin name</i></li>
     * <li>plugin.file=<i>the location of the plugin file</i></li>
     * </ul>
     * 
     * @param locs
     *      The property files' locations.
     *      
     * @return Returns a {@link Properties} object that contains all merged
     *      properties (excepting the <code>plugin</code> and
     *      <code>plugin.file</code> entries).
     */
    private Properties loadPlugins(String[] locs) {
        Properties merged = new Properties();
        for (int i = 0; i < locs.length; i++) {
            Properties props = loadProperties(locs[i]);
            String plugin = props.getProperty("plugin");
            if (plugin != null) {
                log("Loading plugin " + plugin, Project.MSG_WARN);
                props.remove("plugin");
                String file = (String) props.remove("plugin.file");
                loadPlugin(plugin, file, props);
            } else {
                log("Registering properties", Project.MSG_WARN);
                createAndRegisterPropertyGenerator(props);
            }
            merged.putAll(props);
        }
        return merged;
    }
    
    /**
     * Loads the plugin with the given name that is defined in the given file.
     * The properties are attached to the plugin as attributes.
     * 
     * @param name
     *      The plugin's name.
     *      
     * @param file
     *      The file in which the plugin is defined.
     *      
     * @param props
     *      Properties that are attached as attributes to the plugin.
     */
    private void loadPlugin(String name, String file, Properties props) {
        Plugin plugin = new Plugin();
        plugin.setTaskName(ENV_PLUGIN);
        plugin.setName(name);
        plugin.setFile(file);
        plugin.setProject(getProject());
        plugin.setLocation(getLocation());
        for (Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
            Map.Entry next = (Map.Entry) iter.next();
            Attribute attr = new Attribute();
            attr.setName(next.getKey().toString());
            attr.setValue(next.getValue().toString());
            plugin.addAttribute(attr);
        }
        plugin.execute();
    }
    
    /**
     * Stores the merged environment properties in a file that is specified
     * by the <code>env.dist.properties</code> property.
     * 
     * @param props
     *      The properties to save.
     *      
     * @throws BuildException
     *      Any potential {@link IOException}s are transformed into a
     *      {@link BuildException}.
     */
    private void storeEnvProperties(Properties props) throws BuildException {
        try {
            String dest = getProject().getProperty(ENV_DIST_LOCATION);
            File file = new File(dest);
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            props.store(out, "Generated by EL4Ant. Do not modify!");
        } catch (IOException ioe) {
            throw new BuildException(
                    "Failed to write temporary properties file.", ioe);
        }
    }
    
    /**
     * Queries the attribute with the given name and returns its value.
     * 
     * @param name
     *      The attributes name.
     *      
     * @return Returns the attributes value.
     * 
     * @throws BuildException
     *      If the attribute does not exist.
     */
    private String getAttributeValue(String name) throws BuildException {
        String result = null;
        Plugin plugin = ProjectRepository.getInstance().getPlugin(ENV_PLUGIN);
        Iterator iter = plugin.getAttributesByName(name);
        if (iter.hasNext()) {
            result = ((Attribute) iter.next()).getValue();
        } else {
            throw new BuildException("The attribute '" + name
                    + "' is required for the plugin '" + ENV_PLUGIN + "'.");
        }
        return result;
    }
}
