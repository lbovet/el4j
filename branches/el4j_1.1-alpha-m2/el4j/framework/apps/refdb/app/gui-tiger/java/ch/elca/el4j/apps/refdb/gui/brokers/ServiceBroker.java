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
package ch.elca.el4j.apps.refdb.gui.brokers;

import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.utils.Services;

/**
 * Broker for services. Currently only the reference service is implemented.
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
public class ServiceBroker {
    /**
     * Is the cached reference service.
     */
    private static ReferenceService s_cachedReferenceService = null;
    
    /**
     * Hide default constructor.
     */
    protected ServiceBroker() { }
    
    
    /**
     * @return Returns the cached reference service.
     * @see #getReferenceService(boolean)
     */
    public static synchronized ReferenceService getReferenceService() {
        return getReferenceService(true);
    }

    /**
     * Returns an instance of the reference service. If caching is on always the
     * same instance will be returned.
     * 
     * @param allowCaching
     *            Flags if it is allowed to cache the reference service.
     * @return Returns a new or the cached reference service.
     */
    public static synchronized ReferenceService getReferenceService(
        boolean allowCaching) {
        ReferenceService referenceService = null;
        if (s_cachedReferenceService == null || !allowCaching) {
            referenceService = Services.getBean(
                "referenceService",
                ReferenceService.class
            );
        } else {
            referenceService = s_cachedReferenceService;
        }
        
        if (allowCaching) {
            s_cachedReferenceService = referenceService;
        } else {
            s_cachedReferenceService = null;
        }
        return referenceService;
    }
}
