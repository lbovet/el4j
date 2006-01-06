/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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
 * This is the test implicit context passer A on client side.
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
public class ImplicitContextPasserA extends AbstractImplicitContextPasser {
    /**
     * Test data.
     */
    private String m_testData 
        = "This is my testdata for implicit context passer A.";

    /**
     * Received data.
     */
    private String m_receivedData = null;

    /**
     * {@inheritDoc}
     */
    public Object getImplicitlyPassedContext() {
        return m_testData;
    }

    /**
     * {@inheritDoc}
     */
    public void pushImplicitlyPassedContext(Object context) {
        if (context != null) {
            m_receivedData = (String) context;
        }
    }

    /**
     * @return Returns the receivedData.
     */
    public String getReceivedData() {
        return m_receivedData;
    }

    /**
     * @return Returns the testData.
     */
    public String getTestData() {
        return m_testData;
    }
}