package ch.elca.el4j.seam.generic.metadata;

import org.hibernate.type.Type;

/**
 * Enum Field info. Provides metadata information about an enum field of an entity. 
 * 
 * @see FieldInfo
 * 
 * @author  Baeni Christoph (CBA)
 */
public class EnumFieldInfo extends FieldInfo {
	public EnumFieldInfo(Class<?> returnedClass, boolean required, Type hibernateType) {
		super(returnedClass, required, hibernateType);
	}

	public Class<?> getEnumClass() {
		return getReturnedClass();
	}
	
	public Object[] getEnumList() {
		return getEnumClass().getEnumConstants();
	}
	
	@Override
	public String getTypeString() {
	    return "@enum";
	}
}
