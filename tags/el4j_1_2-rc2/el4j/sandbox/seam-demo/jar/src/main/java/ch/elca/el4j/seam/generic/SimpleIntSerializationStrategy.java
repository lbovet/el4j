package ch.elca.el4j.seam.generic;

import java.io.Serializable;

/**
 * Simple example serialization strategy for integers.
 *
 * @author  Baeni Christoph (CBA)
 */
public class SimpleIntSerializationStrategy implements SerializationStrategy {

	public Serializable deserialize(String serializedId) {
		return Integer.parseInt(serializedId);
	}

	public String serialize(Serializable id) {
		return id.toString();
	}
}
