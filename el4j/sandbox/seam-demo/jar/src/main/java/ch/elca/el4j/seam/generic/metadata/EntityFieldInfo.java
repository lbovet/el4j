package ch.elca.el4j.seam.generic.metadata;

import org.hibernate.type.Type;


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
	public EntityFieldInfo(Class returnedClass, boolean required, Type hibernateType) {
		super(returnedClass, required, hibernateType);
	}

	public Class getRelatedClass() {
		return getReturnedClass();
	}
	
	@Override
	public String getTypeString() {
	    return "@entity";
	}
}
