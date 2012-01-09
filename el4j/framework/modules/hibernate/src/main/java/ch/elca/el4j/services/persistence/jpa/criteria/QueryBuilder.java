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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

/**
 * SQL Query Builder. 
 * 
 * TODO: adapt the following example such that it produces valid JPQL. (no ON clause, but AS instead)
 * <p>
 * Example query definition:
 * <pre>
 *      QueryBuilder builder = QueryBuilder.select("dep.NAME", "emp.name");
 *      QueryBuilder subQueryBuilder = QueryBuilder.select("1");
 *      subQueryBuilder.from("Dual ").startAnd().ifNotNull("1 <> {p}", 2).end().endBuilder();
 *
 *      builder.from("DEPARTMENT dep")
 *          .innerJoin("EMPLOYEE emp", "dep.ID = emp.DEPARTMENT_ID")
 *          .innerJoin("UNIVERSITY unv", "unv.ID = dep.UNV_ID")
 *      .startOr()
 *          .ifNotNull("emp.position = {p}", "pos")
 *          .startAnd()
 *              .ifNotNull("emp.SALARY > {p}", 200)
 *              .ifNotNull("emp.SALARY < {p}", 300)
 *          .end()
 *          .exist(subQueryBuilder)
 *          .ifNotNull("dep.ID in {p}", depIds)
 *      .end()
 *      .orderBy(SortOrder.ASC, "dep.ID").endBuilder();
 * </pre>
 * The above query is equivalent to the following SQL:
 * <pre>
 *     SELECT dep.NAME, emp.NAME
 *     FROM DEPARTMENT dep
 *         JOIN emp ON (dep.ID = emp.DEPARTMENT_ID)
 *         JOIN UNIVERSITY unv ON (unv.ID = dep.UNV_ID)
 *     WHERE emp.POSITION = {p}
 *         OR (emp.SALARY > {p} AND emp.SALARY < {p})" 
 *         OR EXISTS (SELECT 1 FROM DUAL WHERE 1 <> {p})
 *     ORDER BY DEP.ID ASC";
 * </pre>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 * @author Huy Hung Nguyen (HUN)
 */
public final class QueryBuilder implements Expression, SelectQuery, CountQuery, Cloneable {

	private static final String PARAM_KEY = "{p}";
	private static final String PARAM = "param";
	
	/** SQL AND. */
	private static final String AND = " AND ";
	
	/** SQL OR. */
	private static final String OR = " OR ";
	
	/** SQL COMMA. */
	private static final String COMMA = " , ";

	/** SQL INNER JOIN. */
	private static final String INNER_JOIN = "join";
	
	/** SQL LEFT JOIN. */
	private static final String LEFT_JOIN = "left outer join";
	
	/** SQL RIGHT JOIN. */
	private static final String RIGHT_JOIN = "right outer join";
	
	/** SQL TRUE. */
	private static final String DB_TRUE_VALUE = "T";
	
	/** SQL FALSE. */
	private static final String DB_FALSE_VALUE = "F";

	/**
	 * The SELECT statement of the query.
	 */
	private String selectQuery;
	
	/**
	 * FROM elements.
	 */
	private List<String> froms = new ArrayList<String>();
	
	/**
	 * ORDER BY restrictions.
	 */
	private List<String> orderBy = new ArrayList<String>();
	
	/**
	 * JOIN elements.
	 */
	private List<String> joins = new ArrayList<String>();
	
	/**
	 * Hierarchical subqueries. Refer to the example in the javadoc of the class.
	 */
	private List<QueryBuilder> subUnionQueryBuilders = new ArrayList<QueryBuilder>();
	
	/**
	 * WHERE predicate as String. 
	 */
	private String where;
	
	/**
	 * Is this query completed and can therefore be used as an inner query?
	 */
	private boolean completed = false;

	/**
	 * Parameters of the SQL query, except those of the SELECT clause.
	 * 
	 * We require Serializable in order to be able to "clone" these parameters.
	 *  (The basic types such as Number subtypes or String do not implement Cloneable, so we use
	 *   serialization for cloning).
	 */
	private List<Serializable> bodyParameters = new ArrayList<Serializable>();
	
	/**
	 * Parameters of the SELECT clause. They are omitted if a count query is executed.
	 */
	private List<Serializable> selectParameters = new ArrayList<Serializable>();

	/**
	 * A transitory JPA query object. Is created on the entityManager at each operation
	 *  on the db. 
	 */
	private Query query;

	/**
	 * The bean class the query object is specified for.
	 */
	private Class<?> m_beanClass;
	
	/**
	 * Specifies the query object for a specific class.
	 * @param beanClass Is the bean class this query object is made for.
	 */
	public QueryBuilder(Class<?> beanClass) {
		m_beanClass = beanClass;
	}
	
	/**
	 * get the bean class this query object is created for.
	 * @return the bean class specified for the query.
	 */
	public Class<?> getBeanClass() {
		return m_beanClass;
	}
	
	/** hidden constructor. 
	 * @param select select */
	private QueryBuilder(String... select) {
		this.selectQuery = StringUtils.join(select, COMMA);
	}

	/** hidden constructor. 
	 * @param query query */
	private QueryBuilder(QueryBuilder... query) {

	}

	/**
	 * Copy constructor (use it to "clone" the object)
	 * @param original the {@link QueryBuilder} to be duplicated
	 */
	public QueryBuilder(QueryBuilder original) {
		
		completed = original.completed;
		m_beanClass = original.m_beanClass;
		selectQuery = original.selectQuery;
		where = original.where;
		
		froms = new ArrayList<String>(original.froms); 
		joins = new ArrayList<String>(original.joins);
		orderBy = new ArrayList<String>(original.orderBy);
		
		for (Serializable el : original.bodyParameters) {
			bodyParameters.add((Serializable) SerializationUtils.clone(el));
		}

		for (Serializable el : original.selectParameters) {
			selectParameters.add((Serializable) SerializationUtils.clone(el));
		}

		for (QueryBuilder el : original.subUnionQueryBuilders) {
			subUnionQueryBuilders.add(new QueryBuilder(el));
		}		
	}

	/** {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bodyParameters == null) ? 0 : bodyParameters.hashCode());
		result = prime * result + (completed ? 1231 : 1237);
		result = prime * result + ((froms == null) ? 0 : froms.hashCode());
		result = prime * result + ((joins == null) ? 0 : joins.hashCode());
		result = prime * result
				+ ((m_beanClass == null) ? 0 : m_beanClass.hashCode());
		result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
		result = prime
				* result
				+ ((selectParameters == null) ? 0 : selectParameters.hashCode());
		result = prime * result
				+ ((selectQuery == null) ? 0 : selectQuery.hashCode());
		result = prime
				* result
				+ ((subUnionQueryBuilders == null) ? 0 : subUnionQueryBuilders
						.hashCode());
		result = prime * result + ((where == null) ? 0 : where.hashCode());
		return result;
	}

	/** {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QueryBuilder other = (QueryBuilder) obj;
		if (bodyParameters == null) {
			if (other.bodyParameters != null) {
				return false;
			}
		} else {
			for (Object bodyObject : bodyParameters) {
				if(bodyObject != null) {
					// the default equals method considers arrays as Object.
					if (bodyObject.getClass().isArray()) {
						Arrays.equals((Object[]) bodyObject, (Object[]) other.bodyParameters.get(bodyParameters.indexOf(bodyObject)));
					} else {
						bodyObject.equals(other.bodyParameters.get(bodyParameters.indexOf(bodyObject)));
					}
				}
			}
		}
		if (completed != other.completed) {
			return false;
		}
		if (froms == null) {
			if (other.froms != null) {
				return false;
			}
		} else if (!froms.equals(other.froms)) {
			return false;
		}
		if (joins == null) {
			if (other.joins != null) {
				return false;
			}
		} else if (!joins.equals(other.joins)) {
			return false;
		}
		if (m_beanClass == null) {
			if (other.m_beanClass != null) {
				return false;
			}
		} else if (!m_beanClass.equals(other.m_beanClass)) {
			return false;
		}
		if (orderBy == null) {
			if (other.orderBy != null) {
				return false;
			}
		} else if (!orderBy.equals(other.orderBy)) {
			return false;
		}
		if (selectParameters == null) {
			if (other.selectParameters != null) {
				return false;
			}
		} else if (!selectParameters.equals(other.selectParameters)) {
			return false;
		}
		if (selectQuery == null) {
			if (other.selectQuery != null) {
				return false;
			}
		} else if (!selectQuery.equals(other.selectQuery)) {
			return false;
		}
		if (subUnionQueryBuilders == null) {
			if (other.subUnionQueryBuilders != null) {
				return false;
			}
		} else if (!subUnionQueryBuilders.equals(other.subUnionQueryBuilders)) {
			return false;
		}
		if (where == null) {
			if (other.where != null) {
				return false;
			}
		} else if (!where.equals(other.where)) {
			return false;
		}
		return true;
	}

	/**
	 * get the SELECT parameters.
	 * @return the SELECT parameters
	 */
	public List<Serializable> getSelectParameters() {
		return selectParameters;
	}
	
	/**
	 * Creates a new query with the given SELECT clause.
	 * @param select select clause
	 * @return a new query
	 */
	public static QueryBuilder select(String... select) {
		return new QueryBuilder(select);
	}

	/**
	 * Adds one or more SELECT clauses to the query.
	 * @param parameters the select parameters
	 * @return the query with the added select parameters
	 */
	public QueryBuilder addSelectParameter(Serializable... parameters) {
		for (Serializable parameter : parameters) {
			if (isParameterAccepted(parameter)) {
				selectParameters.add(parameter);
			} else {
				throw new RuntimeException("Parameters for select statement must be not null or not empty.");
			}
		}
		return this;
	}

	/**
	 * Specifies on which classes the query is based.
	 * @param entityClasses entities' classes
	 * @return this
	 */
	public QueryBuilder from(Class<?>... entityClasses) {
		String[] classNames = new String[entityClasses.length];
		for (int i = 0; i < entityClasses.length; i++) {
			classNames[i] = entityClasses[i].getSimpleName();
		}

		return from(classNames);
	}

	/**
	 * Specifies on which tables the query is based.
	 * @param from tables
	 * @return this
	 */
	public QueryBuilder from(String... from) {
		froms.addAll(Arrays.asList(from));
		return this;
	}

	/**
	 * same as {@link QueryBuilder#from(String...)} except that
	 * from clause is added iff condition evaluates to true.
	 * @param fromExp from string to add
	 * @param cond predicate
	 * @return this
	 */
	public QueryBuilder fromIf(String fromExp, boolean cond) {
		if (cond) {
			return from(fromExp);
		}
		return this;
	}

	/**
	 * Checks whether existent parameters are accepted (non-empty Collection or non-null String) and if so, 
	 * adds a JOIN clause to the query.
	 * @param join join
	 * @param type type
	 * @param on ON clause
	 * @param params parameters
	 * @return this
	 */
	private QueryBuilder join(String join, String type, String on, Serializable... params) {
		if (ArrayUtils.isEmpty(params)) {
			doJoin(join, type, on);
		} else {
			// have some parameters, join only if all parameters are accepted.
			boolean allParamsAccepted = true;
			for (Object param : params) {
				if (!isParameterAccepted(param)) {
					allParamsAccepted = false;
					break;
				}
			}
			if (allParamsAccepted) {
				doJoin(join, type, on);
				bodyParameters.addAll(Arrays.asList(params));
			}
		}

		return this;
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join join
	 * @param on ON clause
	 * @param params parameters
	 * @return this
	 */
	public QueryBuilder innerJoin(String join, String on, Object... params) {
		return join(join, INNER_JOIN, on, params);
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join join
	 * @param on ON clause
	 * @param params parameters
	 * @return this
	 */
	public QueryBuilder innerJoin(String join, String on) {
		return innerJoin(join, on, new Object[] {});
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join join
	 * @param on ON clause
	 * @param params parameters
	 * @return this
	 */
	public QueryBuilder leftJoin(String join, String on, Object... params) {
		return join(join, LEFT_JOIN, on, params);
	}

	/**
	 * Same as leftJoin except that a new object is created as parameter.
	 * @param join join
	 * @param on ON clause
	 * @return this
	 */
	public QueryBuilder leftJoin(String join, String on) {
		return leftJoin(join, on, new Object[] {});
	}

	
	/**
	 *  CAVEAT: currently there are no tests for this. Use with care.
	 * @param builder another QueryBuilder
	 * @return this
	 */
	public QueryBuilder union(QueryBuilder builder) {
		if (builder != null) {
			subUnionQueryBuilders.add(builder);
		}
		return this;
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join join
	 * @param on ON clause
	 * @param params parameters
	 * @return this
	 */
	public QueryBuilder rightJoin(String join, String on, Object... params) {
		return join(join, RIGHT_JOIN, on, params);
	}

	/**
	 * Same as rightJoin except that a new object is created as parameter.
	 * @param join join
	 * @param on ON clause
	 * @return this
	 */
	public QueryBuilder rightJoin(String join, String on) {
		return rightJoin(join, on, null, new Object[] {});
	}

	/**
	 * adds "type join ON ( on )" to the joins field.
	 * @param join join
	 * @param type type 
	 * @param on ON clause
	 */
	private void doJoin(String join, String type, String on) {
		String joinStr = " " + type + " " + join;
		if (StringUtils.isNotBlank(on)) {
			joinStr += " on (" + on + ")";
		}
		joins.add(joinStr);
	}

	/**
	 * like {@link QueryBuilder#joinIf(String, String, boolean, Object...)} but join is
	 * performed iff cond evaluates to true.
	 * @param join join
	 * @param on on clause
	 * @param cond predicate
	 * @param params params
	 * @return this
	 */
	public QueryBuilder joinIf(String join, String on, boolean cond, Object... params) {
		if (cond) {
			return join(join, INNER_JOIN, on, params);
		}
		return this;
	}

	/**
	 * Dont need 'on', used for JPQL.
	 * 
	 * @param join join
	 * @param cond predicate
	 * @return this
	 */
	public QueryBuilder join(String join) {
		doJoin(join, INNER_JOIN, null);
		return this;
	}

	/**
	 * Dont need 'on', used for JPQL.
	 * 
	 * @param join join
	 * @param cond
	 * @return this
	 */
	public QueryBuilder joinIf(String join, boolean cond) {
		if (cond) {
			join(join);
		}
		return this;
	}

	/**
	 * marks the beginning of a list of predicates to be ANDed.
	 * @return condition object
	 */
	public ConditionList<QueryBuilder> startAnd() {
		return new ConditionList<QueryBuilder>(QueryBuilder.this, AND);
	}
	
	/**
	 * marks the beginning of a list of predicates to be ORed.
	 * @return condition object
	 */
	public ConditionList<QueryBuilder> startOr() {
		return new ConditionList<QueryBuilder>(QueryBuilder.this, OR);
	}

	/**
	 * Mark this query as completed.
	 * 
	 * TODO: what is the role of completed vs. non-completed queries?
	 * @return this
	 */
	public QueryBuilder endBuilder() {
		completed = true;
		return this;
	}

	/**
	 * appends the given column to the ordering restrictions
	 * iff not already ordered by the given column.
	 * 
	 * @param sortOrder ASC or DESC
	 * @param orderBy the column to order by
	 * @return this
	 */
	public QueryBuilder orderBy(SortOrder sortOrder, String orderBy) {
		if (StringUtils.isNotBlank(orderBy) && !isAlreadyOrderBy(orderBy)) {
			this.orderBy.add(orderBy + " " + sortOrder);
		}
		return this;
	}

	/**
	 * is there already an ordering restriction for the given
	 * column?
	 * 
	 * @param column column
	 * @return answer
	 */
	private boolean isAlreadyOrderBy(final String column) {
		for (String order : orderBy) {
			if (StringUtils.startsWithIgnoreCase(order, column + " ")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Appends given column to the ordering restrictions iff
	 * cond evaluates to true.
	 * 
	 * @param sortOrder ASC or DESC
	 * @param orderBy column
	 * @param cond predicate
	 * @return this
	 */
	public QueryBuilder orderByIf(SortOrder sortOrder, String orderBy, boolean cond) {
		if (cond) {
			return orderBy(sortOrder, orderBy);
		}

		return this;
	}

	/**
	 * Condition of QueryBuilder.
	 * 
	 * @author LLT
	 * @param <T>
	 */
	public class ConditionList<T extends Expression> implements Expression {
		
		/**
		 * the QueryBuilder or ConditionList which started this condition list.
		 * is returned when .end() is called.
		 */
		protected T parent;
		
		/**
		 * AND or OR.
		 */
		private String operation;
		
		/**
		 * The conditions included in this ConditionList.
		 */
		private List<String> conds = new ArrayList<String>();

		/**
		 * Sole constructor.
		 * @param parent is returned when .end() is called.
		 * @param operation AND or OR
		 */
		public ConditionList(T parent, String operation) {
			this.parent = parent;
			this.operation = operation;
		}

		/**
		 * adds condition to this condition list.
		 * @param predicate condition
		 * @return this
		 */
		public ConditionList<T> ifCond(String predicate) {
			conds.add(predicate);
			return this;
		}

		/**
		 * adds queryPredicate iff cond evaluates to true.
		 * @param queryPredicate the query predicate
		 * @param cond condition
		 * @return this
		 */
		public ConditionList<T> ifCond(String queryPredicate, boolean cond) {
			if (cond) {
				return ifCond(queryPredicate);
			}
			return this;
		}

		public ConditionList<T> ifNotNull(String condition, Serializable parameter) {
			if (isParameterAccepted(parameter)) {
				getBodyParameters().add(parameter);
				conds.add(condition);
			}
			return this;
		}

		/**
		 * same as {@link ConditionList#ifNotNull(String, Object)} iff cond evaluates to true.
		 * @param queryPredicate query's predicate
		 * @param parameter parameter
		 * @param cond cond
		 * @return this
		 */
		public ConditionList<T> ifNotNull(String queryPredicate, Serializable parameter, boolean cond) {
			if (cond) {
				return ifNotNull(queryPredicate, parameter);
			}
			return this;
		}

		/**
		 * same as {@link ConditionList#exist(QueryBuilder)} iff cond evaluates to true.
		 * @param subQueryBuilder subquery
		 * @param cond cond
		 * @return this
		 */
		public ConditionList<T> existIf(QueryBuilder subQueryBuilder, boolean cond) {
			if (cond) {
				return exist(subQueryBuilder);
			}
			return this;
		}

		/**
		 * @param subQueryBuilder subquery
		 * @param obj obj which is tested for nullness.
		 * @return existIf(subQueryBuilder, obj != null)
		 */
		public ConditionList<T> existIfNotNull(QueryBuilder subQueryBuilder, Object obj) {
			return existIf(subQueryBuilder, obj != null);
		}

		/**
		 * adds an EXISTS ( subquery ) to this condition.
		 * @param subQueryBuilder subquery
		 * @return this
		 */
		public ConditionList<T> exist(QueryBuilder subQueryBuilder) {
			if (!subQueryBuilder.completed) {
				throw new RuntimeException("Sub query must be completed first before being set in");
			}
			bodyParameters.addAll(subQueryBuilder.getSelectParameters());
			bodyParameters.addAll(subQueryBuilder.getBodyParameters());
			conds.add(" exists (" + subQueryBuilder.getQuerySelectStr() + ")");

			return this;
		}

		/**
		 * marks the end of an ANDed or ORed list of predicates.
		 * @return the QueryBuilder which started this condition
		 */
		public T end() {
			if (!conds.isEmpty()) {
				if(parent instanceof QueryBuilder) {
					((QueryBuilder) parent).appendWhere(StringUtils.join(conds, operation));
				}
				else {
					parent.append(StringUtils.join(conds, operation));
				}
			}
			return parent;
		}

		/**
		 * Marks the beginning of a new ANDed ConditionList.
		 * @return the nested ConditionList
		 */
		public ConditionList<ConditionList<T>> startAnd() {
			return new ConditionList<ConditionList<T>>(this, AND);
		}
		
		/**
		 * Marks the beginning of a new ORed ConditionList.
		 * @return the nested ConditionList
		 */
		public ConditionList<ConditionList<T>> startOr() {
			return new ConditionList<ConditionList<T>>(this, OR);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(String query) {
			if (!query.isEmpty()) {
				conds.add("(" + query + ")");
			}
		}
	}

	/**
	 * Checks whether the parameter is a non-blank String or a non-empty Collection.
	 * @param parameter the parameter to test
	 * @return true if the parameter is legal, false otherwhise 
	 */
	@SuppressWarnings("unchecked")
	private boolean isParameterAccepted(Object parameter) {
		if (parameter == null) {
			return false;
		}
		boolean paramAccepted = true;
		if (parameter instanceof String) {
			paramAccepted = (StringUtils.isNotBlank((String) parameter));
		} else if (parameter instanceof Collection) {
			paramAccepted = (CollectionUtils.isNotEmpty((Collection) parameter));
		}
		return paramAccepted;
	}

	/**
	 * creates select query with the given EntityManager and stores the prepared query
	 * which is then executed through the getResultList() methods.
	 * @param entityManager em to use
	 * @return this
	 */
	public SelectQuery applySelect(EntityManager entityManager) {
		createJpaQuery(getQuerySelectStr(), entityManager, false, null, false);
		return this;
	}

	/**
	 * creates select query with the given EntityManager and stores the prepared query
	 * which is then executed through the getResultList() methods.
	 * @param entityManager em to use
	 * @param resultClass class of result
	 * @return this
	 */
	public SelectQuery applySelect(EntityManager entityManager, Class<?> resultClass) {
		createJpaQuery(getQuerySelectStr(), entityManager, false, resultClass, false);
		return this;
	}

	/**
	 * creates count query with the given EntityManager and stores the prepared query
	 * which is then executed through the getCount() method.
	 * @param entityManager em to use
	 * @return this
	 */
	public CountQuery applyCount(EntityManager entityManager) {
		createJpaQuery(getQueryCountStr(), entityManager, false, null, true);
		
		return this;
	}

	
	private void createJpaQuery(String queryString, EntityManager entityManager, boolean nativeQuery,
		Class<?> resultClass, boolean isCountQuery) {
		initQuery(queryString, entityManager, nativeQuery, resultClass);
		applyParameters(getAllParameters(queryString, isCountQuery), nativeQuery);
	}

	/**
	 * initializes the 'query' field.
	 * @param queryString query string
	 * @param entityManager em
	 * @param nativeQuery whether to use the em.createNativeQuery or em.createQuery
	 * @param resultClass class of the result
	 */
	private void initQuery(String queryString, EntityManager entityManager, boolean nativeQuery, Class<?> resultClass) {
		String queryStr = positionizeParameters(queryString);
		if (nativeQuery) {
			if (resultClass != null) {
				query = entityManager.createNativeQuery(queryStr, resultClass);
			} else {
				query = entityManager.createNativeQuery(queryStr);
			}
		} else {
			query = entityManager.createQuery(queryStr);
		}
	}

	private List<Object> getAllParameters(String queryString, boolean isCountQuery) {
		// do not apply select param in count query
		List<Object> allParameters = new ArrayList<Object>();
		if (isCountQuery) {
			allParameters.addAll(selectParameters);
		}
		for (QueryBuilder unionQuery : this.subUnionQueryBuilders) {
			allParameters.addAll(unionQuery.getSelectParameters());
			allParameters.addAll(unionQuery.getBodyParameters());
		}
		allParameters.addAll(bodyParameters);
		return allParameters;
	}

	private void applyParameters(List<Object> allParameters, boolean nativeQuery) {
		for (int i = 0; i < allParameters.size(); i++) {
			Object param = null;
			Object parameter = allParameters.get(i);
			if (nativeQuery && parameter instanceof Boolean) {
				param = ((Boolean) parameter).booleanValue() ? DB_TRUE_VALUE : DB_FALSE_VALUE;
			} else {
				param = parameter;
			}
			query.setParameter(PARAM + i, param);
		}
	}

	private String positionizeParameters(String queryString) {
		if (StringUtils.isBlank(queryString)) {
			return queryString;
		}
		String result = queryString;
		int paramCount = 0;
		int curIdx = result.indexOf(PARAM_KEY);
		while (curIdx >= 0) {
			result = StringUtils.replaceOnce(result, PARAM_KEY, ":" + PARAM + String.valueOf(paramCount));
			paramCount++;
			curIdx = result.indexOf(PARAM_KEY, curIdx + 1);
		}

		return result;
	}

	/**
	 * Set parameters of the query.
	 * 
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(List<Serializable> parameters) {
		this.bodyParameters = parameters;
	}

	/**
	 * gets at most maxNumOfRows results, first one being firstRow.
	 * 
	 * @param <T> entity type
	 * @param clazz entity class
	 * @param firstRow first row to receive
	 * @param maxNumOfRows max number of rows to receive
	 * @return retrieved entities
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getResultList(Class<T> clazz, int firstRow, int maxNumOfRows) {
		query.setFirstResult(firstRow);
		query.setMaxResults(maxNumOfRows);
		return (List<T>) query.getResultList();
	}

	/**
	 * @see QueryBuilder#getResultList(Class, int, int).
	 * @param firstRow first row
	 * @param maxNumOfRows maxNumOfRows
	 * @return list of results.
	 */
	@Override
	public List<?> getResultList(int firstRow, int maxNumOfRows) {
		query.setFirstResult(firstRow);
		query.setMaxResults(maxNumOfRows);
		return query.getResultList();
	}

	/**
	 * returns all entities matching the query.
	 * @param <T> entity type
	 * @param clazz entity class
	 * @return results
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getResultList(Class<T> clazz) {
		return (List<T>) query.getResultList();
	}

	/**
	 * @return query.getResultList()
	 */
	public List<?> getRawResultList() {
		return query.getResultList();
	}

	/**
	 * @param <T> entity type
	 * @param clazz entity class
	 * @return the unique entity matching the query
	 */
	@Override
	public <T> T getSingleResult(Class<T> clazz) {
		List<T> resultList = getResultList(clazz);
		if (CollectionUtils.isEmpty(resultList)) {
			return null;
		}

		if (resultList.size() > 1) {
			throw new NonUniqueResultException("Result for query '" + query + "' must contain exactly one item");
		}

		return resultList.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		Number count = (Number) query.getSingleResult();
		return count.intValue();
	}

	/**
	 * appends a WHERE clause
	 * @param query WHERE clause
	 */
	public void appendWhere(String query) {
		where = query;
	}

	/**
	 * @return the select query as string.
	 */
	public String toString() {
		return getQuerySelectStr();
	}

	/**
	 * @return the count query as string.
	 */
	private String getQueryCountStr() {
		return getQueryStr(true);
	}

	/**
	 * @return the select query as string.
	 */
	private String getQuerySelectStr() {
		return getQueryStr(false);
	}

	/**
	 * @param isCount is this a count query?
	 * @return the query as string
	 */
	private String getQueryStr(boolean isCount) {
		StringBuilder result = new StringBuilder();
		
		String selected;
		String orderStr;
		if (isCount) {
			selected = "count(*)";
			orderStr = "";
		} else {
			selected = selectQuery;
			if (orderBy.isEmpty()) {
				orderStr = "";
			} else {
				orderStr = " ORDER BY " + StringUtils.join(orderBy, COMMA);
			}
		}
		
		String fromStr;
		if (CollectionUtils.isEmpty(subUnionQueryBuilders)) {
			fromStr = StringUtils.join(froms, COMMA);
		} else {
			fromStr = getUnionStr();
		}
		
		result.append(" SELECT ");
		result.append(selected);
		result.append(" FROM ");
		result.append(fromStr);
		result.append(" ");
		result.append(StringUtils.join(joins, " "));
		if (StringUtils.isNotBlank(where)) {
			result.append(" WHERE ");
			result.append(where);
		}
		result.append(orderStr);
		
		return result.toString();
	}

	/**
	 * get the UNION clause as a String.
	 * @return the UNION statement
	 */
	private String getUnionStr() {
		StringBuilder result = new StringBuilder();
		List<String> subQueries = new ArrayList<String>();
		for (QueryBuilder builder : this.subUnionQueryBuilders) {
			String queryStr = builder.toString();
			// TODO CODEREVIEW IT1 NLN improve: remove ORDER BY
			int pos = queryStr.indexOf("ORDER BY");
			if (pos != -1) {
				queryStr = queryStr.substring(0, pos - 1);
			}
			subQueries.add(queryStr);
		}
		result.append(StringUtils.join(subQueries, " UNION "));
		return "(" + result.toString() + ")";
	}

	/**
	 * Get all attached parameters.
	 * 
	 * @return the parameters
	 */
	public List<Serializable> getBodyParameters() {
		return bodyParameters;
	}

	/**
	 * drops all ordering constraints.
	 * @return this
	 */
	public QueryBuilder clearOrderBy() {
		this.orderBy.clear();
		return this;
	}

	@Override
	public void append(String query) {
		throw new NotImplementedException();
	}
}
