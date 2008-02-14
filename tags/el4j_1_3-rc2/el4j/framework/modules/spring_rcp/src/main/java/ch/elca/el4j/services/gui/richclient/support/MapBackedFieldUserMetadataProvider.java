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
package ch.elca.el4j.services.gui.richclient.support;

import java.util.Map;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * A FieldUserMetadataProvider backed by a nested map.
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
public class MapBackedFieldUserMetadataProvider implements
    FieldUserMetadataProvider {

    /** 
     * The backing map. The first key is the field name, the
     * second the property name. The contained maps may be null. They may also
     * contain null values.
     **/
    public final Map<String, Map<String, Object>> m_backing;

    /**
     * Constructor.
     * @param backing The backing map. See {@link #m_backing}. 
     *                   Must not be null.
     */
    public MapBackedFieldUserMetadataProvider(
        Map<String, Map<String, Object>> backing) {
        Reject.ifNull(backing);
        m_backing = backing;
    }
    
    /** {@inheritDoc} */
    public Map<String, Object> getAll(String fieldName) {
        return m_backing != null 
             ? m_backing.get(fieldName)
             : null;        
    }
}
