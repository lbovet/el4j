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

package ch.elca.el4j.services.remoting;

import java.util.Map;

import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;


/**
 * This interface decorator adds a map with assembled implicit context to each
 * method of the given interface.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class ContextEnrichmentDecorator implements EnrichmentDecorator {
	/**
	 * Parameter type which has to added.
	 */
	private static final Class CONTEXT_CLASS = Map.class;
	
	/**
	 * This is the name of the context parameter.
	 */
	private static final String CONTEXT_PARAMETER_NAME = "contextMap";

	/**
	 * {@inheritDoc}
	 */
	public String changedInterfaceName(String originalInterfaceName) {
		return originalInterfaceName + "WithContext";
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] changedExtendedInterface(Class[] extendedInterfaces) {
		InterfaceEnricher interfaceIndirector = new InterfaceEnricher();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		Class[] changedInterfaces = new Class[extendedInterfaces.length];
		for (int i = 0; i < extendedInterfaces.length; i++) {
			changedInterfaces[i] = interfaceIndirector.createShadowInterfaceAndLoadItDirectly(
				extendedInterfaces[i], this, cl);
		}
		
		return changedInterfaces;
	}

	/**
	 * {@inheritDoc}
	 */
	public MethodDescriptor changedMethodSignature(MethodDescriptor method) {
		Class[] methodParameterTypes = method.getParameterTypes();
		Class[] methodParameterTypesWithContext = null;
		if (methodParameterTypes == null || methodParameterTypes.length == 0) {
			methodParameterTypesWithContext = new Class[] {CONTEXT_CLASS};
			
		} else {
			methodParameterTypesWithContext
				= new Class[methodParameterTypes.length + 1];
			for (int i = 0; i < methodParameterTypes.length; i++) {
				methodParameterTypesWithContext[i] = methodParameterTypes[i];
			}
			methodParameterTypesWithContext[
				methodParameterTypesWithContext.length - 1] = CONTEXT_CLASS;
		}
		method.setParameterTypes(methodParameterTypesWithContext);
		
		String[] methodParameterNames = method.getParameterNames();
		/**
		 * If the original method no method or has at minimum one parameter and
		 * the parameter names are given from the original method, than add the
		 * new parameter name.
		 */
		if ((methodParameterNames != null
			&& methodParameterNames.length == methodParameterTypes.length)
			|| methodParameterTypesWithContext.length == 1) {
			String[] methodParameterNamesWithContext = null;
			if (methodParameterTypesWithContext.length == 1) {
				methodParameterNamesWithContext
					= new String[] {CONTEXT_PARAMETER_NAME};
			} else {
				methodParameterNamesWithContext
					= new String[methodParameterNames.length + 1];
				for (int i = 0; i < methodParameterNames.length; i++) {
					methodParameterNamesWithContext[i]
						= methodParameterNames[i];
				}
				methodParameterNamesWithContext[
					methodParameterNamesWithContext.length - 1]
					= CONTEXT_PARAMETER_NAME;
			}
			method.setParameterNames(methodParameterNamesWithContext);
		}
		
		return method;
	}
}