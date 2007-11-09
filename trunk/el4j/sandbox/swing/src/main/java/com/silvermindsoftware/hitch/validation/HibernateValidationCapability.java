package com.silvermindsoftware.hitch.validation;

import org.hibernate.validator.ClassValidator;

/**
 * The interface to make a model validateable by hibernate.
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
public interface HibernateValidationCapability {
    /**
     * @return      the hibernate classValidator
     */
    public ClassValidator<?> getClassValidator();
}
