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
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
