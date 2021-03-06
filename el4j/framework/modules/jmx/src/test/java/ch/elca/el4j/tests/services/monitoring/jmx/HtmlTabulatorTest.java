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
package ch.elca.el4j.tests.services.monitoring.jmx;

import java.util.Random;

import org.junit.Test;

import ch.elca.el4j.services.monitoring.jmx.display.HtmlTabulator;

/**
 * JUnit tests for HtmlTabulator.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class HtmlTabulatorTest {

	@Test
	public void testSimpleTableGeneration() {
		Random generator = new Random();
		
		// Cols in [2,10)
		int numCols = generator.nextInt(8) + 2;
		HtmlTabulator table;
		
		String[] titleRow = new String[numCols];
		for (int i = 0; i < numCols; i++) {
			titleRow[i] = "Column " + i;
		}
		table = new HtmlTabulator(titleRow);
		
		// Rows in [10, 210)
		int numRows = generator.nextInt(100) + 10;
		
		for (int i = 0; i < numRows; i++) {
			String[] row = new String[numCols];
			for (int j = 0; j < numCols; j++) {
				row[j] = "" + ((i+j) % 1000);
			}
			table.addRow(row);
		}
		
		// String theTable = table.tabulate();
		// TODO: Parse generated table here?
	}
}
