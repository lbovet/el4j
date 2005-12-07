/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.apps.refdb.gui.views;

import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanView;

/**
 * Abstract reference service view class to provide views access to the 
 * reference service. 
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
public abstract class AbstractRefdbView extends AbstractBeanView {
    /**
     * Reference service object.
     */
    private ReferenceService m_referenceService;

    /**
     * @return Returns the referenceService.
     */
    public ReferenceService getReferenceService() {
        return m_referenceService;
    }

    /**
     * @param referenceService The referenceService to set.
     */
    public void setReferenceService(ReferenceService referenceService) {
        m_referenceService = referenceService;
    }
}
