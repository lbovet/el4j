package com.silvermindsoftware.hitch.events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Interface for property change listener support
 * 
 * @author SWI
 */
public interface PropertyChangeListenerCapability {
    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
    
    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public void addPropertyChangeListener(String key, PropertyChangeListener l);
    
    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public void removePropertyChangeListener(String key, PropertyChangeListener l);
}
