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

package ch.elca.el4j.tests.services.monitoring.jmx;

/**
 * This class is used to test the JMX package. It contains a member, a getter
 * and a setter method for this member and an 'add' method.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class Foo {

    /**
     * The only member variable, used for testing.
     */
    private String m_fullName;

    /**
     * The setter method for the fullName member variable.
     * 
     * @param fullName
     *            The fullName to be set
     */
    public void setFullName(String fullName) {
        this.m_fullName = fullName;
    }

    /**
     * The getter method for the fullName member variable.
     * 
     * @return The fullName
     */
    public String getFullName() {
        return m_fullName;
    }

    /**
     * An adding method to be used for JMX testing.
     * 
     * @param x
     *            The first addend
     * @param y
     *            The second addend
     * @return The sum of x and y
     */
    public int add(int x, int y) {
        return x + y;
    }
}