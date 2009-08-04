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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.notifications;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Utiility to check that the correct notifications arrive in the correct order. 
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class NotificationProcessor {

	/** A logger for warnings when validation fails. */
	private static final Logger s_log = Logger.getLogger(NotificationProcessor.class);
	
	/** The list of called methods. */
	private List<Notification> m_called;
	
	/** The list of expected methods. */
	private List<Notification> m_expected;
	
	/**
	 * Set up the processor. 
	 */
	public NotificationProcessor() {
		m_called = new LinkedList<Notification>();
		m_expected = new LinkedList<Notification>();
	}
	
	/** 
	 * Set the expected notifications.
	 * @param expected The expected notifications.
	 */
	public void expect(Notification ... expected) {
		for (Notification expectedNotification : expected) {
			m_expected.add(expectedNotification);
		}
	}
	
	/**
	 * Called by methods raising notifications.
	 * @param target The notification.
	 */
	public void call(Notification target) {
		m_called.add(target);
	}
	
	/**
	 * Thrown if validate() fails.
	 */
	public static class ValidationFailedException extends RuntimeException {

		/**
		 * @param message The cause.
		 */
		public ValidationFailedException(String message) {
			super(message);
		}
	}
	
	/**
	 * Call at the end of processing to check the right notifications were recieved.
	 * @throws ValidationFailedException if validation falied.
	 */
	public void validate() throws ValidationFailedException {
		validateSize();
		
		// Size is equal so safe to iterate. 
		// Iterate over all notifications and validate.
		for (int i = 0; i < m_called.size(); i++) {
			Notification expected = m_expected.get(i);
			Notification called = m_called.get(i);
			if (!expected.validate(called)) {
				dump(i);
				throw new ValidationFailedException("Notification #" + i 
					+ " invalid.");
			}
		}
	}

	/**
	 * Validate, ignoring the order the messages arrive.
	 * @throws ValidationFailedException if validation failed.
	 */
	public void validateAnyOrder() throws ValidationFailedException {
		validateSize();
		
		for (Notification note : m_expected) {
			boolean valid = false;
			Iterator<Notification> it = m_called.iterator();
			while (it.hasNext()) {
				Notification next = it.next();
				if (note.validate(next)) {
					it.remove();
					valid = true;
					break;
				}
			}
			if (!valid) {
				s_log.warn("No notification matched expected entry " + note.toString());
				throw new ValidationFailedException(note.toString());
			}
		}
	}
	
	/**
	 * Check the number of notifications.
	 * @throws ValidationFailedException If the sizes do not match.
	 */
	private void validateSize() throws ValidationFailedException {
		if (m_called.size() != m_expected.size()) {
			dump();
			throw new ValidationFailedException("Wrong number of calls, expected "
				+ m_expected.size() + ", got " + m_called.size() + ".");
		}
	}
	
	/**
	 * Logger if number does not match.
	 */
	private void dump() {
		s_log.warn("Validation failed. Expected:");
		for (Notification n : m_expected) {
			s_log.warn("  " + n);
		}
		s_log.warn("Recieved:");
		for (Notification n : m_called) {
			s_log.warn("  " + n);
		}
	}
	
	/**
	 * Logger if validation fails.
	 * @param pos The position at which it failed.
	 */
	private void dump(int pos) {
		s_log.warn("Validation failed at entry " + pos);
		s_log.warn("Expected: " + m_expected.get(pos));
		s_log.warn("Got: " + m_called.get(pos));
	}
}
