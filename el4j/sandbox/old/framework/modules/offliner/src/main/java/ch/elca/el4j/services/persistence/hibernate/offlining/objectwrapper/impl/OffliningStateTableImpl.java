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
package ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.impl;

import java.io.Serializable;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.OffliningStateWrappable;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningState;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningStateTable;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Implementation of OffliningStateWrappable using a table. 
 * <p>
 * <i>This is currently used on both the server and the client, though two separate tables are used.</i>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class OffliningStateTableImpl extends AbstractWrapper implements
	OffliningStateWrappable {

	/** The table to use. */
	private final OffliningStateTable m_table;
	
	/**
	 * Constructor for prototype.
	 * @param table The table to use.
	 */
	public OffliningStateTableImpl(OffliningStateTable table) {
		m_table = table;
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!m_wrapper.wrappablePresent(UniqueKeyed.class)) {
			throw new ObjectWrapperRTException("Requires implementation of UniqueKeyed.");
		}
		if (!m_wrapper.wrappablePresent(Mapped.class)) {
			throw new ObjectWrapperRTException("Requires implementation of Mapped.");
		}
		if (!m_wrapper.wrappablePresent(KeyedVersioned.class)) {
			throw new ObjectWrapperRTException("Requires implementation of KeyedVersioned.");
		}
		if (!m_wrapper.wrappablePresent(Typed.class)) {
			throw new ObjectWrapperRTException("Requires implementation of Typed.");
		}
	}

	/** {@inheritDoc} */
	public OffliningState getState() {
		UniqueKey localKey = m_wrapper.wrap(UniqueKeyed.class, m_target).getLocalUniqueKey();
		if (m_table.containsKey(localKey)) {
			return m_table.get(localKey);
		}
		
		/*
		 * Default resolution algorithm.
		 */
		MappingEntry entry = m_wrapper.wrap(Mapped.class, m_target).getEntry();
		Serializable currentVersion = m_wrapper.wrap(KeyedVersioned.class, m_target).getVersion();
		OffliningState state = null;

		if (entry == null || entry.getRemoteKey() == null) {
			// No remote key yet- must be new locally.
			state = OffliningState.NEW;
		} else if (entry.getDeleteVersion() > 0) {
			// Delete version set. Object is marked for deletion.
			state = OffliningState.DELETED;
		} else {
			KeyType type = m_wrapper.wrap(Typed.class, m_target).getType();
			Serializable baseVersion;
			if (type == KeyType.LOCAL) {
				baseVersion = entry.getLocalBaseVersion();
			} else if (type == KeyType.REMOTE) {
				baseVersion = entry.getRemoteBaseVersion();
			} else {
				throw new ObjectWrapperRTException("State table lookup for object with null key.");
			}
			if (currentVersion.equals(baseVersion)) {
				// Version unchanged.
				state = OffliningState.OFFLINED;
			} else {
				// Version changed.
				state = OffliningState.CHANGED;
			}
		}
		return state;
		
	}

	/** {@inheritDoc} */
	public void setState(OffliningState state) {
		UniqueKey localKey = m_wrapper.wrap(UniqueKeyed.class, m_target).getLocalUniqueKey();
		switch (state) {
			case PROCESSED:
			case CONFLICTED:
				m_table.put(localKey, state);
				break;
			default:
				throw new IllegalArgumentException("Can only override statE with PROCESSED or CONFLICTED.");
		}
	}
}
