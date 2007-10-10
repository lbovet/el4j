package com.silvermindsoftware.hitch.validation;

import org.hibernate.validator.ClassValidator;

/**
 * The interface to make a model validateable.
 * 
 * @author SWI
 */
public interface ValidationCapability {
    /**
     * @return      the hibernate classValidator
     */
    public ClassValidator getClassValidator();
}
