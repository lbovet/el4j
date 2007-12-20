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
package ch.elca.el4j.services.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Object to holds criterias to execute queries. A query object can be specified
 * for exactly one class. A query object is an AND-joined set of Criteria objects.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class QueryObject implements Serializable {
    /**
     * The bean class the query object is for.
     */
    private Class<?> m_beanClass;
    
    /**
     * Are the criterias for this query.
     */
    private List<Criteria> m_criterias = new ArrayList<Criteria>();
    
    /**
     * Specifies a general query object.
     */
    public QueryObject() {
        this(null);
    }

    /**
     * Specifies the query object for a specific class.
     * 
     * @param beanClass Is the bean class this query object is made for.
     */
    public QueryObject(Class<?> beanClass) {
        m_beanClass = beanClass;
    }

    /**
     * @return Returns the bean class this query object is made for.
     */
    public Class<?> getBeanClass() {
        return m_beanClass;
    }
    
    /**
     * Adds the given criteria.
     * 
     * @param criteria Is the criteria to add.
     */
    public void addCriteria(Criteria criteria) {
        Reject.ifNull(criteria);
        m_criterias.add(criteria);
    }
    
    /**
     * Adds the given criterias.
     * 
     * @param criterias Are the criterias to add.
     */
    public void addCriterias(Criteria... criterias) {
        Reject.ifNull(criterias);
        for (int i = 0; i < criterias.length; i++) {
            Criteria criteria = criterias[i];
            m_criterias.add(criteria);
        }
    }
    
    /**
     * @return Returns a list of criterias.
     */
    public List<Criteria> getCriteriaList() {
        return new ArrayList<Criteria>(m_criterias);
    }
    
    /**
     * @return Returns an array of criterias.
     */
    public Criteria[] getCriterias() {
        return (Criteria[]) m_criterias.toArray(
            new Criteria[] {});
    }
}
