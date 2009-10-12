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

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Implementation of unique keyed.
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
public class UniqueKeyedImpl extends AbstractWrapper implements
	UniqueKeyed {

	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!m_wrapper.wrappablePresent(KeyedVersioned.class)) {
			throw new ObjectWrapperRTException("Requires implementation of KeyedVersioned.");
		}
		if (!m_wrapper.wrappablePresent(Mapped.class)) {
			throw new ObjectWrapperRTException("Requires implementation of Mapped.");
		}
		if (!m_wrapper.wrappablePresent(Typed.class)) {
			throw new ObjectWrapperRTException("Requires implementation of Typed.");
		}
	}

	/** {@inheritDoc} */
	public UniqueKey getUniqueKey() {
		KeyedVersioned keyed = m_wrapper.wrap(KeyedVersioned.class, m_target);
		Serializable key = keyed.getKey();
		return new UniqueKey(key, m_target.getClass());
	}

	/** {@inheritDoc} */
	public void setUniqueKey(UniqueKey key) throws IllegalArgumentException {
		Serializable realKey = key.getKey();
		m_wrapper.wrap(KeyedVersioned.class, m_target).setKey(realKey);
	}

	/** {@inheritDoc} */
	public UniqueKey getLocalUniqueKey() {
		Typed typed = m_wrapper.wrap(Typed.class, m_target);
		switch(typed.getType()) {
			case NULL:
				throw new IllegalStateException("Trying to get local key from a NULL keyed object.");
			case LOCAL:
				return getUniqueKey();
			case REMOTE:
				Mapped mapped = m_wrapper.wrap(Mapped.class, m_target);
				return mapped.getEntry().getLocalKey();
			default:
				throw new ObjectWrapperRTException("Switch over enum KeyType failed.");
		}
	}
}
