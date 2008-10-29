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
package ch.elca.el4j.util.objectwrapper.impl;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Implementation of keyed/versioned.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/impl/KeyedVersionedHibernateImpl.java $",
 *    "$Revision: 1571 $",
 *    "$Date: 2008-08-06 10:49:49 +0200 (Wed, 06 Aug 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class KeyedVersionedHibernateImpl extends AbstractWrapper implements KeyedVersioned {

	/** The session factory - source of hibernate metadata. */
	private final SessionFactory m_sessionFactory;

	/** The class metadata, retrieved once from the session factory on create(). */
	private ClassMetadata m_meta;
	
	/**
	 * Create a prototype instance.
	 * @param sessionFactory The session factory.
	 */
	public KeyedVersionedHibernateImpl(SessionFactory sessionFactory) {
		m_sessionFactory = sessionFactory;
	}

	/** {@inheritDoc} */
	public Serializable getKey() {
		return m_meta.getIdentifier(m_target, EntityMode.POJO);
	}

	/** {@inheritDoc} */
	public Serializable getVersion() {
		return (Serializable) m_meta.getVersion(m_target, EntityMode.POJO);
	}

	/** {@inheritDoc} */
	public void setKey(Serializable key) {
		m_meta.setIdentifier(m_target, key, EntityMode.POJO);
	}

	/** {@inheritDoc} */
	public void setVersion(Serializable version) {
		// This is a workaround.
		// We do not have a setVersion method on hibernate's metadata,
		// so we need to get the version property's index, look up its name
		// with the index in the list of names and set it by name as a property.
		String versionName = m_meta.getPropertyNames()[m_meta.getVersionProperty()];
		m_meta.setPropertyValue(m_target, versionName, version, EntityMode.POJO);
	}
	
	/** {@inheritDoc} */
	public Class<?> getKeyClass() {
		return m_meta.getIdentifierType().getReturnedClass();
	}
	
	/** {@inheritDoc} */
	public Class<?> getVersionClass() {
		String versionName = m_meta.getPropertyNames()[m_meta.getVersionProperty()];
		return m_meta.getPropertyType(versionName).getReturnedClass();
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		m_meta = m_sessionFactory.getClassMetadata(m_target.getClass());
		if (m_meta == null || m_meta.getIdentifierPropertyName() == null) {
			throw new ObjectWrapperRTException("Failed to create hibernate metadata.");
		}
	}

}
