/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.hibernate.offlining.chunk;

import java.io.Serializable;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;


/**
 * Chunks contain objects and their mapping metadata and are transferred from the client to the server
 * during synchronization.
 * <p>
 * Invariant: The sizes of the object and mapping arrays are equal, and the mapping entry for m_objects[i]
 * is in m_mappings[i].
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class Chunk implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 141196155228433523L;

	/** The objects. */
	private final Object[] m_objects;
	
	/** The mapping entries. */
	private final MappingEntry[] m_mappings;

	/**
	 * @param objects The objects.
	 * @param mappings the entries.
	 */
	public Chunk(Object[] objects, MappingEntry[] mappings) {
		m_objects = objects;
		m_mappings = mappings;
	}

	/**
	 * Get the objects.
	 * @return The objects.
	 */
	public Object[] getObjects() {
		return m_objects;
	}

	/**
	 * Get the mappings.
	 * @return The mappings.
	 */
	public MappingEntry[] getMappings() {
		return m_mappings;
	}
}
