/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors;

import org.springframework.richclient.application.PageComponentDescriptor;

/**
 * Extends the page component descriptor with methods to inform that property
 * changed.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface ExtendedPageComponentDescriptor extends
    PageComponentDescriptor {
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, 
        boolean newValue);
    
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    public void firePropertyChange(String propertyName, int oldValue, 
        int newValue);
    
    /**
     * Fires a property change event.
     * 
     * @param propertyName Is the name of the changed property.
     * @param oldValue Is the old value of given property.
     * @param newValue Is the new value of given property.
     */
    public void firePropertyChange(String propertyName, Object oldValue, 
        Object newValue);
}
