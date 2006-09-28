/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.persistence.ibatis.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This is a convenience class for the sql map client template.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ConvenienceSqlMapClientTemplate extends SqlMapClientTemplate {
    /**
     * Convenience method to insert or update the given parameter object.
     * 
     * @param parameterObject
     *            Is the parameter object.
     * @param objectName
     *            Is the name of the given object type. The statement which will
     *            be used is "insert" + objectName or "update" + objectName.
     * @throws DataAccessException
     *             If general problem occurred.
     * @throws InsertionFailureException
     *             If parameter object could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If parameter object has been modificated in the meantime.
     */
    public void insertOrUpdate(
        final PrimaryKeyOptimisticLockingObject parameterObject,
        final String objectName)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(parameterObject, "Parameter object must not be null.");
        Reject.ifEmpty(objectName, "Name of the object must not be empty.");
        
        if (parameterObject.isKeyNew()) {
            /**
             * Parameter object is new. Must be inserted.
             */
            parameterObject.useGeneratedKey();
            Object keyObject = insert("insert" + objectName, parameterObject);
            if (parameterObject.isKeyNew()) {
                if (keyObject != null) {
                    parameterObject.setKey(keyObject);
                } else {
                    CoreNotificationHelper.notifyInsertionFailure(objectName);
                }
            }
        } else {
            /**
             * Parameter object is not new. Must be updated.
             */
            int count = update("update" + objectName, parameterObject);
            if (count != 1) {
                String message;
                if (parameterObject instanceof PrimaryKeyObject) {
                    PrimaryKeyObject primaryKeyObject 
                        = (PrimaryKeyObject) parameterObject;
                    message = objectName + " with key " 
                        + primaryKeyObject.getKeyAsObject()
                        + " was modified or deleted in the meantime.";
                } else {
                    message = objectName 
                        + " was modified or deleted in the meantime.";
                }
                CoreNotificationHelper.notifyOptimisticLockingFailure(
                    message, objectName);
            } else {
                parameterObject.increaseOptimisticLockingVersion();
            }
        }
    }
    
    /**
     * Query for object in a strong way means, that this method will throw a
     * <code>DataRetrievalFailureException</code> instead of returning
     * <code>null</code>.
     * 
     * @param statementName
     *            Is the name of the statement.
     * @param parameterObject
     *            Is the parameter object.
     * @param objectName
     *            Is the name of the given object type. The statement which will
     *            be used is "delete" + objectName.
     * @return Returns the requested object. Never <code>null</code>.
     * @throws DataAccessException
     *             If general problem occurred.
     * @throws DataRetrievalFailureException
     *             If returned value would be <code>null</code>.
     */
    public Object queryForObjectStrong(final String statementName, 
        final Object parameterObject, final String objectName) 
        throws DataAccessException, DataRetrievalFailureException {
        Reject.ifEmpty(statementName, 
            "Name of the statement must not be empty.");
        Reject.ifNull(parameterObject, "Parameter object must not be null.");
        Reject.ifEmpty(objectName, "Name of the object must not be empty.");
        
        Object result = queryForObject(statementName, parameterObject);
        if (result == null) {
            String message;
            if (parameterObject instanceof PrimaryKeyObject) {
                PrimaryKeyObject primaryKeyObject 
                    = (PrimaryKeyObject) parameterObject;
                message = "The desired " + objectName + " with key " 
                    + primaryKeyObject.getKeyAsObject() + " does not exist.";
            } else {
                message = "The desired " + objectName + " does not exist.";
            }
            CoreNotificationHelper.notifyDataRetrievalFailure(
                message, objectName);
        }
        return result;
    }
    
    /**
     * Does the same as method
     * <code>delete(java.lang.String, java.lang.Object, int)</code> of the
     * underlying class, but on
     * <code>JdbcUpdateAffectedIncorrectNumberOfRowsException</code> the
     * exception will be logged.
     * 
     * @see SqlMapClientTemplate#delete(java.lang.String, java.lang.Object, int)
     * @param parameterObject
     *            Is the parameter object.
     * @param requiredRowsAffected
     *            Is the number of required rows affected.
     * @param objectName
     *            Is the name of the given object type.
     * @throws DataAccessException
     *             If general problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If affected number of rows is not equals the required number.
     */
    public void delete(final Object parameterObject,
        final int requiredRowsAffected, final String objectName)
        throws DataAccessException, 
            JdbcUpdateAffectedIncorrectNumberOfRowsException {

        String statementName = "delete" + objectName;
        int actualRowsAffected = delete(statementName, parameterObject);
        if (actualRowsAffected != requiredRowsAffected) {
            String message;
            if (parameterObject instanceof PrimaryKeyObject) {
                PrimaryKeyObject primaryKeyObject 
                    = (PrimaryKeyObject) parameterObject;
                message = objectName + " with key " 
                    + primaryKeyObject.getKeyAsObject() 
                    + " could not be deleted.";
            } else {
                message = objectName + " could not be deleted.";
            }
            CoreNotificationHelper
                .notifyJdbcUpdateAffectedIncorrectNumberOfRows(
                    message, objectName, 
                    statementName, requiredRowsAffected, actualRowsAffected);
        }
    }
    
    /**
     * Deletes an object in a strong way. This method does the same as method
     * <code>delete(java.lang.String, java.lang.Object)</code> of the
     * underlying class, and in addition to this method, it throws an
     * <code>OptimisticLockingFailureException</code> in case of an optimistic
     * locking failure.
     * 
     * @param statementName
     *            Is the name of the SQL DELETE statement to execute
     * @param parameterObject
     *            Is the parameter object
     * @param objectName
     *            Is the name of the given object type.
     */
    public void deleteStrong(String statementName, Object parameterObject,
        final String objectName) {
        int count = delete(statementName, parameterObject);
        if (count != 1) {
            String message;
            if (parameterObject instanceof PrimaryKeyObject) {
                PrimaryKeyObject primaryKeyObject 
                    = (PrimaryKeyObject) parameterObject;
                message = objectName + " with key "
                    + primaryKeyObject.getKeyAsObject()
                    + " was modified or deleted in the meantime.";
            } else {
                message = objectName
                    + " was modified or deleted in the meantime.";
            }
            CoreNotificationHelper.notifyOptimisticLockingFailure(message,
                objectName);
        }
    }
}
