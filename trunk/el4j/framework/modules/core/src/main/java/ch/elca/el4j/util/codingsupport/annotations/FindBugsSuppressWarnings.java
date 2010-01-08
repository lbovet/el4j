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
package ch.elca.el4j.util.codingsupport.annotations;

import java.lang.annotation.Annotation;

/**
 * 
 * This is a substitute for the annotation SuppressWarnings that FindBugs normally would use.
 * As SuppressWarnings is already used in java.lang package, using this annotation avoids a name clash.
 * FindBugs only cares for the last part of the annotation name, which has to be SuppressWarnings. 
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */

public @interface FindBugsSuppressWarnings {

	/**
	 * Is the name of the bug that we want to suppress.
	 * 
	 * @return
	 */
	public abstract String[] value();

	/**
	 * Is the justification for suppressing this bug.
	 * 
	 * @return
	 */
	
	public abstract String justification();

}
