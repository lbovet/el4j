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

/**
 * Field info. Provides metadata information about a field of an entity. 
 * 
 * @see EnumFieldInfo
 * @see RelationFieldInfo
 * @see EntityFieldInfo
 * @see MultiEntityFieldInfo
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
public class FieldInfo {
    /**
     * The type of the field as special String (TODO used for tags???).
     */
    private String m_typeString;
    
    /**
     * The type of the field as Class.
     */
    private Class<?> m_returnedClass;
    
    /**
     * Is this field required?
     */
    private boolean m_required;
    
    /**
     * @param type             the type of the field as special String
     * @param returnedClass    the type of the field as Class
     * @param required         <code>true</code> if field is required
     */
    public FieldInfo(String type, Class<?> returnedClass, boolean required) {
        this.m_typeString = type;
        this.m_returnedClass = returnedClass;
        this.m_required = required;
    }

    /**
     * @return    the type of the field as special String
     */
    public String getTypeString() {
        return m_typeString;
    }

    /**
     * @return    the type of the field as Class
     */
    public Class<?> getReturnedClass() {
        return m_returnedClass;
    }

    /**
     * @return    <code>true</code> if field is required
     */
    public boolean isRequired() {
        return m_required;
    }
}
