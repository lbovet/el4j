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
package ch.elca.el4j.applications.refdb.gui.executors;

import ch.elca.el4j.applications.keyword.dao.KeywordDao;
import ch.elca.el4j.applications.keyword.dom.Keyword;
import ch.elca.el4j.applications.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.applications.refdb.gui.support.RefdbSchemas;
import ch.elca.el4j.services.gui.richclient.executors.convenience.AbstractBeanPropertiesExecutor;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;

/**
 * Executor to edit keyword properties.
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
public class KeywordPropertiesExecutor extends AbstractBeanPropertiesExecutor {
    /**
     * Default constructor.
     */
    public KeywordPropertiesExecutor() {
        setId("keywordProperties");
    }
    
    /**
     * {@inheritDoc}
     */
    protected PrimaryKeyObject saveBean(PrimaryKeyObject givenBean) {
        Keyword givenKeyword = (Keyword) givenBean;
        KeywordDao keywordDao = ServiceBroker.getKeywordDao();
        return keywordDao.saveOrUpdate(givenKeyword);
    }

    /**
     * {@inheritDoc}
     */
    protected PrimaryKeyObject getBeanByKey(Object key) throws Exception {
        int intKey = ((Number) key).intValue();
        KeywordDao keywordDao = ServiceBroker.getKeywordDao();
        return keywordDao.findById(intKey);
    }

    /**
     * {@inheritDoc}
     */
    public String getSchema() {
        return RefdbSchemas.KEYWORD;
    }
}
