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
package ch.elca.el4j.util.objectwrapper;

/**
 * Base class for all exceptions thrown if an wrapped operation fails because the 
 * underlying object does not have the required capabilities.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/AspectRTException.java $",
 *    "$Revision: 1549 $",
 *    "$Date: 2008-07-30 14:25:13 +0200 (Wed, 30 Jul 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ObjectWrapperRTException extends RuntimeException {

	/**
	 * Empty constructor.
	 */
	public ObjectWrapperRTException() { }

	/**
	 * Constructor taking message.
	 * @param message The message.
	 */
	public ObjectWrapperRTException(String message) {
		super(message);
	}

	/**
	 * Constructor with cause.
	 * @param cause The cause.
	 */
	public ObjectWrapperRTException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message The message.
	 * @param cause the cause.
	 */
	public ObjectWrapperRTException(String message, Throwable cause) {
		super(message, cause);
	}

}
