package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.GenericReferenceDao;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 *
 * Generic DAO for references which is using Hibernate.
 *
 * This DAO is not intended to be used directly. Only the concrete DAOs that are
 * subclasses of this generic DAO should be used directly. To access or modify
 * reference objects which can be of different types, the service layer should
 * be used.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateReferenceDao<T extends Reference,
	ID extends Serializable> extends GenericHibernateDao<T, ID>
	implements GenericReferenceDao<T, ID> {
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getByName(String name) throws DataAccessException,
		DataRetrievalFailureException {
		Reject.ifEmpty(name);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.like("name", name));
		// TODO MZE: Is this really needed although lazy loading is already
		// switched off?
//        for (T reference : result) {
//            getConvenienceHibernateTemplate().initialize(
//                reference.getKeywords());
//        }
		return getConvenienceHibernateTemplate().findByCriteria(criteria);
		


	}
	
	/**
	 * Checks whether the given list contains at least one IncludeCriteria.
	 * @param criteriaList list of criteria
	 * @return true if the list contains at least one IncludeCriteria
	 */
	protected boolean containsIncludeCriteria(List<AbstractCriteria>
		criteriaList) {
		for (AbstractCriteria c : criteriaList) {
			if (c instanceof IncludeCriteria) {
				return true;
			}
		}
		return false;
	}
	
	/**
	* {@inheritDoc}
	*/
	@SuppressWarnings("unchecked")
	public boolean referenceExists(ID id) {
		String domainClassName = getPersistentClassName();
		String domainClassNameLowerCase = domainClassName.toLowerCase();
		String queryString = "select " + domainClassNameLowerCase + ".id from "
			+ domainClassName + " " + domainClassNameLowerCase;
		List<ID> idList = getConvenienceHibernateTemplate().find(queryString);
		if (idList.contains(id)) {
			return true;
		}
		return false;
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> search(String[] fields, String critera) throws DataAccessException, DataRetrievalFailureException {
		return  getConvenienceHibernateTemplate().search(getPersistentClass(), fields, critera);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void createHibernateSearchIndex() throws DataAccessException, DataRetrievalFailureException {
		getConvenienceHibernateTemplate().createHibernateSearchIndex(getAll());
	}
	
	/** {@inheritDoc} */
	public List<Reference> getAllReferencesByKeywords(List<Keyword> keywords) throws DataAccessException {
		if (!CollectionUtils.isEmpty(keywords)) {
			final String query = "select distinct r from Reference r left join r.keywords as keyword "
				+ "where keyword in (:list)";
			return getConvenienceHibernateTemplate().findByNamedParam(query, "list", keywords);
		} else {
			return new ArrayList<Reference>();
		}
	}
}
