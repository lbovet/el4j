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

import javax.swing.UIManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import ch.elca.el4j.util.codingsupport.JodaTimeUtils;

import com.michaelbaranov.microba.calendar.DatePicker;



/**
 * 
 * This class is a small widget that lets a user choose date and time.
 * It is based on the DatePicker class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaDateTimePicker extends DatePicker {

	/**
	 * holds the current date and time.
	 */
	private DateTime m_dateTime;
	
	/**
	 * holds the current time.
	 */
	private LocalTime m_localTime;

	/**
	 * reference to the TimeField in the popup.
	 */
	private TimeField m_timeFieldInPopup;
	
	/**
	 * reference to the TimeField outside of the popup.
	 */
	private TimeField m_timeFieldOutside;

	/**
	 * Constructor which will initialize a JodaDateTimePicker with the current date and time on the machine.
	 */

	public JodaDateTimePicker() {
		super(new Date(), 2, Locale.getDefault(), TimeZone.getDefault());
		m_localTime = new LocalTime();
	}
	
	/**
	 * Constructor taking a LocalDate and midnight (00:00) with which the widget will be initialized.
	 * 
	 * @param initialDate
	 *            is the LocalDate with which the widget will be initialized.
	 */

	public JodaDateTimePicker(LocalDate initialDate) {
		super(initialDate.toDateTimeAtStartOfDay().toDate(), 2, Locale.getDefault(), TimeZone.getDefault());
		m_localTime = new LocalTime(0, 0);
	}


	/**
	 * Constructor taking a DateTime with which the widget will be initialized.
	 * 
	 * @param initialDate
	 *            is the DateTime with which the widget will be initialized
	 */

	public JodaDateTimePicker(DateTime initialDate) {
		super(initialDate.toDate(), 2, Locale.getDefault(), TimeZone.getDefault());
		m_localTime = initialDate.toLocalTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUI() {
		UIManager.put("ch.elca.JodaDateTimePicker.uiID", "ch.elca.el4j.demos.gui.widgets.JodaDateTimePickerUI");
		super.updateUI();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUIClassID() {
		return "ch.elca.JodaDateTimePicker.uiID";
	}


	/**
	 * {@inheritDoc} Overrides setDate from DatePicker and fires a PropertyChange event. Always uses the time that was
	 * last set in setTime.
	 */
	@Override
	public void setDate(Date date) throws PropertyVetoException {
		// do nothing if we get a null value as argument
		if (date == null) {
			return;
		}
		DateTime oldDate = getJodaDateTime();
		// use time settings from localTime
		DateTime newDateTime = JodaTimeUtils.setTimeOfDateTime(new DateTime(date), getLocalTime());
		setJodaDateTime(newDateTime);
		firePropertyChange("jodaDateTime", oldDate, getJodaDateTime());
	}

	/**
	 * Sets only the date part of the DateTime.
	 * 
	 * @param date
	 *            is the date to which we set
	 */

	public void setJodaDate(Date date) {
		// don't change anything in case we get a null as argument
		if (date == null) {
			return;
		}

		m_dateTime = JodaTimeUtils.setDateOfDateTime(m_dateTime, new LocalDate(date));
	}

	/**
	 * Getter method for DateTime.
	 * 
	 * @return the currently set Date as DateTime.
	 */

	public DateTime getJodaDateTime() {
		return m_dateTime;
	}

	/**
	 * Setter method for DateTime.
	 * 
	 * @param date
	 *            is the DateTime with which the widget shall be set.
	 * @throws PropertyVetoException
	 */

	public void setJodaDateTime(DateTime date) throws PropertyVetoException {
		m_dateTime = date;
		super.setDate(date.toDate());

	}
	
	/**
	 * Makes it possible to set if seconds are shown in the TimeFields.
	 * 
	 * @param show set to true if seconds should be shown
	 */

	public void setShowSecondsInTimeField(boolean show) {
		m_timeFieldInPopup.setShowSeconds(show);
		m_timeFieldOutside.setShowSeconds(show);
	}
	
	/**
	 * Lets the DateTimePickerUI pass a reference to the TimeFields.
	 *  
	 * @param timeFieldInPopup is the TimeField in th popup
	 * @param timeFieldOutside is the TimeField outside of the popup (next to the button)
	 */

	public void setTimeFields(TimeField timeFieldInPopup, TimeField timeFieldOutside) {
		m_timeFieldInPopup = timeFieldInPopup;
		m_timeFieldOutside = timeFieldOutside;
	}
	

	/**
	 * Getter method for local time
	 * 
	 * @return the value of m_localTime
	 */

	private LocalTime getLocalTime() {
		return m_localTime;
	}

	/**
	 * Setter method for local time
	 * 
	 * @param localTime
	 *            is the localTime to set
	 */

	private void setLocalTime(LocalTime localTime) {
		m_localTime = localTime;
	}

	/**
	 * Method for to set the time only.
	 * 
	 * @param newTime
	 */

	public void setTime(LocalTime time) {
		// make sure that localTime is set, otherwise the time
		// gets lost when setDate is called through the calendarPane
		setLocalTime(time);
		
		// make sure both TimeFields are synchronized
		m_timeFieldInPopup.setTime(time);
		m_timeFieldOutside.setTime(time);
		DateTime newDateTime = JodaTimeUtils.setTimeOfDateTime(getJodaDateTime(), time);

		DateTime oldDateTime = getJodaDateTime();
		m_dateTime = newDateTime;
		firePropertyChange("jodaDateTime", oldDateTime, newDateTime);

	}

}
