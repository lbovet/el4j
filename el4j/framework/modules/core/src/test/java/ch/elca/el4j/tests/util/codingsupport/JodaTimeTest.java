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
package ch.elca.el4j.tests.util.codingsupport;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import ch.elca.el4j.util.codingsupport.JodaTimeUtils;

/**
 * 
 * This class tests the JodaTimeUtils class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaTimeTest {

	/**
	 * Tests the conversion from a DateTime object to a string.
	 */
	
	@Test
	public void testDateTimeToString() {
		DateTime dt = new DateTime (2009, 10, 16, 10, 28, 20, 0);
		
		assertEquals(JodaTimeUtils.getDateTimeString(dt, JodaTimeUtils.DEFAULT_DATE_WITH_PERIOD),"16.10.2009");
		assertEquals(JodaTimeUtils.getDateTimeString(dt, JodaTimeUtils.DEFAULT_DATE_WITH_WHITESPACE),"16 10 2009");
		assertEquals(JodaTimeUtils.getDateTimeString(dt, JodaTimeUtils.DEFAULT_DATETIME),"16.10.2009 10:28:20");

	
	}
	
	/**
	 * Tests the conversion from a string object to a DateTime object. 
	 * 
	 */
	
	@Test
	public void testStringToDateTime() {
		DateTime dt = JodaTimeUtils.parseDateTime("09.10.16", "yy.MM.dd");
		
		assertEquals(dt.getYear(), 2009);
		assertEquals(dt.getMonthOfYear(), 10);
		assertEquals(dt.getDayOfMonth(), 16);
		
	}
	
}
