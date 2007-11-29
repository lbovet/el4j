package ch.elca.el4j.seam.generic.metadata;

import java.util.HashMap;

/**
 * Entity info. Provides metadata information about an entity. 
 * Currently this info is just FieldInfo for the fields of the entity.
 * 
 * @see FieldInfo
 *
 * @author  Baeni Christoph (CBA)
 */
public class EntityInfo {
	private HashMap<String, FieldInfo> fieldInfos;
	
	public EntityInfo(HashMap<String, FieldInfo> fieldInfos) {
		super();
		this.fieldInfos = fieldInfos;
	}

	public FieldInfo getFieldInfo(String fieldName) {
		return fieldInfos.get(fieldName);
	}
}
