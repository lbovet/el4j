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
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ch.elca.el4j.util.codingsupport.JodaTimeUtils;

/**
 * 
 * This class is a widget that allows the input of time and shows the time with a label.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class TimeField extends JComponent {
	
	/**
	 * Is the label which will display the time.
	 */
	private JLabel m_timeLabel;
	/**
	 * Is the TimeInput which lets the user input a time.
	 */
	private TimeInput m_timeInput; 
	/**
	 * Is the time to which the widget is currently set.
	 */
	private LocalTime m_time;

	/**
	 * Defines if the widget bothers with seconds.
	 */
	private boolean m_showSeconds;
	
	/**
	 * Defines if the widget shows a label under the TextField.
	 */
	private boolean m_showLabel;
	
	/**
	 * Public constructor.
	 * 
	 * @param time is the time to set the widget to
	 * @param showLabel if set to true then a label with the currently selected time is shown under the textfield
	 * 
	 */
	public TimeField(LocalTime time, boolean showLabel) {
		m_time = time;
		m_showLabel = showLabel;
		setLayout(new BorderLayout());
		m_timeInput = new TimeInput(m_time);
		add(m_timeInput, BorderLayout.NORTH);
		if (showLabel) {
			m_timeLabel = new JLabel(formatLocalTimeToString(time), JLabel.CENTER);
			add(m_timeLabel, BorderLayout.CENTER);
			add(new JSeparator(), BorderLayout.SOUTH);
		}
	}
	
	/**
	 * Set timeInput and timeLabel to the specified LocalTime.
	 *  
	 * @param time is the LocalTime to set the components to
	 */
	
	public void setTime(LocalTime time) {
		String timeString = null;
		// use format according to the value of showSeconds
		if (m_showSeconds) {
			timeString = JodaTimeUtils.getLocalTimeString(time, "HH:mm:ss");
		} else {
			timeString = JodaTimeUtils.getLocalTimeString(time, "HH:mm");
		}
		// keep caret position
		
		int caretPosition = m_timeInput.getCaretPosition();
		m_timeInput.setText(timeString);
		m_timeInput.setCaretPosition(caretPosition);
		if (m_showLabel) {
			m_timeLabel.setText(timeString);
		}
	}
	
	/**
	 * When set to false, seconds won't be shown in this widget.
	 * 
	 * @param showSeconds decides if we see seconds in this widget
	 */
	public void setShowSeconds(boolean showSeconds) {
		m_showSeconds = showSeconds;
	}
	
	/**
	 * Delegating method for firePropertyChange, for TimeInput to call.
	 * Makes sure that the PropertyChangeEvent is fired by TimeField.
	 * 
	 * @param string is the name of the event
	 * @param time is the old value of time
	 * @param newTime is the new value of time
	 */
	
	private void fireTimeFieldPropertyChange(String string, LocalTime time, LocalTime newTime) {
		firePropertyChange(string, time, newTime);
		
	}
	
	/**
	 * 
	 * This class is a small JTextField based class that allows input of times.
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
	
	class TimeInput extends JTextField {
		
		/**
		 * Constructor for TimeInput.
		 * 
		 * @param time is the time to set the widget to.
		 */
		
		public TimeInput(LocalTime time) {
			setText(formatLocalTimeToString(time));
			
			/**
			 * Adds a keylistener to this component so we can check every key event.
			 */
			addKeyListener(new KeyAdapter() {
				
				/**
				 * {@inheritDoc}
				 */
				@Override
				public void keyTyped(KeyEvent e) {
					char c = e.getKeyChar();
					
					// make sure that only 
					if (!((Character.isDigit(c) || (c == ':')
							|| (c == KeyEvent.VK_BACK_SPACE)
							|| (c == KeyEvent.VK_LEFT)
							|| (c == KeyEvent.VK_RIGHT)
							|| (c == KeyEvent.VK_ENTER)
							|| (c == KeyEvent.VK_DELETE)
							|| (c == '.')))) {
						e.consume();
					}
					
					// if we already have to much text, then only propergate certain events
					if ((getText().length() > 7 && m_showSeconds)  
						|| (getText().length() > 4 && !m_showSeconds)
						&&
						!((c == KeyEvent.VK_BACK_SPACE)
						|| (c == KeyEvent.VK_DELETE) 
						|| (c == KeyEvent.VK_LEFT)
						|| (c == KeyEvent.VK_ENTER)
						|| (c == KeyEvent.VK_RIGHT)))
						{
						e.consume();
					}
					
					super.keyTyped(e);
						
				}
				/**
				 * {@inheritDoc}
				 */
				@Override
				public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					LocalTime newTime;
					newTime = formatStringToLocalTime(((TimeInput) e.getSource()).getText());
					if (newTime != null) {
						setBackground(Color.WHITE);
						String formattedTime = formatLocalTimeToString(newTime);
						// don't set new values if user was just going backwards or forwards
						char c = e.getKeyChar();
						fireTimeFieldPropertyChange("time", m_time, newTime);

						if (Character.isDigit(c) || c == '-' || c == KeyEvent.VK_ENTER) {
							m_time = newTime;
							if (m_showLabel) {
								m_timeLabel.setText(formattedTime);
							}
							int caretPosition = m_timeInput.getCaretPosition();
							m_timeInput.setText(formattedTime);
							m_timeInput.setCaretPosition(caretPosition);

						}

					} else {
						// in this case the input is invalid
						setBackground(Color.RED);
					}
				}	
			});
		}
	}
	/**
	 * Tries to make a LocalTime out of a string.
	 * 
	 * @param string
	 * @return a LocalTime constructed out of the string, or null if this isn't possible
	 */
	
	private LocalTime formatStringToLocalTime(String string) {
		String[] partsSemicolon = string.split(":");
		String[] partsPeriod = string.split("\\.");
		String[] parts = null;
		// check which is the delimeter between the numbers
		if (partsSemicolon.length >= 2) {
			parts = partsSemicolon;
		} else if (partsPeriod.length >= 2) {
			parts = partsPeriod;
		}
		// case: HH:MM:SS, only use when showSeconds is true
		if (m_showSeconds && parts != null && parts.length == 3 && parts[0].length() == 3 && parts[1].length() == 2 && parts[2].length() == 2) {
			try {
				int hours = Integer.parseInt(parts[0]);
				int minutes = Integer.parseInt(parts[1]);
				int seconds = Integer.parseInt(parts[2]);
			
				
				if (hours < 24 && minutes < 60 && seconds < 60
					&& hours >= 0 && minutes >= 0 && seconds >= 0) {
					return new LocalTime(hours, minutes, seconds);
				} else {
					return null;
				}
			} catch (NumberFormatException e) {
				// if one of the parts wasn't a number
				return null;
			}
		}
		
		// case: HH:MM when m_showSeconds is false
		if (!m_showSeconds && parts != null && parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 2) {
			try {
				int hours = Integer.parseInt(parts[0]);
				int minutes = Integer.parseInt(parts[1]);
				
				if (hours < 24 && minutes < 60
					&& hours >= 0 && minutes >= 0) {
					return new LocalTime(hours, minutes, 0);
				} else {
					return null;
				}
			} catch (NumberFormatException e) {
				// if one of the parts wasn't a number
				return null;
			}
		}
		
		// case: HHMMSS when m_showSeconds is true
		if (m_showSeconds && parts == null && string.length() == 6) {
			try {
				int hours = Integer.parseInt(string.substring(0, 2));
				int minutes = Integer.parseInt(string.substring(2,4));
				int seconds = Integer.parseInt(string.substring(4,6));
		
				if (hours < 24 && minutes < 60 && seconds < 60
					&& hours >= 0 && minutes >= 0 && seconds >= 0) {
					return new LocalTime(hours, minutes, seconds);
				} else {
					return null;
				}
			} catch (NumberFormatException e) {
				// if one of the parts wasn't a number
				return null;
			}
		}
	
		// case HHMM
		if (!m_showSeconds && parts == null && string.length() == 4) {
			try {
				int hours = Integer.parseInt(string.substring(0, 2));
				int minutes = Integer.parseInt(string.substring(2,4));
		
				if (hours < 24 && minutes < 60 
					&& hours >= 0 && minutes >= 0) {
					return new LocalTime(hours, minutes, 0);
				} else {
					return null;
				}
			} catch (NumberFormatException e) {
				// if one of the parts wasn't a number
				return null;
			}
		}
		return null;
	}
	
	
	/**
	 * Makes a nice String out of the time in the DateTime.
	 * 
	 * @param time is the time we format to a string
	 * @return a nice string showing the time
	 */
	
	private String formatLocalTimeToString(LocalTime time) {
		
		if (m_showSeconds) {
			return JodaTimeUtils.getLocalTimeString(time, "HH:mm:ss");
		} else {
			return JodaTimeUtils.getLocalTimeString(time, "HH:mm");

		}	
	}	
}
