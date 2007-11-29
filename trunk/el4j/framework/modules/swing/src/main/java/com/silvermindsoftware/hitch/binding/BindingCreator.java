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
package com.silvermindsoftware.hitch.binding;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;

/**
 * A user defined "binding template" for a specific widget.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @param <T> the type of widget to bind
 *
 * @author Stefan Wismer (SWI)
 */
public interface BindingCreator<T extends JComponent> {
    /**
     * Create the concrete binding.
     * 
     * @param object            the object to bind
     * @param formComponent     the widget to bound to
     * @return                  the corresponding binding
     */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object object, T formComponent);
    
    /**
     * Add validation capability.
     * 
     * @param formComponent     the widget showing the values
     */
    public void addValidation(T formComponent);
}
