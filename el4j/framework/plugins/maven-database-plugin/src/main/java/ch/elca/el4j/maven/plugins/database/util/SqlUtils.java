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
package ch.elca.el4j.maven.plugins.database.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import ch.elca.el4j.maven.plugins.database.holder.DatabaseHolderException;

/**
 * A utility class for SQL file operations used in maven-database-plugin.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Stefan (DST)
 * @author Stefan Wismer (SWI)
 */
public final class SqlUtils {
	/**
	 * The hidden constructor.
	 */
	private SqlUtils() { }
	
	/**
	 * Extract sql statements from given file.
	 * 
	 * @param fileURL               URL of the file
	 * @param statementDelimiter    Separator for sql statements
	 * @param blockDelimiter        Separator for sql blocks
	 * @return                      a list of sql statements
	 */
	public static List<String> extractStmtsFromFile(URL fileURL, String statementDelimiter, String blockDelimiter) {
		return extractStmtsFromFile(fileURL, statementDelimiter, blockDelimiter, null);
	}
	
	/**
	 * Extract sql statements from given file.
	 * 
	 * @param fileURL               URL of the file
	 * @param statementDelimiter    Separator for sql statements
	 * @param blockDelimiter        Separator for sql blocks
	 * @param patterns              a list of search-replace patterns
	 * @return                      a list of sql statements
	 */
	public static List<String> extractStmtsFromFile(URL fileURL, String statementDelimiter, String blockDelimiter,
		List<FindReplacePattern> patterns) {
		
		ArrayList<String> result = new ArrayList<String>();
		String part;
		StringBuffer stmt = new StringBuffer();
		int index;
		final Pattern beginStmtRegex = Pattern.compile("(declare|is|begin|as)", Pattern.CASE_INSENSITIVE);
		Matcher beginStmtMatcher;
		String expectedDelimiter = statementDelimiter;
		boolean insideComment = false;

		/*
		 * General remark: SQL allows Strings to span over multiple lines. That means that trim is never allowed!
		 */
		BufferedReader buffRead = null;
		try {
			buffRead = new BufferedReader(new InputStreamReader(
				fileURL.openStream()));
			while ((part = buffRead.readLine()) != null) {
				if (patterns != null) {
					for (FindReplacePattern pattern : patterns) {
						part = pattern.apply(part);
					}
				}
				
				String trimmed = StringUtils.trimWhitespace(part);
				
				// Filter out comments
				if (part.length() != 0 && !trimmed.startsWith("--") && (!insideComment || trimmed.contains("*/"))) {
					
					// detect multiline comments
					if (part.contains("*/")) {
						part = part.substring(part.indexOf("*/") + "*/".length());
						insideComment = false;
					}
					if (trimmed.startsWith("/*")) {
						part = "";
						insideComment = !trimmed.endsWith("*/");
					}
					
					beginStmtMatcher = beginStmtRegex.matcher(part);
					// Detect begin/end of statement sequence
					if (beginStmtMatcher.matches()) {
						expectedDelimiter = blockDelimiter;
					}
					
					// append "\n" so that "...ABC\nNAME..." is not reduced to "...ABCNAME..."
					if (part.length() != 0) {
						part = part + "\n";
					}
					
					// Split statements by delimiter, by default ';'
					while ((index = part.indexOf(expectedDelimiter)) != -1) {
						
						// add statement to result array
						result.add(stmt.toString() + part.substring(0, index));
						
						// reset expected delimiter
						expectedDelimiter = statementDelimiter;
						// reset statement string
						stmt.setLength(0);
						
						// check if Part has input after the delimiter.
						// If so, continue.
						if (index < part.length()) {
							part = part.substring(index + 1, part.length());
						} else {
							part = "";
						}
					}
					stmt.append(part);
				}
			}
		} catch (IOException e) {
			throw new DatabaseHolderException(e);
		} finally {
			if (buffRead != null) {
				try {
					buffRead.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return result;
	}
}
