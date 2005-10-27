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

package ch.elca.el4j.tests.core.contextpassing;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;

/**
 * This is the test implicit context passer B on client side.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public class ImplicitContextPasserB extends AbstractImplicitContextPasser {
    /**
     * Test data.
     */
    private final double m_testData = 974366.96;

    /**
     * Received data.
     */
    private double m_receivedData = 0;

    /**
     * {@inheritDoc}
     */
    public Object getImplicitlyPassedContext() {
        return new Double(m_testData);
    }

    /**
     * {@inheritDoc}
     */
    public void pushImplicitlyPassedContext(Object context) {
        if (context != null) {
            m_receivedData = ((Double) context).doubleValue();
        }
    }

    /**
     * @return Returns the receivedData.
     */
    public double getReceivedData() {
        return m_receivedData;
    }

    /**
     * @return Returns the testData.
     */
    public double getTestData() {
        return m_testData;
    }
}