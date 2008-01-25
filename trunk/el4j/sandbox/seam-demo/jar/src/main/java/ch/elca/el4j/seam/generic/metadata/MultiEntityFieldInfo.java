package ch.elca.el4j.seam.generic.metadata;

import org.hibernate.type.Type;


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

	public MultiEntityFieldInfo(Class returnedClass, Class relatedClass, boolean required, Type hibernateType) {
		super(returnedClass, required, hibernateType);
		this.relatedClass = relatedClass;
	}

	public Class getRelatedClass() {
		return relatedClass;
	}
	
	@Override
	public String getTypeString() {
	    return "@multiEntity";
	}
}
