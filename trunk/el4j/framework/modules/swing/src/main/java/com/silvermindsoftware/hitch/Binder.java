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
package com.silvermindsoftware.hitch;

import java.awt.Container;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

import com.silvermindsoftware.hitch.binding.BindingCreator;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

/**
 * Interface for convenience beans binding support between model and GUI.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public interface Binder {
	/**
	 * Add all bindings that can be derived from the binding annotations. The name of the annotated fields must match
	 * a property of the model (field prefix "m_" is removed if applicable).
	 *
	 * @param container    the GUI component container to bind to
	 * @param modelId      the optional model identifiers
	 *                     (to select models to bind)
	 * @return             the added binding group
	 */
	public BindingGroup addAutoBinding(Container container, String... modelId);
	
	/**
	 * @param container    the GUI component container to bind to
	 * @param performValidate    validate user input
	 * @param modelId      the optional model identifiers
	 *                     (to select models to bind)
	 * @return             the added binding group
	 */
	public BindingGroup addAutoBinding(Container container,
		boolean performValidate, String... modelId);
	
	/**
	 * Bind a component to a model using a specific binding creator.
	 *
	 * @param container          the GUI component container to bind to
	 * @param component          the form component to bind to
	 * @param creator            the specific binding creator
	 * @param performValidate    determines if a validation should be performed
	 * @return                   the created binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding addAutoBinding(Container container,
		JComponent component, BindingCreator creator, boolean performValidate);
	
	/**
	 * Bind a component to a model using the standard binding creator.
	 *
	 * @param model              the model to bind
	 * @param property           the property of the model to bind
	 * @param component          the form component to bind
	 * @param performValidate    determines if a validation should be performed
	 * @return                   the created binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding addManualBinding(UpdateStrategy strategy, Object model,
		String property, JComponent component, boolean performValidate);
	
	/**
	 * Bind a component to a model using a specific binding creator.
	 *
	 * @param model              the model to bind
	 * @param property           the property of the model to bind
	 * @param component          the form component to bind to
	 * @param creator            the specific binding creator
	 * @param performValidate    determines if a validation should be performed
	 * @return                   the created binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding addManualBinding(Object model,  String property,
		JComponent component, BindingCreator creator, boolean performValidate);
	
	/**
	 * Bind a component to a model completely manually.
	 *
	 * @param binding    the binding to add
	 * @return           the added binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding addManualBinding(AutoBinding binding);
	
	/**
	 * Bind a component to a model completely manually.
	 *
	 * @param binding            the binding to add
	 * @param performValidate    determines if a validation should be performed
	 * @return                   the added binding
	 */
	@SuppressWarnings("unchecked")
	public AutoBinding addManualBinding(AutoBinding binding,
		boolean performValidate);
	
	/**
	 * Remove a binding group.
	 *
	 * @param group    the binding group to remove
	 */
	public void remove(BindingGroup group);
	
	/**
	 * Remove a binding.
	 *
	 * @param binding    the binding to remove
	 */
	@SuppressWarnings("unchecked")
	public void remove(AutoBinding binding);
	
	
	/**
	 * Remove all bindings.
	 */
	public void removeAll();

	/**
	 * Find a specific binding. Parameters being <code>null</code>
	 * mean "don't care".
	 *
	 * @param model        the model to bind
	 * @param property     the property of the model to bind to
	 * @param component    the form component to bind to
	 * @return             the found bindings or <code>null</code> if none
	 */
	public BindingGroup find(Object model, String property,
		JComponent component);

	/**
	 * Add a custom validation responder to a binding.
	 *
	 * @param binding      the binding
	 * @param responder    the validation responder
	 */
	@SuppressWarnings("unchecked")
	public void addValidationResponder(AutoBinding binding,
		ValidationResponder responder);
	
	/**
	 * Add a custom validation responder to a binding group.
	 *
	 * @param group        the binding group
	 * @param responder    the validation responder
	 */
	public void addValidationResponder(BindingGroup group,
		ValidationResponder responder);
	
	
	/**
	 * Bind all managed bindings.
	 */
	public void bindAll();
	
	/**
	 * Unbind all managed bindings.
	 */
	public void unbindAll();
}
