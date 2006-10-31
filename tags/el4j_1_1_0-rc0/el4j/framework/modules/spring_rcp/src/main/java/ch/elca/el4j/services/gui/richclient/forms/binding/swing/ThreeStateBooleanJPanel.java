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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.value.ValueModel;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * <code>JPanel</code> for a three state boolean element group. There are three
 * toggle buttons to receive this behaviour.
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
public class ThreeStateBooleanJPanel extends JPanel 
    implements ChangeListener, PropertyChangeListener {
    /**
     * Is the property name of the <code>true</code>-button.
     */
    public static final String TRUE_BUTTON_PROPERTY_NAME = "true";

    /**
     * Is the property name of the <code>false</code>-button.
     */
    public static final String FALSE_BUTTON_PROPERTY_NAME = "false";

    /**
     * Is the property name of the <code>unknown</code>-button.
     */
    public static final String UNKNOWN_BUTTON_PROPERTY_NAME = "unknown";

    /**
     * True button (first state).
     */
    private final JToggleButton m_trueButton;
    
    /**
     * False button (second state).
     */
    private final JToggleButton m_falseButton;
    
    /**
     * Unknown button (third state).
     */
    private final JToggleButton m_unknownButton;
    
    /**
     * Is group of the three toggle buttons.
     */
    private final ButtonGroup m_buttonGroup;
    
    /**
     * Is the value model for this component.
     */
    private ValueModel m_valueModel = null;

    /**
     * Constructor.
     * 
     * @param trueButton Is the button for state <code>true</code>.
     * @param falseButton Is the button for state <code>false</code>.
     * @param unknownButton Is the button for state <code>unknown</code>.
     */
    public ThreeStateBooleanJPanel(JToggleButton trueButton, 
        JToggleButton falseButton, JToggleButton unknownButton) {
        Reject.ifNull(trueButton);
        Reject.ifNull(falseButton);
        Reject.ifNull(unknownButton);

        m_trueButton = trueButton;
        m_falseButton = falseButton;
        m_unknownButton = unknownButton;

        m_buttonGroup = new ButtonGroup();
        m_buttonGroup.add(m_trueButton);
        m_buttonGroup.add(m_falseButton);
        m_buttonGroup.add(m_unknownButton);
        m_trueButton.setSelected(true);
        
        add(m_trueButton);
        add(m_falseButton);
        add(m_unknownButton);
        
        m_trueButton.addChangeListener(this);
        m_falseButton.addChangeListener(this);
        m_unknownButton.addChangeListener(this);
        
        setEnabled(true);
    }
    
    /**
     * @return Returns <code>true</code> if the "true"-button is selected. 
     */
    public final boolean isTrueSelected() {
        return m_buttonGroup.isSelected(m_trueButton.getModel());
    }
    
    /**
     * @return Returns <code>true</code> if the "false"-button is selected. 
     */
    public final boolean isFalseSelected() {
        return m_buttonGroup.isSelected(m_falseButton.getModel());
    }

    /**
     * @return Returns <code>true</code> if the "unknown"-button is selected. 
     */
    public final boolean isUnknownSelected() {
        return m_buttonGroup.isSelected(m_unknownButton.getModel());
    }
    
    /**
     * @return Returns the falseButton.
     */
    public final JToggleButton getFalseButton() {
        return m_falseButton;
    }

    /**
     * @return Returns the trueButton.
     */
    public final JToggleButton getTrueButton() {
        return m_trueButton;
    }

    /**
     * @return Returns the unknownButton.
     */
    public final JToggleButton getUnknownButton() {
        return m_unknownButton;
    }

    /**
     * @return Returns the buttonGroup.
     */
    public final ButtonGroup getButtonGroup() {
        return m_buttonGroup;
    }

    /**
     * @return Returns the valueModel.
     */
    public final ValueModel getValueModel() {
        return m_valueModel;
    }

    /**
     * @param valueModel The valueModel to set. Can be set only once.
     */
    public final void setValueModel(ValueModel valueModel) {
        if (m_valueModel != null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Value model can be set only once.");
        }
        m_valueModel = valueModel;
        m_valueModel.addValueChangeListener(this);
        propertyChange(null);
    }

    /**
     * {@inheritDoc}
     * 
     * Invoked if a component has been clicked.
     */
    public void stateChanged(ChangeEvent e) {
        Boolean state = null;
        if (isTrueSelected()) {
            state = Boolean.TRUE;
        } else if (isFalseSelected()) {
            state = Boolean.FALSE;
        } else if (isUnknownSelected()) {
            state = null;
        } else {
            CoreNotificationHelper.notifyMisconfiguration("No state selected.");
        }
        m_valueModel.setValue(state);
    }

    /**
     * {@inheritDoc}
     * 
     * Invoked the model has changed its value.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Boolean state = (Boolean) m_valueModel.getValue();
        if (state == null) {
            m_unknownButton.setSelected(true);
        } else if (state.booleanValue()) {
            m_trueButton.setSelected(true);
        } else {
            m_falseButton.setSelected(true);
        }
    }
}
