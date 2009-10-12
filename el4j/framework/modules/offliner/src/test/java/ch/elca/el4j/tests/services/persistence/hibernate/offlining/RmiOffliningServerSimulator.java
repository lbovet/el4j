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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import org.apache.log4j.Logger;

import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.Chunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.DeleteChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.OfflineChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ReturnChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.UpdateChunk;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OffliningServer;


/**
 * For testing purposes, this class is instantiated in the client context and provides a 
 * link to the server context's offlining server.
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
public class RmiOffliningServerSimulator implements OffliningServer {

	/** The logger. All calls to the server can be logged here. */
	private static final Logger s_log = Logger.getLogger(RmiOffliningServerSimulator.class);
	
	/** The target object. */
	private OffliningServer m_target;

	/**
	 * Setter for target.
	 * @param target The new target to set.
	 */
	public void setTarget(OffliningServer target) {
		m_target = target;
	}

	/** {@inheritDoc} */
	public ReturnChunk synchronizeChunk(Chunk chunk) {
		s_log.info("synchronizeChunk() size=" + chunk.getObjects().length);
		return m_target.synchronizeChunk(chunk);
	}

	/** {@inheritDoc} */
	public ReturnChunk synchronizeDeleteChunk(DeleteChunk chunk) {
		s_log.info("synchronizeDeleteChunk()");
		return m_target.synchronizeDeleteChunk(chunk);
	}

	/** {@inheritDoc} */
	public OfflineChunk synchronizeUpdateChunk(UpdateChunk chunk) {
		s_log.info("synchronizeUpdateChunk()");
		return m_target.synchronizeUpdateChunk(chunk);
	}

	/** {@inheritDoc} */
	public ReturnChunk forceLocal(Chunk chunk) {
		s_log.info("forceLocal()");
		return m_target.forceLocal(chunk);
	}
}
