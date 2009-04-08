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
package ch.elca.el4j.services.gui.swing.cookswing.binding;

import cookxml.core.interfaces.NoAdd;

/**
 * This class holds some object and prevents it from being added
 * to the parent.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>    the type of object to hold
 *
 * @author Stefan Wismer (SWI)
 */
public class NoAddValueHolder<T> implements NoAdd {
	/**
	 * The hold object.
	 */
	private T m_object;
	
	/**
	 * @param object    the object
	 */
	public NoAddValueHolder(T object) {
		m_object = object;
	}

	/**
	 * @return Returns the object.
	 */
	public T getObject() {
		return m_object;
	}

	/**
	 * @param object Is the object to set.
	 */
	public void setObject(T object) {
		m_object = object;
	}
}
