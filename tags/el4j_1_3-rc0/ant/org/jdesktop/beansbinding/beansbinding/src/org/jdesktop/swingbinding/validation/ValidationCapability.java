package org.jdesktop.swingbinding.validation;

import org.jdesktop.beansbinding.Property;

/**
 * The interface to make a model validateable.
 * 
 * @author SWI
 */
public interface ValidationCapability {
    /**
     * @return      <code>true</code> if all properties have valid values
     */
    public boolean isValid();
    
    /**
     * @return      <code>true</code> if property has a valid value
     */
    public boolean isValid(String property);
}
