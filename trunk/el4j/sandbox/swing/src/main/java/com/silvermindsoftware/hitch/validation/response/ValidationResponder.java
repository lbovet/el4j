package com.silvermindsoftware.hitch.validation.response;

import javax.swing.JComponent;

/**
 * Interface for handlers that determine how to react on valid/invalid values.
 * 
 * @author SWI
 */
public interface ValidationResponder {
    /**
     * The value in the component is valid.
     * 
     * @param component    the component holding the validated value
     */
    public void setValid(JComponent component);
    
    /**
     * The value in the component is invalid.
     * 
     * @param component    the component holding the invalidated value
     * @param message      the message explaining why the value is invalid
     */
    public void setInvalid(JComponent component, String message);
}
