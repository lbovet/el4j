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
package ch.elca.el4j.apps.keyword.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;

/**
 *
 * This interface represents a DAO for the keyword domain object.
 * It defines the methods which are specific to the keyword domain object.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 */
public interface KeywordDao
	extends ConvenienceGenericHibernateDao<Keyword, Integer> {
	
	/**
	 * Get keyword by name.
	 *
	 * @param name
	 *            Is the name of a keyword.
	 * @return Returns the desired keyword.
	 * @throws DataAccessException
	 *             If general data access problem occurred.
	 * @throws DataRetrievalFailureException
	 *             If keyword could not be retrieved.
	 */
	public Keyword getKeywordByName(String name)
		throws DataAccessException, DataRetrievalFailureException;
	
	/**
	 * Copy the state of the given object onto the persistent object
	 * with the same identifier.
	 * 
	 * Similar to saveOrUpdate, but never associates the given
	 * object with the current Hibernate Session. In case of a new entity,
	 * the state will be copied over as well.
	 * 
	 * Note that merge will not update the identifiers
	 * in the passed-in object graph (in contrast to TopLink)! Consider
	 * registering Spring's IdTransferringMergeEventListener if
	 * you would like to have newly assigned ids transferred to the original
	 * object graph too.
	 * 
	 * @param entity the object to merge with the corresponding persistence instance
	 * @return the updated, registered persistent instance
	 */
	public Object merge(Object entity);
}
