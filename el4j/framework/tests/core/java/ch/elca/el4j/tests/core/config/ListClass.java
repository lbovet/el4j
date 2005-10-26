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

package ch.elca.el4j.tests.core.config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for testing the ListPropertyMergeConfigurer class. It has
 * a list member and provides a setter and a getter method for this member.
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
public class ListClass {

    /** The test list. */
    private List m_listTest = new ArrayList();

        /**
     * @return Returns the listTest.
     */
    public List getListTest() {
        return m_listTest;
    }

    /**
     * @param listTest
     *            The listTest to set.
     */
    public void setListTest(List listTest) {
        this.m_listTest = listTest;
    }

}