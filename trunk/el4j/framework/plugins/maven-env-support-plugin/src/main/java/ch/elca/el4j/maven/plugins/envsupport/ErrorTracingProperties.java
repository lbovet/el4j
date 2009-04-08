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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.resources.util.InterpolationFilterReader;

/**
 * This {@link Properties} implementation logs all attempts to access a not existing key thought the method get().
 * It is a specific implementation for {@link InterpolationFilterReader} and should therefore only be used in connection
 * with this reader.
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
	 * The list of not existing keys that where tried to access.
	 */
	private List<String> m_errors = new ArrayList<String>();
	
	/** {@inheritDoc} */
	@Override
	public synchronized Object get(Object key) {
		Object result = super.get(key);
		if (result == null) {
			m_errors.add((String) key);
		}
		return result;
	}
	
	/**
	 * @return    a list of not existing keys that where tried to access
	 */
	public List<String> getErrors() {
		return m_errors;
	}
	
	/**
	 * Clear the list of access errors.
	 */
	public void clearErrors() {
		m_errors.clear();
	}
	
}
