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

package ch.elca.el4j.tests.env;

/**
 * This class is used for testing purposes only.
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
public class ServletContainer {

    /** The container's name. */
    private String m_container;
    
    /** The container's port. */
    private int m_port;

    /**
     * @return Returns the container's name.
     */
    public String getContainer() {
        return m_container;
    }

    /**
     * Sets the container's name.
     * 
     * @param container
     *      The name.
     */
    public void setContainer(String container) {
        m_container = container;
    }

    /**
     * @return Returns the container's port.
     */
    public int getPort() {
        return m_port;
    }

    /**
     * Sets the container's port.
     * 
     * @param port
     *      The container's port.
     */
    public void setPort(int port) {
        m_port = port;
    }
}
