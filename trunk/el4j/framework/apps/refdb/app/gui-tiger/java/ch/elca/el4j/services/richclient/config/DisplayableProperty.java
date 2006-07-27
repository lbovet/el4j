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
package ch.elca.el4j.services.richclient.config;

import ch.elca.el4j.util.dom.reflect.Property;

/**
 * a proxy permitting to configure and query a {@link ch.elca.el4j.services.dom.info.Property}'s 
 * visibility.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class DisplayableProperty {
    /**
     * The proxied property.
     */
    public final Property prop;
    
    /**
     * should this property be hidden?
     */
    public boolean hidden = false;

    /**
     * creates a proxy for {@code p}.
     * @param p .
     **/
    DisplayableProperty(Property p) {
        prop = p;
    }
    
    /** returns whether this property is visible.
     * @return .
     */
    public boolean isVisible() {
        return !hidden;
    }
}