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
package ch.elca.el4j.services.persistence.hibernate.offlining.impl;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import ch.elca.el4j.services.persistence.hibernate.offlining.generic.StringCaster;


/**
 * An entry in the keymap table.
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
@Entity @Table(name = "KEYMAP")
@SequenceGenerator(name = "keyid_generator", sequenceName = "keymap_sequence")
public class MappingEntry implements Serializable {

	/**
	 * The key.
	 */
	private int m_id;
	
	/**
	 * The unique key for the local version of this object.
	 */
	private UniqueKey m_localKey;
	
	/**
	 * The unique key for the remote version of this object.
	 */
	private UniqueKey m_remoteKey;

	/**
	 * The remote base version. This is the version under which the object was last offlined 
	 * from the server. It must be restored when the object is recommitted.
	 */
	private Serializable m_remoteBaseVersion;
	
	/**
	 * The local base version. This is used to determine if an object has been changed in the local db.
	 */
	private Serializable m_localBaseVersion;
	
	/**
	 * The delete version. If this is non-zero the object is marked for deletion and
	 * the version indicates the order in which the objects have to be deleted on the 
	 * server.
	 */
	private long m_deleteVersion;
	
	/**
	 * The version this entry was last synchronized under.
	 * This allows us to find all entries that still need checking.
	 */
	private int m_synchronizeVersion;
	
	/**
	 * Empty constructor.
	 */
	public MappingEntry() { }
	
	
	
	/**
	 * Default Constructor.
	 * @param localKey The local key.
	 * @param remoteKey The remote key. May be <code>null</code>.
	 * @param localBaseVersion The local base version.
	 * @param remoteBaseVersion The remote base version.
	 */
	public MappingEntry(UniqueKey localKey, UniqueKey remoteKey,
		Serializable localBaseVersion, Serializable remoteBaseVersion) {
		m_localKey = localKey;
		m_remoteKey = remoteKey;
		m_localBaseVersion = localBaseVersion;
		m_remoteBaseVersion = remoteBaseVersion;
	}



	/**
	 * @return The id / key.
	 */
	@Id @Column(name = "ID") 
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "keyid_generator")
	public int getId() {
		return m_id;
	}

	/**
	 * Get the localKey.
	 * @return The localKey.
	 */
	@Transient
	public UniqueKey getLocalKey() {
		return m_localKey;
	}

	/**
	 * Get the remoteKey.
	 * @return The remoteKey.
	 */
	@Transient
	public UniqueKey getRemoteKey() {
		return m_remoteKey;
	}

	/**
	 * Get the local base version.
	 * @return The local base version.
	 */
	@Transient
	public Serializable getLocalBaseVersion() {
		return m_localBaseVersion;
	}
	
	/**
	 * Get the remote base version.
	 * @return The remote base version.
	 */
	@Transient
	public Serializable getRemoteBaseVersion() {
		return m_remoteBaseVersion;
	}

	/**
	 * Get the deleteVersion.
	 * @return The deleteVersion.
	 */
	@Column(name = "DELETEVERSION")
	public long getDeleteVersion() {
		return m_deleteVersion;
	}

	/**
	 * @return A string for the local key for database purposes.
	 */
	@Column(name = "LOCALKEY")
	public String getLocalKeyAsString() {
		return m_localKey.toString();
	}
	
	/**
	 * @return A string for the remote key for database purposes.
	 */
	@Column(name = "REMOTEKEY")
	public String getRemoteKeyAsString() {
		if (m_remoteKey == null) {
			return "null";
		}
		return m_remoteKey.toString();
	}
	
	/**
	 * @return A string for the local base version for the database.
	 */
	@Column(name = "LOCALBASEVERSION")
	public String getLocalBaseVersionAsString() {
		return new StringCaster().toString(m_localBaseVersion);
	}
	
	/**
	 * @return A string for the remote base version for the database.
	 */
	@Column(name = "REMOTEBASEVERSION")
	public String getRemoteBaseVersionAsString() {
		return new StringCaster().toString(m_remoteBaseVersion);
	}

	
	/**
	 * Get the synchronizeVersion.
	 * @return The synchronizeVersion.
	 */
	@Column(name = "SYNCVERSION")
	public int getSynchronizeVersion() {
		return m_synchronizeVersion;
	}

	/**
	 * Setter for id.
	 * @param id The new id to set.
	 */
	public void setId(int id) {
		m_id = id;
	}

	/**
	 * Setter for localKey.
	 * @param localKey The new localKey to set.
	 */
	public void setLocalKey(UniqueKey localKey) {
		m_localKey = localKey;
	}

	/**
	 * Setter for remoteKey.
	 * @param remoteKey The new remoteKey to set.
	 */
	public void setRemoteKey(UniqueKey remoteKey) {
		m_remoteKey = remoteKey;
	}

	/**
	 * Setter for remoteBaseVersion.
	 * @param remoteBaseVersion The new remoteBaseVersion to set.
	 */
	public void setRemoteBaseVersion(Serializable remoteBaseVersion) {
		m_remoteBaseVersion = remoteBaseVersion;
	}

	/**
	 * Setter for localBaseVersion.
	 * @param localBaseVersion The new localBaseVersion to set.
	 */
	public void setLocalBaseVersion(Serializable localBaseVersion) {
		m_localBaseVersion = localBaseVersion;
	}

	/**
	 * Setter for deleteVersion.
	 * @param deleteVersion The new deleteVersion to set.
	 */
	public void setDeleteVersion(long deleteVersion) {
		m_deleteVersion = deleteVersion;
	}

	/**
	 * Setter for synchronizeVersion.
	 * @param synchronizeVersion The new synchronizeVersion to set.
	 */
	public void setSynchronizeVersion(int synchronizeVersion) {
		m_synchronizeVersion = synchronizeVersion;
	}
	
	/**
	 * Set the local key from a string. Used when restoring from database.
	 * @param key The local key as string.
	 */
	public void setLocalKeyAsString(String key) {
		m_localKey = UniqueKey.fromString(key);
	}
	
	/**
	 * Set the remote key from a string. Used when restoring from database.
	 * @param key The remote key as string.
	 */
	public void setRemoteKeyAsString(String key) {
		m_remoteKey = UniqueKey.fromString(key);
	}
	
	/**
	 * Set the local base version from a string. Used when restoring from database.
	 * @param ver The local base version as string.
	 */
	public void setLocalBaseVersionAsString(String ver) {
		m_localBaseVersion = (Serializable) new StringCaster().fromString(ver);
	}
	
	/**
	 * Set the remote base version from a string. Used when restoring from database.
	 * @param ver The remote base version as string.
	 */
	public void setRemoteBaseVersionAsString(String ver) {
		m_remoteBaseVersion = (Serializable) new StringCaster().fromString(ver);
	}
	
	/**
	 * @return The class of the domain object.
	 */
	@Transient public Class<?> getObjectClass() {
		return getLocalKey().getObjectClass();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C ").append(m_localKey.getObjectClass());
		builder.append(", L ").append(m_localKey.m_key); 
		builder.append(", R ").append(m_remoteKey == null ? "(null)" : m_remoteKey.m_key); 
		builder.append(", LB ").append(m_localBaseVersion);
		builder.append(", RB ").append(m_remoteBaseVersion);
		builder.append(", D ").append(m_deleteVersion);
		builder.append(", S ").append(m_synchronizeVersion);
		return builder.toString();
	}
	
	/**
	 * Compare mapping entries by version.
	 */
	public static class ByDeleteVersion implements Comparator<MappingEntry>, Serializable {
		/** SerialVersionUid. */
		private static final long serialVersionUID = 635273411891478046L;
		
		/** {@inheritDoc} */
		public int compare(MappingEntry o1, MappingEntry o2) {
			// How to compare longs.
			return (int) (o1.getDeleteVersion() - o2.getDeleteVersion());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof MappingEntry) {
			MappingEntry other = (MappingEntry) obj;
			return m_id == other.m_id;
		} else {
			return false;
		}
	}



	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return m_id;
	};

	
	
}
