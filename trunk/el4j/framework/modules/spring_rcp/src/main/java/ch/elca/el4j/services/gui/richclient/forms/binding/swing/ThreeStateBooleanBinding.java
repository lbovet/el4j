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
package ch.elca.el4j.services.gui.richclient.forms.binding.swing;

import javax.swing.JComponent;

import org.springframework.binding.form.FieldFace;
import org.springframework.binding.form.FormModel;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binding for the given three state boolean element group.
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
public class ThreeStateBooleanBinding extends AbstractSwingBinding {
    /**
     * Constructor.
     * 
     * @param control Is the control to bind.
     * @param formModel Is the model to bind.
     * @param formPropertyPath Is the property path inside the form.
     */
    public ThreeStateBooleanBinding(ThreeStateBooleanJPanel control, 
        FormModel formModel, String formPropertyPath) {
        super(control, formModel, formPropertyPath, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent doBindControl() {
        ThreeStateBooleanJPanel control 
            = (ThreeStateBooleanJPanel) getJComponent();
        
        control.setValueModel(getValueModel());
        FieldFace trueButtonDescriptor 
            = getFieldFace(
                ThreeStateBooleanJPanel.TRUE_BUTTON_PROPERTY_NAME);
        FieldFace falseButtonDescriptor 
            = getFieldFace(
                ThreeStateBooleanJPanel.FALSE_BUTTON_PROPERTY_NAME);
        FieldFace unknownButtonDescriptor 
            = getFieldFace(
                ThreeStateBooleanJPanel.UNKNOWN_BUTTON_PROPERTY_NAME);
        
        control.getTrueButton().setText(
            trueButtonDescriptor.getDisplayName());
        control.getTrueButton().setToolTipText(
            trueButtonDescriptor.getDescription());
        control.getFalseButton().setText(
            falseButtonDescriptor.getDisplayName());
        control.getFalseButton().setToolTipText(
            falseButtonDescriptor.getDescription());
        control.getUnknownButton().setText(
            unknownButtonDescriptor.getDisplayName());
        control.getUnknownButton().setToolTipText(
            unknownButtonDescriptor.getDescription());
        return control;
    }
    
    /**
     * @param buttonProperty
     *            Is the button property we'd like to know face for.
     * @return Return the face for the given button.
     */
    protected FieldFace getFieldFace(
        String buttonProperty) {
        Reject.ifEmpty(buttonProperty);
        String propertyPath = formPropertyPath + "." + buttonProperty;
        FieldFace faceDescriptor 
            = getFormModel().getFieldFace(propertyPath);
        return faceDescriptor;
    }
}
