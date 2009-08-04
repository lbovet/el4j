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
package com.silvermindsoftware.hitch.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;


import com.silvermindsoftware.hitch.validation.ValidatingBindingListener;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.util.config.GenericConfig;

/**
 * This class is used for creating bindings.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class BindingFactory {
	/**
	 * An instance of the factory (used for singleton).
	 */
	private static BindingFactory s_bindingFactory;
	
	/**
	 * Default mapping of properties.
	 */
	private static final DefaultProperties DEFAULT_PROPERTIES
		= new DefaultProperties();
	
	/**
	 * Map containing user defined binding mappings.
	 */
	@SuppressWarnings("unchecked")
	private Map<JComponent, BindingCreator> m_specialBinding;
	
	/**
	 * Map containing user defined validation responder.
	 */
	@SuppressWarnings("unchecked")
	private Map<JComponent, ValidationResponder> m_specialValidationResponder;
	
	
	/**
	 * The hidden constructor.
	 */
	@SuppressWarnings("unchecked")
	protected BindingFactory() {
		m_specialBinding = new HashMap<JComponent, BindingCreator>();
		m_specialValidationResponder
			= new HashMap<JComponent, ValidationResponder>();
	}
	
	/**
	 * @return    an instance of this class
	 */
	public static BindingFactory getInstance() {
		if (s_bindingFactory == null) {
			s_bindingFactory = new BindingFactory();
		}
		return s_bindingFactory;
	}
	
	/**
	 * @return      the default properties
	 */
	public static DefaultProperties getDefaultProperties() {
		return DEFAULT_PROPERTIES;
	}
	
	/**
	 * Registers a custom binding strategy.
	 *
	 * @param component     the component to bind
	 * @param binding       the custom binding
	 */
	@SuppressWarnings("unchecked")
	public void register(JComponent component, BindingCreator binding) {
		m_specialBinding.put(component, binding);
	}
	
	/**
	 * Registers a custom validation responder.
	 *
	 * @param component     the bound component
	 * @param responder     the custom validation responder
	 */
	public void registerValidationResponder(JComponent component,
		ValidationResponder responder) {
		m_specialValidationResponder.put(component, responder);
	}
	
	/**
	 * @param strategy          the binding update strategy
	 * @param modelObject       the instance of the model
	 * @param modelProperty     the property of the model to bound
	 * @param formComponent     the form component to bound to
	 * @return                  a AutoBinding object representing the binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding createBinding(UpdateStrategy strategy,
		Object modelObject, String modelProperty, JComponent formComponent) {
		
		Property modelPropertyName = PropertyUtil.create(modelProperty);
		
		AutoBinding binding = null;
		// is a special binding registered?
		if (m_specialBinding.containsKey(formComponent)) {
			binding = m_specialBinding.get(formComponent).createBinding(
				modelPropertyName.getValue(modelObject), formComponent);
		} else {
			// create a default binding
			Property fromPropertyName = DEFAULT_PROPERTIES.getDefaultProperty(
				formComponent);
			if (fromPropertyName != null) {
				binding = Bindings.createAutoBinding(strategy,
					modelObject, modelPropertyName,
					formComponent, fromPropertyName);
			}
		}
		return binding;
	}
	
	/**
	 * @param formComponent    the GUI component
	 * @return                 a {@link BindingListener} that performs the
	 *                         validation
	 */
	@SuppressWarnings("unchecked")
	public BindingListener createValidationListener(JComponent formComponent) {
		ValidationResponder responder;
		if (m_specialValidationResponder.containsKey(formComponent)) {
			responder = m_specialValidationResponder.get(formComponent);
		} else {
			GenericConfig config = GUIApplication.getInstance().getConfig();
			responder = (ValidationResponder) config.get(
				"validationResponder");
		}
		
		if (m_specialBinding.containsKey(formComponent)) {
			m_specialBinding.get(formComponent).addValidation(formComponent);
		}
		return new ValidatingBindingListener(responder);
	}
}
