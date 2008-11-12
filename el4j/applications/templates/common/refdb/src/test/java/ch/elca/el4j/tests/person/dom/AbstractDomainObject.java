/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.tests.person.dom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Base class of domain objects. This replaces the EL4J one because we don't want final
 * getters/setters for key and version.
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
@MappedSuperclass
public abstract class AbstractDomainObject implements Serializable {

	/**
	 * Serialization id.
	 */
	private static final long serialVersionUID = 8934464588242308573L;

	/** The key. */
	protected int m_key;
	
	/** The version. */
	protected int m_version;

	/** Is this object new? */
	private boolean m_isNew = true;
	
	/**
	 * Get the key.
	 * @return The key.
	 */
	@Id @GeneratedValue(strategy = GenerationType.AUTO,
		generator = "keyid_generator")
	@Column(name = "KEYID")
	public int getKey() {
		return m_key;
	}

	/**
	 * Get the version.
	 * @return The version.
	 */
	@Version
	public int getVersion() {
		return m_version;
	}
	
	/**
	 * Is this object new?
	 * @return <code>true</code> if this object is new.
	 */
	@Transient
	public boolean isNew() {
		return m_isNew;
	}

	/**
	 * Setter for key.
	 * @param key The new key to set.
	 */
	public void setKey(int key) {
		m_key = key;
		m_isNew = false;
	}

	/**
	 * Setter for version.
	 * @param version The new version to set.
	 */
	public void setVersion(int version) {
		m_version = version;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof AbstractDomainObject) {
			AbstractDomainObject other = (AbstractDomainObject) obj;
			if (isNew()) {
				return false;
			} else {
				return getKey() == other.getKey();
			}
		} else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		if (m_isNew) {
			return super.hashCode();
		} else {
			return m_key;
		}
	}
	
	
	
}
