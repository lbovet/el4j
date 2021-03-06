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

package ch.elca.el4j.services.remoting.protocol.ejb.generator;

/**
 * This interface is used as a facade to the EJB remoting code. It helps
 * gathering the informations used to generate EJB wrapper classes.
 * 
 * <p/><b>Important</b>: This class is duplicated into the build system plug in
 * that creates the wrapper classes. This minimizes the usage of reflection
 * mechanisms to a minimum.
 *
 * <p/><b>Copy this interface definition to the build system plugin that crates
 * the EJB classes after each modification.</b>
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public interface EjbGeneratorFacade {

    /**
     * Parses the given string that contains different Spring configuration
     * files and assembles all the information needed to generate EJB wrappers.
     * 
     * @param inclusiveLocations A String containing several Spring
     *      configuration locations, comma-separated. Don't add any white
     *      spaces. <code>inclusive_1,inclusive_2,...,inclusive_n</code>
     *      
     * @param exclusiveLocations A String containing several Spring
     *      configuration locations, comma-separated. Don't add any white
     *      spaces.
     *      <code>exclusive_1,exclusive_2,...,exclusive_m</code>
     * 
     * @return Returns the informations to build EJB wrappers.
     */
    public EjbBean[] getEjbBeans(String inclusiveLocations,
            String exclusiveLocations);
}
