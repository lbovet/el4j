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
package ch.elca.el4j.services.gui.richclient.forms.binding;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;

import ch.elca.el4j.services.gui.richclient.forms.binding.swing.ThreeStateBooleanBinding;
import ch.elca.el4j.services.gui.richclient.forms.binding.swing.ThreeStateBooleanJPanel;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binder for a three state boolean component.
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
public class ThreeStateBooleanBinder extends AbstractBinder {
    /**
     * Default contructor.
     */
    protected ThreeStateBooleanBinder() {
        super(null);
    }

    /**
     * {@inheritDoc}
     * 
     * Creates a control for this binder type.
     */
    protected JComponent createControl(Map context) {
        ThreeStateBooleanJPanel control = new ThreeStateBooleanJPanel(
            createStateButton(), createStateButton(), createStateButton());
        control.setLayout(createLayoutManager());
        return control;
    }
    
    /**
     * @return Returns the create toggle button.
     */
    protected JToggleButton createStateButton() {
        return new JRadioButton();
    }
    
    /**
     * @return Returns the created layout manager.
     */
    protected LayoutManager createLayoutManager() {
        // Checkstyle: MagicNumber off
        return new FlowLayout(FlowLayout.LEFT, 5, 5);
        // Checkstyle: MagicNumber on
    }

    /**
     * {@inheritDoc}
     * 
     * Binds the given <code>ThreeStateBooleanJPanel</code> with the given 
     * form model.
     */
    protected Binding doBind(JComponent control, FormModel formModel,
        String formPropertyPath, Map context) {
        Reject.ifFalse(control instanceof ThreeStateBooleanJPanel, 
            "Given control must of type " 
            + ThreeStateBooleanJPanel.class.getName() + ".");
        return new ThreeStateBooleanBinding((ThreeStateBooleanJPanel) control, 
            formModel, formPropertyPath);
    }
}
