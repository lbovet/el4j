/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.core.io.support;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class represents a simplified view of a module (EL4Ant build system
 * unit). It contains its name, the configuration files and the dependencies.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class Module {
    
    /** The delimiter used to separate configuration files and dependencies. */
    public static final String DELIMITER = ",";
    
    /** The module's name. */
    private String m_name;
    
    /** The module's configuration files. */
    private List m_configFiles;
    
    /** The module's dependencies. */
    private List m_dependencies;
    
    /**
     * Creates a new module with the given name.
     * 
     * @param name
     *      The module's name.
     */
    public Module(String name) {
        m_name = name;
        m_configFiles = new ArrayList();
        m_dependencies = new ArrayList();
    }
    
    /**
     * Adds all items -- separated by {@link #DELIMITER} -- to the configuration
     * file list.
     * 
     * @param configFiles
     *      The list of configuration files to add.
     */
    public void addAllConfigFiles(String configFiles) {
        StringTokenizer tokenizer = new StringTokenizer(configFiles, DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            addConfigFile(tokenizer.nextToken());
        }
    }
    
    /**
     * Adds the given configuration file to the module.
     * 
     * @param configFile
     *      The configuration file to add.
     */
    public void addConfigFile(String configFile) {
        m_configFiles.add(configFile);
    }
    
    /**
     * Adds all item -- separated by {@link #DELIMITER} -- to the dependency
     * list.
     * 
     * @param dependencies
     *      The list of dependencies to add.
     */
    public void addAllDependencies(String dependencies) {
        StringTokenizer tokenizer = new StringTokenizer(
                dependencies, DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            addDependency(tokenizer.nextToken());
        }
    }
    
    /**
     * Adds the given dependency to the module.
     * 
     * @param dependency
     *      The dependency to add.
     */
    public void addDependency(String dependency) {
        m_dependencies.add(dependency);
    }
    
    /**
     * @return Returns all configuration files.
     */
    public String[] getConfigFiles() {
        return (String[]) m_configFiles.toArray(
                new String[m_configFiles.size()]);
    }
    
    /**
     * @return Returns all dependencies.
     */
    public String[] getDependencies() {
        return (String[]) m_dependencies.toArray(
                new String[m_dependencies.size()]);
    }
    
    /**
     * @return Returns the configuration file list.
     */
    public List getConfigFilesAsList() {
        return new ArrayList(m_configFiles);
    }
    
    /**
     * @return Returns the dependency list.
     */
    public List getDependenciesAsList() {
        return new ArrayList(m_dependencies);
    }
    
    /**
     * @return Returns the module's name.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Module '" + m_name + "'";
    }
}
