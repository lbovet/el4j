package ch.elca.el4j.gui.model.mixin;

/**
 * The interface to make a java bean save and restore its properties.
 * 
 * @author SWI
 */
public interface SaveRestoreCapability {
    /**
     * Save all the writable java bean properties.
     */
    public void save();
    
    /**
     * Restore all the writable java bean properties.
     */
    public void restore();
}
