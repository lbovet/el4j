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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;

import org.joda.time.LocalTime;

import ch.elca.el4j.util.codingsupport.JodaTimeUtils;

import com.michaelbaranov.microba.calendar.ui.basic.BasicDatePickerUI;


/**
 * 
 * This class is a helper class for JodaDateTimePicker.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaDateTimePickerUI extends BasicDatePickerUI {
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installComponents() {
		super.installComponents();
				
		TimeField timeFieldInPopup = new TimeField(new LocalTime(), true);
		TimeField timeFieldOutside = new TimeField(new LocalTime(), false);

		
		// make changes to the time be propagated to JodaDateTimePicker
		timeFieldInPopup.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("time")) {
					((JodaDateTimePicker) peer).setTime((LocalTime) evt.getNewValue());
				}
			}	
			
		});
		
		// make changes to the time be propagated to JodaDateTimePicker
		timeFieldOutside.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("time")) {
					((JodaDateTimePicker) peer).setTime((LocalTime) evt.getNewValue());
				}
			}	
			
		});
		
		// necessary so that the whole popup doesn't vanish when the calendarPane looses focus.
		calendarPane.setFocusLostBehavior(JFormattedTextField.PERSIST);

		popup.add(timeFieldInPopup, BorderLayout.NORTH);
		peer.add(timeFieldOutside, BorderLayout.CENTER);

		// gives the JodaDateTimePicker a reference to the TimeFields
		((JodaDateTimePicker) peer).setTimeFields(timeFieldInPopup, timeFieldOutside);
		
		// rearranging components that were set in call to super
		peer.remove(field);
		peer.add(field, BorderLayout.WEST);
		
		peer.revalidate();
		peer.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		// make sure that peer is a JodaTimeDatePicker
		if (c instanceof JodaDateTimePicker) {
			peer = (JodaDateTimePicker) c;
		}
	}
	

	
	/**
	 * Gets called as factory method.
	 * 
	 * @param c  the father component
	 * @return a new JodaDateTimePickerUI
	 */
	public static ComponentUI createUI(JComponent c) {
		return new JodaDateTimePickerUI();
	}
	

	
}
