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

package ch.elca.el4j.services.persistence.generic.dto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class is an abstract dto which uses a string as key value and an integer
 * for optimistic locking version controlling.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
@MappedSuperclass
public abstract class AbstractStringKeyIntOptimisticLockingDto
	extends AbstractIntOptimisticLockingDto
	implements PrimaryKeyOptimisticLockingObject {
	
	/**
	 * The logger.
	 */
	private static Log s_logger = LogFactory.getLog(AbstractStringKeyIntOptimisticLockingDto.class);
	
	/**
	 * Primary key.
	 */
	private String m_key;
	
	/**
	 * Has the hashCode value been leaked while being in transient state?
	 */
	private boolean m_transientHashCodeLeaked = false;

	/**
	 * {@inheritDoc}
	 */
	@Transient
	public boolean isKeyNew() {
		return m_key == null;
	}

	/**
	 * @return Returns the key.
	 */
	@Id
	@Column(name = "KEYID")
	public String getKey() {
		return m_key;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transient
	public Object getKeyAsObject() {
		return isKeyNew() ? null : getKey();
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		m_key = key;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setKey(Object keyObject) {
		String key = (keyObject == null) ? null : keyObject.toString();
		setKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		if (m_key == null) {
			m_transientHashCodeLeaked = true;
			return super.hashCode();
		} else {
			if (m_transientHashCodeLeaked) {
				s_logger.error("hashCode() has be called once on transient state and once on persistent state  "
					+ "of object '" + this.toString() + "' (" + getClass().toString() + "). "
					+ "This can happen if you insert a transient object into a collection and persist them afterwards. "
					+ "Save the objects before you insert them into a collection!");
			}
			return m_key.hashCode();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AbstractStringKeyIntOptimisticLockingDto) {
			AbstractStringKeyIntOptimisticLockingDto other
				= (AbstractStringKeyIntOptimisticLockingDto) obj;
			return ObjectUtils.nullSaveEquals(m_key, other.m_key);
		} else {
			return false;
		}
	}
}
