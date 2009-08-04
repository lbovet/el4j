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
package ch.elca.el4j.demos.gui.events;

/**
 * This event informs about the search of a reference from refDB.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SearchRefDBEvent {
	/**
	 * The fields where to search.
	 */
	private String[] m_fields;
	/**
	 * The value that the {@link #m_field} should match.
	 */
	private String m_value;
	
	/**
	 * @param fields    the field where to search
	 * @param value    the value that the field should match
	 */
	public SearchRefDBEvent(String[] fields, String value) {
		m_fields = fields;
		m_value = value;
	}
	/**
	 * @return    the fields where to search
	 */
	public String[] getFields() {
		return m_fields;
	}
	
	/**
	 * @param fields    the fields where to search
	 */
	public void setFields(String[] fields) {
		this.m_fields = fields;
	}
	
	/**
	 * @return    the value that the {@link #m_field} should match
	 */
	public String getValue() {
		return m_value;
	}
	
	/**
	 * @param value    the value that the {@link #m_field} should match
	 */
	public void setValue(String value) {
		this.m_value = value;
	}

}
