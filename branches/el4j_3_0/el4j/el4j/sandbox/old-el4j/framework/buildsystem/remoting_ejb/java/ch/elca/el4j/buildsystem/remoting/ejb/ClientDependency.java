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
package ch.elca.el4j.buildsystem.remoting.ejb;

import org.apache.tools.ant.Task;

/**
 * This class represents a container specific client dependency.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public class ClientDependency extends Task {

    /** The dependency's jar file name. */
    private String m_name;

    /**
     * @return Returns the dependency's jar file name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the dependency's jar file name.
     *
     * @param dependency
     *      The jar file name to set.
     */
    public void setName(String dependency) {
        this.m_name = dependency;
    }
}
