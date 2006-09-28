/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.transaction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.metadata.Attributes;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;

/**
 * Attribute source for Java5 transaction annotations. Byside annotation 
 * {@link Transactional} it is possible to use annotation
 * {@link RollbackConstraint} where the latest annotation is optional for 
 * transaction declaration. 
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
public class ExtendedAnnotationTransactionAttributeSource 
    extends AnnotationTransactionAttributeSource {
    
    /**
     * Is the source where attributes will be fetched.
     */
    protected final Attributes m_attributes;
    
    /**
     * Constructor.
     * 
     * @param attributes Is the source where attributes will be fetched.
     */
    public ExtendedAnnotationTransactionAttributeSource(
        Attributes attributes) {
        m_attributes = attributes;
    }
    
    /**
     * @param method Is the method to find attributes for.
     * @return Returns found attributes.
     */
    protected Collection findAllAttributes(Method method) {
        return m_attributes.getAttributes(method);
    }

    /**
     * @param clazz Is the clazz to find attributes for.
     * @return Returns found attributes.
     */
    protected Collection findAllAttributes(Class clazz) {
        return m_attributes.getAttributes(clazz);
    }

    /**
     * {@inheritDoc}
     * 
     * Adds rollback constraints defined in {@link RollbackConstraint} to the
     * existing transaction attribute.
     */
    @SuppressWarnings("unchecked")
    protected TransactionAttribute findTransactionAttribute(Collection atts) {
        if (atts == null) {
            return null;
        }

        RuleBasedTransactionAttribute rbta = null;
        
        // See if there is a transaction annotation.
        for (Object att : atts) {
            if (att instanceof Transactional) {
                Transactional ruleBasedTx = (Transactional) att;

                rbta = new RuleBasedTransactionAttribute();
                rbta.setPropagationBehavior(ruleBasedTx.propagation().value());
                rbta.setIsolationLevel(ruleBasedTx.isolation().value());
                rbta.setTimeout(ruleBasedTx.timeout());
                rbta.setReadOnly(ruleBasedTx.readOnly());

                ArrayList<RollbackRuleAttribute> rollBackRules 
                    = new ArrayList<RollbackRuleAttribute>();

                Class[] rbf = ruleBasedTx.rollbackFor();
                for (int i = 0; i < rbf.length; ++i) {
                    RollbackRuleAttribute rule 
                        = new RollbackRuleAttribute(rbf[i]);
                    rollBackRules.add(rule);
                }

                String[] rbfc = ruleBasedTx.rollbackForClassName();
                for (int i = 0; i < rbfc.length; ++i) {
                    RollbackRuleAttribute rule 
                        = new RollbackRuleAttribute(rbfc[i]);
                    rollBackRules.add(rule);
                }

                Class[] nrbf = ruleBasedTx.noRollbackFor();
                for (int i = 0; i < nrbf.length; ++i) {
                    NoRollbackRuleAttribute rule 
                        = new NoRollbackRuleAttribute(nrbf[i]);
                    rollBackRules.add(rule);
                }

                String[] nrbfc = ruleBasedTx.noRollbackForClassName();
                for (int i = 0; i < nrbfc.length; ++i) {
                    NoRollbackRuleAttribute rule 
                        = new NoRollbackRuleAttribute(nrbfc[i]);
                    rollBackRules.add(rule);
                }

                rbta.getRollbackRules().addAll(rollBackRules);
                
                break;
            }
        }
        
        if (rbta != null) {
            for (Object att : atts) {
                if (att instanceof RollbackConstraint) {
                    RollbackConstraint rollbackConstraint 
                        = (RollbackConstraint) att;
                    
                    ArrayList<RollbackRuleAttribute> rollBackRules 
                        = new ArrayList<RollbackRuleAttribute>();

                    Class[] rbf = rollbackConstraint.rollbackFor();
                    for (int i = 0; i < rbf.length; ++i) {
                        RollbackRuleAttribute rule 
                            = new RollbackRuleAttribute(rbf[i]);
                        rollBackRules.add(rule);
                    }

                    String[] rbfc = rollbackConstraint.rollbackForClassName();
                    for (int i = 0; i < rbfc.length; ++i) {
                        RollbackRuleAttribute rule 
                            = new RollbackRuleAttribute(rbfc[i]);
                        rollBackRules.add(rule);
                    }

                    Class[] nrbf = rollbackConstraint.noRollbackFor();
                    for (int i = 0; i < nrbf.length; ++i) {
                        NoRollbackRuleAttribute rule 
                            = new NoRollbackRuleAttribute(nrbf[i]);
                        rollBackRules.add(rule);
                    }

                    String[] nrbfc 
                        = rollbackConstraint.noRollbackForClassName();
                    for (int i = 0; i < nrbfc.length; ++i) {
                        NoRollbackRuleAttribute rule 
                            = new NoRollbackRuleAttribute(nrbfc[i]);
                        rollBackRules.add(rule);
                    }

                    rbta.getRollbackRules().addAll(rollBackRules);
                    
                    break;
                }
            }
            
            /**
             * If no no-rollback-rule for error/runtime-exception is defined
             * add the rollback-rule for error/runtime-exception.
             */
            RollbackRuleAttribute rollbackRuleRuntimeException
                = new RollbackRuleAttribute(RuntimeException.class);
            String rollbackRuleRuntimeExceptionName 
                = rollbackRuleRuntimeException.getExceptionName();
            boolean useRollbackRuleRuntimeException = true;
            
            RollbackRuleAttribute rollbackRuleError
                = new RollbackRuleAttribute(Error.class);
            String rollbackRuleErrorName 
                = rollbackRuleError.getExceptionName();
            boolean useRollbackRuleError = true;
            
            List<RollbackRuleAttribute> allRules = rbta.getRollbackRules();
            for (RollbackRuleAttribute rule : allRules) {
                if (rule instanceof NoRollbackRuleAttribute) {
                    String name = rule.getExceptionName();
                    if (rollbackRuleRuntimeExceptionName.equals(name)) {
                        useRollbackRuleRuntimeException = false;
                    } else if (rollbackRuleErrorName.equals(name)) {
                        useRollbackRuleError = false;
                    }
                }
            }
            
            if (useRollbackRuleRuntimeException) {
                allRules.add(
                    new RollbackRuleAttribute(RuntimeException.class));
            }
            if (useRollbackRuleError) {
                allRules.add(
                    new RollbackRuleAttribute(Error.class));
            }
        }

        return rbta;
    }
}
