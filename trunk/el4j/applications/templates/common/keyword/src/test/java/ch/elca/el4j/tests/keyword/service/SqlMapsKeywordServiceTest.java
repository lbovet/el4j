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
package ch.elca.el4j.tests.keyword.service;

/**
 * 
 * iBatis-specific test class for <code>KeywordService</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class SqlMapsKeywordServiceTest extends AbstractKeywordServiceTest {

    /**
     * {@inheritDoc}
     */
    protected String[] getIncludeConfigLocations() {
        return new String[] {
            "classpath*:mandatory/*.xml",
            "classpath*:mandatory/keyword/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/ibatis/*.xml",
            "classpath*:scenarios/dataaccess/ibatis/keyword/*.xml",
            "classpath*:optional/interception/transactionJava5Annotations.xml"};
    }

    /**
     * {@inheritDoc}
     */
    protected String[] getExcludeConfigLocations() {
        return null;
    }
    
}
