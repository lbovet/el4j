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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
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
public final class QueryBuilder implements Expression, SelectQuery, CountQuery {

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
	 * TODO: where is this used?
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
	 * TODO: What's this?
	 */
	private List<QueryBuilder> subUnionQueryBuilders = new ArrayList<QueryBuilder>();
	
	/**
	 * WHERE Predicate as String. 
	 */
	private String where;
	
	/**
	 * Is this query completed and can therefore be used as an inner query?
	 */
	private boolean completed = false;

	/**
	 * Parameters of the SQL query, except those of the SELECT clause.
	 */
	private List<Object> bodyParameters = new ArrayList<Object>();
	
	/**
	 * Parameters of the SELECT clause. They are omitted if a count query is executed.
	 */
	private List<Object> selectParameters = new ArrayList<Object>();

	/**
	 * TODO: What's this?
	 */
	private Query query;

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
	 * get SelectParameters.
	 * 
	 * @return the selectParameters
	 */
	public List<Object> getSelectParameters() {
		return selectParameters;
	}
	
	/**
	 * Creates a new Query with the given SELECT clause.
	 * @param select select clause
	 * @return a new query
	 */
	public static QueryBuilder select(String... select) {
		return new QueryBuilder(select);
	}

	/**
	 * TODO: what's this?
	 */
	public QueryBuilder addSelectParameter(Object... parameters) {
		for (Object parameter : parameters) {
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
	 * TODO: what's this?
	 */
	private QueryBuilder join(String join, String type, String on, Object... params) {
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
	 * @param join
	 * @param on
	 * @param params
	 * @return
	 */
	public QueryBuilder innerJoin(String join, String on, Object... params) {
		return join(join, INNER_JOIN, on, params);
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join
	 * @param on
	 * @param params
	 * @return
	 */
	public QueryBuilder innerJoin(String join, String on) {
		return innerJoin(join, on, new Object[] {});
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join
	 * @param on
	 * @param params
	 * @return
	 */
	public QueryBuilder leftJoin(String join, String on, Object... params) {
		return join(join, LEFT_JOIN, on, params);
	}

	public QueryBuilder leftJoin(String join, String on) {
		return leftJoin(join, on, new Object[] {});
	}

	public QueryBuilder union(QueryBuilder builder) {
		if (builder != null) {
			subUnionQueryBuilders.add(builder);
		}
		return this;
	}

	/**
	 * Only used for native sql.
	 * 
	 * @param join
	 * @param on
	 * @param params
	 * @return
	 */
	public QueryBuilder rightJoin(String join, String on, Object... params) {
		return join(join, RIGHT_JOIN, on, params);
	}

	public QueryBuilder rightJoin(String join, String on) {
		return rightJoin(join, on, null, new Object[] {});
	}

	/**
	 * TODO: what are the parameters here?
	 * adds "type join ON ( on )" to the joins field.
	 * @param join what?
	 * @param type what?
	 * @param on what?
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
	 * @param join
	 * @param cond
	 * @return
	 */
	public QueryBuilder join(String join) {
		doJoin(join, INNER_JOIN, null);
		return this;
	}

	/**
	 * Dont need 'on', used for JPQL.
	 * 
	 * @param join
	 * @param cond
	 * @return
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

		public ConditionList<T> ifNotNull(String condition, Object parameter) {
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
		public ConditionList<T> ifNotNull(String queryPredicate, Object parameter, boolean cond) {
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
				parent.append(StringUtils.join(conds, operation));
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
	 * TODO: what's this?
	 * @param parameter
	 * @return
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

	// TODO: do we need these? 
//	public QuerySelect applyNativeSelect(EntityManager entityManager) {
//		createJpaQuery(getQuerySelectStr(), entityManager, true, null);
//		return this;
//	}
//
//	public QuerySelect applyNativeSelect(EntityManager entityManager, Class<?> resultClass) {
//		createJpaQuery(getQuerySelectStr(), entityManager, true, resultClass);
//		return this;
//	}
//
//	public QueryCount applyNativeCount(EntityManager entityManager) {
//		createJpaQuery(getQueryCountStr(), entityManager, true, null);
//		return this;
//	}

//	public <T extends DefaultSearchCriteria, U extends AbstractEntity> SearchResult<T, U> getSearchResult(
//		Class<U> resultClass, EntityManager entityManager, T criteria) {
//
//		SearchResult<T, U> searchResult = new SearchResult<T, U>(criteria);
//		int total = applyCount(entityManager).getCount();
//		searchResult.setTotal(total);
//		if (total > 0) {
//			List<U> items = applySelect(entityManager).getResultList(resultClass, criteria.getFirstRowIdx(),
//				criteria.getNumberOfRows());
//			searchResult.getItems().addAll(items);
//		}
//
//		return searchResult;
//	}

	
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
	public void setParameters(List<Object> parameters) {
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
	 * TODO: this seems wrong. what's this for?
	 */
	@Override
	public void append(String query) {
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
	 * TODO: what's this?
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
	public List<Object> getBodyParameters() {
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
}
