/*
 * Project: KnowHow
 *
 * Copyright 2008 by ELCA Informatik AG
 * Steinstrasse 21, CH-8036 Zurich
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatik AG ("Confidential Information"). You
 * shall not disclose such "Confidential Information" and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.services.persistence.jpa.util;

/**
 * Exception thrown from jpa query.
 *
 * @author Sandra Weber (swr)
 */
public class QueryException extends RuntimeException {

	/**
	 * Constructor taking a message.
	 * @param message The message.
	 */
	public QueryException(String message) {
		super(message);
	}
}
