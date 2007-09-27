package ch.elca.ttrich;

/**
 * Enum Field info. Provides metadata information about an enum field of an entity. 
 * 
 * @see FieldInfo
 * 
 * @author  Baeni Christoph (CBA)
 */
public class EnumFieldInfo extends FieldInfo {
	public EnumFieldInfo(Class returnedClass, boolean required) {
		super("enum", returnedClass, required);
	}
	
	public EnumFieldInfo(String type, Class returnedClass, boolean required) {
		super(type, returnedClass, required);
	}

	public Class getEnumClass() {
		return getReturnedClass();
	}
	
	public Object[] getEnumList() {
		return getEnumClass().getEnumConstants();
	}
}
