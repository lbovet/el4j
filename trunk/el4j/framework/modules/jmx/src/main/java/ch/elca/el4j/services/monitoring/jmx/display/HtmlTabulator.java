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

import java.util.LinkedList;
import java.util.List;

/**
 * Creates HTML tables for displaying data.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class HtmlTabulator {
	
	/**
	 * The number of columns this table has.
	 */
	int m_numColumns;
	
	/**
	 * The title row.
	 */
	String[] m_title;
	
	/**
	 * The table data.
	 */
	List<String[]> m_table;
	
	/**
	 * The colour to use for the title background.
	 */
	String m_titleColor = "#FFFF80";
	
	/**
	 * The colour to use for marked rows.
	 * Every (m_markColor)'th row is marked.
	 */
	String m_markColor = "#FFFFC0";
	
	//Checkstyle: MagicNumber off
	
	/**
	 * The interval to mark rows at.
	 */
	int m_markInterval = 3;
	
	//Checkstyle: MagicNumber on
	
	/**
	 * @param titles The titles of the columns.
	 */
	public HtmlTabulator(String... titles) {
		m_numColumns = titles.length;
		m_table = new LinkedList<String[]>();
		m_title = titles;
	}
	
	/**
	 * @param args A row of data.
	 * @throws RuntimeException If the number of arguments does not match
	 * the number of columns.
	 */
	public void addRow(String ... args) throws RuntimeException {
		if (args.length != m_numColumns) {
			throw new RuntimeException("Got " + args.length
				+ " arguments but expected " + m_numColumns + ".");
		}
		m_table.add(args);
	}
	
	/**
	 * @return The table.
	 */
	public String tabulate() {
		String table = "<table>\n";
		
		// Title
		table += "<tr bgcolor=\"" + m_titleColor + "\"> ";
		for (String element : m_title) {
			table += "<td><b>" + element + "</b></td> ";
		}
		table += "</tr>\n";
		
		// Rows
		int rowCounter = 0;
		for (String[] currentRow : m_table) {
			rowCounter++;
			if (rowCounter == m_markInterval) {
				table += "<tr bgcolor=\"" + m_markColor + "\"> ";
				rowCounter = 0;
			} else {
				table += "<tr> ";
			}
			
			for (String element : currentRow) {
				table += "<td>" + element + "</td> ";
			}
			table += "</tr>\n";
		}
		table += "</table>\n";
		return table;
	}
	
}
