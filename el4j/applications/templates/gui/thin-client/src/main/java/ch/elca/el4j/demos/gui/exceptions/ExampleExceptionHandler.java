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
package ch.elca.el4j.demos.gui.exceptions;

import ch.elca.el4j.services.gui.swing.exceptions.Handler;

/**
 * This class represents an example exception handler.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class ExampleExceptionHandler implements Handler {
	/** {@inheritDoc} */
	public boolean recognize(Exception e) {
		return (e != null);
	}
	
	/** {@inheritDoc} */
	public int getPriority() {
		return 0;
	}
	
	/** {@inheritDoc} */
	public boolean handle(Exception e) {
		System.err.println("An exception occurred!");
		e.printStackTrace(System.err);
		
		return false;
	}
}
