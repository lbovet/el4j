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
package ch.elca.el4j.services.gui.swing.exceptions;

import cookxml.core.exception.CookXmlException;
import cookxml.core.interfaces.ExceptionHandler;

/**
 * A cookXml exception handler that redirects all exceptions to the exception handler of module Swing.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public final class CookXmlExceptionHandler implements ExceptionHandler {
	/**
	 * The single instance.
	 */
	private static final ExceptionHandler s_instance = new CookXmlExceptionHandler();
	
	/**
	 * The hidden constructor.
	 */
	private CookXmlExceptionHandler() { }

	/**
	 * @return    the singleton instance
	 */
	public static ExceptionHandler getInstance() {
		return s_instance;
	}
	
	/** {@inheritDoc} */
	public void handleException(String msg, Exception ex) throws CookXmlException {
		Exceptions.getInstance().handle(ex);
	}

}
