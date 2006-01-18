/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.apps.refdb.gui.views;

import java.util.List;

import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.events.QueryObjectEvent;

/**
 * Reference view.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ReferenceView extends AbstractRefdbView {
    /**
     * {@inheritDoc}
     */
    protected void onQueryObjectEvent(QueryObjectEvent event) {
        if (isControlCreated() && isQueryObjectCommingFromNeighbour(event)) {
            QueryObject queryObject = event.getQueryObject();
            List list = getReferenceService().searchReferences(queryObject);
            getDataList().clear();
            getDataList().addAll(list);
            getBeanTableModel().fireTableDataChanged();
        }
    }
}
