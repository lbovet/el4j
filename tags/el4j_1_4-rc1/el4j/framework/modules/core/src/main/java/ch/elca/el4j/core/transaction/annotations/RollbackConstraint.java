/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.transaction.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a subsection of class 
 * {@link org.springframework.transaction.annotation.Transactional}. It is
 * mostly used on interfaces to pre-define the rule-based behavior of
 * transactions. It is only useful in combination with annotation
 * {@link org.springframework.transaction.annotation.Transactional} that is
 * mostly defined on implementing class.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @see org.springframework.transaction.annotation.Transactional
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RollbackConstraint {
    /**
     * Defines zero (0) or more exception {@link Class classes}, which must be
     * a subclass of {@link Throwable}, indicating which exception types must
     * cause a transaction rollback.
     * <p>
     * This is the preferred way to construct a rollback rule, matching the
     * exception class and subclasses.
     * <p>
     * Similar to
     * {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}
     */
    Class<? extends Throwable>[] rollbackFor() default { };
    
    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}), indicating which exception types must
     * cause a transaction rollback.
     * <p>
     * This can be a substring, with no wildcard support at present. A value of
     * "ServletException" would match {@link javax.servlet.ServletException} and
     * subclasses, for example.
     * <p>
     * <b>NB: </b>Consider carefully how specific the pattern is, and whether to
     * include package information (which isn't mandatory). For example,
     * "Exception" will match nearly anything, and will probably hide other
     * rules. "java.lang.Exception" would be correct if "Exception" was meant to
     * define a rule for all checked exceptions. With more unusual
     * {@link Exception} names such as "BaseBusinessException" there is no need
     * to use a FQN.
     * <p>
     * Similar to
     * {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}
     */
    String[] rollbackForClassName() default { };
    
    /**
     * Defines zero (0) or more exception {@link Class Classes}, which must be
     * a subclass of {@link Throwable}, indicating which exception types must
     * <b>not</b> cause a transaction rollback.
     * <p>
     * This is the preferred way to construct a rollback rule, matching the
     * exception class and subclasses.
     * <p>
     * Similar to
     * {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}
     */
    Class<? extends Throwable>[] noRollbackFor() default { };
    
    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}) indicating which exception types must
     * <b>not</b> cause a transaction rollback.
     * <p>
     * See the description of {@link #rollbackForClassName()} for more info on
     * how the specified names are treated.
     * <p>
     * Similar to
     * {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}
     */
    String[] noRollbackForClassName() default { };
}
