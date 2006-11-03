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

package ch.elca.el4j.tests.util.metadata.attributes;

/**
 * An ExampleAttributeOne which only contains a private member in order to store
 * a multiplication factor.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class ExampleAttributeOne implements ExampleAttributeInterface {

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
    public ExampleAttributeOne(int factor) {
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
        m_factor = factor;
    }
}