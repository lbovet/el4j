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
package ch.elca.el4j.services.persistence.hibernate.offlining.impl;

import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.Chunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.DeleteChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.OfflineChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ReturnChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.UpdateChunk;

/**
 * Offlining server. This runs on the server and recieves the offlining client's calls.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface OffliningServer {

	/**
	 * Synchronize a chunk.
	 * @param chunk The chunk.
	 * @return The return chunk. 
	 */
	ReturnChunk synchronizeChunk(Chunk chunk);
	
	/**
	 * Delete a chunk of entries.
	 * @param chunk The chunk.
	 * @return The result of deletion.
	 */
	ReturnChunk synchronizeDeleteChunk(DeleteChunk chunk);

	/**
	 * Check a chunk of unchanged entries for server updates.
	 * @param chunk The chunk.
	 * @return The updates.
	 */
	OfflineChunk synchronizeUpdateChunk(UpdateChunk chunk);
	
	/**
	 * Force the first object of the chunk to overwrite the server version
	 * with the local one if no dependent conflicts.
	 * @param chunk A chunk.
	 * @return The return chunk as for synchronize.
	 */
	ReturnChunk forceLocal(Chunk chunk);
}
