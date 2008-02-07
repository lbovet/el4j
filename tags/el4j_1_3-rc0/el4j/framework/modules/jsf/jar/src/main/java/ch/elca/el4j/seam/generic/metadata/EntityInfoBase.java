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

import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

/**
 * Entity info base. Provides (and computes) EntityInfo for each entity.
 * 
 * @see EntityInfo
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA)
 * @author Wismer Stefan (SWI)
 */
public class EntityInfoBase {
    /**
     * The hibernate session factory.
     */
    private SessionFactory m_sessionFactory;

    /**
     * The entity type information provider.
     */
    private HashMap<String, EntityInfo> m_entityInfos
        = new HashMap<String, EntityInfo>();

    /**
     * @param sessionFactory    the hibernate session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        m_sessionFactory = sessionFactory;
    }

    /**
     * @param metadata     the hibernate class metadata
     * @param fieldName    the field name
     * @param required     <code>true</code> if field is required
     * @return             the corresponding field info
     */
    private FieldInfo createFieldInfo(ClassMetadata metadata, String fieldName,
        boolean required) {
        
        Type hibernateType = metadata.getPropertyType(fieldName);
        Class<?> returnedClass = hibernateType.getReturnedClass();

        FieldInfo result = null;
        if (hibernateType instanceof EntityType) {
            result = new EntityFieldInfo(
                returnedClass, required, hibernateType);
        } else if (hibernateType instanceof CollectionType) {
            CollectionType collectionType = (CollectionType) hibernateType;
            Class<?> relatedClass = collectionType.getElementType(
                (SessionFactoryImplementor) m_sessionFactory)
                .getReturnedClass();

            result = new MultiEntityFieldInfo(returnedClass, relatedClass,
                required, hibernateType);
        } else if (hibernateType.getName()
            .equals("org.hibernate.type.EnumType")) {
            result = new EnumFieldInfo(returnedClass, required, hibernateType);
        } else {
            result = new FieldInfo(returnedClass, required, hibernateType);
        }
        return result;
    }

    /**
     * @param entityClass   the entity class as Class
     * @return              field info about the entity class as {@link HashMap}
     */
    private HashMap<String, FieldInfo> computeFieldInfos(Class<?> entityClass) {
        HashMap<String, FieldInfo> fieldInfos
            = new HashMap<String, FieldInfo>();
        ClassMetadata metadata = m_sessionFactory.getClassMetadata(entityClass);
        String[] fieldList = metadata.getPropertyNames();
        boolean[] nullability = metadata.getPropertyNullability();

        for (int i = 0; i < fieldList.length; i++) {
            String fieldName = fieldList[i];
            boolean required = !nullability[i];

            fieldInfos.put(fieldName, createFieldInfo(metadata, fieldName,
                required));
        }

        return fieldInfos;
    }

    /**
     * @param entityClassName    the entity class as String
     * @return                   entity info about the entity class
     *                           as {@link FieldInfo}
     */
    private EntityInfo computeEntityInfo(String entityClassName) {
        Class<?> entityClass;
        try {
            entityClass = Class.forName(entityClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Entity Class not found: "
                + entityClassName);
        }

        return new EntityInfo(computeFieldInfos(entityClass));
    }

    /**
     * @param entityClassName    the entity class as String
     * @return                   entity info about the entity class
     */
    public EntityInfo getEntityInfo(String entityClassName) {
        if (m_entityInfos.get(entityClassName) == null) {
            m_entityInfos.put(entityClassName,
                computeEntityInfo(entityClassName));
        }

        return m_entityInfos.get(entityClassName);
    }

    /**
     * @param entityClass    the entity class as Class
     * @return               entity info about the entity class
     */
    public EntityInfo getEntityInfo(Class<?> entityClass) {
        return getEntityInfo(entityClass.getName());
    }
}
