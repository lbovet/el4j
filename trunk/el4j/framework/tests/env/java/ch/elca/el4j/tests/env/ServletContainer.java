/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.tests.env;

/**
 * This class is used for testing purposes only.
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
public class ServletContainer {

    /** Whether the deployment unit is packed or not. */
    private boolean m_unpacked;
    
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

    /**
     * @return Returns whether the deployment unit is unpacked.
     */
    public boolean isUnpacked() {
        return m_unpacked;
    }

    /**
     * Sets whether the deployment units is unpacked. 
     * 
     * @param unpacked
     *      <code>true</code> for unpacked, <code>false</code> for packed.
     */
    public void setUnpacked(boolean unpacked) {
        m_unpacked = unpacked;
    }
}
