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
package ch.elca.el4j.maven.logging;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.ExceptionUtils;


/**
 * Base class for various formatting loggers.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public abstract class AbstractFormattingLogger extends AbstractLogger {

	/**
	 * Delegating constructor.
	 *
	 * @param threshold
	 *            Logger threshold (debug, info, warn, error, fatal).
	 * @param name
	 *            The logger name.
	 */
	public AbstractFormattingLogger(int threshold, String name) {
		super(threshold, name);
	}

	/**
	 * Get the appropriate prefix for messages of level <code>level</code>.
	 * @param level The logger level.
	 * @return The prefix string for this level.
	 */
	protected abstract String getPrefix(int level);
	
	/**
	 * Get the appropriate suffix for messages of level <code>level</code>.
	 * @param level The logger level.
	 * @return The suffix string for this level.
	 */
	protected abstract String getSuffix(int level);
	
	/**
	 * Get the appropriate text for messages of level <code>level</code>.
	 * Defaults to [LEVEL].
	 * @param level The logger level.
	 * @return The text for this level.
	 */
	protected String getText(int level) {
		String levelPrefix;
		switch (level) {
			case Logger.LEVEL_DEBUG:
				levelPrefix = "[DEBUG] ";
				break;
			case Logger.LEVEL_INFO:
				levelPrefix = "[INFO] ";
				break;
			case Logger.LEVEL_WARN:
				levelPrefix = "[WARNING] ";
				break;
			case Logger.LEVEL_ERROR:
				levelPrefix = "[ERROR] ";
				break;
			case Logger.LEVEL_FATAL:
				levelPrefix = "[FATAL] ";
				break;
			default:
				// Can never happen.
				throw new RuntimeException("Invalid level.");
		}
		return levelPrefix;
	}
	
	/**
	 * @param message    the message
	 * @return           the message prepared for output
	 */
	protected String getMessage(String message) {
		return message;
	}
	
	/**
	 * Output a message with the correct formatting for the level. Add a
	 * stacktrace if present.
	 *
	 * Defaults to adding prefix, text and suffix to all messages and traces.
	 *
	 * @param level
	 *            The logger level.
	 * @param message
	 *            The message.
	 * @param throwable
	 *            An exception to provide a stacktrace, or null.
	 */
	protected void out(int level, String message, Throwable throwable) {
		String prefix = getPrefix(level);
		String suffix = getSuffix(level);
		String text = getText(level);
		String msg = getMessage(message);

		// Checkstyle: Using System.out is ok here
		// because we are implementing a logger!
		System.out.println(prefix + text + msg + suffix);

		if (throwable != null) {
			System.out.print(prefix);
			System.out.print(getMessage(ExceptionUtils.getFullStackTrace(throwable)));
			System.out.print(suffix);
		}
	}

	// Level handlers - just pass on the parameters to out()
	// addingthe correct level.
	
	/** {@inheritDoc} */
	public void debug(String message, Throwable throwable) {
		if (isDebugEnabled()) {
			out(Logger.LEVEL_DEBUG, message, throwable);
		}
	}

	/** {@inheritDoc} */
	public void info(String message, Throwable throwable) {
		if (isInfoEnabled()) {
			out(Logger.LEVEL_INFO, message, throwable);
		}
	}

	/** {@inheritDoc} */
	public void warn(String message, Throwable throwable) {
		if (isWarnEnabled()) {
			out(Logger.LEVEL_WARN, message, throwable);
		}
	}

	/** {@inheritDoc} */
	public void error(String message, Throwable throwable) {
		if (isErrorEnabled()) {
			out(Logger.LEVEL_ERROR, message, throwable);
		}
	}

	/** {@inheritDoc} */
	public void fatalError(String message, Throwable throwable) {
		if (isFatalErrorEnabled()) {
			out(Logger.LEVEL_FATAL, message, throwable);
		}
	}

	/** {@inheritDoc} */
	public Logger getChildLogger(String name) {
		return this;
	}

}
