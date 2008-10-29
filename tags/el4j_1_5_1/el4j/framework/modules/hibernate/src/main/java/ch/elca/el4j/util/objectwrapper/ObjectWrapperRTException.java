/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
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
