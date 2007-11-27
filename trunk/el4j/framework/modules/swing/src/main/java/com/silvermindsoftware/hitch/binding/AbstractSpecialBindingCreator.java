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

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * This abstract class just provides an update strategy field to its subclasses.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @param <T>    the type of form component it belongs to
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractSpecialBindingCreator<T extends JComponent>
    implements SpecialBindingCreator<T> {
    
    /**
     * The update strategy (r/r once, rw).
     */
    protected UpdateStrategy m_updateStrategy = UpdateStrategy.READ_WRITE;

    /**
     * @return    the update strategy
     */
    public UpdateStrategy getUpdateStrategy() {
        return m_updateStrategy;
    }

    /**
     * @param updateStrategy    the update strategy to set
     */
    public void setUpdateStrategy(UpdateStrategy updateStrategy) {
        m_updateStrategy = updateStrategy;
    }
}
