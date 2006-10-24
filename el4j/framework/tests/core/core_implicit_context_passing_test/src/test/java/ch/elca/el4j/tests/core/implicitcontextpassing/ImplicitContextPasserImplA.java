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
package ch.elca.el4j.tests.core.implicitcontextpassing;

import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;

/**
 * This is the test implicit context passer A with different kinds of data. 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author David Stefan (DST)
 */

public class ImplicitContextPasserImplA extends AbstractImplicitContextPasser 
    implements ImplicitContextPassTester {
    
    /**
     * Test data.
     */
    private Object m_testData = null;

    /**
     * Received data.
     */
    private Object m_receivedData = null;

    /**
     * Set which data to use.
     * 
     * @param option
     *            Kind of data to use
     */
    public void setDataToUse(int option) {
        List<Object> temp;
        switch (option) {
            case RESET:
                m_testData = null;
                m_receivedData = null;
                break;
            case NULL_TEST:
                m_testData = null;
                break;
            case STRING_TEST:
                m_testData = "This is my test data string";
                break;
            case INT_TEST:
                m_testData = Integer.MIN_VALUE;
                break;
            case FLOAT_TEST:
                m_testData = Float.MAX_VALUE;
                break;
            case DOUBLE_TEST:
                m_testData = Double.NEGATIVE_INFINITY;
                break;
            case LIST_TEST:
                temp = new ArrayList<Object>();
                temp.add("");
                temp.add("bar");
                temp.add(Double.NaN);
                m_testData = temp;
                break;
            case NULL_LIST_TEST:
                temp = new ArrayList<Object>();
                m_testData = temp;
                break;
            default:
                throw new RuntimeException("Type of test not supported");
        }
    }

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
            m_receivedData = context;
        }
    }

    /**
     * @return Returns the receivedData.
     */
    public Object getReceivedData() {
        return m_receivedData;
    }

    /**
     * @return Returns the testData.
     */
    public Object getTestData() {
        return m_testData;
    }
}