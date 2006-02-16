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
package ch.elca.el4j.services.gui.richclient.forms.binding.swing;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.richclient.form.binding.support.AbstractBinding;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binding for the given three state boolean element group.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ThreeStateBooleanBinding extends AbstractBinding {
    /**
     * Is the JComponent to bind with the form model.
     */
    private final ThreeStateBooleanJPanel m_control;

    /**
     * Constructor.
     * 
     * @param control Is the control to bind.
     * @param formModel Is the model to bind.
     * @param formPropertyPath Is the property path inside the form.
     */
    public ThreeStateBooleanBinding(ThreeStateBooleanJPanel control, 
        FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, Boolean.class);
        m_control = control;
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent doBindControl() {
        m_control.setValueModel(getValueModel());
        FormPropertyFaceDescriptor trueButtonDescriptor 
            = getFormPropertyFaceDescriptor(
                ThreeStateBooleanJPanel.TRUE_BUTTON_PROPERTY_NAME);
        FormPropertyFaceDescriptor falseButtonDescriptor 
            = getFormPropertyFaceDescriptor(
                ThreeStateBooleanJPanel.FALSE_BUTTON_PROPERTY_NAME);
        FormPropertyFaceDescriptor unknownButtonDescriptor 
            = getFormPropertyFaceDescriptor(
                ThreeStateBooleanJPanel.UNKNOWN_BUTTON_PROPERTY_NAME);
        
        m_control.getTrueButton().setText(
            trueButtonDescriptor.getDisplayName());
        m_control.getTrueButton().setToolTipText(
            trueButtonDescriptor.getDescription());
        m_control.getFalseButton().setText(
            falseButtonDescriptor.getDisplayName());
        m_control.getFalseButton().setToolTipText(
            falseButtonDescriptor.getDescription());
        m_control.getUnknownButton().setText(
            unknownButtonDescriptor.getDisplayName());
        m_control.getUnknownButton().setToolTipText(
            unknownButtonDescriptor.getDescription());
        return m_control;
    }
    
    /**
     * @param buttonProperty
     *            Is the button property we'd like to know the form property
     *            face descriptor.
     * @return Return theform porpoerty face descriptor for the given button.
     */
    protected FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(
        String buttonProperty) {
        Reject.ifEmpty(buttonProperty);
        String propertyPath = formPropertyPath + "." + buttonProperty;
        FormPropertyFaceDescriptor faceDescriptor 
            = getFormModel().getFormPropertyFaceDescriptor(propertyPath);
        return faceDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    protected void readOnlyChanged() {
        m_control.setEnabled(isEnabled() && !isReadOnly());
    }

    /**
     * {@inheritDoc}
     */
    protected void enabledChanged() {
        m_control.setEnabled(isEnabled() && !isReadOnly());
    }
}
