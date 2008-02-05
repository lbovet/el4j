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
package ch.elca.el4j.seam.generic.metadata;

import java.util.HashMap;

/**
 * Entity info. Provides metadata information about an entity. 
 * Currently this info is just FieldInfo for the fields of the entity.
 * 
 * @see FieldInfo
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author  Baeni Christoph (CBA)
 */
public class EntityInfo {
    /**
     * A map that stores a FieldInfo to each field (expressed as String).
     */
    private HashMap<String, FieldInfo> m_fieldInfos;

    /**
     * @param fieldInfos    the fieldInfo
     */
    public EntityInfo(HashMap<String, FieldInfo> fieldInfos) {
        super();
        m_fieldInfos = fieldInfos;
    }

    /**
     * @param fieldName    the field name
     * @return             the FieldInfo for this field
     */
    public FieldInfo getFieldInfo(String fieldName) {
        return m_fieldInfos.get(fieldName);
    }
}
