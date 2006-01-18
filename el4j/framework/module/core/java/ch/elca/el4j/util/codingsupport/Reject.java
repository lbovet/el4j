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

package ch.elca.el4j.util.codingsupport;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * This class provides support for assertions and for design by contract.
 * Methods from this class are used at the beginning of a method, before anyone
 * is working with the given parameters.
 * 
 * Parameters which are checked at the end of a method or inside a method should
 * be checked by using the <code>assert</code> keyword of JDK 1.4. More about
 * this you can find 
 * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/lang/assert.html">here</a>
 * .
 * 
 * <b>Example:</b>
 * <code><pre>
 *     public class AccountDao {
 *         public void saveAccount(Account x) {
 *             Reject.ifNull(x);
 *             ...
 *         }
 *     }
 * </pre></code>
 * In this example a 
 * <code>ch.elca.el4j.util.codingsupport.PreconditionRTException</code> will be
 * thrown if the given account will be null. In that way we can pervent that an
 * ugli <code>NullPointerException</code> can be thrown. It is also possible to 
 * add a reason to the reject method, if it is not absolutly clear what is 
 * checked.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public final class Reject {
    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(Reject.class);

    /**
     * Default constructor.
     */
    private Reject() {
    }
    
    ///////////////////
    // Preconditions //
    ///////////////////

    /**
     * Method to ensure that an object is not null. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param object
     *            Is the object to be analyzed.
     */
    public static void ifNull(Object object) {
        ifNull(object, null);
    }

    /**
     * Method to ensure that an object is not null. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param object
     *            Is the object to be analyzed.
     * @param reason
     *            Is the message to explain the reason of the exception.
     */
    public static void ifNull(Object object, String reason) {
        checkCondition(object != null, reason, PreconditionRTException.class);
    }

    /**
     * Method to ensure that a condition is true. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param condition
     *            Is the condition to be analyzed.
     */
    public static void ifFalse(boolean condition) {
        ifFalse(condition, null);
    }

    /**
     * Method to ensure that a condition is true. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param condition
     *            Is the condition to be analyzed.
     * @param reason
     *            Is the message to explain the reason of the exception.
     */
    public static void ifFalse(boolean condition, String reason) {
        checkCondition(condition, reason, PreconditionRTException.class);
    }

    /**
     * Method to ensure that a string is not empty. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param s
     *            Is the string to be analyzed.
     */
    public static void ifEmpty(String s) {
        ifEmpty(s, null);
    }

    /**
     * Method to ensure that a string is not empty. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param s
     *            Is the string to be analyzed.
     * @param reason
     *            Is the message to explain the reason of the exception.
     */
    public static void ifEmpty(String s, String reason) {
        checkCondition(StringUtils.hasText(s), reason,
                PreconditionRTException.class);
    }
    
    /**
     * Method to ensure that a collection is not empty.
     * 
     * @param c
     *            The collection.
     */
    public static void ifEmpty(Collection c) {
        ifEmpty(c, null);
    }
    
    /**
     * Method to ensure that a collection is not empty.
     * 
     * @param c
     *            The collection.
     * @param reason
     *            Message that explains the reason of the thrown exception.
     */
    public static void ifEmpty(Collection c, String reason) {
        checkCondition(!CollectionUtils.isEmpty(c), reason,
                PreconditionRTException.class);
    }

    ////////////////////
    // Common methods //
    ////////////////////

    /**
     * This method throws an exception of give conditionException type, if the
     * condition is false. The given string will be added on created exception.
     * 
     * @param condition
     *            Is the condition to check.
     * @param reason
     *            Is the message to explain the reason of the exception.
     * @param conditionException
     *            Is the exception which will be thrown.
     */
    private static void checkCondition(boolean condition, String reason,
            Class conditionException) {
        if (!condition) {
            AbstractConditionRTException e = getConditionException(reason,
                    conditionException);
            s_logger.error(e.getMessage());
            throw e;
        }

    }

    /**
     * This method creates an instance of the desired exception.
     * 
     * @param reason
     *            Is the message to explain the reason of the exception.
     * @param conditionException
     *            Is the exception which will be returned.
     * @return Returns the created instance of a AbstractConditionRTException.
     */
    private static AbstractConditionRTException getConditionException(
        String reason, Class conditionException) {
        try {
            if (reason != null && reason.length() > 0) {
                Constructor c = conditionException
                        .getConstructor(new Class[] {String.class});
                return (AbstractConditionRTException) c
                        .newInstance(new Object[] {reason});
            } else {
                return (AbstractConditionRTException) conditionException
                    .newInstance();
            }
        } catch (Exception e) {
            assert false;
            throw new RuntimeException(e);
        }
    }
}