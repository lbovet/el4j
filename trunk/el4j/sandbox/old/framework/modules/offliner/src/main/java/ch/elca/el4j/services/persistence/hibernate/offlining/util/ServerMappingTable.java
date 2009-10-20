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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;


/**
 * The "mapping table" for the server. Because it is only used temporarily during synchronization,
 * it is not stored in a separate database. Instead, it is maintained in-memory.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class ServerMappingTable {

	/**
	 * The list of all entries by primary key. (The "database".) 
	 */
	private Map<Integer, MappingEntry> m_entries;

	/**
	 * Constructor.
	 */
	public ServerMappingTable() {
		m_entries = new HashMap<Integer, MappingEntry>();
	}
	
	/*
	 * The public interface.
	 */
	
	/**
	 * Clear the table.
	 */
	public void clear() {
		m_entries.clear();
	}
	
	/**
	 * @return All entries.
	 */
	public MappingEntry[] getAll() {
		return m_entries.values().toArray(new MappingEntry[m_entries.size()]);
	}
	
	/**
	 * Save an entry.
	 * @param entry The entry.
	 */
	public void saveEntry(MappingEntry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Null mapping entry in table.");
		}
		m_entries.put(entry.getId(), entry);
	}
	
	/**
	 * Get an entry by local key.
	 * @param localKey The key.
	 * @return The mapping entry, or <code>null</code> if none present.
	 */
	public MappingEntry getLocal(Serializable localKey) {
		MappingEntry found = null;
		for (MappingEntry entry : m_entries.values()) {
			if (localKey.equals(entry.getLocalKey().getKey())) {
				found = entry;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Get an entry by remote key.
	 * @param remoteKey The key.
	 * @return The entry, or <code>null</code> if none found.
	 */
	public MappingEntry getRemote(Serializable remoteKey) {
		if (remoteKey == null) {
			throw new IllegalArgumentException("Remote Key is null");
		}
		MappingEntry found = null;
		for (MappingEntry entry : m_entries.values()) {
			if (entry.getRemoteKey() == null) {
				// Probably a legitimate case if it's new in the local db.
				continue;
			}
			if (remoteKey.equals(entry.getRemoteKey().getKey())) {
				found = entry;
				break;
			}
		}
		return found;
	}
	
	/*
	 * End of public interface.
	 */
}
