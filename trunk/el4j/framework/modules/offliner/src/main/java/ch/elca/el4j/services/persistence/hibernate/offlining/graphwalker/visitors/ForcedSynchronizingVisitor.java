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
 * Special synchronizing visitor that can force an overwrite. 
 * Used for conflict resolution.
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
public class ForcedSynchronizingVisitor extends ServerSynchronizingVisitor {

	/** The object to force. */
	private final Object m_forceObject;
	
	/**
	 * Create the visitor.
	 * @param wrapper The object wrapper.
	 * @param registry The dao registry.
	 * @param obj The object to force.
	 */
	public ForcedSynchronizingVisitor(ObjectWrapper wrapper, DaoRegistry registry, Object obj) {
		super(wrapper, registry);
		m_forceObject = obj;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(Object node) throws NodeException {
		// Check if it's our object. Comparing references
		// directly saves some code.
		if (node == m_forceObject) {
			// The fact that we're here means all children went ok.
			// As we're forcing, ignore all offline state or other indicators.
			KeyedVersioned nodeKeyed = m_wrapper.wrap(
				KeyedVersioned.class, node);
			Mapped nodeMapped = m_wrapper.wrap(Mapped.class, node);
			Serializable localVersion = nodeKeyed.getVersion();
			MappingEntry entry = nodeMapped.getEntry();
			Serializable remoteKey = entry.getRemoteKey().getKey();
			Class<?> objectClass = entry.getRemoteKey().getObjectClass();
			
			Object databaseInstance = TotallyGenericDaoUtility
				.getDao(m_daoRegistry, objectClass).findById(remoteKey);
			Serializable currentDbVer = m_wrapper.wrap(
				KeyedVersioned.class, databaseInstance).getVersion();
			nodeKeyed.setVersion(currentDbVer);
			nodeKeyed.setKey(remoteKey);
			
			try {
				saveObject(node);
			} catch (Exception e) {
				throw new NodeException(new Conflict(Conflict.Phase.FORCE, e, node, null));
			}
			// The save was ok, so update the mapping entry.
			entry.setRemoteBaseVersion(nodeKeyed.getVersion());
			entry.setLocalBaseVersion(localVersion);
			nodeMapped.setEntry(entry);
		} else {
			// On everything else, just sync normally.
			super.visit(node);
		}
	}
}
