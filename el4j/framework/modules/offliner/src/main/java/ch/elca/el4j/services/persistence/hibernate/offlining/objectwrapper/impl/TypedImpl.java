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

import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Implementation of typed. Requires KeyedVersioned.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class TypedImpl extends AbstractWrapper implements Typed {

	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!m_wrapper.wrappablePresent(KeyedVersioned.class)) {
			throw new ObjectWrapperRTException("Requires KeyedVersioned.");
		}
	}

	/** {@inheritDoc} */
	public KeyType getType() {
		Serializable key = m_wrapper.wrap(KeyedVersioned.class, m_target) 
			.getKey();
		if (key instanceof Long) {
			Long keyAsLong = (Long) key;
			return (keyAsLong.equals(0L) ? KeyType.NULL 
				: (keyAsLong > 0L ? KeyType.REMOTE : KeyType.LOCAL));
		} else if (key instanceof Integer) {
			Integer keyAsInt = (Integer) key;
			return (keyAsInt.equals(0) ? KeyType.NULL 
				: (keyAsInt > 0 ? KeyType.REMOTE : KeyType.LOCAL));
		} else {
			throw new IllegalArgumentException("Key not of type Integer or Long."
				+ " The default implementation requires this.");
		}
	}

	/** {@inheritDoc} */
	public void nullKey() {
		Class<?> keyClass = m_wrapper.wrap(KeyedVersioned.class, m_target).getKeyClass();
		if (Long.class.isAssignableFrom(keyClass)) {
			m_wrapper.wrap(KeyedVersioned.class, m_target).setKey(0L);
		} else if (Integer.class.isAssignableFrom(keyClass)) {
			m_wrapper.wrap(KeyedVersioned.class, m_target).setKey(0);
		} else {
			throw new IllegalStateException("The default implementation requires "
				+ "keys of type Integer or Long.");
		}
	}
}
