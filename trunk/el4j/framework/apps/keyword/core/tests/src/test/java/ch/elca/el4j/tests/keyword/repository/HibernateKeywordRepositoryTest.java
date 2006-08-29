/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.keyword.repository;

/**
 * 
 * Test class for <code>KeywordHibernateRepository</code>.
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
public class HibernateKeywordRepositoryTest
    extends AbstractKeywordRepositoryTest {
    
    /**
     * {@inheritDoc}
     */
    protected String[] getIncludeConfigLocations() {
        return new String[] {
            "classpath:optional/interception/methodTracing.xml",
            "classpath*:mandatory/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/repository/*.xml",
            "classpath*:optional/interception/transactionJava5Annotations.xml"};
    }

    /**
     * {@inheritDoc}
     */
    protected String[] getExcludeConfigLocations() {
        return null;
    }
    
}
