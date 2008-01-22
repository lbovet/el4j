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
package ch.elca.el4j.seam.generic.humanization;

import java.util.Comparator;

/**
 * Comparator needed to sort fields based on their humanization labels.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA)
 */
public class HumanizationComparator implements Comparator<String> {
    /**
     * The name of the entity whose field names should be compared.
     */
    private String m_entityName;

    /**
     * @param entityName    the name of the entity whose field names
     *                      should be compared.
     */
    public HumanizationComparator(String entityName) {
        m_entityName = entityName;
    }

    /** {@inheritDoc} */
    public int compare(String fieldName1, String fieldName2) {
        String humanized1 = Humanization.getFieldName(m_entityName, fieldName1);
        String humanized2 = Humanization.getFieldName(m_entityName, fieldName2);

        return humanized1.compareToIgnoreCase(humanized2);
    }
}