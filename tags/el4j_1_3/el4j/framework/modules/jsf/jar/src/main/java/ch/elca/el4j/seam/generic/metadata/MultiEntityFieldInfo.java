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
 * Multi Entity Field info. Provides metadata information about a multi entity
 * field of an entity. A multi entity field is a field that can references
 * multiple other entities.
 * 
 * @see FieldInfo
 * @see RelationFieldInfo
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA)
 */
public class MultiEntityFieldInfo extends FieldInfo implements
    RelationFieldInfo {
    
    /**
     * The related class for a field.
     */
    private Class<?> m_relatedClass;

    /**
     * @param required
     * @param returnedClass    the type of the field as Class
     * @param relatedClass     the related class
     * @param required         <code>true</code> if field is required
     * @param hibernateType    the hibernate type of the field
     */
    public MultiEntityFieldInfo(Class<?> returnedClass, Class<?> relatedClass,
        boolean required, Type hibernateType) {
        super(returnedClass, required, hibernateType);
        this.m_relatedClass = relatedClass;
    }

    /** {@inheritDoc} */
    public Class<?> getRelatedClass() {
        return m_relatedClass;
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeString() {
        return "@multiEntity";
    }
}
