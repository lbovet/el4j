/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.database.util;

import java.util.regex.Pattern;

/**
 * This class represents a find {@link Pattern} together with its replacement String.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class FindReplacePattern {
	/**
	 * The pattern to find.
	 */
	private Pattern m_findPattern;
	
	/**
	 * The replacement String.
	 */
	private String m_replacement; 
	
	/**
	 * @param findPattern    the pattern to find
	 * @param replacement    the replacement String
	 */
	public FindReplacePattern(String findPattern, String replacement) {
		m_findPattern = Pattern.compile(findPattern, Pattern.CASE_INSENSITIVE);
		m_replacement = replacement;
	}
	
	/**
	 * @param findPattern    the pattern to find
	 * @param replacement    the replacement String
	 * @param patternFlag    the find pattern flag to use (see {@link Pattern#compile(String, int)})}
	 */
	public FindReplacePattern(String findPattern, String replacement, int patternFlag) {
		m_findPattern = Pattern.compile(findPattern, patternFlag);
		m_replacement = replacement;
	}
	
	/**
	 * Perform the find-replace operation.
	 * 
	 * @param input    the input String
	 * @return         the transformed output String, where all occurrences of the find pattern got
	 *                 replaced by the replacement String.
	 */
	public String apply(String input) {
		return m_findPattern.matcher(input).replaceAll(m_replacement);
	}
}
