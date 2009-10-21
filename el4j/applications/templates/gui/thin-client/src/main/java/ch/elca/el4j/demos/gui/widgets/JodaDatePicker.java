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
package ch.elca.el4j.demos.gui.widgets;

import java.beans.PropertyVetoException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.michaelbaranov.microba.calendar.DatePicker;


/**
 * 
 * This class is a thin wrapper around the DatePicker to enable working with LocalDate.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaDatePicker extends DatePicker {

	/**
	 * Constructor which initialized the widget to the current system date.
	 */
	public JodaDatePicker() {
		super(new Date(), 2, Locale.getDefault(), TimeZone.getDefault());
   
		
	}

	/**
	 * Constructor taking a DateTime with which the widget will be initialized.
	 * 
	 * @param initialDate
	 *            is the DateTime with which the widget will be initialized
	 */

	public JodaDatePicker(DateTime initialDate) {

		super(initialDate.toDate(), 2, Locale.getDefault(), TimeZone.getDefault());

	
	}
	
	
	
	/**
	 * Constructor taking a LocalDate with which the widget will be initialized.
	 * 
	 * @param initialDate
	 *            is the LocalDate with which the widget will be initialized.
	 */

	public JodaDatePicker(LocalDate initialDate) {
		super(initialDate.toDateTimeAtStartOfDay().toDate(), 2, Locale.getDefault(), TimeZone.getDefault());
	}

	/*
	 * // setDate version when using DateTime
	 * @Override public void setDate(Date date) throws PropertyVetoException { DateTime dateTime = getDateTime();
	 * super.setDate(date); firePropertyChange("dateTime", dateTime, new DateTime(date)); }
	 */
	/**
	 * Overrides setDate from DatePicker and fires a PropertyChange event.
	 */
	@Override
	public void setDate(Date date) throws PropertyVetoException {
		LocalDate oldDate = getJodaDate();
		super.setDate(date);
		firePropertyChange("jodaDate", oldDate, new LocalDate(date));
	}

	/**
	 * Getter method for DateTime.
	 * 
	 * @return the currently set Date as DateTime.
	 */

	public DateTime getDateTime() {
		return new DateTime(super.getDate());
	}

	/**
	 * Setter method for DateTime.
	 * 
	 * @param date
	 *            is the DateTime with which the widget shall be set.
	 * @throws PropertyVetoException
	 */

	public void setDateTime(DateTime date) throws PropertyVetoException {
		super.setDate(date.toDate());

	}

	/**
	 * Getter method for LocalDate.
	 * 
	 * @return a LocalDate object which is set to the Date chosen in the widget.
	 */

	public LocalDate getJodaDate() {
		return new LocalDate(super.getDate());
	}

	/**
	 * Setter method for setting the date of the widget with a LocalDate.
	 * 
	 * @param date
	 *            is the LocalDate to which we wan't to set the widget.
	 * @throws PropertyVetoException
	 */

	public void setJodaDate(LocalDate date) throws PropertyVetoException {

		super.setDate(date.toDateTimeAtStartOfDay().toDate());
	}

	

	
		
	

	
}
