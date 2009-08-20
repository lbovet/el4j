/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate.criteria;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;

/**
 * A utility class that allows to perform some modifications on {@link DetachedCriteria}s.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public final class DetachedCriteriaUtils {
	
	/**
	 * The hidden constructor.
	 */
	private DetachedCriteriaUtils() { }
	
	/**
	 * @param criteria    the criteria to clone
	 * @return            the cloned criteria
	 */
	public static DetachedCriteria clone(DetachedCriteria criteria) {
		return (DetachedCriteria) SerializationUtils.clone(criteria);
	}
	
	/**
	 * @param criteria    the criteria to manipulate
	 * @param orders      the orders to remove. If no orders are provided, all orders will be removed
	 * @return            the criteria
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria removeOrders(DetachedCriteria criteria, Order... orders) {
		List<OrderEntry> orderEntries = (List<OrderEntry>) getCriteriaField(criteria, "orderEntries");
		orderEntries.clear();
		
		return criteria;
	}
	
	/**
	 * @param criteria    the criteria whose projection should be removed
	 * @return            the "clean" criteria
	 */
	public static DetachedCriteria removeProjection(DetachedCriteria criteria) {
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		return criteria;
	}
	
	/**
	 * @param criteria    the criteria whose projection should be removed
	 * @return            the "clean" criteria
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria removeCriterionEntries(DetachedCriteria criteria) {
		List<Criterion> criterionEntries = (List<Criterion>) getCriteriaField(criteria, "criterionEntries");
		criterionEntries.clear();
		
		return criteria;
	}
	
	/**
	 * @param criteria    the criteria whose criterion entries to get
	 * @return            a list of criterion entries
	 */
	@SuppressWarnings("unchecked")
	public static List<Criterion> getCriterionEntries(DetachedCriteria criteria) {
		return (List<Criterion>) getCriteriaField(criteria, "criterionEntries");
	}
	
	/**
	 * @param criteria     the criteria to get the field
	 * @param fieldName    the field name
	 * @return             the value stored in that field
	 */
	private static Object getCriteriaField(DetachedCriteria criteria, String fieldName) {
		try {
			Field implField = DetachedCriteria.class.getDeclaredField("impl");
			implField.setAccessible(true);
			CriteriaImpl impl = (CriteriaImpl) implField.get(criteria);
			
			Field orderField = CriteriaImpl.class.getDeclaredField(fieldName);
			orderField.setAccessible(true);
			
			return orderField.get(impl);
		} catch (Exception e) {
			throw new IllegalStateException("Implementation of DetachedCriteria or CriteriaImpl has changed!");
		}
	}
}
