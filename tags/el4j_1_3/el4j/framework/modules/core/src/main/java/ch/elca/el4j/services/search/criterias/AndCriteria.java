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
package ch.elca.el4j.services.search.criterias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Criteria that combines n Criteria with AND (i.e. all 
 *  Criterias must be true).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp Oser (POS)
 */
public class AndCriteria implements Criteria {

    protected List<Criteria> m_criterias;
    
    public AndCriteria (Criteria left, Criteria right) {
        this(new Criteria[] { left, right});    
    }
    
    public AndCriteria (Criteria... criterias){
        m_criterias  = new ArrayList<Criteria>(criterias.length);
        m_criterias.addAll(Arrays.asList(criterias));
    }

    public void add(Criteria c) {
        m_criterias.add(c);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getSqlWhereCondition() {
        return "("+ StringUtils.arrayToDelimitedString(
            CriteriaHelper.applyToSqlWhereCondition(m_criterias.toArray(new Criteria[0])), 
            " AND ")+ ")";
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "";
    }

    public List<Criteria> getCriterias() {
        return m_criterias;
    }
}
