package ch.elca.ttrich;

/**
 * Multi Entity Field info. Provides metadata information about a multi entity field of an entity.
 * A multi entity field is a field that can references multiple other entities.  
 * 
 * @see FieldInfo
 * @see RelationFieldInfo
 * 
 * @author  Baeni Christoph (CBA)
 */
public class MultiEntityFieldInfo extends FieldInfo implements RelationFieldInfo {
	private Class relatedClass;

	public MultiEntityFieldInfo(String type, Class returnedClass, Class relatedClass, boolean required) {
		super(type, returnedClass, required);
		this.relatedClass = relatedClass;
	}

	public Class getRelatedClass() {
		return relatedClass;
	}
}
