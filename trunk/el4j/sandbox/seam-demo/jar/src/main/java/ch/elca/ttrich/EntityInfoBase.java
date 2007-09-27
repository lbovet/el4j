package ch.elca.ttrich;

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
 * CAUTION: This code is hibernate specific, thus not portable!
 * 
 * @see EntityInfo
 *
 * @author  Baeni Christoph (CBA)
 */
public class EntityInfoBase {
	private SessionFactory m_SessionFactory;
	
	private HashMap<String, EntityInfo> m_EntityInfos = new HashMap<String, EntityInfo>();
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		m_SessionFactory = sessionFactory;
	}
	
	private FieldInfo createFieldInfo(ClassMetadata metadata, String fieldName, boolean required) {
		Type hibernateType = metadata.getPropertyType(fieldName);
		Class returnedClass = hibernateType.getReturnedClass();
		
		if (hibernateType instanceof EntityType) {
			return new EntityFieldInfo("entity", returnedClass, required);
		} else if (hibernateType instanceof CollectionType) {
			CollectionType collectionType = (CollectionType)hibernateType;
			Class relatedClass = collectionType.getElementType(
					(SessionFactoryImplementor)m_SessionFactory).getReturnedClass();
			
			return new MultiEntityFieldInfo("multiEntity", returnedClass, relatedClass, required);
		} else if (hibernateType.getName().equals("org.hibernate.type.EnumType")) {
			return new EnumFieldInfo("enum", returnedClass, required);
		} else {
			String type; 
			
			if (String.class.isAssignableFrom(returnedClass)) {
				type = "string";
			} else if (Integer.class.isAssignableFrom(returnedClass) || Long.class.isAssignableFrom(returnedClass)) {
				type = "integer";
			} else if (Boolean.class.isAssignableFrom(returnedClass)) {
				type = "boolean";
			} else if (returnedClass.isPrimitive()) {
				if ((returnedClass == Integer.TYPE) || (returnedClass == Long.TYPE)) {
					type = "integer";
				} else if (returnedClass == Boolean.TYPE) {
					type = "boolean";
				} else {
					throw new RuntimeException("Unknown type: " + returnedClass.getName());
				}
			} else {
				throw new RuntimeException("Unknown type: " + returnedClass.getName());
			}
			
			return new FieldInfo(type, returnedClass, required);
		}
	}
	
	private HashMap<String, FieldInfo> computeFieldInfos(Class entityClass) {
		HashMap<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();
		ClassMetadata metadata = m_SessionFactory.getClassMetadata(entityClass);
		String[] fieldList = metadata.getPropertyNames();
		boolean nullability[] = metadata.getPropertyNullability();
		
		for (int i = 0; i < fieldList.length; i++) {
			String fieldName = fieldList[i];
			boolean required = !nullability[i];
						
			fieldInfos.put(fieldName, createFieldInfo(metadata, fieldName, required));
		}
		
		return fieldInfos;
	}
	
	private EntityInfo computeEntityInfo(String entityClassName) {
		Class entityClass;
		try {
			entityClass = Class.forName(entityClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Entity Class not found: " + entityClassName);
		}
		
		return new EntityInfo(computeFieldInfos(entityClass));
	}
	
	public EntityInfo getEntityInfo(String entityClassName) {
		if (m_EntityInfos.get(entityClassName) == null) {
			m_EntityInfos.put(entityClassName, computeEntityInfo(entityClassName));
		}
		
		return m_EntityInfos.get(entityClassName);
	}
	
	public EntityInfo getEntityInfo(Class entityClass) {
		return getEntityInfo(entityClass.getName());
	}
}
