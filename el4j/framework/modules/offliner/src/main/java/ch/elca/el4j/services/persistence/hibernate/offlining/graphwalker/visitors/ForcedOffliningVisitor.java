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

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.offlining.Conflict;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeException;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.TotallyGenericDaoUtility;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Version of the offlining visitor that forces an object to be overwritten.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class ForcedOffliningVisitor extends OffliningVisitor {

	/** The object to force. */
	private final Object m_forceObject;

	/**
	 * Create the visitor.
	 * @param wrapper The object wrapper.
	 * @param localRegistry The local dao registry.
	 * @param forceObject The object to force.
	 */
	public ForcedOffliningVisitor(ObjectWrapper wrapper, DaoRegistry localRegistry,
		Object forceObject) {
		super(wrapper, localRegistry);
		m_forceObject = forceObject;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(Object node) throws NodeException {
		if (node != m_forceObject) {
			// If it's not our special one,
			// offline as usual.
			super.visit(node);
			return;
		}
		
		// Ok, it is ours.
		// The fact that we are here means no dependent conflicts were encountered
		// along the way.

		Mapped nodeMapped = m_wrapper.wrap(Mapped.class, node);
		MappingEntry entry = nodeMapped.getEntry();
	
		if (entry == null) {
			// Trying to force an object that's not offline?
			// Do it anyway.
			super.visit(node);
			return;
		}
		
		KeyedVersioned nodeKeyed = m_wrapper.wrap(
			KeyedVersioned.class,	node);
		
		// Save the remote version. If we succeed, this becomes the remote base.
		Serializable remoteVersion = nodeKeyed.getVersion();
		
		// Load the local instance, and get its version.
		Serializable localKey = entry.getLocalKey().getKey();
		Object localInstance = TotallyGenericDaoUtility.getDao(
			m_localRegistry, entry.getLocalKey().getObjectClass()).findById(localKey);
		Serializable localVersion = m_wrapper.wrap(KeyedVersioned.class,
			localInstance).getVersion();
		
		// Set the key and version to the local one to allow an overwrite.
		nodeKeyed.setKey(localKey);
		nodeKeyed.setVersion(localVersion);
		
		// Try and save. This should work unless there's something else wrong
		// in the db.
		try {
			saveObject(node);
		} catch (Exception e) {
			throw new NodeException(new Conflict(Conflict.Phase.FORCE, e, null, node));
		}
		
		// Success. Save the mapping entry back.
		// Base versions become those the databases hold.
		entry.setLocalBaseVersion(nodeKeyed.getVersion());
		entry.setRemoteBaseVersion(remoteVersion);
		
		nodeMapped.setEntry(entry);
	}
	
	
	
	
}
