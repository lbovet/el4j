/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.objectwrapper.impl;

import java.io.Serializable;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;

/**
 * Impelementation of KeyedVersioned that uses the provided methods of the EL4J base class. It is
 * thus more efficient.
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
public class KeyedVersionedEl4jImpl extends AbstractWrapper implements KeyedVersioned {

	/**
	 * The target cast to the correct class.
	 */
	private AbstractIntKeyIntOptimisticLockingDto m_targetDto;
	
	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!(m_target instanceof AbstractIntKeyIntOptimisticLockingDto)) {
			throw new ObjectWrapperRTException("This implementation is only for the "
				+ "AbstractIntKeyIntOptimisticLockingDto class.");
		}
		m_targetDto = (AbstractIntKeyIntOptimisticLockingDto) m_target;
	}

	/** {@inheritDoc} */
	public Serializable getKey() {
		return m_targetDto.getKey();
	}

	/** {@inheritDoc} */
	public Class<?> getKeyClass() {
		return Integer.class;
	}

	/** {@inheritDoc} */
	public Serializable getVersion() {
		return m_targetDto.getOptimisticLockingVersion();
	}

	/** {@inheritDoc} */
	public Class<?> getVersionClass() {
		return Integer.class;
	}

	/** {@inheritDoc} */
	public void setKey(Serializable key) {
		if (key instanceof Integer) {
			Integer i = (Integer) key;
			m_targetDto.setKey(i);
		} else {
			throw new ObjectWrapperRTException("The target class requires integer keys.");
		}
	}

	/** {@inheritDoc} */
	public void setVersion(Serializable version) {
		if (version instanceof Integer) {
			Integer i = (Integer) version;
			m_targetDto.setOptimisticLockingVersion(i);
		} else {
			throw new ObjectWrapperRTException("The target class requires integer versions.");
		}
	}

}
