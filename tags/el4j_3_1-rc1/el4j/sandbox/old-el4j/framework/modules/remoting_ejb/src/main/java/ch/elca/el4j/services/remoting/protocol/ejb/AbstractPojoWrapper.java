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

package ch.elca.el4j.services.remoting.protocol.ejb;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * Superclass of all EJB beans containing the spring application context.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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