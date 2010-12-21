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
package ch.elca.el4j.apps.keyword.dao.impl.hibernate;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;

/**
 *
 * Implementation of the keyword DAO which is using Hibernate.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 */
@Repository("keywordDao")
public class HibernateKeywordDao
	extends GenericHibernateDao<Keyword, Integer>
	implements KeywordDao {
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Keyword getKeywordByName(String name)
		throws DataAccessException, DataRetrievalFailureException {
		
		String queryString = "from Keyword keyword where name = :name";
				
		List keywordList = getHibernateTemplate()
			.findByNamedParam(queryString, "name", name);
		if (keywordList.isEmpty()) {
			throw new DataRetrievalFailureException("The desired keyword could"
				+ " not be retrieved.");
		} else {
			return (Keyword) keywordList.get(0);
		}
	}

	/** {@inheritDoc} */
	public Object merge(Object entity) {
		return getConvenienceHibernateTemplate().merge(entity);
	}
}
