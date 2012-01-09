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
package ch.elca.el4j.tests.services.persistence.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;

import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.services.persistence.jpa.criteria.SortOrder;

/**
 * Test {@link QueryBuilder}.
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @author Simon Stelling (SST)
 * @author Huy Hung Nguyen (HUN)
 */
public class QueryBuilderTest {

	public void testCorrectSql(String expectedSQL, String position, Double salMin, Double salMax, List<String> depIds) {
		QueryBuilder builder = QueryBuilder.select("dep.NAME", "emp.name");
		QueryBuilder subQueryBuilder = QueryBuilder.select("1");
		subQueryBuilder.from("Dual ").startAnd().ifNotNull("1 <> {p}", 2).end().endBuilder();

		builder.from("DEPARTMENT dep").innerJoin("EMPLOYEE emp", "dep.ID = emp.DEPARTMENT_ID").innerJoin(
			"UNIVERSITY unv", "unv.ID = dep.UNV_ID").startOr().ifNotNull("emp.position = {p}", position).startAnd()
			.ifNotNull("emp.SALARY > {p}", salMin).ifNotNull("emp.SALARY < {p}", salMax).end().exist(subQueryBuilder)
			.ifNotNull("dep.ID in {p}", (Serializable) depIds).end()

			.orderBy(SortOrder.ASCENDING, "dep.ID").endBuilder();
		String actualQuery = builder.toString();

		String expectedSQLProcessed = StringUtils.deleteWhitespace(expectedSQL).toUpperCase().trim();
		actualQuery = StringUtils.deleteWhitespace(actualQuery).toUpperCase().trim();

		Assert.assertEquals(expectedSQLProcessed, actualQuery);

		// builder.applyNativeSelect(getEntityManager()).getResultList();
	}

	@Test
	public void testCorrectSql1() {
		String expectedSQL1 = "SELECT dep.NAME, emp.NAME" + " FROM DEPARTMENT dep"
			+ " JOIN EMPLOYEE emp ON (dep.ID = emp.DEPARTMENT_ID)" + " JOIN UNIVERSITY unv ON (unv.ID = dep.UNV_ID)"
			+ " WHERE emp.POSITION = {p}" + "    OR (emp.SALARY > {p} AND emp.SALARY < {p})"
			+ "    OR EXISTS (SELECT 1 FROM DUAL WHERE 1 <> {p}) ORDER BY DEP.ID ASCENDING";

		testCorrectSql(expectedSQL1, "pos", new Double(200), new Double(200), null);
	}

	@Test
	public void testCorrectSql2() {
		String expectedSQL2 = "SELECT dep.NAME, emp.NAME" + " FROM DEPARTMENT dep"
			+ " JOIN EMPLOYEE emp ON (dep.ID = emp.DEPARTMENT_ID)" + " JOIN UNIVERSITY unv ON (unv.ID = dep.UNV_ID)"
			+ " WHERE EXISTS (SELECT 1 FROM DUAL WHERE 1 <> {p})" + " ORDER BY DEP.ID ASCENDING";

		testCorrectSql(expectedSQL2, "", null, null, new ArrayList<String>());
	}

	@Test
	public void testCorrectSql3() {
		String expectedSQL3 = "SELECT dep.NAME, emp.NAME" + " FROM DEPARTMENT dep"
			+ " JOIN EMPLOYEE emp ON (dep.ID = emp.DEPARTMENT_ID)" + " JOIN UNIVERSITY unv ON (unv.ID = dep.UNV_ID)"
			+ " WHERE EXISTS (SELECT 1 FROM DUAL WHERE 1 <> {p}) OR dep.ID in {p}" + " ORDER BY DEP.ID ASCENDING";

		List<String> idList = new ArrayList<String>();
		idList.add("1");

		testCorrectSql(expectedSQL3, null, null, null, idList);
	}
	
	public QueryBuilder createNewQueryBuilder() {
		QueryBuilder builder = QueryBuilder.select("dep.NAME", "emp.name");
		QueryBuilder subQueryBuilder = QueryBuilder.select("1");
		subQueryBuilder.from("Dual ").startAnd().ifNotNull("1 <> {p}", 2).end().endBuilder();
		
		builder.from("DEPARTMENT dep").innerJoin("EMPLOYEE emp", "dep.ID = emp.DEPARTMENT_ID").innerJoin(
				"UNIVERSITY unv", "unv.ID = dep.UNV_ID").startOr().ifNotNull("emp.position = {p}", "pos").startAnd()
				.ifNotNull("emp.SALARY > {p}", 200).ifNotNull("emp.SALARY < {p}", 300).end().exist(subQueryBuilder)
				.ifNotNull("dep.ID in {p}", (Serializable) null).end()
				.orderBy(SortOrder.ASCENDING, "dep.ID").endBuilder();
		
		return builder;
	}
	
	@Test
	public void testDuplicateQueryBuilder() {
		QueryBuilder builder = createNewQueryBuilder();
		QueryBuilder builderCopy = new QueryBuilder(builder);
		
		Assert.assertTrue(builder.equals(builderCopy));
	}
	
	@Test
	public void testIndependencyDuplicatedQB() {
		QueryBuilder builder = createNewQueryBuilder();
		QueryBuilder builderCopy = new QueryBuilder(builder);
		
		builder = builder.clearOrderBy();
		Assert.assertTrue(!builder.equals(builderCopy));
	}

}
