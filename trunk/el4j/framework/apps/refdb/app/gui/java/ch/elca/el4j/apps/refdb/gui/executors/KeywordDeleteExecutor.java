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
package ch.elca.el4j.apps.refdb.gui.executors;

import java.util.List;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.gui.support.RefdbSchemas;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.executors.convenience.AbstractBeanDeleteExecutor;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;

/**
 * Executor to delete keywords.
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
public class KeywordDeleteExecutor 
    extends AbstractBeanDeleteExecutor<KeywordDto> {
    /**
     * Default constructor.
     */
    public KeywordDeleteExecutor() {
        setId("keywordDelete");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBeans(List<KeywordDto> beans) {
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        referenceService.removeKeywords(beans);
    }        
            
    /**
     * {@inheritDoc}
     */
    protected PrimaryKeyObject getBeanByKey(Object key) throws Exception {
        int intKey = ((Number) key).intValue();
        ReferenceService referenceService 
            = ServiceBroker.getReferenceService();
        PrimaryKeyObject newBean 
            = referenceService.getKeywordByKey(intKey);
        return newBean;
    }    

    /**
     * {@inheritDoc}
     */
    public String getSchema() {
        return RefdbSchemas.KEYWORD;
    }

}
