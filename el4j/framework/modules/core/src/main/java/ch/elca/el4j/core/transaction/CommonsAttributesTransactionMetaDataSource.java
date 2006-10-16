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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * Meta data source for transactional metadata of type
 * <b>Commons Attributes</b>.
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
public class CommonsAttributesTransactionMetaDataSource
    extends TransactionMetaDataSource {
    
    /**
     * Searches for one transaction attribute and adds found found rollback 
     * rules for rule based transaction attributes. 
     * 
     * {@inheritDoc}
     */
    @Override
    protected Collection filterMetaData(Collection metaData) {
        if (metaData == null || metaData.isEmpty()) {
            return null;
        }
        
        TransactionAttribute txAttribute = null;

        // Check whether there is a transaction attribute.
        for (Iterator itMetaData = metaData.iterator(); 
            itMetaData.hasNext() && txAttribute == null;) {
            Object att = itMetaData.next();
            if (att instanceof TransactionAttribute) {
                txAttribute = (TransactionAttribute) att;
            }
        }

        // Check if we have a RuleBasedTransactionAttribute.
        if (txAttribute != null 
            && txAttribute instanceof RuleBasedTransactionAttribute) {
            RuleBasedTransactionAttribute rbta 
                = (RuleBasedTransactionAttribute) txAttribute;
            // We really want value: bit of a hack.
            List<RollbackRuleAttribute> rollbackRules
                = new ArrayList<RollbackRuleAttribute>();
            for (Iterator itMetaData = metaData.iterator(); 
                itMetaData.hasNext();) {
                Object attribute = itMetaData.next();
                if (attribute instanceof RollbackRuleAttribute) {
                    rollbackRules.add((RollbackRuleAttribute) attribute);
                }
            }
            // Repeatedly setting this isn't elegant, but it works.
            rbta.setRollbackRules(rollbackRules);
            
            // Finalize the rollback rule behavior. 
            finalizeRollbackBehavior(rbta);
        }
        
        return txAttribute == null ? Collections.EMPTY_SET 
            : Collections.singleton(txAttribute);
    }
}
