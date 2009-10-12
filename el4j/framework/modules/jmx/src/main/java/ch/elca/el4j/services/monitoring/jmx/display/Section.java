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
package ch.elca.el4j.services.monitoring.jmx.display;

/**
 * Section of a page.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class Section {

	/** Section title. */
	private String m_name;
	
	/** Section content. */
	private String m_content;
	
	/**
	 * @param name Creates a new section with this name.
	 */
	public Section(String name) {
		m_name = name;
		m_content = "";
	}
	
	/**
	 * @param string Adds <code>string</code> to content.
	 */
	public void add(String string) {
		m_content += string;
	}
	
	/**
	 * @param string Adds content between p tags and newline at end.
	 */
	public void addLine(String string) {
		add(HtmlDisplayManager.tag("p", string) + "\n");
	}
	
	/**
	 * @param string Adds a warning - currently in italics and red.
	 */
	public void addWarning(String string) {
		add(HtmlDisplayManager.tagRecursive(
			"<font color=#ff0000>" + string + "</font>",
			"p", "i") + "\n");
	}
	
	/**
	 * Adds a 1x1 table with the key in the title and the value in the table.
	 * @param key A property key.
	 * @param value A property value.
	 */
	public void addProperty(String key, String value) {
		HtmlTabulator tab = new HtmlTabulator(key);
		tab.addRow(value);
		m_content += HtmlDisplayManager.tag("p", tab.tabulate()) + "\n";
	}
	
	/**
	 * @return The content.
	 */
	public String getContent() {
		return m_content;
	}
	
	/**
	 * @return The section name (title).
	 */
	public String getName() {
		return m_name;
	}
	
	
}
