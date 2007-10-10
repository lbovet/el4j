package com.silvermindsoftware.hitch.validation;

import java.awt.Container;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.hibernate.validator.ClassValidator;

import com.silvermindsoftware.hitch.validation.response.StandardValidationResponder;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

public class ValidatorFactory {
    private Map<Class, ClassValidator> validators;
    private Map<Container, ValidationResponder> listeners;
    
    public ValidatorFactory() {
        validators = new HashMap<Class, ClassValidator>();
        listeners = new HashMap<Container, ValidationResponder>();
    }
    
    /**
     * Returns a propertyChangeListener to validate the model.
     * 
     * @param modelObject           the instance of the model
     * @param container             the Swing container holding the bound component
     * @param propertyToComponent   the map between properties and components
     * @return                      a propertyChangeListener to validate the model
     */
    public PropertyChangeListener getPropertyChangeListener(Object modelObject, Container container, Map<String, JComponent> propertyToComponent) {
        ClassValidator validator = validators.get(modelObject.getClass());
        if (validator == null) {
            try {
                validator = ((ValidationCapability) modelObject).getClassValidator();
                validators.put(modelObject.getClass(), validator);
            } catch (Exception e) {
                //log.warn(modelObject.getClass().toString() + " has no ValidationCapability.", e);
                return null;
            }
        }
        
        ValidationResponder listener = listeners.get(container);
        if (listener == null) { 
            try {
                listener = (ValidationResponder)container;
            } catch (Exception e) {
                //log.info(container.getClass().toString() + " is no ValidationListener.", e);
                listener = new StandardValidationResponder();
                listeners.put(container, listener);
            }
        }
        
        return new ValidatorListener(validator, listener, propertyToComponent);
    }
}
