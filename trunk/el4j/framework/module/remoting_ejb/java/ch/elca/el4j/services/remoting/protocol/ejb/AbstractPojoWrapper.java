/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.services.remoting.protocol.ejb;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Superclass of all EJB beans containing the spring application context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Nicolas Schiper (NSC)
 */
public abstract class AbstractPojoWrapper {

    /** The spring application context. */
    private static ApplicationContext s_appContext = null;
    
    /**
     * Initializes an application context with the given locations to include
     * or exclude respectively and returns a bean instance configured with the
     * given name.
     *  
     * @param inclusiveLocations
     *      Configuration locations to include.
     *      
     * @param exclusiveLocations
     *      Configuration locations to exclude.
     *      
     * @param beanName
     *      The name of the bean to return.
     *      
     * @return Returns a bean with the given name that has been initialized in
     *      the application context defined by the inclusive and exclusive
     *      configuration locations.
     */
    protected static synchronized Object getBean(String[] inclusiveLocations,
            String[] exclusiveLocations,
            String beanName) {
        if (s_appContext == null) {
            s_appContext = new ModuleApplicationContext(inclusiveLocations,
                    exclusiveLocations,
                    true, null);
        }
        return s_appContext.getBean(beanName);
    }
}