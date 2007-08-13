package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.GenericReferencedObjectDao;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;

/**
 * 
 * Generic DAO for referenced objects which is using Hibernate.
 * 
 * This DAO is not intended to be used directly. Only the concrete DAOs that are
 * subclasses of this generic DAO should be used directly.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateReferencedObjectDao<T, ID extends Serializable> 
    extends GenericHibernateDao<T, ID>
    implements GenericReferencedObjectDao<T, ID> {
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> getByReference(ID id) throws DataAccessException {
        String domainClassName = getPersistentClassName();
        String queryString = "from " + domainClassName + " "
            + domainClassName.toLowerCase() + " where keyToReference "
            + " = :key";
        return getConvenienceHibernateTemplate().findByNamedParam(
            queryString, "key", id);
    }

}
