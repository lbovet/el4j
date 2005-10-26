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

package ch.elca.el4j.tests.util.attributes;

/**
 * An ExampleAttributeOne which only contains a private member in order to store
 * a multiplication factor after multiplying the given factor by 10.
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
public class ExampleAttributeTwo implements ExampleAttributeInterface {

    /**
     * The constant number to be multiplied with second factor.
     */
    public static final int CONSTANT_FACTOR = 10;

    /**
     * Factor member of this class.
     */
    private int m_factor;

    /**
     * Constructor which takes the factor and saves it.
     * 
     * @param factor
     *            The factor to set.
     */
    public ExampleAttributeTwo(int factor) {
        setFactor(factor);
    }

    /**
     * {@inheritDoc}
     */
    public int getFactor() {
        return m_factor;
    }

    /**
     * {@inheritDoc}
     */
    public void setFactor(int factor) {
        m_factor = factor * CONSTANT_FACTOR;
    }
}