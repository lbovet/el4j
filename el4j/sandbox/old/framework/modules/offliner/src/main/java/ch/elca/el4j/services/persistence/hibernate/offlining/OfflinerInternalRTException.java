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
package ch.elca.el4j.services.persistence.hibernate.offlining;

/**
 * Represents a serious problem in the offliner code a.k.a. "this should never happen".
 * If you ever see one of these it's probably a offliner bug.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class OfflinerInternalRTException extends RuntimeException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -3384451782567112758L;

	/**
	 * Constructor for RuntimeException.
	 * @param message The message.
	 * @param cause The exception that caused this offliner error.
	 */
	public OfflinerInternalRTException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message The message.
	 */
	public OfflinerInternalRTException(String message) {
		super(message);
	}

	/**
	 * @param cause The exception that caused this offliner error.
	 */
	public OfflinerInternalRTException(Throwable cause) {
		super(cause);
	}

	
}
