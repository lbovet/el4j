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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.interfaces.KeyedVersioned;


/**
 * Implementation of KeyedVersioned that uses reflection on properties "key" and "version". 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/impl/KeyedVersionedReflectionImpl.java $",
 *    "$Revision: 1571 $",
 *    "$Date: 2008-08-06 10:49:49 +0200 (Wed, 06 Aug 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class KeyedVersionedReflectionImpl extends AbstractWrapper implements
	KeyedVersioned {

	/** getKey(). */
	private Method m_keyGetter;
	
	/** setKey(). */
	private Method m_keySetter;
	
	/** getVersion(). */
	private Method m_versionGetter;
	
	/** setVersion(). */
	private Method m_versionSetter;
	
	/** The key class. */
	private Class<?> m_keyClass;
	
	/** The version class. */
	private Class<?> m_versionClass;
	
	/** The key property name. */
	private String m_keyName;
	
	/** The version property name. */
	private String m_versionName;
	
	/**
	 * Empty constructor which uses "key" and "version" as properties. 
	 */
	public KeyedVersionedReflectionImpl() {
		this("key", "version");
	}
	
	/**
	 * Create the wrapper class providing property names for key and version.
	 * @param keyName The key property name.
	 * @param versionName The version property name.
	 */
	public KeyedVersionedReflectionImpl(String keyName, String versionName) {
		m_keyName = keyName.substring(0, 1).toUpperCase(Locale.getDefault())
			+ keyName.substring(1);
		m_versionName = versionName.substring(0, 1).toUpperCase(Locale.getDefault())
			+ versionName.substring(1);
	}
	
	/** {@inheritDoc} */
	@Override
	public void create() {
		Class<?> cls = m_target.getClass();
		try {
			m_keyGetter = cls.getMethod("get" + m_keyName, new Class<?>[0]);
			m_keyClass = m_keyGetter.getReturnType();
			m_keySetter = cls.getMethod("set" + m_keyName, m_keyClass);
			m_versionGetter = cls.getMethod("get" + m_versionName, new Class<?>[0]);
			m_versionClass = m_versionGetter.getReturnType();
			m_versionSetter = cls.getMethod("set" + m_versionName, m_versionClass);
		} catch (NoSuchMethodException ex) {
			throw new ObjectWrapperRTException("Failed to get method via reflection.", ex);
		}
	}

	/** {@inheritDoc} */
	public Serializable getKey() {
		try {
			return (Serializable) m_keyGetter.invoke(m_target, new Object[0]);
		} catch (IllegalAccessException e) {
			throw new ObjectWrapperRTException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectWrapperRTException(e);
		}
	}

	/** {@inheritDoc} */
	public Serializable getVersion() {
		try {
			return (Serializable) m_versionGetter.invoke(m_target, new Object[0]);
		} catch (IllegalAccessException e) {
			throw new ObjectWrapperRTException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectWrapperRTException(e);
		}
	}

	/** {@inheritDoc} */
	public void setKey(Serializable key) {
		try {
			m_keySetter.invoke(m_target, key);
		} catch (IllegalAccessException e) {
			throw new ObjectWrapperRTException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectWrapperRTException(e);
		}

	}

	/** {@inheritDoc} */
	public void setVersion(Serializable version) {
		try {
			m_versionSetter.invoke(m_target, version);
		} catch (IllegalAccessException e) {
			throw new ObjectWrapperRTException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectWrapperRTException(e);
		}

	}

	/** {@inheritDoc} */
	public Class<?> getKeyClass() {
		return m_keyClass;
	}

	/** {@inheritDoc} */
	public Class<?> getVersionClass() {
		return m_versionClass;
	}
	
	

}
