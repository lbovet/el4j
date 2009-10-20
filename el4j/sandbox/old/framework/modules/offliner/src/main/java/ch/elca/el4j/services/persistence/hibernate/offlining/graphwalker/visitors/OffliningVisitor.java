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
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.GenericSerializableUtil;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * The OffliningVisitor is responsible for storing objects into the local database.
 * It is called on all objects in object graphs passed to <code>offline</code>. 
 * <p>
 * All objects passed to <code>offline</code> must have remote (database) keys. However, if an object graph contains
 * cycles the offlining visitor may see an object with a local key. As offlining changes object identity,
 * the graph walker cannot be aware of this.
 * <code>preVisit</code> returns PROCESSED if an object already has a local key, otherwhise UNSEEN.
 * <p>
 * <code>visit</code> works as follows: If the object's key is already mapped, we have a previous version in the 
 * local database. In this case, update the local object. If not, null the key and save the object to the local db.
 * The new key the object gets is its local key, record the objects' former remote key and new local key in the
 * mapping table along with its remote version.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class OffliningVisitor implements NodeVisitor {

	/**
	 * A logger.
	 */
	private static final Logger s_log = Logger.getLogger(OffliningVisitor.class);
	
	/**
	 * The object wrapper.
	 */
	protected final ObjectWrapper m_wrapper;
	
	/**
	 * The local dao registry. Saves objects (the actual offlining) and is used
	 * to load local versions on re-offlining. 
	 */
	protected final DaoRegistry m_localRegistry;
	
	/**
	 * @param wrapper The object wrapper.
	 * @param localRegistry The local dao registry.
	 */
	public OffliningVisitor(ObjectWrapper wrapper, DaoRegistry localRegistry) {
		m_wrapper = wrapper;
		m_localRegistry = localRegistry;
	}

	/** {@inheritDoc} */
	public Object markError(Object node, Object cause) {
		return Conflict.newDependent(Conflict.Phase.OFFLINE, node);
	}

	/** {@inheritDoc} */
	public NodeState preVisit(Object node) {
		KeyType type = m_wrapper.wrap(Typed.class, node).getType();
		switch (type) {
			case NULL:
				throw new OfflinerInternalRTException("Null key encountered in offline().");
			case LOCAL:
				return NodeState.PROCESSED;
			case REMOTE:
				return NodeState.UNSEEN;
			default:
				throw new OfflinerInternalRTException("Enum switch failed.");
		}
	}

	/** {@inheritDoc} */
	public void visit(Object node) throws NodeException {
		Mapped nodeMapped = m_wrapper.wrap(Mapped.class, node);
		MappingEntry entry = nodeMapped.getEntry();
		
		if (entry == null) {
			visitNew(node, nodeMapped);
		} else {
			visitExisting(node, entry);
			nodeMapped.setEntry(entry);
		}
	}
	
	/**
	 * Visit a new object (that does not have a mapping entry yet).
	 * @param node The node.
	 * @param nodeMapped The mapped interface for this node
	 * (convenience parameter, as we created the wrapper when we found the node to be new).
	 */
	private void visitNew(Object node, Mapped nodeMapped) {
		s_log.debug("Object is new.");

		KeyedVersioned nodeKeyed = m_wrapper.wrap(KeyedVersioned.class, node);
		UniqueKeyed nodeUniqueKeyed = m_wrapper.wrap(UniqueKeyed.class, node);
		
		// Offline it then store the key in the map.
		UniqueKey remoteId = nodeUniqueKeyed.getUniqueKey();
		Serializable remoteVersion = nodeKeyed.getVersion();
		Typed nodeTyped = m_wrapper.wrap(Typed.class, node);
		nodeTyped.nullKey();
		
		// Save the object, giving it the local key.
		saveObject(node);
		
		UniqueKey localId = nodeUniqueKeyed.getUniqueKey();
		s_log.debug("Object got local key " + localId);

		// New objects get local and remote base versions set to current version.
		nodeMapped.setEntry(new MappingEntry(localId, remoteId,
			nodeKeyed.getVersion(), remoteVersion));
	}
	
	/**
	 * Visit an object that exists in the local db. We distinguish three cases:
	 * <ol><li>The object exists under the same version in the local db. This happens when several
	 * offline operations in sequence are done. Ignore the object.</li>
	 * <li>The object exists under a newer version in the local db. This means we are re-offlining.
	 * Set the key/version to the local one.</li>
	 * <li>The object is newer on the server. This means we are updating from the server.
	 * Overwrite the local object with the remote one.</li></ol>
	 * <i>Note: The server only updates newer objects on the server (sync phase 3) if all offlined
	 * changes were successfully written back. Therefore we cannot overwrite an offlined and changed
	 * object by mistake.</i>
	 * @param node The node.
	 * @param entry The mapping entry (for convenience).
	 * @throws NodeException If the node is newer in the local than in the remote database.
	 */
	private void visitExisting(Object node, MappingEntry entry) throws NodeException {
		KeyedVersioned nodeKeyed = m_wrapper.wrap(
			KeyedVersioned.class,	node);
		UniqueKeyed uniqueKeyed = m_wrapper.wrap(
			UniqueKeyed.class, node);
		
		s_log.debug("Object is offlined under local key " + entry.getLocalKey());
		// We've seen this object before, it's in the local db.
		// Check if the version has been updated, if so update in the local db.
		// Otherwise skip it.
		Serializable version = entry.getRemoteBaseVersion();
		Serializable currentVersion = nodeKeyed.getVersion();
		
		
		int difference = GenericSerializableUtil.compare(version, currentVersion);
		
		if (difference < 0) {
			// The remote object has been changed and will replace the local one.
			s_log.debug("Updating changed object to local.");
			entry.setRemoteBaseVersion(nodeKeyed.getVersion());
			uniqueKeyed.setUniqueKey(entry.getLocalKey());
			nodeKeyed.setVersion(entry.getLocalBaseVersion());
			try {
				saveObject(node);
			} catch (Exception e) {
				throw new NodeException(new Conflict(Conflict.Phase.OFFLINE, e, null, node));
			}
			entry.setLocalBaseVersion(nodeKeyed.getVersion());
		} else if (difference == 0) {
			s_log.debug("Version unchanged, skipping save.");
			// We must set the key to the local one.
			nodeKeyed.setKey(entry.getLocalKey().getKey());
		} else {
			// The REMOTE version went down.
			// Complain.
			
			s_log.debug("Remote version higher in local db.");
			throw new NodeException(new Conflict(Conflict.Phase.OFFLINE, null, null, node));
		}
	}
	
	/**
	 * Save an object into the local db. Hibernate sets its id for us
	 * and may change the version too.
	 * @param obj The object.
	 */
	protected void saveObject(Object obj) {
		TotallyGenericDaoUtility.getDao(m_localRegistry, obj).saveOrUpdate(obj);
		s_log.info("Saving " + obj.getClass() + " " + obj.toString());
	}
}
