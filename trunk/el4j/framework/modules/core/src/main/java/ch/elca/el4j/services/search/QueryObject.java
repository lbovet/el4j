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

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.search.criterias.AndCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.CriteriaHelper;
import ch.elca.el4j.services.search.criterias.Order;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Object to holds criterias to execute queries. A query object can be specified
 * for exactly one class. A query object is an AND-joined set of Criteria 
 * objects.  <a>
 * 
 * Features: <br>
 *  <ul>
 *   <li> OR-criterias, NOT-criterias, AND-criterias refer to 
 *     {@link CriteriaHelper} for some convenience support.
 *   <li> paging support (see methods {@link setFirstResult}
 *         {@link setMaxResults} and {@link setDefaultMaxResults}
 *   <li> ordering support (often required when doing paging)
 *  </ul>
 *  
 * Example on how to use this (with paging, ordering and criteria): <br> <br>
 *  
 *  <code>
 *   <pre>
 *       // code fragments taken from HibernateKeywordDaoTest 
 *   
 *       query = new QueryObject();
 *      
 *       // criteria is deliberately a bit noisy
 *       query.addCriteria(
 *           or(and(not(new ComparisonCriteria("name","Ghost","!=","String")), 
 *                  (or(not(like("name", "%host%")),
 *                      like("name", "%host%"))))));
 *                      
 *      query.addOrder(Order.desc("name"));
 *      query.setMaxResults(2);
 *      query.setFirstResult(4);
 *      
 *      // dao is typically a generic dao implementation
 *      list = dao.findByQuery(query);
 *    </pre>
 *  </code>
 * <a> 
 * Sample uses in EL4J: {@link GenericDao}, {@link CriteriaTransformer} <br> <br>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Philipp Oser (POS)
 */
public class QueryObject implements Serializable {
    /**
     * The bean class the query object is for.
     */
    private Class<?> m_beanClass;
    
    /**
     * The criterias for this query. (They are logically connected with
     *  AND).
     */
    private AndCriteria m_criterias = new AndCriteria();
    
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
     * Adds the given criteria. The criterias are combined via
     *  "AND" (it's a logical conjunction of Criterias).
     *  This method can be used with one or n Criteria(s). 
     * 
     * @param criteria Is the criteria to add.
     */
    public void addCriteria(Criteria... criteria) {
        Reject.ifNull(criteria);
        if (criteria != null) {
            for (Criteria c : criteria) {
                m_criterias.add(c);
            }
        }
    }
    
    /**
     * Adds the given criterias.
     * 
     * @param criterias Are the criterias to add. The criterias are combined via
     *  "AND" (it's a logical conjunction of Criterias).
     * @deprecated please use the more versatile {@link addCriteria} method
     */
    public void addCriterias(Criteria... criterias) {
        Reject.ifNull(criterias);
        for (int i = 0; i < criterias.length; i++) {
            Criteria criteria = criterias[i];
            m_criterias.add(criteria);
        }
    }
    
    /**
     * @return Returns a list of criterias (all criterias must be valid
     *   for this query object (they are combined with AND)).
     */
    public List<Criteria> getCriteriaList() {
        return m_criterias.getCriterias();
    }
    
    /**
     * @return Returns an array of criterias. (all criterias must be true
     *   for this query (they are combined with AND)).
     */
    public Criteria[] getCriterias() {
        List<Criteria> crits = getCriteriaList(); 
        return crits.toArray(new Criteria[0]);
    }
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "QueryObject [Type: "+getBeanClass()+
                           " Query: "+m_criterias.getSqlWhereCondition()+"]";
    }
    
    public AndCriteria getAndCriterias () {
        return m_criterias;
    }
    
    ///////// paging support ////////////////////
    
    /**
     *  Constant (=-1) to use for property firstResult. 
     * NO_CONSTRAINT means we do not constrain anything
     */ 
    public static final int NO_CONSTRAINT = -1;    
    
    /**
     * Can be updated via setter {@link setDefaultMaxResults}
     */
    static int s_defaultMaxResults = 100;
    
    protected int m_firstResult;
    
        
    /**
     * Default value is s_defaultMaxResults
     */
    protected int m_maxResults = s_defaultMaxResults;

    List<Order> m_orderConstraints = new ArrayList<Order>();
    
    /**
     * What is the id of the first result we want to get back?
     * By default there is no constraint on the first result.
     * @param firstResult
     */
    public void setFirstResult(int firstResult) {
        m_firstResult = firstResult;
    }
     
    /**
     * How many results do we want to get back at most?
     *  (The default can be set via the {@link setDefaultMaxResults}
     *   method). It defaults to 100.
     * @param maxResults
     */
    public void setMaxResults(int maxResults) {
        m_maxResults = maxResults;
    }

    /**
     * @see setFirstResult
     * @return
     */
    public int getFirstResult() {
        return m_firstResult;
    }

    /**
     * @see setMaxResults
     * @return
     */    
    public int getMaxResults() {
        return m_maxResults;
    }

    /**
     * @see setDefaultMaxResults
     * @return
     */
    public static int getDefaultMaxResults() {
        return s_defaultMaxResults;
    }

    /**
     * How many results shall we return by default?
     *   Defaults to 100.
     * @param defaultMaxResults
     */
    public static void setDefaultMaxResults(int defaultMaxResults) {
        s_defaultMaxResults = defaultMaxResults;
    }
 
    /**
     * Add an ordering constraint (particularly useful
     *  when doing paging) <br> <br>
     *   Example usage: <br>
     *    <code> query.addOrder(Order.desc("name")); </code>
     * @param order
     */
    public void addOrder(Order order){
        m_orderConstraints.add(order);
    }

    /**
     * Get the list of all defined ordering constraints.
     * @return
     */
    public List<Order> getOrderConstraints() {
        return m_orderConstraints;
    }
   
}
