/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.remoting.jaxws.service;

/**
 * This class simply stores an integer value.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SomeIntValue {
	/**
	 * An integer value.
	 */
	private int m_someValue;
	
	/**
	 * Default constructor for JAX-WS.
	 */
	public SomeIntValue() { }
	
	/**
	 * @param value    the new value
	 */
	public SomeIntValue(int value) {
		m_someValue = value;
	}

	/**
	 * @return Returns the someValue.
	 */
	public int getSomeValue() {
		return m_someValue;
	}

	/**
	 * @param someValue Is the someValue to set.
	 */
	public void setSomeValue(int someValue) {
		this.m_someValue = someValue;
	}
	
}
