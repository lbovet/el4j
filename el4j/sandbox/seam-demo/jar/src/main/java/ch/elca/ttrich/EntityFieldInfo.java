package ch.elca.ttrich;

/**
 * Entity Field info. Provides metadata information about an entity field of an entity.
 * An entity field is a field that references one other entity. 
 * 
 * @see FieldInfo
 * @see RelationFieldInfo
 * @see MultiEntityFieldInfo
 * 
 * @author  Baeni Christoph (CBA)
 */
public class EntityFieldInfo extends FieldInfo implements RelationFieldInfo{
	public EntityFieldInfo(String type, Class returnedClass, boolean required) {
		super(type, returnedClass, required);
	}

	public Class getRelatedClass() {
		return getReturnedClass();
	}
}
