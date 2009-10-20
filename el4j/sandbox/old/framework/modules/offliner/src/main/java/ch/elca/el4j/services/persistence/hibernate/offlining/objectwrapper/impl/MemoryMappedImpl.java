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

import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.ServerMappingTable;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Mapped implementation that uses an in-memory table class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class MemoryMappedImpl extends AbstractWrapper implements Mapped {

	/**
	 * The mapping table.
	 */
	private final ServerMappingTable m_table;
	
	/**
	 * Create this wrapper.
	 * @param table The mapping table.
	 */
	public MemoryMappedImpl(ServerMappingTable table) {
		m_table = table;
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!m_wrapper.wrappablePresent(KeyedVersioned.class)
			|| !m_wrapper.wrappablePresent(Typed.class)) {
			throw new ObjectWrapperRTException("Required implementations KeyedVersioned and Typed "
				+ "not present.");
		}
	}

	/** {@inheritDoc} */
	public MappingEntry getEntry() {
		Serializable key = m_wrapper.wrap(KeyedVersioned.class, m_target).getKey();
		KeyType type = m_wrapper.wrap(Typed.class, m_target).getType(); 
		if (type == KeyType.NULL) {
			throw new OfflinerInternalRTException("Null key in mapped object.");
		}
		
		MappingEntry entry;
		if (type == KeyType.LOCAL) {
			entry = m_table.getLocal(key);
		} else if (type == KeyType.REMOTE) {
			entry = m_table.getRemote(key);
		} else {
			throw new OfflinerInternalRTException("Unhandled key type " + type);
		}
		return entry;
	}

	/** {@inheritDoc} */
	public void setEntry(MappingEntry entry) {
		m_table.saveEntry(entry);
	}
}
