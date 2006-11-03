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
package ch.elca.el4j.core.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;

import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;

/**
 * Meta data source for transactional metadata of type <b>Java 5 Annotation</b>.
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
public class AnnotationTransactionMetaDataSource
    extends TransactionMetaDataSource {
    
    /**
     * Adds rollback constraints defined in {@link RollbackConstraint} to the
     * existing transaction attribute. Uses only the first found transaction
     * and rollback constraint annotation.
     * 
     * {@inheritDoc}
     */
    @Override
    protected Collection filterMetaData(Collection metaData) {
        if (metaData == null || metaData.isEmpty()) {
            return null;
        }

        RuleBasedTransactionAttribute rbta = null;
        
        // See if there is a transactional metadata.
        for (Object att : metaData) {
            if (att instanceof Transactional) {
                Transactional ruleBasedTx = (Transactional) att;
                rbta = new RuleBasedTransactionAttribute();
                addMetaDataTransactional(rbta, ruleBasedTx);
                break;
            }
        }
        
        // If there was a transactional metadata look out for a
        // roolback constraint.
        Collection result;
        if (rbta != null) {
            for (Object att : metaData) {
                if (att instanceof RollbackConstraint) {
                    RollbackConstraint rollbackConstraint 
                        = (RollbackConstraint) att;
                    addMetaDataRollbackConstraint(rbta, rollbackConstraint);
                    break;
                }
            }
            finalizeRollbackBehavior(rbta);
            result = Collections.singleton(rbta);
        } else {
            result = Collections.EMPTY_SET;
        }
        return result;
    }

    /**
     * Adds the given transactional metadata to the given rule based rollback
     * attribute.
     * 
     * @param rbta Is the rule based rollback attribute to complete
     * @param ruleBasedTx Is the transactional metadata to add.
     */
    @SuppressWarnings("unchecked")
    protected void addMetaDataTransactional(
        RuleBasedTransactionAttribute rbta, Transactional ruleBasedTx) {
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
    }

    /**
     * Adds the given rollback constraint to the given rule based rollback
     * attribute.
     * 
     * @param rbta Is the rule based rollback attribute to complete
     * @param rollbackConstraint Is the rollback constraint to add.
     */
    @SuppressWarnings("unchecked")
    protected void addMetaDataRollbackConstraint(
        RuleBasedTransactionAttribute rbta, 
        RollbackConstraint rollbackConstraint) {
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
    }
}
