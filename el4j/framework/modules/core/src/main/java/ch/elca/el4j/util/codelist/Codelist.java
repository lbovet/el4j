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
package ch.elca.el4j.util.codelist;

import java.util.Locale;

import ch.elca.el4j.util.codingsupport.SerializableEnum;

/**
 * Interface to be implemented by java enums which represent codelists.
 *
 * This interface is inspired by a concept for a central maintenance and management
 * of codelists. These are deployed to software components for pure offline use.
 * According to the concept, the enums implementing this interface are generated
 * by a java code generator.
 * 
 * For a convenient implementation of the described functionality use the 
 * CodelistUtility class in this package. Since the extensibility of enum types 
 * is not supported by the language construct, the functional implementation is 
 * provided in the CodelistUtility class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */

public interface Codelist extends SerializableEnum {
	
	/**
	 * Getter for the globally unique ID of the code.
	 *
	 * This ID should be uniquely defined over all codes in all available codelists.
	 *
	 * @return The unique ID of the code
	 */
	public int getID();
	
	/**
	 * Getter for the intCode of the code.
	 *
	 * This is defined to be a number representing the code inside the codelist. 
	 *
	 * @return The intCode of the code
	 */
	public int getIntCode();
		
	/**
	 * Getter for the short textual description of the code in a given language.
	 *
	 * @param lang Java Locale of the desired language for the returned text
	 * @return The short textual description of the code
	 */
	public String getShortText(Locale lang);
	
	/**
	 * Getter for the long textual description of the code in a given language.
	 *
	 * @param lang Java Locale of the desired language for the returned text
	 * @return The short textual description of the code
	 */
	public String getLongText(Locale lang);

}
