/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.envsupport;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This {@link Properties} implementation logs all attempts to access a not existing key thought the method get().
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class ErrorTracingProperties extends Properties {
	/**
	 * The set of not existing keys that were tried to access.
	 */
	private Set<String> m_errors = new HashSet<String>();
	
	/**
	 * The set of existing keys that could be access successfully.
	 */
	private Set<String> m_successes = new HashSet<String>();
	
	/** {@inheritDoc} */
	@Override
	public synchronized Object get(Object key) {
		Object result = super.get(key);
		if (result == null) {
			m_errors.add((String) key);
		} else {
			m_successes.add((String) key);
		}
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getProperty(String key) {
		String result = super.getProperty(key);
		if (result == null) {
			m_errors.add((String) key);
		} else {
			m_successes.add((String) key);
		}
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getProperty(String key, String defaultValue) {
		String result = super.getProperty(key, defaultValue);
		if (result == null) {
			m_errors.add((String) key);
		} else {
			m_successes.add((String) key);
		}
		return result;
	}
	
	/**
	 * @return    a set of not existing keys that were tried to access
	 */
	public Set<String> getErrors() {
		return m_errors;
	}
	
	/**
	 * Clear the set of access errors.
	 */
	public void clearErrors() {
		m_errors.clear();
	}
	
	
	/**
	 * @return    a set of existing keys that could be access successfully
	 */
	public Set<String> getSuccesses() {
		return m_successes;
	}
	
	/**
	 * Clear the set of successful access.
	 */
	public void clearSuccesses() {
		m_successes.clear();
	}
}
