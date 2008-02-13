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
package ch.elca.el4j.services.persistence.hibernate.criteria;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.AndCriteria;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.services.search.criterias.NotCriteria;
import ch.elca.el4j.services.search.criterias.OrCriteria;
import ch.elca.el4j.services.search.criterias.Order;

/**
 * 
 * This class transforms the EL4J Criteria of a given <code>QueryObject</code>
 * into the corresponding Hibernate DetachedCriteria.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 * @author Philipp Oser (POS)
 */
public class CriteriaTransformer {

    private static Log s_logger = LogFactory.getLog(CriteriaTransformer.class);    
    
    /**
     * Hide default constructor.
     */
    protected CriteriaTransformer() { };
    
    /**
     * Transforms EL4J Criteria of the given <code>QueryObject</code> into the
     * corresponding Hibernate DetachedCriteria.
     * 
     * @param query
     *            the query object whose criteria will be transformed
     * @param domainObjectClass
     *            the class of the domain object for which the Hibernate
     *            criteria will be generated
     * @return the Hibernate criteria corresponding to the
     *         <code>QueryObject</code>'s EL4J criteria.
     */
    public static DetachedCriteria transform(QueryObject query,
        Class<?> domainObjectClass) {
        
        // Hibernate criteria for the domain object.
        DetachedCriteria hibernateCriteria
            = DetachedCriteria.forClass(domainObjectClass);
        
        // List of EL4J criteria.
        List<Criteria> el4jCriteriaList = query.getCriteriaList();
        
        // Conversion from EL4J criteria to Hibernate criteria.
        Iterator<Criteria> it = el4jCriteriaList.iterator();
                      
        while (it.hasNext()) {
            Criteria currentEl4jCriteria = (Criteria) it.next();
            
            Criterion hibernateCriterion = 
                el4jCriteria2HibernateCriterion(currentEl4jCriteria);
            if (hibernateCriterion != null) {
                hibernateCriteria.add(hibernateCriterion);
            }
        }
        
        addOrderConstraints(hibernateCriteria, query);
        
        return hibernateCriteria;
    }

    protected static void addOrderConstraints (DetachedCriteria hibernateCriteria,
        QueryObject query) {
        
        List<Order> orderConstraints = query.getOrderConstraints();
        for (Order o : orderConstraints){
            if (o.isAscending()) {
                hibernateCriteria.addOrder(org.hibernate.criterion.Order.asc(o.getPropertyName()));
            } else {
                hibernateCriteria.addOrder(org.hibernate.criterion.Order.desc(o.getPropertyName()));                
            }
        }
        
    }
    
    
    /**
     * Converts EL4J Criteria to Hibernate Criterion
     * @param criteria
     * @return the converted Criterion
     */
    protected static Criterion el4jCriteria2HibernateCriterion(Criteria criteria) {
        Criterion criterion = null;
        
        if (criteria instanceof OrCriteria) {
            Junction combination = Restrictions.disjunction();
            
            addCriteriaListToJunction(((OrCriteria)criteria).getCriterias(), combination);
            criterion = combination;
        } else if (criteria instanceof AndCriteria) {
            Junction combination = Restrictions.conjunction();
            
            addCriteriaListToJunction(((AndCriteria)criteria).getCriterias(), combination);
            criterion = combination;
        } else if (criteria instanceof NotCriteria) {
            Criteria innerCriteria = ((NotCriteria)criteria).getCriteria();
            criterion = Restrictions.not(el4jCriteria2HibernateCriterion(innerCriteria));
        } else if (criteria instanceof AbstractCriteria) {
            AbstractCriteria abstractCrit = (AbstractCriteria) criteria;
            
            String currentCriteriaField = abstractCrit.getField();
            Object currentCriteriaValue = abstractCrit.getValue();

            if (criteria instanceof LikeCriteria) {
                LikeCriteria currentEl4jLikeCriteria = (LikeCriteria) criteria;
                if (currentEl4jLikeCriteria.isCaseSensitive().booleanValue()) {
                    criterion = Expression.like(currentCriteriaField,
                        currentCriteriaValue);
                } else {
                    criterion = Expression.like(currentCriteriaField,
                        currentCriteriaValue).ignoreCase();
                }
            } else if (criteria instanceof ComparisonCriteria) {
                String operator = ((ComparisonCriteria)criteria).getOperator();
                if (operator.equals("=")){
                    criterion = Expression.eq(currentCriteriaField,
                                        currentCriteriaValue);
                } else if (operator.equals("<")){
                    criterion = Expression.lt(currentCriteriaField,
                        currentCriteriaValue);
                } else if (operator.equals("<=")){
                    criterion = Expression.le(currentCriteriaField,
                        currentCriteriaValue);
                } else if (operator.equals(">")){
                    criterion = Expression.gt(currentCriteriaField,
                        currentCriteriaValue);
                } else if (operator.equals(">=")){
                    criterion = Expression.ge(currentCriteriaField,
                        currentCriteriaValue);
                } else if (operator.equals("!=")){
                    criterion = Expression.ne(currentCriteriaField,
                        currentCriteriaValue);
                }else {
                    s_logger.info(" Operator not handled "+operator);                    
                }
                
            } else {
                s_logger.info(" Criteria not handled "+criteria);
            }
        } else {
            s_logger.info(" Criteria not handled "+criteria);
        }
        return criterion;
    }

    /**
     * @param currentEl4jCriteria
     * @param combination
     */
    protected static void addCriteriaListToJunction(
        List<Criteria> criterias, Junction combination) {
        for (Criterion c : apply2HibernateCriterion(criterias)) {
            combination.add(c);
        }
    }
    
    /** 
     * Apply operator (from functional programming) 
     * @param criterias must not be null
     * @return
     */
    protected static Criterion[] apply2HibernateCriterion(List<Criteria> criterias ){
        Criterion[] result = new Criterion[criterias.size()];
        
        for (int i = 0; i < criterias.size(); i++) {
            result[i] = el4jCriteria2HibernateCriterion(criterias.get(i));
        }
        return result;
    }    
    
}
