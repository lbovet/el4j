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
 * Sent by the client to the server with all unchanged mapping entries.
 * The server responds by checking each one and sending back a chunk of updates.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class UpdateChunk implements Serializable {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 3958450823380653041L;
	
	/** The mapping entries to check. */
	private final MappingEntry[] m_entries;

	/**
	 * @param entries The mapping entries.
	 */
	public UpdateChunk(MappingEntry[] entries) {
		m_entries = entries;
	}

	/**
	 * Get the entries.
	 * @return The entries.
	 */
	public MappingEntry[] getEntries() {
		return m_entries;
	}
}
