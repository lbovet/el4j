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

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;

import com.michaelbaranov.microba.calendar.DatePicker;

import ch.elca.el4j.services.gui.richclient.forms.binding.swing.DatePickerBinding;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binder for date picker from
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
public class DatePickerBinder extends AbstractBinder {
    /**
     * Is the date style that must be used by date picker.
     */
    private int m_dateStyle = DateFormat.SHORT;
    
    /**
     * Default constructor.
     */
    protected DatePickerBinder() {
        super(Date.class);
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent createControl(Map context) {
        DatePicker datePicker = new DatePicker();
        datePicker.setDateStyle(getDateStyle());
        return datePicker;
    }

    /**
     * {@inheritDoc}
     */
    protected Binding doBind(JComponent control, FormModel formModel,
        String formPropertyPath, Map context) {
        Reject.ifFalse(control instanceof DatePicker);
        DatePicker datePicker = (DatePicker) control;
        Binding binding 
            = new DatePickerBinding(datePicker, formModel, formPropertyPath);
        return binding;
    }

    /**
     * @return the dateStyle
     */
    public final int getDateStyle() {
        return m_dateStyle;
    }

    /**
     * @param dateStyle the dateStyle to set
     */
    public final void setDateStyle(int dateStyle) {
        m_dateStyle = dateStyle;
    }
}
