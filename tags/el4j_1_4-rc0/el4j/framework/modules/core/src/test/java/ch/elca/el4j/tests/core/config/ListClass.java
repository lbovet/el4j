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

package ch.elca.el4j.tests.core.config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for testing the ListPropertyMergeConfigurer class. It has
 * a list member and provides a setter and a getter method for this member.
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
public class ListClass {

    /** The test list. */
    private List<String> m_listTest = new ArrayList<String>();

        /**
     * @return Returns the listTest.
     */
    public List<String> getListTest() {
        return m_listTest;
    }

    /**
     * @param listTest
     *            The listTest to set.
     */
    public void setListTest(List<String> listTest) {
        this.m_listTest = listTest;
    }

}