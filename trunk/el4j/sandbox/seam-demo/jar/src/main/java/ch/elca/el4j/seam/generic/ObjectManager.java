package ch.elca.el4j.seam.generic;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import ch.elca.el4j.seam.generic.metadata.EntityInfoBase;
import ch.elca.el4j.seam.generic.metadata.EnumFieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldInfo;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.j4persist.generic.dao.ConvenienceGenericDao;
import ch.elca.j4persist.generic.dao.impl.DefaultDaoRegistry;

/**
 * Object Manager. Somewhat similiar to Seam's EntityHome and EntityQuery.
 * Uses EL4J GenericDao for database operation
 * To be used from within JSF/Seam Pages
 * 
 * CAUTION: Minor parts of this code are hibernate specific, thus not portable!
 *
 * @author  Baeni Christoph (CBA)
 */
@Name("objectManager")
@Scope(CONVERSATION)
public class ObjectManager implements Serializable {
	@In("#{daoRegistry}")
	private DefaultDaoRegistry m_DaoRegistry;
	
	@In("#{sessionFactory}")
	private SessionFactory m_SessionFactory;
	
	@In("#{entityInfoBase}")
	private EntityInfoBase m_EntityInfoBase;
    
	@In(create=true)
	private Searching searching;
	
	private Serializable m_EntityId;
	private Object m_Entity;
	private boolean m_Managed;
	
	private HashMap<String,HashMap<String,Object[]>> m_Entities = new HashMap<String,HashMap<String,Object[]>>();
	
	private SerializationStrategy m_SerializationStrategy = new SimpleIntSerializationStrategy();
	private HashMap<Object,String> m_CachedSerializedIds = new HashMap<Object,String>();

	private String m_EntityClassName;
	
	@RequestParameter
	public void setEntityId(String parameter) {
    	if ((parameter == null) || (parameter.equals(""))) {
    		return;
    	}
    	m_EntityId = deserializeId(parameter);
	}
	
	@Begin(join=true)
	public Object getInstance(String entityClassName) {
		m_EntityClassName = entityClassName;
		ConvenienceGenericDao dao = getDao(m_EntityClassName);
		
		if (m_Entity == null) {
			if (m_EntityId != null) {
				m_Entity = dao.findById(m_EntityId);
				m_Managed = true;
			} else {
				try {
					m_Entity = Class.forName(entityClassName).newInstance();
				} catch (InstantiationException e) {
					return null;
				} catch (IllegalAccessException e) {
					return null;
				} catch (ClassNotFoundException e) {
					return null;
				}
			}
		}
		
		return m_Entity;
	}
	
	public boolean getManaged() {
		return m_Managed;
	}
	
	public String serializeId(Object entity) {
		if (m_CachedSerializedIds.get(entity) == null) {
			ClassMetadata metadata = m_SessionFactory.getClassMetadata(entity.getClass());
						
			Serializable id = metadata.getIdentifier(entity, EntityMode.POJO);
			String serializedId = m_SerializationStrategy.serialize(id);
			
			m_CachedSerializedIds.put(entity, serializedId);
		}

		return m_CachedSerializedIds.get(entity);
	}
	
	private Serializable deserializeId(String serializedId) {
		return m_SerializationStrategy.deserialize(serializedId);
	}
	
	@End(beforeRedirect=true)
	public String persistAndRedirect(String viewId) {
		persist();
		
		return viewId;
	}
	
	public String persist() {
		ConvenienceGenericDao dao = getDao(m_EntityClassName);
		
		dao.saveOrUpdate(m_Entity);
		
		return null;
	}
	
	@End(beforeRedirect=true)
	public String updateAndRedirect(String viewId) {
		update();
		
		return viewId;
	}
	
	public String update() {
		ConvenienceGenericDao dao = getDao(m_EntityClassName);
		
		dao.saveOrUpdate(m_Entity);
		
		return null;
	}
	
	@End(beforeRedirect=true)
	public String removeAndRedirect(String viewId) {
		remove();
		
		return viewId;
	}
	
	public String remove() {
		ConvenienceGenericDao dao = getDao(m_EntityClassName);
		
		dao.deleteObject(m_Entity);
		
		return null;
	}
	
	//x experimental
	//x really belongs here? (but where else? utils is not a seam comp!)
	@End(beforeRedirect=true)
	public String redirectTo(String viewId) {
		return viewId;
	}
	
	/* CAUTION: This function obviously does not scale at all!!!
	 *          But as long as we have no table component with better pagination support
	 *          than rich:DataTable we cannot make use of anything else than this!
	 *          Sad but true... 
	 */
	//x @Begin(join=true)
	public Object[] getEntities(String entityClassName) {
		String restrict = searching.getRestrictionString();
		
		if (m_Entities.get(entityClassName) == null) {
			m_Entities.put(entityClassName, new HashMap<String,Object[]>());
		}
		
		HashMap<String,Object[]> restrictionMap = m_Entities.get(entityClassName);
		if (restrictionMap.get(restrict) == null) {
			ConvenienceGenericDao dao = getDao(entityClassName);
			QueryObject queryObject = getQuery(entityClassName);
			
			restrictionMap.put(restrict, dao.findByQuery(queryObject).toArray());
		}
		
		return restrictionMap.get(restrict);
	}
	
	private QueryObject getQuery(String entityClassName) {
		Map<String,String> restrictions = searching.getRestrictionMap();
		Class entityClass;
		try {
			entityClass = Class.forName(entityClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		QueryObject queryObject = new QueryObject(entityClass);
				
		for (String fieldName: restrictions.keySet()) {
			String value = restrictions.get(fieldName);
			Criteria criteria;
			
			FieldInfo fieldInfo = m_EntityInfoBase.getEntityInfo(entityClassName).getFieldInfo(fieldName);
			String fieldType = fieldInfo.getTypeString();
			
			if (fieldType.equals("boolean")) {
				criteria = ComparisonCriteria.equals(fieldName, Boolean.parseBoolean(value));
			} else if (fieldType.equals("enum")) {
				if ((value == null) || (value.equals(""))) {
					criteria = null;
				} else {
					Class enumClass = ((EnumFieldInfo)fieldInfo).getEnumClass();
					Enum enumValue =  Enum.valueOf(enumClass, value);

					criteria = ComparisonCriteria.equals(fieldName, enumValue);
				}
			} else {
				criteria = null;
			}
			
			if (criteria != null) {
				queryObject.addCriteria(criteria);
			}
		}
		
		return queryObject;
	}
	
	protected ConvenienceGenericDao getDao(String entityClassName) {
		try {
			return (ConvenienceGenericDao) m_DaoRegistry.getFor(Class.forName(entityClassName));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
