/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.socketstatistics;


/**
 * Exception handling for class RefelctiveDelegator.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class DelegationException extends RuntimeException {
	/**
	 * Constructor.
	 * 
	 * @param message
	 *            message of exception
	 */
	public DelegationException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            message of exception
	 * @param cause
	 *            cause of exception
	 */
	public DelegationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            cause of exception
	 */
	public DelegationException(Throwable cause) {
		super(cause);
	}
}