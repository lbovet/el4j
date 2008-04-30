package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.GenericFileDao;
import ch.elca.el4j.util.codingsupport.Reject;


/**
 * Generic DAO for files or file descriptors which is using
 * Hibernate.
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
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateFileDao<T, ID extends Serializable> 
    extends GenericHibernateReferencedObjectDao<T, ID>
    implements GenericFileDao<T, ID> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> getByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
        Reject.ifEmpty(name);
        String domainClassName = getPersistentClassName();
        String queryString = "from " + domainClassName + " " 
            + domainClassName.toLowerCase() + " where name = :name"; 
        List<T> result = getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        return result;
    }
    
}

        
    

