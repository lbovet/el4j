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

import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.gui.support.RefdbSchemas;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.executors.convenience.AbstractBeanPropertiesExecutor;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;

/**
 * Executor to edit reference properties.
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
public class ReferencePropertiesExecutor 
    extends AbstractBeanPropertiesExecutor {
    /**
     * Default constructor.
     */
    public ReferencePropertiesExecutor() {
        setId("referenceProperties");
    }
    
    /**
     * {@inheritDoc}
     */
    protected PrimaryKeyObject saveBean(PrimaryKeyObject givenBean) {
        ReferenceDto givenReference = (ReferenceDto) givenBean;
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        return referenceService.saveReference(givenReference);
    }

    /**
     * {@inheritDoc}
     */
    protected PrimaryKeyObject getBeanByKey(Object key) throws Exception {
        int intKey = ((Number) key).intValue();
        ReferenceService referenceService 
            = ServiceBroker.getReferenceService();
        return referenceService.getReferenceByKey(intKey);
    }

    /**
     * {@inheritDoc}
     */
    public String getSchema() {
        return RefdbSchemas.REFERENCE;
    }
}
