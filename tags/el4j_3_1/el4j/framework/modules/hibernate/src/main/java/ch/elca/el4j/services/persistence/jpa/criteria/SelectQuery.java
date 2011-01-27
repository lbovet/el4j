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

import java.util.List;

/**
 * Specifies the retrieval methods applicable to a standard SELECT query.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST) 
 */
public interface SelectQuery {

	/**
	 * returns the results in the given range.
	 * @param firstRow first row
	 * @param maxNumOfRows max number of rows returned
	 * @return 'maxNumOfRows' records, starting from index 'firstRow'
	 */
	public List<?> getResultList(int firstRow, int maxNumOfRows);
	
	/**
	 * returns all results.
	 * @param <T> entity type
	 * @param clazz entity class
	 * @return all results.
	 */
	public <T> List<T> getResultList(Class<T> clazz);
	
	/**
	 * type-safe variant of {@link SelectQuery#getResultList(int, int)}.
	 * @param <T> entity type
	 * @param clazz entity class
	 * @param firstRow first row
	 * @param maxNumOfRows max number of rows returned
	 * @return 'maxNumOfRows' records, starting from index 'firstRow'
	 */
	public <T> List<T> getResultList(Class<T> clazz, int firstRow, int maxNumOfRows);
	
	/**
	 * @param <T> entity type
	 * @param clazz entity class
	 * @return single result entity
	 */
	public <T> T getSingleResult(Class<T> clazz);
	
}
