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

/**
 * Chunk the server sends back to the client with changed data.
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
public class OfflineChunk implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7221647925276329293L;
	
	/** The changed objects to offline. */
	private final Object[] m_objects;

	/**
	 * Create the chunk.
	 * @param objects The objects.
	 */
	public OfflineChunk(Object[] objects) {
		m_objects = objects;
	}

	/**
	 * Get the objects.
	 * @return The objects.
	 */
	public Object[] getObjects() {
		return m_objects;
	}
}
