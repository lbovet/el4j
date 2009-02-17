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

import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;


/**
 * Sent from the server back to the client after a synchronize. Contains the updated mappings and
 * all conflicts.
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
public class ReturnChunk implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 4082212778123321406L;

	/** The mapping entries. */
	private final MappingEntry[] m_mappings;
	
	/** The conflicts encountered. */
	private final Conflict[] m_conflicts;

	/**
	 * @param mappings The mappings.
	 * @param conflicts The conflicts.
	 */
	public ReturnChunk(MappingEntry[] mappings, Conflict[] conflicts) {
		m_mappings = mappings;
		m_conflicts = conflicts;
	}

	/**
	 * Get the mappings.
	 * @return The mappings.
	 */
	public MappingEntry[] getMappings() {
		return m_mappings;
	}

	/**
	 * Get the conflicts.
	 * @return The conflicts.
	 */
	public Conflict[] getConflicts() {
		return m_conflicts;
	}
	
	
}
