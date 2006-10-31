/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.services.monitoring.notification;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;

/**
 * This class is used to notify on events which are core based.
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
public final class CoreNotificationHelper {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(CoreNotificationHelper.class);
    
    /**
     * Hide default constructor.
     */
    private CoreNotificationHelper() {
    } 

    /**
     * Method to notify that an essential property of a bean is lacking. In
     * every case a <code>MissconfigurationRTException</code> will be thrown.
     * 
     * @param propertyName
     *            Is the name of the lacking property.
     * @param concernedBean
     *            Is the bean where the property is missing.
     * @throws MisconfigurationRTException Will be thrown everytime.
     */
    public static void notifyLackingEssentialProperty(String propertyName,
            Object concernedBean) throws MisconfigurationRTException {
        assert propertyName != null && propertyName.length() > 0;
        assert concernedBean != null;

        Class beanClass = concernedBean.getClass();
        String beanClassName = beanClass.getName();
        String beanName;
        try {
            Method m = beanClass.getMethod("getBeanName", (Class[]) null);
            beanName = (String) m.invoke(concernedBean, (Object[]) null);
        } catch (Exception e) {
            // Bean name could not be extracted.
            beanName = null;
        }

        String message = "The property '" + propertyName + "' is required! ";
        if (StringUtils.hasText(beanName)) {
            message += "Concerned bean is of class '" + beanClassName
                    + "' and has name '" + beanName + "'.";
        } else {
            message += "Concerned bean is of class '" + beanClassName + "'.";
        }

        notifyMisconfiguration(message);
    }
    
    /**
     * Method to check if an essential property is not <code>null</code>.
     * 
     * @param essentialProperty
     *            Is the property which should not be null.
     * @param propertyName
     *            Is the name of the lacking property.
     * @param concernedBean
     *            Is the bean where the property is missing.
     * @throws MisconfigurationRTException
     *             If essential property is misconfigured.
     */
    public static void notifyIfEssentialPropertyIsEmpty(
        Object essentialProperty, String propertyName, Object concernedBean) 
        throws MisconfigurationRTException {
        if (essentialProperty == null) {
            notifyLackingEssentialProperty(propertyName, concernedBean);
        }
    }
    
    /**
     * Method to check if an essential property string is has text.
     * 
     * @param essentialStringProperty
     *            Is the property which should have text.
     * @param propertyName
     *            Is the name of the lacking property.
     * @param concernedBean
     *            Is the bean where the property is missing.
     * @throws MisconfigurationRTException
     *             If essential property is misconfigured.
     * @see StringUtils#hasText(java.lang.String)
     */
    public static void notifyIfEssentialPropertyIsEmpty(
        String essentialStringProperty, String propertyName, 
        Object concernedBean) throws MisconfigurationRTException {
        if (!StringUtils.hasText(essentialStringProperty)) {
            notifyLackingEssentialProperty(propertyName, concernedBean);
        }
    }
    
    /**
     * Method to log that a misconfiguration has occurred.
     * This method will always throw an exception.
     * 
     * @param message
     *            Is the message which explains the misconfiguration.
     * @throws MisconfigurationRTException
     *            Will be thrown in every case.
     */
    public static void notifyMisconfiguration(String message) 
        throws MisconfigurationRTException {
        s_logger.error(message);
        throw new MisconfigurationRTException(message);
    }
    
    /**
     * Logs the message and the Exception cause and translates it to a
     * {@link MisconfigurationRTException}.
     *  
     * @param message
     *            describes the misconfiguration.
     * @param cause
     *            the cause exception.
     * @throws MisconfigurationRTException
     *            Will be thrown in every case.
     */
    public static void notifyMisconfiguration(String message, Throwable cause)
        throws MisconfigurationRTException {
        s_logger.error(message, cause);
        throw new MisconfigurationRTException(message, cause);
    }

    /**
     * Replaces place holders in the message with the values provided as
     * parameters.
     * 
     * @param message
     *            message that contains place holders.
     * @param parameters
     *            list of parameters that replace the place holders.
     * @throws MisconfigurationRTException
     *            Will be thorwn in every case.
     * @see MessageFormat
     */
    public static void notifyMisconfiguration(String message,
            Object... parameters) throws MisconfigurationRTException {
        String msg = MessageFormat.format(message, parameters);
        notifyMisconfiguration(msg);
    }
    
    
    /**
     * Method to log that an object does not exist. This method will always
     * throw an exception.
     * 
     * @param detailedMessage
     *            Is the detailed message.
     * @param objectName
     *            Is the name of the object.
     * @throws DataRetrievalFailureException
     *             Will be thrown in every case.
     */
    public static void notifyDataRetrievalFailure(
        String detailedMessage, String objectName) 
        throws DataRetrievalFailureException {
        String message = StringUtils.hasText(detailedMessage) 
            ? detailedMessage
                : "The desired " + objectName + " does not exist.";
        s_logger.error(message);
        throw new DataRetrievalFailureException(message);
    }

    /**
     * Method to log that an object made problems while insertion. This method
     * will always throw an exception.
     * 
     * @param detailedMessage
     *            Is the detailed message.
     * @param objectName
     *            Is the name of the object.
     * @throws InsertionFailureException
     *             Will be thrown in every case.
     */
    public static void notifyInsertionFailure(
        String detailedMessage, String objectName) 
        throws InsertionFailureException {
        String message = StringUtils.hasText(detailedMessage) 
            ? detailedMessage
                : "Insertion of " + objectName + " failed.";
        s_logger.error(message);
        throw new InsertionFailureException(message);
    }
    
    /**
     * Notify violation of data integrity of an object.
     * Will always throw an exception.
     * 
     * @param detailedMessage
     *            Is the detailed message.
     * @param objectName
     *            Is the name of the object.
     * @throws DataIntegrityViolationException
     *             Will be thrown in every case.
     */
    public static void notifyDataIntegrityViolationFailure(
        String detailedMessage, String objectName) 
        throws DataIntegrityViolationException {
        String message = StringUtils.hasText(detailedMessage) 
            ? detailedMessage
                : "Integrity of " + objectName + " violated.";
        s_logger.error(message);
        throw new DataIntegrityViolationException(message);
    }


    /**
     * Method to log that the object has already been modified. This method
     * will always throw an exception.
     * 
     * @param detailedMessage
     *            Is the detailed message.
     * @param objectName
     *            Is the name of the object.
     * @throws OptimisticLockingFailureException
     *             Will be thrown in every case.
     */
    public static void notifyOptimisticLockingFailure(
        String detailedMessage, String objectName) 
        throws OptimisticLockingFailureException {
        String message = StringUtils.hasText(detailedMessage) 
            ? detailedMessage
                : objectName + " was modified or deleted in the meantime.";
        s_logger.error(message);
        throw new OptimisticLockingFailureException(message);
    }

    /**
     * Method to log that the object could not be updated correctly. This method
     * will always throw an exception.
     * 
     * @param detailedMessage
     *            Is the detailed message.
     * @param objectName
     *            Is the name of the object.
     * @param sql
     *            Is the SQL we tried to execute.
     * @param expected
     *            Is the expected number of rows affected.
     * @param actual
     *            Is the actual number of rows affected.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             Will be thrown in every case.
     */
    public static void notifyJdbcUpdateAffectedIncorrectNumberOfRows(
        String detailedMessage, String objectName, 
        String sql, int expected, int actual) 
        throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        String message = StringUtils.hasText(detailedMessage) 
            ? detailedMessage
                : objectName + " could not be updated correctly.";
        s_logger.error(message);
        throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(
            sql, expected, actual);
    }
    
    /**
     * Same behaviour as call of method 
     * <code>notifyDataRetrievalFailure(null, String)</code>.
     * 
     * @see #notifyDataRetrievalFailure(String, String)
     */
    public static void notifyDataRetrievalFailure(String objectName) 
        throws DataRetrievalFailureException {
        notifyDataRetrievalFailure(null, objectName);
    }

    /**
     * Same behaviour as call of method 
     * <code>notifyInsertionFailure(null, String)</code>.
     * 
     * @see #notifyInsertionFailure(String, String)
     */
    public static void notifyInsertionFailure(String objectName) 
        throws InsertionFailureException {
        notifyInsertionFailure(null, objectName);
    }

    /**
     * Same behaviour as call of method 
     * <code>notifyDataIntegrityViolationFailure(null, String)</code>.
     * 
     * @see #notifyDataIntegrityViolationFailure(String, String)
     */
    public static void notifyDataIntegrityViolationFailure(String objectName) 
        throws DataIntegrityViolationException {
        notifyDataIntegrityViolationFailure(null, objectName);
    }

    
    /**
     * Same behaviour as call of method 
     * <code>notifyOptimisticLockingFailure(null, String)</code>.
     * 
     * @see #notifyOptimisticLockingFailure(String, String)
     */
    public static void notifyOptimisticLockingFailure(String objectName) 
        throws OptimisticLockingFailureException {
        notifyOptimisticLockingFailure(null, objectName);
    }
    
    /**
     * Same behaviour as call of method
     * <code>notifyOptimisticLockingFailure(null, String, String, int, int)
     * </code>.
     * 
     * @see #notifyJdbcUpdateAffectedIncorrectNumberOfRows(String, String,
     *      String, int, int)
     */
    public static void notifyJdbcUpdateAffectedIncorrectNumberOfRows(
        String objectName, String sql, int expected, int actual) 
        throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        notifyJdbcUpdateAffectedIncorrectNumberOfRows(
            null, objectName, sql, expected, actual);
    }
}