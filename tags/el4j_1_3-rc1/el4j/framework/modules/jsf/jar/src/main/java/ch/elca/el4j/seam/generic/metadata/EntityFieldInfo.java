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
 * Entity Field info. Provides metadata information about an entity field of an
 * entity. An entity field is a field that references one other entity. 
 * 
 * @see FieldInfo
 * @see RelationFieldInfo
 * @see MultiEntityFieldInfo
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
public class EntityFieldInfo extends FieldInfo implements RelationFieldInfo {
    /**
     * @param returnedClass    the type of the field as Class
     * @param required         <code>true</code> if field is required
     * @param hibernateType    the hibernate type of the field
     */
    public EntityFieldInfo(Class<?> returnedClass, boolean required,
        Type hibernateType) {
        super(returnedClass, required, hibernateType);
    }

    /** {@inheritDoc} */
    public Class<?> getRelatedClass() {
        return getReturnedClass();
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeString() {
        return "@entity";
    }
}
