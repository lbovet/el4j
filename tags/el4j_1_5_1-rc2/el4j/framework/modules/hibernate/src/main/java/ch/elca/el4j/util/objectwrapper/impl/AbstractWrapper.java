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

import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;

/**
 * Base class of wrapper implementations. ObjectWrapper.wrap calls setTarget to pass the target object then create(),
 * which the implementation must override. If create returns false an ObjectWrapperRTException is thrown from wrap.
 * <p>
 * The abstract wrapper implementations are created as prototypes and passed to ObjectWrapper, which clones them 
 * whenever one is needed.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/ObjectWrapper/impl/AbstractAspect.java $",
 *    "$Revision: 1549 $",
 *    "$Date: 2008-07-30 14:25:13 +0200 (Wed, 30 Jul 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public abstract class AbstractWrapper implements Cloneable {

	/** The target object. */
	protected Object m_target;
	
	/** The ObjectWrapper object. Allows one wrapped to require another. */
	protected ObjectWrapper m_wrapper;
	
	/**
	 * Setter for ObjectWrapper.
	 * @param wrapper The new ObjectWrapper to set.
	 */
	public void setWrapper(ObjectWrapper wrapper) {
		m_wrapper = wrapper;
	}

	/**
	 * Set the target object.
	 * @param target The target object.
	 */
	public void setTarget(Object target) {
		if (m_target != null) {
			throw new IllegalStateException("Cannot reset target.");
		}
		m_target = target;
	}
	
	/**
	 * Override this in implementations to instantiate an object. A successful return indicates 
	 * creation succeeded.
	 *  @throws ObjectWrapperRTException If creation failed.
	 */
	public abstract void create() throws ObjectWrapperRTException;

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
