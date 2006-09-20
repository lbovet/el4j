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

import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
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
    extends AbstractBeanDeleteExecutor<Keyword> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBeans(List<Keyword> beans) {
        ArrayList<Object> keys = new ArrayList<Object>(); 
        for (PrimaryKeyObject pko : beans) {
            keys.add(pko.getKeyAsObject());
        }
        
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        referenceService.removeKeywords(keys);
    }

    /** {@inheritDoc} */
    @Override
    protected Keyword reloadBean(Keyword entity) throws Exception {
        int intKey = entity.getKey();
        KeywordDao keywordDao = ServiceBroker.getKeywordDao();
        Keyword newBean = keywordDao.findById(intKey);
        return newBean;
    }

    /**
     * {@inheritDoc}
     */
    public String getSchema() {
        return RefdbSchemas.KEYWORD;
    }
}
