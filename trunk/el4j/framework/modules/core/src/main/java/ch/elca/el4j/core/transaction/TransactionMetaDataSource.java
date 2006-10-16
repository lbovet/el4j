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
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.CollectionUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.metadata.DefaultGenericMetaDataSource;

/**
 * Base class for transactional metadata sources.
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
public class TransactionMetaDataSource 
    extends DefaultGenericMetaDataSource 
    implements TransactionAttributeSource, InitializingBean {

    /**
     * {@inheritDoc}
     */
    public TransactionAttribute getTransactionAttribute(
        Method method, Class targetClass) {
        Collection c = getMetaData(method, targetClass);
        if (CollectionUtils.isEmpty(c)) {
            return null;
        } else {
            return (TransactionAttribute) c.iterator().next();
        }
    }

    /**
     * Checks the given rule based rollback attribute and corrects the 
     * rollback rule if necessary. 
     * 
     * @param rbta Is the rule based rollback attribute to finalize.
     */
    @SuppressWarnings("unchecked")
    protected void finalizeRollbackBehavior(
        RuleBasedTransactionAttribute rbta) {
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

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getMetaDataDelegator(), "metaDataDelegator", this);
    }
}
