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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads configuration from .java files.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
	 * STATE: We have found a starting multiline comment (/ *) marker
	 * and are now searching for a end of multiline comment marker (* /).
	 */
	class CommentState implements LineReadingState {

		/**
		 * Reading state before we have found the multiline comment marker.
		 */
		private LineReadingState ostate;

		/**
		 * Search Pattern 1.
		 * get lines with a * / but not followed by a " aka multiline comment start
		 */
		private Pattern p1 = Pattern.compile(".*\\*/[^\"]*?");
		
		/**
		 * Constructor. 
		 * Taking the previous LineReadingState as input to return to it
		 * after we found the end of multiline comment marker.
		 *
		 * @param oldState the previous LineReadingState
		 */
		public CommentState(LineReadingState oldState) {
			ostate = oldState;
		}
		
		/** {@inheritDoc} */
		public LineReadingState processLine(String line) {
			Matcher m = p1.matcher(line);
			if (m.matches()) {
				return ostate;
			} else {
				return this;
			}
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
		 * Search Pattern 1.
		 * get lines, but omit commented lines with // before "
		 */
		private Pattern p1 = Pattern.compile("[^\"(//)]*\"(.*)\".*");
		
		/**
		 * Search Pattern 2.
		 * get lines with a /* but not after a  " aka multiline comment start
		 */
		private Pattern p2 = Pattern.compile("[^\"]*?/\\*.*");
		
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
			
			//check for multiline start
			Matcher m1 = p2.matcher(line);
			if (m1.matches()) {
				return new CommentState(this);
			} else {
				Matcher m2 = p1.matcher(line);
				if (m2.matches()) {
					m_data.add(m2.group(1));
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
}
