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
package ch.elca.el4j.apps.refdb.dom;

import java.util.Locale;

import ch.elca.el4j.util.codelist.Codelist;

/**
 * Enum type describing the origin of a reference.
 * This is implemented as a codelist using the given
 * interface and the helper class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public enum LinkOrigin implements Codelist {

	/**
	 * Code for file system origin. 
	 */
	FILE_SYSTEM(1, 1),
	
	/**
	 * Code for delicious social bookmark origin.
	 */
	DELICIOUS(2, 2),
	
	/**
	 * Code for digg social bookmark origin.
	 */
	DIGG(3, 3);
	
	/**
	 * Internal field for ID.
	 * This is supposed to be unique over all implemented codelists in one application.
	 */
	private int ID;
	
	/**
	 * Internal field for intCode.
	 * This is supposed to be unique for this codelist. 
	 */
	private int intCode;
	
	/**
	 * Constructor.
	 * 
	 * @param id The ID of the code.
	 * @param intcode The intCode of the code.
	 */
	LinkOrigin(int id, int intcode) {
		this.ID = id;
		this.intCode = intcode;
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public int getIntCode() {
		return intCode;
	}

	@Override
	public String getLongText(Locale lang) {
		//returning the simple name
		return this.name();
	}

	@Override
	public String getShortText(Locale lang) {
		//returning the simple name
		return this.name();
	}

	@Override
	public String getValue() {
		return this.name();
	}

	
	
}
