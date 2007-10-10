package com.silvermindsoftware.hitch.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jdesktop.beansbinding.AutoBinding;

import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

public class ValidatorListener implements PropertyChangeListener {
    private ClassValidator validator;
    private ValidationResponder listener;
    private Map<String, JComponent> propertyToComponent;

    public ValidatorListener(ClassValidator validator,
            ValidationResponder listener,
            Map<String, JComponent> propertyToComponent) {
        this.validator = validator;
        this.listener = listener;
        this.propertyToComponent = propertyToComponent;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (validator == null || listener == null) {
            return;
        }
        if (evt.getSource() instanceof AutoBinding) {
            AutoBinding bindingObject = (AutoBinding) evt.getSource();

            Set<String> validProperties = new HashSet<String>(
                    propertyToComponent.keySet());

            InvalidValue[] validationMessages = validator
                    .getInvalidValues(bindingObject.getSourceObject());

            if (validationMessages.length > 0) {
                for (int i = 0; i < validationMessages.length; i++) {
                    JComponent formComponent = propertyToComponent
                            .get(validationMessages[i].getPropertyName());

                    listener.setInvalid(formComponent, validationMessages[i]
                            .getMessage());
                    validProperties.remove(validationMessages[i]
                            .getPropertyName());
                }
            }
            for (String validProperty : validProperties) {
                listener.setValid(propertyToComponent.get(validProperty));
            }
        }
    }
}
