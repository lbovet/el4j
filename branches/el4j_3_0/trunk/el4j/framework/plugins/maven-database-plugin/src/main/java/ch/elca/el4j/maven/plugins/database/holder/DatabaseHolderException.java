/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.database.holder;

/**
 *
 * This class is thrown wherever an exception is encountered during execution.
 * We need this to be able to catch some of the exceptions we have thrown
 * earlier.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Stefan (DST)
 */
public class DatabaseHolderException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -2477553692644057146L;

	/**
	 * Constructor.
	 * @param cause Cause of exception
	 */
	public DatabaseHolderException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor.
	 * @param msg The exception message
	 * @param cause The cause of the exception
	 */
	public DatabaseHolderException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
