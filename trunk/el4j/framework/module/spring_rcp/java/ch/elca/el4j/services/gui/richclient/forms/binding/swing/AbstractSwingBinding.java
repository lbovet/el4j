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
import org.springframework.richclient.form.binding.support.AbstractBinding;

/**
 * Abstract binding for swing classes.
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
public abstract class AbstractSwingBinding extends AbstractBinding {
    /**
     * Is the JComponent to bind with the form model.
     */
    private final JComponent m_control;
    
    /**
     * Constructor.
     * 
     * @param control Is the control to bind.
     * @param formModel Is the model to bind.
     * @param formPropertyPath Is the property path inside the form.
     * @param requiredSourceClass Is the required class for the binding.
     */
    public AbstractSwingBinding(JComponent control, FormModel formModel, 
        String formPropertyPath, Class requiredSourceClass) {
        super(formModel, formPropertyPath, requiredSourceClass);
        m_control = control;
    }
    
    /**
     * @return Returns the <code>JComponent</code> for this binding.
     */
    protected JComponent getJComponent() {
        return m_control;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void readOnlyChanged() {
        getJComponent().setEnabled(isEnabled() && !isReadOnly());
    }

    /**
     * {@inheritDoc}
     */
    protected void enabledChanged() {
        getJComponent().setEnabled(isEnabled() && !isReadOnly());
    }
}
