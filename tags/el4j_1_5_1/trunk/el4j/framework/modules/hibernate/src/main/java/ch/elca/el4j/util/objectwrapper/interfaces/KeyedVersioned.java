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
package ch.elca.el4j.util.objectwrapper.interfaces;

import java.io.Serializable;

import ch.elca.el4j.util.objectwrapper.Wrappable;

/**
 * Aspect of having a key and version.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/KeyedVersionedAspect.java $",
 *    "$Revision: 1571 $",
 *    "$Date: 2008-08-06 10:49:49 +0200 (Wed, 06 Aug 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public interface KeyedVersioned extends Wrappable {

	/**
	 * @return The key this object holds.
	 */
	Serializable getKey();
	
	/**
	 * Set the object's key.
	 * @param key The key to set.
	 */
	void setKey(Serializable key);
	
	/**
	 * @return The version.
	 */
	Serializable getVersion();
	
	/**
	 * Set the version.
	 * @param version The version to set.
	 */
	void setVersion(Serializable version);
	
	/**
	 * @return The effective class of the key property.
	 */
	Class<?> getKeyClass();
	
	/**
	 * @return The effective class of the version property.
	 */
	Class<?> getVersionClass();
}
