/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.core.implicitcontextpassing;


/**
 * This interface provides common data of ImplicitContextPasserImplA and
 * ImplicitContextPasserImplA.
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

public interface ImplicitContextPassTester {

    /**
     * Reset m_testData and m_receivedData, i.e. set them null
     */
    public static final int RESET = 0;
    /**
     * Tests passing of null value.
     */
    public static final int NULL_TEST = 1;
    /**
     * Tests passing of a test string.
     */
    public static final int STRING_TEST = 2;
    /**
     * Tests passing of smallest integer.
     */
    public static final int INT_TEST = 3;
    /**
     * Tests passing of greatest float.
     */
    public static final int FLOAT_TEST = 4;
    /**
     * Tests passing of negative infinity.
     */
    public static final int DOUBLE_TEST = 5;
    /**
     * Tests passing of a simple lists with two string elements.
     */
    public static final int LIST_TEST = 6;
    /**
     * Tests passing of a empty list.
     */
    public static final int NULL_LIST_TEST = 7;

    /**
     * Set which data to use.
     * 
     * @param option
     *            Kind of data to use
     */
    public abstract void setDataToUse(int option);

}