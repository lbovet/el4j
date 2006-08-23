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

package ch.elca.el4j.core.io.support;

import java.util.ArrayList;
import java.util.List;

/**
 * This class simplifies writing ordered configuration location providers.
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
public abstract class AbstractOrderedConfigLocationProvider 
    implements ConfigLocationProvider {

    /** 
     * The module sorter that computes the list of modules preserving their
     * hierarchical constraints.
     */
    private ModuleSorter m_moduleSorter = new DefaultModuleSorter();

    /**
     * Sets the module sorter used to compute the list of modules while
     * preserving their hierarchical constraints. Default sorter is a
     * {@link DefaultModuleSorter}.
     * 
     * @param moduleSorter 
     *      The module sorter to use.
     * 
     * @see ModuleSorter
     */
    public void setModuleSorter(ModuleSorter moduleSorter) {
        m_moduleSorter = moduleSorter;
    }

    /**
     * Sorts an unordered list of modules using the hierarchical constraints
     * defined across the modules.
     * 
     * @param modules
     *      The list of modules to sort.
     *      
     * @return Returns an ordered list of modules that fulfill the partial
     *      order defined by the module's hierarchical constraints.
     */
    protected Module[] sortModules(Module[] modules) {
        return m_moduleSorter.sortModules(modules);
    }
    
    /**
     * Merges the configuration locations of the provided list of modules,
     * preserving the module's order.
     * 
     * @param modules
     *      The ordered list of modules which configuration locations has to be
     *      merged.
     *       
     * @return Returns the ordered list of configuration locations declared
     *      by the modules.
     */
    protected String[] mergeConfigLocations(Module[] modules) {
        List configLocations = new ArrayList();
        for (int i = 0; i < modules.length; i++) {
            configLocations.addAll(modules[i].getConfigFilesAsList());
        }
        
        return (String[]) configLocations.toArray(
                new String[configLocations.size()]);
    }
}
