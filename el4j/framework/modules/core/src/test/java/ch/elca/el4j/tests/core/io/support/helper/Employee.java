/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.core.io.support.helper;

/**
 * An employee test bean.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class Employee {
    /**
     * Prename of the employee.
     */
    private String m_prename;
    
    /**
     * Lastname of the employee.
     */
    private String m_lastname;

    /**
     * @return Returns the lastname.
     */
    public final String getLastname() {
        return m_lastname;
    }

    /**
     * @param lastname Is the lastname to set.
     */
    public final void setLastname(String lastname) {
        m_lastname = lastname;
    }

    /**
     * @return Returns the prename.
     */
    public final String getPrename() {
        return m_prename;
    }

    /**
     * @param prename Is the prename to set.
     */
    public final void setPrename(String prename) {
        m_prename = prename;
    }
}
