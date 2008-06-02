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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.core.context.ModuleApplicationContextUtils;
import ch.elca.el4j.plugins.beans.resolve.Resolver;
import ch.elca.el4j.plugins.beans.resolve.ResolverManager;

/**
 * Creates a bean path (array of beans files) from a ModuleApplicationContext -
 * style definition of include and exclude locations and a classpath (Url[])
 * to search.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class BeanPathResolver {

    /**
     * Resolve the bean path.
     * 
     * @param inclusiveLocations The inclusive locations.
     * @param exclusiveLocations The exclusive locations.
     * @param classpath The classpath to search.
     * @return The bean files found.
     */
    public String[] resolve(String[] inclusiveLocations,
        String[] exclusiveLocations, URL[] classpath) {
        
        // Create a classloader for the target classpath and launch the 
        // resolver with it.
        
        try {
            ClassLoader loader = new URLClassLoader(classpath);

            ResolverRunner r 
                = new ResolverRunner(inclusiveLocations, exclusiveLocations);
            r.setContextClassLoader(loader);
            r.start();
            r.join();
            
            String[] result = r.getResult();
            
            return filterResult(result, classpath);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When we use module-core to resolve the elements, we get module-core and
     * all its dependencies on the classpath by default. Here, we filter out
     * any results that didn't come from our classpath. 
     * 
     * @param data The data a search for beans files returned.
     * @param classpath The classpath.
     * @return The data stripped of all elements that are not from this 
     * classpath.
     */
    private String[] filterResult(String[] data, URL[] classpath) {
        Resolver r = new ResolverManager(classpath);
        List<String> accepted = new ArrayList<String>();
        for (String file : data) {
            if (r.accept(file)) {
                accepted.add(file);
            }
        }
        return accepted.toArray(new String[0]);
    }
    
    /**
     * Run with a different contextClassLoader. 
     */
    class ResolverRunner extends Thread {

        /** run() puts its results here. */
        private String[] m_result;
        
        /** Inclusive locations. */
        private String[] m_inclusive;
        
        /** Exclusive locations. */
        private String[] m_exclusive;
        
        /**
         * Set up the thread to run.
         * @param inclusive Config locations.
         * @param exclusive Config locations.
         */
        public ResolverRunner(String[] inclusive, String[] exclusive) {
            m_inclusive = inclusive;
            m_exclusive = exclusive;
        }
        
        /**
         * @return Returns the result.
         */
        public String[] getResult() {
            return m_result;
        }

        /** {@inheritDoc} */
        @Override
        public void run() {
            ModuleApplicationContextConfiguration config 
                = new ModuleApplicationContextConfiguration();
            config.setInclusiveConfigLocations(new String[0]);
            config.setExclusiveConfigLocations(new String[0]);
            ModuleApplicationContext ctx = new ModuleApplicationContext(config);
            
            ModuleApplicationContextUtils utils 
                = new ModuleApplicationContextUtils(ctx);
            
            // AllowBeanDefinitionOverriding is an unused parameter?
            String[] result = utils.calculateInputFiles(
                m_inclusive, m_exclusive, false);
            m_result = result;
        }
    }
}
