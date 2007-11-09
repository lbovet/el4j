package ch.elca.el4j.model.mixin;

/**
 * The interface to make a java bean save and restore its properties.
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
