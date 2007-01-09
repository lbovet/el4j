/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.applications.refdb.gui.views;

import java.util.List;

import ch.elca.el4j.applications.refdb.dom.Reference;
import ch.elca.el4j.applications.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.applications.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.event.RefreshEvent;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanTableView;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.events.QueryObjectEvent;

/**
 * Reference view.
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
public class ReferenceView extends AbstractBeanTableView {
    /**
     * {@inheritDoc}
     */
    protected void onQueryObjectEvent(QueryObjectEvent event) {
        if (isControlCreated() && isQueryObjectComingFromNeighbour(event)) {
            QueryObject queryObject = event.getQueryObject(Reference.class);
            if (queryObject != null) {
                ReferenceService referenceService 
                    = ServiceBroker.getReferenceService();
                List list = referenceService.searchReferences(queryObject);
                setBeans(list);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onRefreshEvent(RefreshEvent event) {
        // TODO Auto-generated method stub
    }
}
