/*
 * Project: KnowHow
 *
 * Copyright 2008 by ELCA Informatik AG
 * Steinstrasse 21, CH-8036 Zurich
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatik AG ("Confidential Information"). You
 * shall not disclose such "Confidential Information" and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */

package ch.elca.el4j.services.persistence.jpa.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.services.persistence.jpa.helper.JpaHelperImpl;
import ch.elca.el4j.services.persistence.jpa.util.QueryException;;

/**
 * Query object returned by DataService.
 *	@param <T> The type parameter.
 *
 * @author David Bernhard (dab)
 */
public class JpaQuery<T> {

	/** The logger. */
	private static final Logger s_log
		= LoggerFactory.getLogger(JpaQuery.class);

	/** Order of a sort. */
	public enum Order {
		/** Ascending order. */
		ASCENDING,
		/** Descending order. */
		DESCENDING
	}

	/** The possible relations (eq, neq ...). */
	public enum Relation {
		/** equals. */
		EQ,
		/** Not equals. */
		NE,
		/** Less or equal. */
		LE,
		/** Greater or equal. */
		GE,
		/** Less than. */
		LT,
		/** Greater than. */
		GT;
	};

	/** The conditions (used for warning message if there is a failure). */
	private Map<String, Object> conditions;

	/** The cirteria object for this query. */
	private DetachedCriteria criteria;

	/**
	 * Flag to indicate an exception should be thrown if
	 * no data is returned.
	 */
	private boolean failOnNull;

	/**
	 * Flag to indicate returned instances must be detached.
	 */
	private boolean detach;

	/** The domain class this query is for. */
	private Class<T> domainClass;

	/** The entity manager. */
	private EntityManager em;

	/**
	 * Create the query object.
	 * @param cls The class to query for.
	 * @param context The context.
	 * @param ds The data service.
	 */
	public JpaQuery(Class<T> cls,  JpaHelperImpl ds) {
		domainClass = cls;
		criteria = DetachedCriteria.forClass(cls);
		criteria.setResultTransformer(
			CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		failOnNull  = false;
		conditions = new HashMap<String, Object>();
		em = ds.getEntityManager();
	}

	/**
	 * Add a restriction.
	 * @param key The property name.
	 * @param value The property value.
	 * @return this
	 */
	public JpaQuery<T> where(String key, Object value) {
		if (value == null) {
			return whereNull(key);
		}
		criteria.add(Restrictions.eq(key, value));
		conditions.put(key, value);
		return this;
	}

	/**
	 * Add a hibernate criterion.
	 * @param c The criterion.
	 * @return this
	 */
	public JpaQuery<T> where(Criterion c) {
		criteria.add(c);
		return this;
	}

	/**
	 * Add a restriction.
	 * @param key The property name.
	 * @param r The relation type.
	 * @param value The property value.
	 * @return this
	 */
	public JpaQuery<T> where(String key, Relation r, Object value) {
		switch (r) {
			case EQ:
				criteria.add(Restrictions.eq(key, value));
				break;
			case NE:
				criteria.add(Restrictions.ne(key, value));
				break;
			case LE:
				criteria.add(Restrictions.le(key, value));
				break;
			case LT:
				criteria.add(Restrictions.lt(key, value));
				break;
			case GE:
				criteria.add(Restrictions.ge(key, value));
				break;
			case GT:
				criteria.add(Restrictions.gt(key, value));
				break;
			default:
				// This should never happen as we're switching on an enum.
				throw new QueryException("Not yet implemented: " + r);
		}
		return this;
	}

	/**
	 * Add an is-null restriction.
	 * @param key The property name.
	 * @return this
	 */
	public JpaQuery<T> whereNull(String key) {
		criteria.add(Restrictions.isNull(key));
		conditions.put(key, "(null)");
		return this;
	}

	/**
	 * Add an is--not-null restriction.
	 * @param key The property name.
	 * @return this
	 */
	public JpaQuery<T> whereNotNull(String key) {
		criteria.add(Restrictions.isNotNull(key));
		conditions.put(key, "(not null)");
		return this;
	}

	/**
	 * Add an order.
	 * @param key The property to order by.
	 * @param order The order to use.
	 * @return this
	 */
	public JpaQuery<T> order(String key, Order order) {
		if (order == Order.ASCENDING) {
			criteria.addOrder(org.hibernate.criterion.Order.asc(key));
		} else {
			criteria.addOrder(org.hibernate.criterion.Order.desc(key));
		}
		return this;
	}

	/**
	 * Add a data extent (a property to be eagerly fetched).
	 * @param names The names of the properties to fetch.
	 * @return this
	 */
	public JpaQuery<T> extent(String... names) {
		for (String name : names) {
			criteria.setFetchMode(name, FetchMode.JOIN);
		}
		return this;
	}

	/**
	 * Fail if no elements are returned.
	 * @return this.
	 */
	public JpaQuery<T> failOnNull() {
		failOnNull = true;
		return this;
	}

	/**
	 * Detach all returned elements.
	 * @return this.
	 */
	public JpaQuery<T> detach() {
		detach = true;
		return this;
	}

	/**
	 * Execute the query.
	 * @return The query result.
	 */
	public List<T> execute() {
		Session session = session();

		// Cast required because return type is "List", ok because we know
		// what type of elements are in it.
		@SuppressWarnings("unchecked")
		List<T> list = criteria.getExecutableCriteria(session).list();
		if (failOnNull && list.isEmpty()) {
			throw new NoResultException("Empty list returned, fail on "
				+ "null is set.");
		}

		if (detach) {
			for (T element : list) {
				session.evict(element);
			}
		}

		return list;
	}

	/**
	 * Execute the query, expecting a unique element.
	 * If none is found, return null. If several are found,
	 * throw an exception.
	 * @return The unique element matching the criteria.
	 */
	public T executeUnique() {
		Session session = session();

		// Cast required because return type is "List", ok because we know
		// what type of elements are in it.
		@SuppressWarnings("unchecked")
		List<T> list = criteria.getExecutableCriteria(session).list();

		if (list.size() == 0) {
			if (failOnNull) {
				throw new NoResultException("No element found");
			} else {
				return null;
			}
		} else if (list.size() == 1) {
			T element = list.get(0);
			if (detach) {
				session.evict(element);
			}
			return element;
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Multiple matches (");
			builder.append(list.size());
			builder.append(") for query: ");
			builder.append("SELECT FROM ");
			builder.append(domainClass.getSimpleName());
			for (String key : conditions.keySet()) {
				builder.append(" WHERE ");
				builder.append(key);
				builder.append(" = '");
				builder.append(conditions.get(key));
				builder.append("'");
			}
			builder.append(";");

			throw new NonUniqueResultException(builder.toString());
		}
	}

	/**
	 * Check all elements of a list match a type and return it cast if so.
	 * Throw an exception if not.
	 * @param list The list to check.
	 * @param query The query, to use for the error message if we fail.
	 * @param params The parameters, to use for the error message if we fail.
	 * @return The list cast to the correct type.
	 */
	// Safe because we do a manual check.
	// Required because we get a raw list from spring.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<T> checkTypes(List list, String query, Object... params) {
		for (Object o : list) {
			if (!domainClass.isAssignableFrom(o.getClass())) {
				StringBuilder builder = new StringBuilder(
					"HQL query returned different type than expected. Wanted ");
				builder.append(domainClass.getName());
				builder.append(" Got ");
				builder.append(o.getClass().getName());
				builder.append(" Query ");
				builder.append(query);
				for (Object p : params) {
					builder.append(", ");
					builder.append(p);
				}
				throw new QueryException(builder.toString());
			}
		}

		return (List<T>) list;
	}

	/**
	 * Build a query.
	 * @param queryString The string in a query language.
	 * @param params The parameters. Indexing is numerical.
	 * @return The query object,
	 */
	private Query query(String queryString, Object... params) {
		Query query = em.createQuery(queryString);
		// Loop with index variable as we use it in setParameter.
		for (int i = 0; i < params.length; i++) {
			// Check that no null parameter was passed.
			// This is usually an error as "obj = null" as SQL/HQL will always
			// be false - correct is "is null" which doesn't take a parameter.
			if (params[i] == null) {
				throw new QueryException("Found a null parameter in a "
					+ "HQL query. This is almost certainly a mistake.");
			}

			// Parameters in the query start at 1.
			query.setParameter(i + 1, params[i]);
		}
		return query;
	}

	/**
	 * Detach an object from the session.
	 * @param object The object to detach.
	 */
	private void detach(Object object) {
		// Ok as long as we're using hibernate.
		Session session = (Session) em.getDelegate();
		session.evict(object);
	}

	/**
	 * Execute a HQL query.
	 *
 	 * Warning: You must pass "distinct" yourself if desired. Due to the
	 * nature of HQL, this is not fully typesafe - types are checked but we
	 * cannot prevent an invalid tpye in the "from" clause.
	 * @param query The query string.
	 * @param params The parameters.
	 * @return The single object, if present.
	 */
	public List<T> executeHQL(String query, Object... params) {
		Query q = query(query, params);
		List<T> list = checkTypes(q.getResultList(), query, params);

		if (failOnNull && list.isEmpty()) {
			throw new NoResultException("Empty list returned, fail on "
				+ "null is set.");
		}

		if (detach) {
			for (T element : list) {
				detach(element);
			}
		}

		return list;
	}

	/**
	 * Execute HQL and epxect a single item. Throw an exception if more than
	 * one is found.
	 * 
	 * Warning: You must pass "distinct" yourself if desired. Due to the
	 * nature of HQL, this is not fully typesafe - types are checked but we
	 * cannot prevent an invalid tpye in the "from" clause.
	 * @param query The query string.
	 * @param params The parameters.
	 * @return The single object, if present.
	 */
	public T executeHQLUnique(String query, Object... params) {

		Query q = query(query, params);
		List<T> list = checkTypes(q.getResultList(), query, params);

		if (list.size() == 0) {
			if (failOnNull) {
				throw new NoResultException("No element found");
			} else {
				return null;
			}
		} else if (list.size() == 1) {
			T element = list.get(0);
			if (detach) {
				detach(element);
			}
			return element;
		} else {
			StringBuilder builder = new StringBuilder(
				"Multiple matches for query ");
			builder.append(query);
			for (Object p : params) {
				builder.append(", ");
				builder.append(p);
			}
			throw new NonUniqueResultException(builder.toString());
		}
	}

	/**
	 * Get a session.
	 * @return The session.
	 */
	private Session session() {
		// Works as long as we use hibernate.
		Session session = (Session) em.getDelegate();
		if (!session.isOpen()) {
			// This happens during testing when we don't have a web container.
			session = session.getSessionFactory().openSession();
		}
		return session;
	}
}
