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
package ch.elca.el4j.maven.plugins.springide;

import java.io.BufferedReader;

/**
 * Base class for reading configuration from files.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public abstract class AbstractReader {

	/** Marker for the beginning of a configuration include entry. */
	protected static final String MARKER_INCLUDE = "$$ BEANS INCLUDE $$";
	
	/** Marker for the beginning of a configuration exclude entry. */
	protected static final String MARKER_EXCLUDE = "$$ BEANS EXCLUDE $$";
		
	/** Inclusive conf locations. This is set by a method. */
	protected String[] m_inclusive;
	
	/** Exclusive conf locations. This is set by a method. */
	protected String[] m_exclusive;
	
	/** The current state. */
	protected LineReadingState m_state;
	
	/**
	 * Read from an input buffer and process.
	 * @param r The buffer to read from.
	 */
	public abstract void read(BufferedReader r);
	
	/**
	 * State in the line reading state machine.
	 */
	protected interface LineReadingState {
		/**
		 * Process a line.
		 * @param line The line to process.
		 * @return the new state. Can be <code>this</code>.
		 */
		LineReadingState processLine(String line);
	}
	
	/**
	 * @return Returns the inclusive configuration.
	 */
	public String[] getInclusive() {
		return m_inclusive;
	}

	/**
	 * @return Returns the exclusive configuration.
	 */
	public String[] getExclusive() {
		return m_exclusive;
	}
}
