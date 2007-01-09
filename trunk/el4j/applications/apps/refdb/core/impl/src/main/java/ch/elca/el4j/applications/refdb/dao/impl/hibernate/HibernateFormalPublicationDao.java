package ch.elca.el4j.applications.refdb.dao.impl.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.applications.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.applications.refdb.dom.Book;
import ch.elca.el4j.applications.refdb.dom.FormalPublication;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * DAO for formal publications which is using Hibernate.
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
public class HibernateFormalPublicationDao
    extends GenericHibernateReferenceDao<FormalPublication, Integer>
    implements FormalPublicationDao {

    /**
     * Creates a new HibernateformalPublicationDao instance.
     */
    public HibernateFormalPublicationDao() {
        setPersistentClass(FormalPublication.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<FormalPublication> findAll() throws DataAccessException {
        List<FormalPublication> result = getConvenienceHibernateTemplate()
            .find("from FormalPublication formalPublication left join "
                + "fetch formalPublication.keywords");
        
        List finalResult = new ArrayList();
        for (FormalPublication currentPublication : result) {
            if (!(currentPublication instanceof Book)) {
                finalResult.add(currentPublication);
            } 
        }
        return finalResult;
    }
    
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
            List nextList = new ArrayList();
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
            if (!(currentPublication instanceof Book)) {
                finalResult.add(currentPublication);
            }
        }
        return finalResult;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<FormalPublication> getByName(String name)
        throws DataAccessException {
        Reject.ifEmpty(name);
        String queryString = "from FormalPublication formalPublication "
            + "where name = :name";
        List<? extends FormalPublication> result
            = getConvenienceHibernateTemplate()
                .findByNamedParam(queryString, "name", name);
        
        List<FormalPublication> finalResult 
            = new ArrayList<FormalPublication>();
        for (FormalPublication currentPublication : result) {
            if (!(currentPublication instanceof Book)) {
                getConvenienceHibernateTemplate().initialize(
                    currentPublication.getKeywords());
                finalResult.add(currentPublication);
            }
        }
        return finalResult;
    }
    
}
