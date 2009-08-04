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
package ch.elca.el4j.env.xml;

import java.util.Properties;

import org.springframework.util.Assert;

/**
 * A utility class to evaluate maven-styled expressions.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public final class ResolverUtils {
	/**
	 * The hidden constructor.
	 */
	private ResolverUtils() { }
	
	/**
	 * Resolve a String containing maven-styled expressions.
	 * 
	 * @param expression    the expression to resolve
	 * @param values        the variable->value mapping
	 * @return              the resolved String
	 */
	public static String resolve(String expression, Properties values) {
		Assert.notNull(expression);
		
		StringBuilder builder = new StringBuilder();
		int cursor = 0;
		int start = 0;
		int end = 0;
		while ((start = expression.indexOf("${", cursor)) >= 0) {
			end = expression.indexOf("}", cursor + 2);
			if (end >= 0) {
				String value = values.getProperty(expression.substring(start + 2, end));
				if (value != null) {
					builder.append(expression.substring(cursor, start));
					builder.append(value);
				} else {
					builder.append(expression.substring(cursor, end + 1));
				}
				cursor = end + 1;
			} else {
				break;
			}
		}
		
		return builder.toString() + expression.substring(cursor);
	}
}
