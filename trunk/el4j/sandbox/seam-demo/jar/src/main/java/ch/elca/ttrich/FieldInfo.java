package ch.elca.ttrich;

/**
 * Field info. Provides metadata information about a field of an entity. 
 * 
 * @see EnumFieldInfo
 * @see RelationFieldInfo
 * @see EntityFieldInfo
 * @see MultiEntityFieldInfo
 *
 * @author  Baeni Christoph (CBA)
 */
public class FieldInfo {
	private String typeString;
	private Class returnedClass;
	private boolean required;
	
	
	public String getTypeString() {
		return typeString;
	}
	
	public Class getReturnedClass() {
		return returnedClass;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public FieldInfo(String type, Class returnedClass, boolean required) {
		this.typeString = type;
		this.returnedClass = returnedClass;
		this.required = required;
	}
}
