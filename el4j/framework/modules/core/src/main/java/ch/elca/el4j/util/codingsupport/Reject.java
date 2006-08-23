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
import java.lang.reflect.InvocationTargetException;
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
 * thrown if the given account will be null. That way, we can prevent that an
 * ugly <code>NullPointerException</code> can be thrown. 
 * 
 * <p> It is also possible to add a reason to the reject method, if it is not 
 * absolutely clear what is checked. Alternatively, you can customize the
 * exception to be thrown in case the condition is violated by providing the
 * exception class and the arguments to its constructor. Existence and 
 * uniqueness of a constructor capable of taking the provided arguments is not
 * verified statically and must therefore be ensured by the user.  
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE), Adrian Moos(AMS)
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
        ifNull(object, PreconditionRTException.class);
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
        ifNull(object, PreconditionRTException.class, reason);
    }
    
    
    /**
     * Method to ensure that an object is not null. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param object
     *            Is the object to be analyzed.
     * @param exceptionType
     *            the kind of {@link PreconditionRTException} to be thrown in
     *            case the precondition is violated.
     * @param exceptionArguments 
     *            constructor arguments for {@code exceptionType}.
     */
    public static 
    void ifNull(Object object, 
                Class<? extends PreconditionRTException> exceptionType,
                Object... exceptionArguments) {
        
        checkCondition(object != null, exceptionType, exceptionArguments);
    }
    
    
    
    /**
     * Method to ensure that a condition is true. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param condition
     *            Is the condition to be analyzed.
     */
    public static void ifFalse(boolean condition) {
        ifFalse(condition, PreconditionRTException.class);
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
        ifFalse(condition, PreconditionRTException.class, reason);
    }

    /**
     * Method to ensure that a condition is true. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param condition
     *            Is the condition to be analyzed.
     * @param exceptionType
     *            the kind of {@link PreconditionRTException} to be thrown in
     *            case the precondition is violated.
     * @param exceptionArguments 
     *            constructor arguments for {@code exceptionType}.
     */
    public static 
    void ifFalse(boolean condition, 
                 Class<? extends PreconditionRTException> exceptionType,
                 Object... exceptionArguments) {
        
        checkCondition(condition, exceptionType, exceptionArguments);
    }
    
    
    /**
     * Method to ensure that a string is not empty. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param s
     *            Is the string to be analyzed.
     */
    public static void ifEmpty(String s) {
        ifEmpty(s, PreconditionRTException.class);
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
        ifEmpty(s, PreconditionRTException.class, reason);
    }
    
    /**
     * Method to ensure that a string is not empty. Used at the beginning of a
     * method before working with parameters.
     * 
     * @param s
     *            Is the string to be analyzed.
     * @param exceptionType
     *            the kind of {@link PreconditionRTException} to be thrown in
     *            case the precondition is violated.
     * @param exceptionArguments 
     *            constructor arguments for {@code exceptionType}.
     */
    public static 
    void ifEmpty(String s, 
                 Class<? extends PreconditionRTException> exceptionType,
                 Object... exceptionArguments) {
        
        checkCondition(
            StringUtils.hasText(s), 
            exceptionType, 
            exceptionArguments
        );
    }
    
    
    /**
     * Method to ensure that a collection is not empty.
     * 
     * @param c
     *            The collection.
     */
    public static void ifEmpty(Collection<?> c) {
        ifEmpty(c, PreconditionRTException.class);
    }
    
    /**
     * Method to ensure that a collection is not empty.
     * 
     * @param c
     *            The collection.
     * @param reason
     *            Message that explains the reason of the thrown exception.
     */
    public static void ifEmpty(Collection<?> c, String reason) {
        ifEmpty(c, PreconditionRTException.class, reason);
    }
    
    /**
     * Method to ensure that a collection is not empty.
     * 
     * @param c
     *            The collection.    
     * @param exceptionType
     *            the kind of {@link PreconditionRTException} to be thrown in
     *            case the precondition is violated.
     * @param exceptionArguments 
     *            constructor arguments for {@code exceptionType}.
     */
    public static 
    void ifEmpty(Collection<?> c, 
                 Class<? extends PreconditionRTException> exceptionType,
                 Object... exceptionArguments) {
       
        checkCondition(
            !CollectionUtils.isEmpty(c),
            exceptionType,
            exceptionArguments
        );
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
     * @param conditionException
     *            Is the exception which will be thrown.
     * @param constructorArgs
     *            Are the arguments to the exception's constructor.
     */
    private static void 
    checkCondition(boolean condition,
                   Class<? extends PreconditionRTException> conditionException,
                   Object... constructorArgs) {
        
        if (!condition) {
            AbstractConditionRTException e = instantiateConditionException(
                conditionException,
                constructorArgs
            );
            s_logger.error(e.getMessage());
            throw e;
        }
    }
    
    /** instantiates an AbstractConditionRTException of the supplied type
     * using the supplied constructor arguments. 
     **/
    private static <T extends AbstractConditionRTException> 
    T instantiateConditionException(Class<T> exceptionClass, 
                                    Object... constructorArgs) {
        
        for (Constructor<T> c : exceptionClass.getConstructors()) {
            try {
                return c.newInstance(constructorArgs);
            } catch (IllegalArgumentException e) {
                continue;
            } catch (InstantiationException e) {
                // exceptionClass is abstract
                assert false;
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                continue;
            } catch (InvocationTargetException e) {
                assert false;
                throw new RuntimeException(
                    "error instantiating " + exceptionClass.getName()
                    + ": constructor threw ", e
                );
            }
        }
        assert false;
        throw new IllegalArgumentException(
            "error instantiating " + exceptionClass.getName() + ": " 
            + "there appears to be no accessible constructor accepting the "
            + "provided arguments."
        );
    }
}