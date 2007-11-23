/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package com.silvermindsoftware.hitch.validation.response;

import javax.swing.JComponent;

/**
 * Interface for handlers that determine how to react on valid/invalid values.
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
public interface ValidationResponder {
    /**
     * The value in the component is valid.
     * 
     * @param object       the validated object
     * @param component    the component holding the validated value
     */
    public void setValid(Object object, JComponent component);
    
    /**
     * The value in the component is valid.
     * 
     * @param object       the validated object
     * @param component    the component holding the validated value
     * @param valid        <code>true</code> if values is valid
     */
    public void setValid(Object object, JComponent component, boolean valid);
    
    /**
     * The value in the component is invalid.
     * 
     * @param object       the validated object
     * @param component    the component holding the invalidated value
     * @param message      the message explaining why the value is invalid
     */
    public void setInvalid(Object object, JComponent component, String message);
}
