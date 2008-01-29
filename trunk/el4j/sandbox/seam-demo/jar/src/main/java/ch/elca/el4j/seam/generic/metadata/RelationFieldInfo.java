package ch.elca.el4j.seam.generic.metadata;


/**
 * Relation Entity Field info. Provides metadata information about a relation field of an entity.
 * A relation field is a field, that references one or multiple other entities.
 * 
 * @see FieldInfo
 * @see EntityFieldInfo
 * @see MultiEntityFieldInfo
 * 
 * @author  Baeni Christoph (CBA)
 */
public interface RelationFieldInfo {
	public Class<?> getRelatedClass();
}
