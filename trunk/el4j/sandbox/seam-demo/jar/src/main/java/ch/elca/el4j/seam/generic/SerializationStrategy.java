package ch.elca.el4j.seam.generic;

import java.io.Serializable;

/**
 * Serialization strategy interface, used by the generic ObjectManager
 * 
 * Purpose: (de)serialize primary key / identifiers to/from a string suitable
 * to be used as http request parameter.
 * 
 * For each kind of primary key /identifier type a concrete class, implementing
 * this interface should be created.
 *
 * @author  Baeni Christoph (CBA)
 */
public interface SerializationStrategy {
	String serialize(Serializable id);
	Serializable deserialize(String serializedId);
}
