package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.LinkDao;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;

/**
 * 
 * DAO for links which is using Hibernate.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class HibernateLinkDao 
    extends GenericHibernateReferenceDao<Link, Integer> implements LinkDao {

    /**
     * Creates a new HibernateLinkDao instance.
     */
    public HibernateLinkDao() {
        setPersistentClass(Link.class);
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Link> findByQuery(QueryObject query) 
        throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer
            .transform(query,
            Link.class);

        // HACK! IncludeCriteria are handled in the search method. In a future
        // version, this will be replaced by an easier solution, where the
        // IncludeCriteria will be treated by the CriteriaTransformer class.
        
        List criteriaList = query.getCriteriaList();

        if (containsIncludeCriteria(criteriaList)) {

            Iterator it = criteriaList.iterator();
            AbstractCriteria currentCriterion;
            List resultList = new ArrayList();
            List currentList;

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
                        Link.class);
                }
            }
            Iterator it2 = resultList.iterator();
            currentList = (List) it2.next();
            List nextList = new ArrayList();
            while (it2.hasNext()) {
                nextList = (List) it2.next();
                currentList.retainAll(nextList);
            }
            return currentList;

        // Executed if the query does not include any IncludeCriteria    
        } else {
            return getConvenienceHibernateTemplate().findByCriteria(
                hibernateCriteria);
        }
    }
    
}
