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

package ch.elca.el4j.core.io.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 * @author Martin Zeltner (MZE)
 */
public class DefaultModuleSorter implements ModuleSorter {
	
	/** The static logger. */
	private static Logger s_logger = LoggerFactory.getLogger(DefaultModuleSorter.class);
	
	/** The list with root modules. */
	private List<Module> m_rootModules;
	
	/** Maps from module names (String) to their children (Module). */
	private Map<String, List<Module>> m_children;
	
	/** Maps from modules (Module) to their dependencies (String). */
	private Map<Module, List<String>> m_dependencies;
	
	/**
	 * {@inheritDoc}
	 */
	public Module[] sortModules(Module[] modules) {
		m_rootModules = new ArrayList<Module>();
		m_children = new HashMap<String, List<Module>>();
		m_dependencies = new HashMap<Module, List<String>>();
		
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
		Set<String> moduleNames = new HashSet<String>();
		for (int i = 0; i < modules.length; i++) {
			Module m = modules[i];
			moduleNames.add(m.getName());
		}
		for (int i = 0; i < modules.length; i++) {
			Module m = modules[i];
			String[] deps = m.getDependencies();
			
			if (deps.length > 0) {
				// Add current module as child to all its parents but only if
				// the parent is one of the known modules.
				List<String> wellKnownChilds = new ArrayList<String>();
				for (int j = 0; j < deps.length; j++) {
					String dependencyName = deps[j];
					if (moduleNames.contains(dependencyName)) {
						addChild(dependencyName, m);
						wellKnownChilds.add(dependencyName);
					}
				}
				if (wellKnownChilds.size() > 0) {
					// Copy dependency information of well known dependencies.
					m_dependencies.put(m, wellKnownChilds);
				} else {
					// There are no well known childs so the given module is a
					// root.
					m_rootModules.add(m);
				}
			} else {
				// Given module has no dependencies, so it is a root module.
				m_rootModules.add(m);
			}
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
		List<Module> children = m_children.get(module);
		if (children == null) {
			children = new ArrayList<Module>();
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
		List<Module> ordered = new ArrayList<Module>();
		while (!m_rootModules.isEmpty()) {
			Module nextRoot = m_rootModules.remove(0);
			ordered.add(nextRoot);
			
			List<Module> children = m_children.get(nextRoot.getName());
			if (children != null) {
				for (Module child : children) {
					List<String> deps = m_dependencies.get(child);
					if (deps != null) {
						deps.remove(nextRoot.getName());
						if (deps.size() == 0) {
							m_dependencies.remove(child);
							m_rootModules.add(child);
						}
					}
				}
			}
		}
		
		checkUnresolvedDependencies();
		
		return ordered.toArray(new Module[ordered.size()]);
	}
	
	/**
	 * Checks if there are unresolved dependencies.
	 */
	private void checkUnresolvedDependencies() {
		if (!m_dependencies.isEmpty() && s_logger.isWarnEnabled()) {
			StringBuffer buffer = new StringBuffer(
				"Unresolved modules with dependencies (ignored): ");
			
			Iterator<Map.Entry<Module, List<String>>> iter
				= m_dependencies.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Module, List<String>> next = iter.next();
				buffer.append("[");
				buffer.append(next.getKey());
				buffer.append(": ");
				Iterator<String> diter = next.getValue().iterator();
				while (diter.hasNext()) {
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
			s_logger.warn(buffer.toString());
		}
	}
}
