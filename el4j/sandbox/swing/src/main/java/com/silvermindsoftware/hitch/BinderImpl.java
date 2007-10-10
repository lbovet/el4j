package com.silvermindsoftware.hitch;

import java.awt.Container;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.Binding.SyncFailure;

import com.silvermindsoftware.hitch.binding.BindingFactory;
import com.silvermindsoftware.hitch.binding.SpecialBinding;
import com.silvermindsoftware.hitch.meta.ComponentMeta;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.meta.ModelMeta;
import com.silvermindsoftware.hitch.validation.ValidatorFactory;

/**
 * The implementation of the binder
 * 
 * @author SWI, based on Brandon Goodin's Hitch
 */
public class BinderImpl extends AbstractBinder implements Binder {

    private static final BindingFactory bindingFactory = new BindingFactory();

    protected ValidatorFactory validatorFactory;
    protected Map<String, Map<String, JComponent>> propertyToComponentCache;
    
    protected Map<String, JComponent> propertyToComponent;
    
    public BinderImpl() {
        propertyToComponent = new HashMap<String, JComponent>();
        validatorFactory = new ValidatorFactory();
        propertyToComponentCache = new HashMap<String, Map<String,JComponent>>();
    }
    
    /** {@inheritDoc} */
    public BindingGroup getAutoBinding(Container container, String... modelId) {
        return getAutoBinding(container, true, modelId);
    }
    
    /** {@inheritDoc} */
    public BindingGroup getAutoBinding(Container container, boolean performValidate, String... modelId) {
        FormMeta formMeta = getFormMeta(container.getClass());
        
        BindingGroup bindings = new BindingGroup();

        for (Iterator<ComponentMeta> it = formMeta.getComponentMetaIterator(); it
                .hasNext();) {
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
            
            
            AutoBinding b = bindingFactory.getBinding(modelObject, componentMeta.getModelPropertyName(), formComponent);
            if (performValidate) {
                // bug in Beans Binding: not all events are generated (see issue 4)
                b.addBindingListener(new BindingListener() {
                    public void bindingBecameBound(Binding binding) {System.out.println("bound");}
                    public void bindingBecameUnbound(Binding binding) {System.out.println("unbound");}
                    public void sourceEdited(Binding binding) {System.out.println("src");};
                    public void syncFailed(Binding binding, SyncFailure... failures) {System.out.println("failed");}
                    public void targetEdited(Binding binding) {System.out.println("target");};
                    public void synced(Binding binding) {System.out.println("Validate");}
                });
                
                // this doesn't do what is expected
                PropertyChangeListener p = validatorFactory.getPropertyChangeListener(
                        modelObject, container,
                        getPropertyToComponentMap(container, componentMeta.getModelId()));
                b.addPropertyChangeListener(p);
            }
            
            bindings.addBinding(b);
       }
        return bindings;
    }
    
    protected Map<String, JComponent> getPropertyToComponentMap(Container container, String modelId) {
        final String propertyId = container.hashCode() + "." + modelId;
        
        Map<String, JComponent> propertyToComponent;
        propertyToComponent = propertyToComponentCache.get(propertyId);
        
        if (propertyToComponent == null) {
            propertyToComponent = new HashMap<String, JComponent>();
            
            FormMeta formMeta = getFormMeta(container.getClass());
            for (Iterator<ComponentMeta> it = formMeta
                    .getComponentMetaIterator(); it.hasNext();) {
                ComponentMeta componentMeta = it.next();
    
                // get container field
                Field componentField = componentMeta.getComponentField();
    
                // get form container
                JComponent formComponent = null;
                try {
                    formComponent = (JComponent) componentField.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("IllegalAccessException: "
                            + e.getMessage()
                            + "occured while retrieving component field "
                            + componentField.getName() + " of type "
                            + componentField.getType(), e);
                }
                
                propertyToComponent.put(componentMeta.getModelPropertyName(), formComponent);
            }
            propertyToComponentCache.put(propertyId, propertyToComponent);
        }
        return propertyToComponent;
    }
    
    /** {@inheritDoc} */
    public void registerBinding(JComponent component, SpecialBinding binding) {
        bindingFactory.register(component, binding);
    }
    
    protected boolean skipModel(String modelId, String[] modelIdList) {
     // check to see if update should occur for particular model objects
        if (modelIdList != null && modelIdList.length > 0) {

            Arrays.sort(modelIdList);

            if (Arrays.binarySearch(
                    modelIdList,
                    modelId) < 0) {
                return true;
            }
        }
        return false;
    }
}
