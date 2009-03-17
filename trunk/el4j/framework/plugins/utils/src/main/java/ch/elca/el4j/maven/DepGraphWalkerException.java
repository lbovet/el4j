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
package ch.elca.el4j.maven;

public class DepGraphWalkerException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -2477553692644057146L;

	/**
	 * Constructor.
	 * @param cause Cause of exception
	 */
	public DepGraphWalkerException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor.
	 * @param msg The exception message
	 * @param cause The cause of the exception
	 */
	public DepGraphWalkerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}