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
package ch.elca.el4j.services.richclient.binders;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.springframework.binding.form.FormModel;

import ch.elca.el4j.services.gui.richclient.forms.binding.swing.AbstractSwingBinding;
import ch.elca.el4j.services.gui.richclient.forms.binding.swing.ThreeStateBooleanJPanel;
import ch.elca.el4j.services.richclient.naming.Naming;

public class ThreeStateBooleanBinding extends AbstractSwingBinding {
    /**
     * Constructor. See 
     * {@link AbstractSwingBinding#AbstractSwingBinding(JComponent, FormModel,
     * String, Class)}.
     * @param control see super
     * @param formModel see super
     * @param formPropertyPath see super
     */
    public ThreeStateBooleanBinding(ThreeStateBooleanJPanel control, 
                                    FormModel formModel,
                                    String formPropertyPath) {
        super(control, formModel, formPropertyPath, Boolean.class);
    }

    /** {@inheritDoc} */
    @Override
    protected JComponent doBindControl() {
        ThreeStateBooleanJPanel control 
            = (ThreeStateBooleanJPanel) getJComponent();

        control.setValueModel(getValueModel());
        
        configure(control.getTrueButton(),    "true");
        configure(control.getFalseButton(),   "false");
        configure(control.getUnknownButton(), "any");
        return control;
    }
    
    /**
     * Configures a value button.
     * @param button the button to configure
     * @param representedValue the value represented by the button
     */
    protected void configure(JToggleButton button, String representedValue) {
        button.setText(
            Naming.instance().getConstantValueFaceProperty(
                "boolean",
                representedValue,
                "displayName"
            )
        );
        button.setToolTipText(
            Naming.instance().getConstantValueFaceProperty(
                "boolean", 
                representedValue, 
                "description"
            )
        );
    }
}
