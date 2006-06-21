/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.richclient.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Utility class for <code>java.awt.Component</code>s.
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
public class ComponentUtils {
    /**
     * Hide default constructor.
     */
    protected ComponentUtils() { }
    
    /**
     * Adds the given mouse listener recusivly to each component.
     * 
     * @param component Is the component where to start recursion.
     * @param mouseListener Is the mouse listener to add.
     */
    public static void addMouseListenerRecursivly(Component component, 
        MouseListener mouseListener) {
        Reject.ifNull(component);
        Reject.ifNull(mouseListener);
        if (component instanceof Container) {
            Container container = (Container) component;
            Component[] components = container.getComponents();
            if (components != null) {
                for (int i = 0; i < components.length; i++) {
                    Component childComponent = components[i];
                    if (childComponent != component) {
                        addMouseListenerRecursivly(childComponent,
                            mouseListener);
                    }
                }
            }
        }
        component.addMouseListener(mouseListener);
    }
    
    /**
     * Adds the given focus listener recusivly to each component.
     * 
     * @param component Is the component where to start recursion.
     * @param focusListener Is the focus listener to add.
     */
    public static void addFocusListenerRecursivly(Component component, 
        FocusListener focusListener) {
        Reject.ifNull(component);
        Reject.ifNull(focusListener);
        if (component instanceof Container) {
            Container container = (Container) component;
            Component[] components = container.getComponents();
            if (components != null) {
                for (int i = 0; i < components.length; i++) {
                    Component childComponent = components[i];
                    if (childComponent != component) {
                        addFocusListenerRecursivly(childComponent,
                            focusListener);
                    }
                }
            }
        }
        component.addFocusListener(focusListener);
    }
}
