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
import java.util.Iterator;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;

import com.silvermindsoftware.hitch.binding.BindingFactory;
import com.silvermindsoftware.hitch.binding.SpecialBindingCreator;
import com.silvermindsoftware.hitch.meta.ComponentMeta;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.meta.ModelMeta;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

/**
 * The implementation of the binder.
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
public class BinderImpl extends AbstractBinder implements Binder {
    /**
     * The factory for bindings.
     */
    private static final BindingFactory BINDING_FACTORY
        = BindingFactory.getInstance();
    
    /**
     * Use {@link BinderManager#getBinder(java.awt.Component)} instead.
     */
    BinderImpl() { }
    
    /** {@inheritDoc} */
    public BindingGroup getAutoBinding(Container container, String... modelId) {
        return getAutoBinding(container, true, modelId);
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public BindingGroup getAutoBinding(Container container,
        boolean performValidate, String... modelId) {
        
        FormMeta formMeta = getFormMeta(container.getClass());
        
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
                formComponent = (JComponent) componentField.get(container);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            if (formComponent == null) {
                throw new IllegalStateException("Form container named "
                        + componentField.getName() + " was not found");
            }
            
            
            AutoBinding b = BINDING_FACTORY.getBinding(modelObject,
                componentMeta.getModelPropertyName(), formComponent);
            if (performValidate) {
                b.addBindingListener(BINDING_FACTORY.getValidationListener(
                    formComponent));
            }
            
            bindings.addBinding(b);
        }
        return bindings;
    }
    
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding getSpecialBinding(Object model, JComponent component,
        SpecialBindingCreator creator, boolean performValidate) {
        
        AutoBinding b = creator.createBinding(model, component);
        if (performValidate) {
            creator.addValidation(component);
            b.addBindingListener(BINDING_FACTORY.getValidationListener(
                component));
        }
        return b;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void registerBinding(JComponent component,
        SpecialBindingCreator binding) {
        
        BINDING_FACTORY.register(component, binding);
    }
    
    /** {@inheritDoc} */
    public void registerValidationResponder(JComponent component,
        ValidationResponder responder) {
        
        BINDING_FACTORY.registerValidationResponder(component, responder);
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void registerValidationResponder(BindingGroup group,
        ValidationResponder responder) {
        for (Binding binding : group.getBindings()) {
            JComponent component = (JComponent) binding.getTargetObject();
            BINDING_FACTORY.registerValidationResponder(component, responder);
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
