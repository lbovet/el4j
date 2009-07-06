/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.tests.services.persistence.generic.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.search.QueryObject;

/**
 * Just has all empty methods of the generic dao interface
 *  (for testing convenience)
 *  
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author pos
 */
public class DummyDao<T> implements GenericDao<T>{

	private Class<T> m_persistentClass;

	@SuppressWarnings("unchecked")
		public DummyDao() {
			this.m_persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		}
	
	public void delete(Collection<T> entities)
			throws OptimisticLockingFailureException, DataAccessException {
	}

	public List<T> findByQuery(QueryObject q) throws DataAccessException {
		return null;
	}

	public int findCountByQuery(QueryObject query) throws DataAccessException {
		return 0;
	}

	public List<T> getAll() throws DataAccessException {
		return null;
	}

	public Class<T> getPersistentClass() {
		return m_persistentClass;
	}
	
	public void setPersistentClass(Class<T> c) {
		m_persistentClass = c;
	}

	public T refresh(T entity) throws DataAccessException,
			DataRetrievalFailureException {
		return null;
	}

	public T saveOrUpdate(T entity) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		return null;
	}

	public T reload(T entity) throws DataAccessException, DataRetrievalFailureException {
		return null;
	}

}
