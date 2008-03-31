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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;


import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Property;

import com.silvermindsoftware.hitch.binding.BindingCreator;
import com.silvermindsoftware.hitch.binding.BindingFactory;
import com.silvermindsoftware.hitch.meta.ComponentMeta;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.meta.ModelMeta;
import com.silvermindsoftware.hitch.validation.ValidatingBindingListener;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.util.config.GenericConfig;

/**
 * This class is the default implementation of the binder.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI), based on Hitch by Brandon Goodin
 */
public class BinderImpl implements Binder {
    
    /**
     * The managed bindings.
     */
    protected Set<BindingGroup> m_bindings = new HashSet<BindingGroup>();

    /**
     * Use {@link BinderManager}.
     */
    BinderImpl() { }
    
    /** {@inheritDoc} */
    public BindingGroup addAutoBinding(Container container, String... modelId) {
        return addAutoBinding(container, true, modelId);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public BindingGroup addAutoBinding(Container container,
        boolean performValidate, String... modelId) {
        
        FormMeta formMeta = BinderManager.getFormMetaData(container.getClass());
        
        BindingGroup bindings = new BindingGroup();

        for (Iterator<ComponentMeta> it = formMeta.getComponentMetaIterator();
            it.hasNext();) {
            
            ComponentMeta componentMeta = it.next();

            // check to see if update should occur for particular model objects
            if (skipModel(componentMeta.getModelId(), modelId)) {
                break;
            }

            // get container field
            Field componentField = componentMeta.getComponentField();

            // get model values
            ModelMeta modelMeta = formMeta.getModelMeta(componentMeta
                    .getModelId());
            Field modelField = modelMeta.getModelField();
            Object modelObject = null;
            try {
                modelObject = modelField.get(container);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            JComponent formComponent = null;

            try {
                // check if formComponent will be of type JComponent
                Object tmpComponent = componentField.get(container);
                if (!JComponent.class.isAssignableFrom(
                    tmpComponent.getClass())) {
                    break;
                }
                formComponent = (JComponent) tmpComponent;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            if (formComponent == null) {
                throw new IllegalStateException("Form container named "
                        + componentField.getName() + " was not found");
            }
            
            AutoBinding binding = addManualBinding(UpdateStrategy.READ_WRITE,
                modelObject, componentMeta.getModelPropertyName(),
                formComponent, performValidate);
            
            // collect inserted bindings
            if (binding != null) {
                bindings.addBinding(binding);
            }
        }
        return bindings;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding addAutoBinding(Container container,
        JComponent component, BindingCreator creator, boolean performValidate) {
        
        FormMeta formMeta = BinderManager.getFormMetaData(container.getClass());

        for (Iterator<ComponentMeta> it = formMeta.getComponentMetaIterator();
            it.hasNext();) {
            
            ComponentMeta componentMeta = it.next();
            try {
                if (componentMeta.getComponentField().get(container)
                    .equals(component)) {
                    
                    // get model values
                    ModelMeta modelMeta = formMeta.getModelMeta(componentMeta
                            .getModelId());
                    Field modelField = modelMeta.getModelField();
                    Object modelObject = null;
                    try {
                        modelObject = modelField.get(container);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    
                    return addManualBinding(modelObject, 
                        componentMeta.getModelPropertyName(),
                        component, creator, performValidate);
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding addManualBinding(UpdateStrategy strategy, Object model,
        String property, JComponent component, boolean performValidate) {
        
        // create a default binding
        AutoBinding b = BindingFactory.getInstance()
            .createBinding(strategy, model, property, component);
        
        if (b != null) {
            return addManualBinding(b, performValidate);
        } else {
            return null;
        } 
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding addManualBinding(Object model, String property,
        JComponent component, BindingCreator creator, boolean performValidate) {
        
        Property modelPropertyName = BeanProperty.create(property);
        
        AutoBinding b = creator.createBinding(
            modelPropertyName.getValue(model), component);
        addManualBinding(b);
        
        if (performValidate) {
            creator.addValidation(component);
        }
        
        return b;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding addManualBinding(AutoBinding binding,
        boolean performValidate) {
        
        addManualBinding(binding);
        
        if (performValidate) {
            GenericConfig config = GUIApplication.getInstance().getConfig();
            addValidationResponder(binding, (ValidationResponder) 
                config.get("validationResponder"));
        }
        return binding;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding addManualBinding(AutoBinding binding) {
        BindingGroup g = new BindingGroup();
        g.addBinding(binding);
        m_bindings.add(g);
        
        return binding;
    }
    
    /** {@inheritDoc} */
    public void addValidationResponder(BindingGroup group,
        ValidationResponder responder) {
        
        group.addBindingListener(new ValidatingBindingListener(responder));
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void addValidationResponder(AutoBinding binding,
        ValidationResponder responder) {
        
        binding.addBindingListener(new ValidatingBindingListener(responder));
    }

    /** {@inheritDoc} */
    public void bindAll() {
        for (BindingGroup group : m_bindings) {
            group.bind();
        }
    }
    
    /** {@inheritDoc} */
    public void unbindAll() {
        for (BindingGroup group : m_bindings) {
            group.unbind();
        }
    }

    /** {@inheritDoc} */
    public void removeAll() {
        unbindAll();
        m_bindings.clear();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public BindingGroup find(Object model, String property,
        JComponent component) {
        
        BindingGroup result = new BindingGroup();
        
        Property modelPropertyName = null;
        if (property != null) {
            modelPropertyName = BeanProperty.create(property);
        }
        for (BindingGroup group : m_bindings) {
            for (Binding binding : group.getBindings()) {
                if ((model != null && model == binding.getSourceObject())
                    || (modelPropertyName != null
                        && binding.getSourceProperty().equals(modelPropertyName))
                    || (component != null
                        && binding.getTargetObject() == component)) {
                    result.addBinding(binding);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public void remove(BindingGroup binding) {
        m_bindings.remove(binding);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void remove(AutoBinding binding) {
        for (BindingGroup group : m_bindings) {
            for (Binding bindingInGroup : group.getBindings()) {
                if (bindingInGroup == binding) {
                    group.removeBinding(binding);
                    return;
                }
            }
        }
    }

    
    /**
     * Check if update should occur for particular model objects.
     * 
     * @param modelId        the model id given by the annotation
     * @param modelIdList    list of available models
     * @return               true if model should be skipped
     */
    protected boolean skipModel(String modelId, String[] modelIdList) {
        if (modelIdList != null && modelIdList.length > 0) {
            Arrays.sort(modelIdList);

            if (Arrays.binarySearch(modelIdList, modelId) < 0) {
                return true;
            }
        }
        return false;
    }

}
