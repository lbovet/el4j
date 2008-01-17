package ch.elca.j4persist.hibernate.dao;


import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * This is a convenience class for the Hibernate template.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/ConvenienceHibernateTemplate.java $",
 *    "$Revision:1059 $",
 *    "$Date:2006-09-04 13:33:11 +0000 (Mo, 04 Sep 2006) $",
 *    "$Author:mathey $"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class ConvenienceHibernateTemplate extends HibernateTemplate {
    
    
    /**
     * for paging: what is the id of the first result to return?
     *  NO_CONSTRAINT means we do not constrain anything 
     */
    int m_firstResult = QueryObject.NO_CONSTRAINT;
    
    
    /**
     * Constructor.
     * @param sessionFactory SessionFactory to create Sessions
     */
    public ConvenienceHibernateTemplate(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    /**
     * Retrieves the persistent instance given by its identifier in a strong
     * way: does the same as the <code>get(Class, java.io.Serializable)</code>
     * method, but throws a <code>DataRetrievalException</code> instead of
     * <code>null</code> if the persistent instance could not be found.
     * 
     * @see HibernateTemplate#get(Class, java.io.Serializable)
     * @param entityClass
     *            The class of the object which should be returned.
     * @param id
     *            An identifier of the persistent instance
     * @param objectName
     *            Name of the persistent object type.
     * @return the persistent instance
     * @throws org.springframework.dao.DataAccessException
     *             in case of Hibernate errors
     * @throws org.springframework.dao.DataRetrievalFailureException
     *             in case the persistent instance is null
     */
    public Object getByIdStrong(Class entityClass, Serializable id,
        final String objectName) throws DataAccessException,
        DataRetrievalFailureException {

        Reject.ifNull(id, "The identifier must not be null.");
        Reject.ifEmpty(objectName, "The name of the persistent object type "
            + "must not be empty.");
        Object result = get(entityClass, id);
        
        if (result == null || !(entityClass.isInstance(result))) {
            String message = "The desired " + objectName + " could not be"
                + " retrieved.";
            CoreNotificationHelper.notifyDataRetrievalFailure(message,
                objectName);
        }
        return result;
    }
    
    /**
     * Retrieves a persistent instance with the help of a parameterized query:
     * does the same as the
     * <code>findByNamedParam(String, String, Object)</code> method, but
     * returns a persistent instance instead of a list of persistent objects and
     * throws a <code>DataRetrievalException</code> if the returned list does
     * not contain exactly one element.
     * 
     * @see HibernateTemplate#findByNamedParam(String, String, Object)
     * @param queryString
     *            The string corresponding to HQL query
     * @param paramName
     *            The name of the parameter
     * @param value
     *            The value of the parameter
     * @param objectName
     *            Name of the persistent object type.
     * @return the persistent instance returned by the query
     * @throws org.springframework.dao.DataAccessException
     *             in case of Hibernate errors
     * @throws org.springframework.dao.DataRetrievalFailureException
     *             in case the list of persistent instances is empty, or if it
     *             contains more than one object
     */
    public Object findByNamedParamStrong(String queryString,
        String paramName, Object value, final String objectName)
        throws DataAccessException, DataRetrievalFailureException {
        
        Reject.ifEmpty(paramName);
        Reject.ifNull(value);
        Reject.ifEmpty(objectName, "The name of the persistent object type "
            + "must not be empty.");
        List result = findByNamedParam(queryString, paramName, value);
        if (result.size() != 1) {
            String message = "";
            if (result.isEmpty()) {
                message = "The desired " + objectName
                    + " does not exist.";
            } else if (result.size() > 0) {
                message = "The query resulted in more than one persistent "
                    + " instance.";
            }
            CoreNotificationHelper.notifyDataRetrievalFailure(message,
                objectName);
        }
        return result.get(0);
    }
    
    /**
     * Saves or updates the given persistent instance in a strong way: does the
     * same as the <code>saveOrUpdate(Object)</code> method, but throws a more
     * specific <code>OptimisticLockingFailureException</code> in the case of
     * an optimistic locking failure.
     * 
     * @see HibernateTemplate#saveOrUpdate(Object)
     * @param entity
     *            the persistent entity to save or update
     * @param objectName
     *            Name of the persistent object type.
     * @throws DataAccessException
     *             in case of Hibernate errors
     * @throws OptimisticLockingFailureException
     *             in case optimistic locking fails
     */
    public void saveOrUpdateStrong(Object entity, final String objectName)
        throws DataAccessException, OptimisticLockingFailureException {
    	
        Reject.ifNull(entity);
        Reject.ifEmpty(objectName, "The name of the persistent object type "
            + "must not be empty.");
        try {
            saveOrUpdate(entity);
        } catch (HibernateOptimisticLockingFailureException holfe) {
            String message = "The current " + objectName + " was modified or"
                + " deleted in the meantime.";
            CoreNotificationHelper.notifyOptimisticLockingFailure(
                message, objectName, holfe);
        }
    }
    
    /**
     * Deletes the persistent instance given by its identifier in a strong way:
     * first, the persistent instance is retrieved with the help of the
     * identifier. If it exists, it will be deleted, otherwise a
     * <code>DataRetrievalFailureException</code> will be thrown.
     * 
     * @see HibernateTemplate#delete(Object)
     * @param entityClass
     *            The class of the object which should be deleted.
     * @param id
     *            The identifier of the persistent instance to delete
     * @param objectName
     *            Name of the persistent object type.
     * @throws org.springframework.dao.DataRetrievalFailureException
     *             in case the persistent instance to delete is null
     */
    public void deleteStrong(Class entityClass, Serializable id,
        final String objectName) throws DataRetrievalFailureException {
        Reject.ifEmpty(objectName, "The name of the persistent object type "
            + "must not be empty.");
        Object toDelete = null;
        try {
            toDelete = getByIdStrong(entityClass, id, objectName);
        } catch (DataRetrievalFailureException e) {
            String message = "The current " + objectName + " was "
                + "deleted in the meantime.";
            CoreNotificationHelper.notifyOptimisticLockingFailure(
                message, objectName, null);
        }
        delete(toDelete);
    }
    
    
    
    /**
     * Count number of results of a search.
     * @param criteria
     * @return
     * @throws DataAccessException
     */
    public int findCountByCriteria(final DetachedCriteria criteria)
       throws DataAccessException {

        Assert.notNull(criteria, "DetachedCriteria must not be null");
        Object result =  execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Criteria executableCriteria = criteria.getExecutableCriteria(session);
                executableCriteria.setProjection( Projections.rowCount() );
                
                prepareCriteria(executableCriteria);

                return executableCriteria.uniqueResult();
            }
        }, true);
                      
        return (Integer)result; 
    }
    
    public int getFirstResult() {
        return m_firstResult;
    }

    public void setFirstResult(int firstResult) {
        m_firstResult = firstResult;
    }    
    
    /**
     * Overload parent class to support also a constraint
     *  of the id of the first result to load. 
     * {@inheritDoc}
     * 
     *  TODO shall we drop this and instead use the
     *   findByCriteria(DetachedCriteria,int,int) method? 
     */
    protected void prepareQuery(Query queryObject) {
        super.prepareQuery(queryObject);
        
        if (getFirstResult() != QueryObject.NO_CONSTRAINT){
            queryObject.setFirstResult(getFirstResult());
        }
        
    }
}
