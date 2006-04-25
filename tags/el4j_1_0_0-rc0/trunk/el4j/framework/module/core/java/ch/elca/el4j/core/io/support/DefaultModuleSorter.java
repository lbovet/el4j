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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class sorts a list of unordered modules using their dependency
 * information.
 * 
 * <p/>The algorithm computes the children of each module and the set of root
 * nodes, i.e. nodes without dependencies. Then it removes the next root
 * <i>r</i> from the root list and adds it to the sorted modules list. The
 * algorithm then iterates over all the children of r, removing the dependency
 * to r. If a children has no more unsatisfied dependencies, it's added to the
 * list of root nodes. The algorithm performs this step as long as there are
 * nodes in the root nodes list. Finally, it checks that there are no more
 * unsatisfied dependencies, which could happen if there are cycles in the
 * dependency graph.  
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
public class DefaultModuleSorter implements ModuleSorter {
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(DefaultModuleSorter.class);
    
    /** The list with root modules. */
    private List m_rootModules;
    
    /** Maps from module names (String) to their children (Module). */
    private Map m_children;
    
    /** Maps from modules (Module) to their dependencies (String). */
    private Map m_dependencies;
    
    /**
     * {@inheritDoc}
     */
    public Module[] sortModules(Module[] modules) {
        m_rootModules = new ArrayList();
        m_children = new HashMap();
        m_dependencies = new HashMap();
        
        buildInternalModel(modules);
        Module[] flat = computeOrderedList();
        return flat;
    }

    /**
     * Creates an internal representation of the hierarchy defined by the
     * modules.
     * 
     * @param modules
     *      The list of modules to build the inner presentation for.
     */
    private void buildInternalModel(Module[] modules) {
        for (int i = 0; i < modules.length; i++) {
            String[] deps = modules[i].getDependencies();
            
            if (deps.length == 0) {
                m_rootModules.add(modules[i]);
                continue;
            }
            
            // add current module as child to all its parents
            for (int j = 0; j < deps.length; j++) {
                addChild(deps[j], modules[i]);
            }
            
            // copy dependency information
            m_dependencies.put(modules[i], modules[i].getDependenciesAsList());
        }
    }
    
    /**
     * Adds a children to a node.
     * 
     * @param module
     *      The name of the module which the child is attached to.
     *      
     * @param child
     *      The child module.
     */
    private void addChild(String module, Module child) {
        List children = (List) m_children.get(module);
        if (children == null) {
            children = new ArrayList();
            m_children.put(module, children);
        }
        children.add(child);
    }
    
    /**
     * Returns an ordered list of modules that satisfy the order constraints
     * defined by module dependencies. 
     * @return Returns an list of modules that satisfy the order constraints
     *      defined by the modules' dependencies. 
     */
    private Module[] computeOrderedList() {
        List ordered = new ArrayList();
        while (!m_rootModules.isEmpty()) {
            Module nextRoot = (Module) m_rootModules.remove(0);
            ordered.add(nextRoot);
            
            List children = (List) m_children.get(nextRoot.getName());
            if (children == null) {
                continue;
            }
            
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                Module child = (Module) iter.next();
                List deps = (List) m_dependencies.get(child);
                
                if (deps != null) {
                    deps.remove(nextRoot.getName());
                    
                    if (deps.size() == 0) {
                        m_dependencies.remove(child);
                        m_rootModules.add(child);
                    }
                }
            }
        }
        
        checkUnresolvedDependencies();
        
        return (Module[]) ordered.toArray(new Module[ordered.size()]);
    }
    
    /**
     * Checks if there are unresolved dependencies indicating that there
     * are cycles.
     */
    private void checkUnresolvedDependencies() {
        if (!m_dependencies.isEmpty()) {
            StringBuffer buffer = new StringBuffer(
                "Configuration contains cycles! "
                    + "Unresolved modules with dependencies (ignored): ");
            
            Iterator iter = m_dependencies.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry next = (Map.Entry) iter.next();
                buffer.append("[");
                buffer.append(next.getKey());
                buffer.append(": ");
                List deps = (List) next.getValue();
                for (Iterator diter = deps.iterator(); diter.hasNext();) {
                    buffer.append(diter.next());
                    if (diter.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append("]");
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
            
            s_logger.error(buffer.toString());
        }
    }
}
