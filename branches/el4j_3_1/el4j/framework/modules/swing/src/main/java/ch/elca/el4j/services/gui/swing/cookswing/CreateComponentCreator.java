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
package ch.elca.el4j.services.gui.swing.cookswing;

import java.lang.reflect.Method;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CookXmlException;
import cookxml.core.exception.CreatorException;
import cookxml.core.interfaces.Creator;

/**
 * The cookSwing creator for general purpose &lt;create-component&gt;s.
 * The create-method is invoked when the XML tag is opened and
 * the finish-method when the tag is closed.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class CreateComponentCreator implements Creator {
	// <create-component> specific attributes
	/**
	 * The XML attribute name for the create method.
	 */
	protected static final String CREATE_METHOD = "create-method";
	
	/**
	 * The XML attribute name for the finish method.
	 */
	protected static final String FINISH_METHOD = "finish-method";
	
	/**
	 * The logger.
	 */
	private static final Logger s_logger = LoggerFactory
		.getLogger(CreateComponentCreator.class);
	
	/** {@inheritDoc} */
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws CreatorException {
		
		Object form = decodeEngine.getVariable("this");
		String methodName = elm.getAttribute(CREATE_METHOD);
		
		if (!methodName.equals("")) {
			return invokeMethod(form, methodName);
		} else {
			return new JPanel();
		}
	}

	/** {@inheritDoc} */
	public Object editFinished(String parentNS, String parentTag, Element elm,
		Object parentObj, Object obj, DecodeEngine decodeEngine) throws CookXmlException {
		
		Object form = decodeEngine.getVariable("this");
		String methodName = elm.getAttribute(FINISH_METHOD);
		
		if (!methodName.equals("")) {
			invokeMethod(form, methodName, obj);
		}
		return obj;
	}
	
	/**
	 * Invoke a potentially private method.
	 *
	 * @param form          the form object
	 * @param methodName    the method name
	 * @param parameters    optional parameters
	 * @return              the return value of the invoked method
	 */
	protected Object invokeMethod(Object form, String methodName, Object... parameters) {
		Class<?> formClass = form.getClass();
		while (formClass != Object.class) {
			// list all methods to access private methods as well
			final Method[] publicMethods = formClass.getDeclaredMethods();
			for (int i = 0; i < publicMethods.length; ++i) {
				if (methodName.equals(publicMethods[i].getName())) {
					try {
						publicMethods[i].setAccessible(true);
						return publicMethods[i].invoke(form, parameters);
					} catch (Exception e) {
						// try next method
						continue;
					}
				}
			}
			formClass = formClass.getSuperclass();
		}
		s_logger.error("Error processing <create-component>. Could not find method '" + methodName + "'.");
		return null;
	}
}
