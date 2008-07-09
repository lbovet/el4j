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
package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;

/**
 * This interface extends {@link ConvenienceGenericDao} with query methods using
 * {@link DetachedCriteria}s.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>     the domain object type
 * @param <ID>    the id of the domain object to find
 *
 * @author Stefan Wismer (SWI)
 */
public interface ConvenienceGenericHibernateDao<T, ID extends Serializable>
	extends ConvenienceGenericDao<T, ID> {
	
	/**
	 * @param entity    The domain object to save or update
	 * @return          The saved or updated object
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	/**
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		int firstResult, int maxResults) throws DataAccessException;
	
	/**
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @return                     the number of objects that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findCountByCriteria(DetachedCriteria)
	 */
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	
	/**
	 * @return    the convenience Hibernate template
	 */
	public ConvenienceHibernateTemplate getConvenienceHibernateTemplate();
	
	/**
	 * @return    the default {@link Order} to order the results
	 */
	public Order[] getDefaultOrder();

	/**
	 * @param defaultOrder    the default {@link Order} to order the results
	 */
	public void setDefaultOrder(Order... defaultOrder);
}
