/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.jpa.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.AndCriteria;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.services.search.criterias.NotCriteria;
import ch.elca.el4j.services.search.criterias.OrCriteria;

/**
 * 
 * Transforms {@link QueryObject}s into JPA {@link CriteriaQuery}s.
 *
 * @param <T> entity type
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class CriteriaTransformer<T> {

	/**
	 * private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(CriteriaTransformer.class);
	
	/**
	 * the class of the entity which the criteria is for.
	 */
	private Class<T> entityClass;
	
	/**
	 * The root where fields are looked up.
	 * Needs to be ThreadLocal because we get it from the CriteriaQuery which is created in <code>transform</code>.
	 */
	private ThreadLocal<Root<T>> root = new ThreadLocal<Root<T>>();
	
	/**
	 * The criteriaBuilder to use.
	 */
	private CriteriaBuilder cb;
	
	/**
	 * Sole Constructor.
	 * @param entityClass the class of the entities the QueryObjects should search
	 * @param cb CriteriaBuilder to use
	 */
	public CriteriaTransformer(Class<T> entityClass, CriteriaBuilder cb) {
		this.entityClass = entityClass;
		this.cb = cb;
	}
	
	/**
	 * Transforms a EL4J QueryObject into a JPA CriteriaQuery.
	 * @param query query object
	 * @return the CriteriaQuery
	 */
	public CriteriaQuery<T> transform(QueryObject query) {

		CriteriaQuery<T> result = cb.createQuery(entityClass);
		root.set(result.from(entityClass));
		
		// build predicates
		List<Criteria> el4jCriteriaList = query.getCriteriaList();
		List<Predicate> restrictions = new ArrayList<Predicate>();
		for (Criteria c : el4jCriteriaList) {
			restrictions.add(criteria2predicate(c));
		}
		
		// build order constraints
		List<Order> orders = new ArrayList<Order>();
		for (ch.elca.el4j.services.search.criterias.Order o : query.getOrderConstraints()) {
			orders.add(el4jOrder2jpaOrder(o));
		}
		result.orderBy(orders.toArray(new Order[0]));
				
		result.select(root.get()).where(restrictions.toArray(new Predicate[0]));
		
		return result;
	}
	
	/**
	 * recursively transforms EL4J {@link Criteria} trees into JPA {@link Predicate} trees.
	 * @param criteria the criteria to transform
	 * @throws IllegalArgumentException if unknown predicates are found
	 * @return the equivalent Predicate
	 */
	private Predicate criteria2predicate(Criteria criteria) throws IllegalArgumentException {
		Predicate result;
		
		if (criteria instanceof OrCriteria) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Criteria c : ((OrCriteria) criteria).getCriterias()) {
				predicates.add(criteria2predicate(c));
			}
			
			result = cb.or(predicates.toArray(new Predicate[0]));
		} else if (criteria instanceof AndCriteria) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Criteria c : ((AndCriteria) criteria).getCriterias()) {
				predicates.add(criteria2predicate(c));
			}
			
			result = cb.and(predicates.toArray(new Predicate[0]));
		} else if (criteria instanceof NotCriteria) {
			Criteria innerCriteria = ((NotCriteria) criteria).getCriteria();
			
			result = cb.not(criteria2predicate(innerCriteria));
		} else if (criteria instanceof AbstractCriteria) {
			AbstractCriteria abstractCrit = (AbstractCriteria) criteria;
			
			String fieldName = abstractCrit.getField();
			Object criteriaValue = abstractCrit.getValue();

			if (criteria instanceof LikeCriteria) {
				LikeCriteria likeCriteria = (LikeCriteria) criteria;
				if (likeCriteria.isCaseSensitive()) {
					result = cb.like(root.get().<String>get(fieldName), (String) criteriaValue);
				} else {
					result = cb.like(cb.upper(root.get().<String>get(fieldName)), 
						cb.upper(cb.literal((String) criteriaValue))); 
				}
			} else if (criteria instanceof ComparisonCriteria) {
				result = comparisonCriteria2predicate((ComparisonCriteria) criteria);
			} else {
				throw new IllegalArgumentException("unknown AbstractCriteria " + abstractCrit);
			}
		} else {
			throw new IllegalArgumentException("unknown criteria " + criteria);
		}
		
		return result;
	}
	
	/**
	 * transforms a ComparisonCriteria into a JPA predicate.
	 * @param cc the ComparisonCriteria to transform
	 * @return the equivalent Predicate
	 */
	@SuppressWarnings("unchecked")
	private Predicate comparisonCriteria2predicate(ComparisonCriteria cc) {
		String operator = cc.getOperator();
		String fieldName = cc.getField();
		Object criteriaValue = cc.getValue();
		Expression<Comparable<Object>> fieldValue = null;
		try {
			fieldValue = root.get().<Comparable<Object>>get(fieldName);
		} catch (ClassCastException e) {
			// given field is not comparable to given value
			throw new IllegalArgumentException(e);
		}
		
		Predicate result;
		
		if (operator.equals("=")) {
			result = cb.equal(root.get().get(fieldName), criteriaValue);
		} else if (operator.equals("<")) {
			result = cb.lessThan(fieldValue, (Comparable<Object>) criteriaValue);
		} else if (operator.equals("<=")) {
			result = cb.lessThanOrEqualTo(fieldValue, (Comparable<Object>) criteriaValue);
		} else if (operator.equals(">")) {
			result = cb.greaterThan(fieldValue, (Comparable<Object>) criteriaValue);
		} else if (operator.equals(">=")) {
			result = cb.greaterThanOrEqualTo(fieldValue, (Comparable<Object>) criteriaValue);
		} else if (operator.equals("!=")) {
			result = cb.notEqual(root.get().get(fieldName), criteriaValue);
		} else {
			throw new IllegalArgumentException("Unknown comparison operator: " + operator);
		}
		
		return result;
	}
	
	/**
	 * translates EL4J's Order into JPA's Order.
	 * @param order the order to translate
	 * @return the translated order
	 */
	private Order el4jOrder2jpaOrder(ch.elca.el4j.services.search.criterias.Order order) {
		if (order.isAscending()) {
			return cb.asc(root.get().get(order.getPropertyName()));
		} else {
			return cb.desc(root.get().get(order.getPropertyName()));
		}
	}
	
}
