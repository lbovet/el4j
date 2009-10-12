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
package ch.elca.el4j.maven.plugins.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads configuration from .java files.
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
public class JavaReader extends AbstractReader {

	/** {@inheritDoc} */
	@Override
	public void read(BufferedReader r) {
		try {
			String line;
			m_state = new SearchingState();
			while ((line = r.readLine()) != null) {
				m_state = m_state.processLine(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * STATE : We are searching for a marker to begin reading at.
	 */
	class SearchingState implements LineReadingState {
		
		/** {@inheritDoc} */
		public LineReadingState processLine(String line) {
			LineReadingState state = this;
			if (line.contains(MARKER_INCLUDE)) {
				state = new ReadingState(true);
			} else if (line.contains(MARKER_EXCLUDE)) {
				state = new ReadingState(false);
			}
			return state;
		}
	}
	
	/**
	 * STATE : We saw a "begin" marker. Now we read until the end.
	 */
	class ReadingState implements LineReadingState {
		
		/** Whether we are collecting inclusive or exclusive data. */
		private boolean m_include;
		
		/** The data we collect. */
		private List<String> m_data;
		
		/**
		 * @param inclusive
		 * Whether we are collecting inclusive or exclusive data.
		 */
		public ReadingState(boolean inclusive) {
			m_include = inclusive;
			m_data = new LinkedList<String>();
		}
		
		/** {@inheritDoc} */
		public LineReadingState processLine(String line) {
			final String regex = ".*\"(.*)\".*";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(line);
			if (m.matches()) {
				m_data.add(m.group(1));
			}
			if (line.contains("}")) {
				String[] result = m_data.toArray(new String[0]);
				if (m_include) {
					m_inclusive = result;
				} else {
					m_exclusive = result;
				}
				return new SearchingState();
			} else {
				return this;
			}
		}
	}
}
