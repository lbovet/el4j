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
package ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors;

import java.io.Serializable;

import org.apache.log4j.Logger;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.OffliningStateWrappable;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningState;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * ServerSynchronizingVisitor is run on the server to resynchronize the objects it gets in Chunks. 
 * <ul><li><code>visit</code> tries to save its object to the database and throws a NodeException
 * if and only if that save fails. </li>
 * <li><code>preVisit</code> uses the mapping table to mark already seen and already conflicted objects.</li>
 * <li><code>markError</code> is necessary here; it marks an object the graph walker identifies as 
 * "dependent-error" as errors in the mapping table too to prevent it from being processed again
 * and to return a consistent mapping table to the client after the operation has run.</li></ul>
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
public class ServerSynchronizingVisitor implements NodeVisitor {

	/** A logger. */
	private static final Logger s_log = Logger.getLogger(ServerSynchronizingVisitor.class);
	
	/** The object wrapper. */
	protected final ObjectWrapper m_wrapper;
	
	/** The dao registry for saving. */
	protected final DaoRegistry m_daoRegistry;
	
	/**
	 * @param wrapper The object wrapper.
	 * @param registry The server dao registry.
	 */
	public ServerSynchronizingVisitor(ObjectWrapper wrapper, DaoRegistry registry) {
		m_wrapper = wrapper;
		m_daoRegistry = registry;
	}

	/** {@inheritDoc} */
	public Object markError(Object node, Object cause) {
		Mapped mapped = m_wrapper.wrap(Mapped.class, node);
		KeyedVersioned keyed = m_wrapper.wrap(KeyedVersioned.class, node);
		MappingEntry entry = mapped.getEntry();
		m_wrapper.wrap(OffliningStateWrappable.class, node).setState(OffliningState.CONFLICTED);
		keyed.setKey(entry.getRemoteKey().getKey());
		return Conflict.newDependent(node);
	}

	/** {@inheritDoc} 
	 * Check if the node is already processed or conflicted. 
	 */
	public NodeState preVisit(Object node) {
		NodeState color = NodeState.UNSEEN;
		
		Typed nodeTyped = m_wrapper.wrap(Typed.class, node);
		OffliningStateWrappable nodeState = m_wrapper.wrap(OffliningStateWrappable.class, node);
		
		if (nodeState.getState() == OffliningState.CONFLICTED) {
			s_log.debug("Dependent conflict.");
			color = NodeState.ERROR;
		} else if (nodeTyped.getType() == KeyType.REMOTE) {
			// A remote key - this object was already synchronized or conflicted.
			s_log.debug("Key is remote. Skipping.");
			color = NodeState.PROCESSED;
		}
		return color;
	}

	/** {@inheritDoc} */
	public void visit(Object node) throws NodeException {
		s_log.debug("Synchronize: running on " + node);
		
		KeyedVersioned nodeKeyed = m_wrapper
			.wrap(KeyedVersioned.class, node);
		Mapped nodeMapped = m_wrapper
			.wrap(Mapped.class, node);
		OffliningStateWrappable nodeState 
			= m_wrapper.wrap(OffliningStateWrappable.class, node);
		
		MappingEntry entry = nodeMapped.getEntry();
		
		try {
			Object objectVersion = nodeKeyed.getVersion();
			Object baseVersion = entry.getRemoteBaseVersion();
			s_log.debug("Object version " + objectVersion 
				+ ", Base Version " + baseVersion);
			OffliningState state = nodeState.getState();
			if (state == OffliningState.OFFLINED) {
				// No modification - so no need to commit.
				s_log.debug("Unchanged version, skipping.");
				nodeState.setState(OffliningState.PROCESSED);
			}
			process(node, entry);
		} catch (NodeException co) {
			// We need to record the conflict to prevent anyone else
			// from trying to reuse this object.
			s_log.debug("Processing conflict.");
			nodeState.setState(OffliningState.CONFLICTED);
			throw co;
		} finally {
			if (nodeState.getState() != OffliningState.CONFLICTED) {
				nodeState.setState(OffliningState.PROCESSED);
			}
			// Force remote key.
			nodeMapped.setEntry(entry);
		}
	}
	
	/**
	 * Delegate method to process an object based on its type.
	 * @param node The object.
	 * @param entry The mapping entry. Modified by method.
	 * @throws NodeException If there is a conflict while committing.
	 */
	private void process(Object node, MappingEntry entry) 
		throws NodeException {
		// Get a new reference as we can't modify the parameter.
		// Do not use node from here on.
		Object copy = node;
		KeyedVersioned keyed = m_wrapper.wrap(KeyedVersioned.class,	copy);
		OffliningStateWrappable nodeState = m_wrapper.wrap(OffliningStateWrappable.class, copy);
		
		s_log.debug("State: " + nodeState.getState().name());
		switch (nodeState.getState()) {
		
			case CHANGED:
				// Changed : remote key exists. Restore it and commit.
				Serializable localVersion = keyed.getVersion();
				keyed.setKey(entry.getRemoteKey().getKey());
				keyed.setVersion(entry.getRemoteBaseVersion());
				try {
					saveObject(copy);
				} catch (Exception e) {
					// A conflict occurred. Load the remote version and stick both in a
					// conflict object.
					Object remote = fetchRemote(entry.getRemoteKey());
					throw new NodeException(new Conflict(e, copy, remote));
				}
				entry.setRemoteBaseVersion(keyed.getVersion());
				entry.setLocalBaseVersion(localVersion);
				break;
				
			case PROCESSED:
				// Already processed in this update. Make sure the key is ok.
				keyed.setKey(entry.getRemoteKey().getKey());
				break;
				
			case NEW:
				// New object. Save, get its key and update the table.
				localVersion = keyed.getVersion();
				m_wrapper.wrap(Typed.class, copy).nullKey();
				try {
					saveObject(copy);
				} catch (Exception e) {
					throw new NodeException(new Conflict(e, copy, null));
				}
				entry.setLocalBaseVersion(localVersion);
				entry.setRemoteKey(new UniqueKey(keyed.getKey(), node.getClass()));
				entry.setRemoteBaseVersion(keyed.getVersion());
				break;
				
			case DELETED:
				// Don't delete anything here. The deletion runs after the updates.
				break;
			case CONFLICTED:
				throw new OfflinerInternalRTException("This should never happen.");
			case OFFLINED:
				throw new OfflinerInternalRTException("Offlined but unchanged items should"
					+ " not get here.");
			default: 
				throw new OfflinerInternalRTException("Switch over enum type failed.");
		}
	}
	
	/**
	 * Save an object back to database.
	 * @param obj The object to save.
	 * @return The saved object.
	 * @throws Exception The saveOrUpdate can throw any exception it likes,
	 * it will be caught and wrapped in a Conflict.
	 */
	protected Object saveObject(Object obj) throws Exception {
		s_log.debug("Saving object.");
		TotallyGenericDaoUtility.getDao(m_daoRegistry, obj).saveOrUpdate(obj);
		return obj;
	}
	
	/**
	 * Fetch the remote version of an object using its unique key.
	 * This is used if an object with local key is conflicted so we can return both versions
	 * in the conflict object. 
	 * If something goes wrong here, bail out and return null.
	 * The reason for this is that if it's not a version conflict, we don't want to abort the whole
	 * synchronizing process.
	 * @param remoteKey The UniqueKey for the remote object.
	 * @return The remote object.
	 */
	private Object fetchRemote(UniqueKey remoteKey) {
		Object o;
		try {
			o = TotallyGenericDaoUtility.getDao(m_daoRegistry, remoteKey.getObjectClass())
				.findById(remoteKey.getKey()); 
		} catch (Exception e) {
			o = null;
		}
		return o;
	}
}
