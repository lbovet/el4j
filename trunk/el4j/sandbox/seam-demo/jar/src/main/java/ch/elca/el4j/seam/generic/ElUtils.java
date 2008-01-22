package ch.elca.el4j.seam.generic;

import java.util.List;

import org.jboss.seam.Component;

import ch.elca.el4j.seam.generic.metadata.EntityInfoBase;
import ch.elca.el4j.seam.generic.metadata.EnumFieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldLists;
import ch.elca.el4j.seam.generic.metadata.RelationFieldInfo;

/**
 * The heart of the generic master/detail views.
 * 
 * A facade, consisting of functions intended to be exposed to and used
 * from JSF view templates as a spring bean via Seam's DelegatingVariableResolver.
 * 
 * The functionality provided includes (among others): automatic computation of
 * entity type, entity field list, field types etc.
 *
 * !!! CAUTION !!!
 * The code herein heavily relies on the field information provided by the FieldInfoBase
 * which in turn is hibernate specific and depends on the metadata provided by hibernate.
 * There is quite a bit of obscure "magic" involved, several implicit assumptions are made
 * and this implementation contains some highly unportable, fragile hacks.
 * !!! CAUTION !!!
 *
 * @author  Baeni Christoph (CBA)
 */
public class ElUtils {
	private EntityShortNameMapping m_ShortNameMapping;
	private EntityInfoBase m_EntityInfoBase;
	private FieldLists m_FieldLists;
	
	public void setShortNameMapping(EntityShortNameMapping entityShortnameMapping) {
		m_ShortNameMapping = entityShortnameMapping;
	}
	
	public void setEntityInfoBase(EntityInfoBase entityInfoBase) {
		m_EntityInfoBase = entityInfoBase;
	}
	
	public void setFieldLists(FieldLists fieldLists) {
		m_FieldLists = fieldLists;
	}
	
	/* Boolean parsing support */
	
	public boolean getBooleanValue(String booleanString) {
		if (booleanString == null) {
			return false;
		}
	
		if (booleanString.equalsIgnoreCase("yes") || booleanString.equalsIgnoreCase("y")) {
			return true;
		} else if (booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("t")) {
			return true;
		} else if (booleanString.equalsIgnoreCase("1")) {
			return true;
		}
		
		return false;
	}
	
	/* Shortname mapping based support */
	
	public String getEntityClassName(String entityShortName) {		
		return m_ShortNameMapping.getClassName(entityShortName);
	}
	 
	public String getEntityShortName(String entityClassName) {
		return m_ShortNameMapping.getShortName(entityClassName);
	}
	
	/* FieldLists based support */
	
	public String[] computeFieldList(String entityClassName, String shown, String hidden) {
		return m_FieldLists.computeFieldList(entityClassName, shown, hidden);
	}
	
	public TableColumn[] computeColumnList(String entityClassName, String shown, String hidden) {
		return m_FieldLists.computeColumnList(entityClassName, shown, hidden);
	}
	
	/* FieldInfo based support */
	
	private FieldInfo getFieldInfo(String entityClassName, String fieldName) {
		return m_EntityInfoBase.getEntityInfo(entityClassName).getFieldInfo(fieldName);
}
	
	public String getFieldType(String entityClassName, String fieldName) {
		return getFieldInfo(entityClassName, fieldName).getTypeString();
	}
	
	public String getColumnType(String entityClassName, String columnName) {
		return getFieldType(entityClassName, columnName);
	}
	
	public boolean isRequired(String entityClassName, String fieldName) {
        return getFieldInfo(entityClassName, fieldName).isRequired();
    }
	
	public Object[] getEnumList(String entityClassName, String fieldName) {
		EnumFieldInfo enumFieldInfo = (EnumFieldInfo)(getFieldInfo(entityClassName, fieldName));
		
		return enumFieldInfo.getEnumList();
	}
	
	/*public Object[] getEnumList(Object entity, String fieldName) {
		return getEnumList(entity.getClass().getName(), fieldName);
	}*/
	
	/* Default master/detail page view id support */
	
	public String getDefaultDetailPage(String entityShortName) {
		return PageViewIDHelper.getDefaultDetailPage(entityShortName);
	}
	
	public String getDefaultMasterPage(String entityShortName) {
		return PageViewIDHelper.getDefaultMasterPage(entityShortName);
	}
	
	public String deriveEntityShortName() {
		return PageViewIDHelper.deriveEntityShortName();
	}
	
	/* Relation support */
	
	public String getRelatedEntityClassName(String entityClassName, String fieldName) {
		FieldInfo fieldInfo = getFieldInfo(entityClassName, fieldName);
		Class relatedClass = ((RelationFieldInfo)fieldInfo).getRelatedClass();
		
		return relatedClass.getName();
	}
	
	public String getRelatedEntityShortName(String entityClassName, String fieldName) {
		return getEntityShortName(getRelatedEntityClassName(entityClassName, fieldName));
	}
	
	public Object[] getRelatedEntities(String entityClassName, String fieldName) {
		String relatedEntityClassName = getRelatedEntityClassName(entityClassName, fieldName);
		EntityManager entityManager = (EntityManager)Component.getInstance("entityManager");
		
		return entityManager.getAllEntities(relatedEntityClassName);
	}
	
	public String getDefaultRelatedDetailPage(String entityClassName, String fieldName) {
		String relatedEntityShortName = getRelatedEntityShortName(entityClassName, fieldName);
		
		return getDefaultDetailPage(relatedEntityShortName);
	}
	
	/* Filter support */
	
	public String[] parseFilterList(String filterOn) {
		return m_FieldLists.parseList(filterOn);
	}
	
	public String getFilterType(String entityClassName, String filterName) {
		return getFieldType(entityClassName, filterName);
	}
	
	public Object[] getFilterEnumList(String entityClassName, String filterName) {
		return getEnumList(entityClassName, filterName);
	}
}