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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.util.StringUtils;

import com.michaelbaranov.microba.calendar.DatePicker;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Binding for <code>java.util.Date</code> and date picker from
 * <a href="http://microba.sf.net">http://microba.sf.net</a>.
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
public class DatePickerBinding extends CustomBinding {
    /**
     * Is the control of this binding.
     */
    protected final DatePicker m_datePicker;

    /**
     * Constructor.
     * 
     * @param datePicker Is the control to bind.
     * @param formModel Is the form model to bind
     * @param formPropertyPath Is the path of form property.
     */
    public DatePickerBinding(DatePicker datePicker, FormModel formModel, 
        String formPropertyPath) {
        super(formModel, formPropertyPath, Date.class);
        m_datePicker = datePicker;
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent doBindControl() {
        try {
            m_datePicker.setDate((Date) getValue());
        } catch (PropertyVetoException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Veto not allowed!", e);
        }
        m_datePicker.addPropertyChangeListener(DatePicker.PROPERTY_NAME_DATE, 
            new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!isReadOnly()) {
                    controlValueChanged(m_datePicker.getDate());
                }
            }
        });
        
        /**
         * HACK!
         * 
         * Finds the inner text field and add a property change listener for 
         * property "value".
         */
        Component[] components = m_datePicker.getComponents();
        JTextComponent textField = null;
        if (components != null) {
            for (int i = 0; textField == null 
                && i < components.length; i++) {
                Component c = components[i];
                if (c instanceof JTextComponent) {
                    textField = (JTextComponent) c;
                }
            }
        }
        if (textField != null) {
            final JTextComponent INNER_TEXT_FIELD = textField;
            INNER_TEXT_FIELD.addPropertyChangeListener("value", 
                new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    Object value = evt.getNewValue();
                    if (!isReadOnly() 
                        && (value == null 
                            || !StringUtils.hasText(value.toString()))) {
                        controlValueChanged(null);
                    }
                }
            });
//            INNER_TEXT_FIELD.addCaretListener(new CaretListener() {
//                public void caretUpdate(CaretEvent e) {
//                    String text = INNER_TEXT_FIELD.getText();
//                    if (!isReadOnly() && !StringUtils.hasText(text)) {
//                        controlValueChanged(null);
//                    }
//                }
//            });
        }
        
        // TODO Add possablility that textfield text will be checked by 
        // rule source while typing. Now enter in textfield or focus lost of 
        // textfield is needed.
        // TODO On clearing textfield (without popup) the date should be set to
        // null.

        return m_datePicker;
    }

    /**
     * {@inheritDoc}
     */
    protected void readOnlyChanged() {
        boolean editable = !isReadOnly();
        m_datePicker.setEnabled(editable);
        m_datePicker.setFieldEditable(editable);
    }

    /**
     * {@inheritDoc}
     */
    protected void enabledChanged() {
        boolean editable = !isReadOnly();
        m_datePicker.setEnabled(editable);
        m_datePicker.setFieldEditable(editable);
    }

    /**
     * {@inheritDoc}
     */
    protected void valueModelChanged(Object newValue) {
        try {
            m_datePicker.setDate((Date) newValue);
        } catch (PropertyVetoException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Veto not allowed!", e);
        }
    }
}
