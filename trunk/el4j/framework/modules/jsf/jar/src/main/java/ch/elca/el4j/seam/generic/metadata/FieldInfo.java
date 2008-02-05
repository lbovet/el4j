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

import org.hibernate.type.Type;

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
     * The type of the field as special String. This is generally the simple
     * name of the class or one of the following: @enum or @multiEntity
     */
    private String m_typeString;
    
    /**
     * The hibernate type.
     */
    private Type m_type;
    
    /**
     * The type of the field as Class.
     */
    private Class<?> m_returnedClass;
    
    /**
     * Is this field required?
     */
    private boolean m_required;
    
    /**
     * @param returnedClass    the type of the field as Class
     * @param required         <code>true</code> if field is required
     * @param hibernateType    the hibernate type of the field
     */
    public FieldInfo(Class<?> returnedClass, boolean required,
        Type hibernateType) {
        
        m_typeString = returnedClass.getSimpleName();
        m_returnedClass = returnedClass;
        m_required = required;
        m_type = hibernateType;
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

    /**
     * @return    the hibernate type
     */
    public Type getType() {
        return m_type;
    }
}
