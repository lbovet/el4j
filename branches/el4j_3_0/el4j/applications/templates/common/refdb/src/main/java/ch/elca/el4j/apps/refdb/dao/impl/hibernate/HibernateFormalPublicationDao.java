package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;

/**
 *
 * DAO for formal publications which is using Hibernate.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 */
@AutocollectedGenericDao("formalPublicationDao")
public class HibernateFormalPublicationDao
	extends GenericHibernateReferenceDao<FormalPublication, Integer>
	implements FormalPublicationDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FormalPublication> findByQuery(QueryObject query)
		throws DataAccessException {

		DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(
			query, FormalPublication.class);

		// HACK! IncludeCriteria are handled in the search method. In a future
		// version, this will be replaced by an easier solution, where the
		// IncludeCriteria will be treated by the CriteriaTransformer class.

		List criteriaList = query.getCriteriaList();
		List currentList = new ArrayList();

		if (containsIncludeCriteria(criteriaList)) {

			Iterator it = criteriaList.iterator();
			AbstractCriteria currentCriterion;
			List resultList = new ArrayList();

			while (it.hasNext()) {
				currentCriterion = (AbstractCriteria) it.next();
				if (currentCriterion instanceof IncludeCriteria) {
					hibernateCriteria.createCriteria(
						((IncludeCriteria) currentCriterion).getField()).add(
							Expression.eq("key",
								((IncludeCriteria) currentCriterion)
								.getIntegerValue()));
					currentList = getConvenienceHibernateTemplate()
						.findByCriteria(hibernateCriteria);
					resultList.add(currentList);
					hibernateCriteria = CriteriaTransformer.transform(query,
						FormalPublication.class);
				}
			}
			Iterator it2 = resultList.iterator();
			currentList = (List) it2.next();
			List nextList = null;
			while (it2.hasNext()) {
				nextList = (List) it2.next();
				currentList.retainAll(nextList);
			}

			// Executed if the query does not include any IncludeCriteria
		} else {
			currentList = getConvenienceHibernateTemplate().findByCriteria(
				hibernateCriteria);
		}

		Iterator it3 = currentList.iterator();
		FormalPublication currentPublication;
		List finalResult = new ArrayList();
		while (it3.hasNext()) {
			currentPublication = (FormalPublication) it3.next();
			finalResult.add(currentPublication);
		}
		
		return finalResult;
	}

}
