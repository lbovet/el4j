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
package ch.elca.el4j.buildsystem.remoting.ejb;

import org.apache.tools.ant.Task;

/**
 * This class represents a container specific client dependency.
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
