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
package ch.elca.el4j.maven.logging.console;

import ch.elca.el4j.maven.logging.AbstractFormattingLogger;


/**
 * Logger that outputs ansi colored text.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class ColorLogger extends AbstractFormattingLogger {

	/**
	 * Delegating constructor.
	 *
	 * @param threshold
	 *            Logger threshold (debug, info, warn, error, fatal).
	 * @param name
	 *            The logger name.
	 */
	public ColorLogger(int threshold, String name) {
		super(threshold, name);
	}

	/**
	 * Helper class to handle ansi coloring.
	 */
	static abstract class Ansi {
		
		/**
		 * To keep checkstyle happy. This is never called.
		 */
		protected Ansi() {
			
		}
		
		// Constants for colors.
		static final int RESET = 0;

		static final int FGBLACK = 30;
		static final int FGRED = 31;
		static final int FGGREEN = 32;
		static final int FGYELLOW = 33;
		static final int FGBLUE = 34;
		static final int FGMAGENTA = 35;
		static final int FGCYAN = 36;
		static final int FGWHITE = 37;

		static final int BGBLACK = 40;
		static final int BGRED = 41;
		static final int BGGREEN = 42;
		static final int BGYELLOW = 43;
		static final int BGBLUE = 44;
		static final int BGMAGENTA = 45;
		static final int BGCYAN = 46;
		static final int BGWHITE = 47;

		static final int INTENSE = 1;

		/**
		 * Create an ansi code from the given values.
		 *
		 * @param values
		 *            A varargs list of values.
		 * @return The ansi code for these values.
		 */
		static String ansi(int... values) {
			if (values.length == 0) {
				return "";
			}
			String code = "\033[";
			for (int current : values) {
				code += Integer.toString(current) + ";";
			}
			// Strip last ; again
			code = code.substring(0, code.length() - 1);
			code += "m";
			return code;

		}
	}

	/** {@inheritDoc} */
	protected String getPrefix(int level) {
		if (System.getProperty("colorlogger.whiteConsole") != null) {
			switch(level) {
				case LEVEL_DEBUG:
					return Ansi.ansi(Ansi.FGCYAN);
				case LEVEL_INFO:
					return Ansi.ansi(Ansi.FGBLUE);
				case LEVEL_WARN:
					return Ansi.ansi(Ansi.FGMAGENTA);
				case LEVEL_ERROR:
					return Ansi.ansi(Ansi.FGRED);
				case LEVEL_FATAL:
					return Ansi.ansi(Ansi.BGBLACK, Ansi.FGYELLOW);
				default:
					throw new RuntimeException("Invalid level.");
			}
		} else {
			switch(level) {
				case LEVEL_DEBUG:
					return Ansi.ansi(Ansi.INTENSE, Ansi.FGWHITE);
				case LEVEL_INFO:
					return Ansi.ansi(Ansi.FGWHITE);
				case LEVEL_WARN:
					return Ansi.ansi(Ansi.INTENSE, Ansi.FGYELLOW);
				case LEVEL_ERROR:
					return Ansi.ansi(Ansi.INTENSE, Ansi.FGRED);
				case LEVEL_FATAL:
					return Ansi.ansi(Ansi.BGRED, Ansi.INTENSE, Ansi.FGWHITE);
				default:
					throw new RuntimeException("Invalid level.");
			}
		}
		
	}
	
	@Override
	protected String getMessage(String message) {
		if (System.getProperty("colorlogger.cygwin") != null) {
			// escape backslashes
			return message.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
		} else {
			return message;
		}
	}

	/** {@inheritDoc} */
	protected String getSuffix(int level) {
		return Ansi.ansi(Ansi.RESET);
	}

	
	
}
