package ch.elca.el4j.demos.gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.BindingGroup;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;

import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.model.mixin.SaveRestoreCapability;

import zappini.designgridlayout.DesignGridLayout;

/**
 * This class demonstrates a form that has a cancel button to restore the
 * original value.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@Form(autoBind = true)
public class CancelableDemoForm extends JPanel {
    /**
     * The first name. Bound to the model.
     */
    private JTextField firstName;
    
    private JButton m_okButton;
    private JButton m_cancelButton;
    
    @ModelObject(isDefault = true)
    private Person person;
    
    /**
     * The binder instance variable.
     */
    private final Binder m_binder = BinderManager.getBinder(this);
    
    public CancelableDemoForm(GUIApplication app) {
        createComponents();
        createLayout();
        
        // assign actions
        m_okButton.setAction(app.getAction(this, "applyChanges"));
        m_cancelButton.setAction(app.getAction(this, "discardChanges"));
        
        
        // creating model entirely programmatically:
        person = new DefaultPerson();
        person = PropertyChangeListenerMixin.addPropertyChangeMixin(person);

        // initialize model
        person.setFirstName("Nobody");
        
        // save properties
        ((SaveRestoreCapability) person).save();
        
        // bind the variable "person" to "this"
        // this interprets the @ModelObject annotation (see above)
        BindingGroup group = m_binder.getAutoBinding(this);
        group.bind();
    }
    
    @Action
    public void applyChanges() {
        ((SaveRestoreCapability) person).save();
    }
    
    @Action
    public void discardChanges() {
        ((SaveRestoreCapability) person).restore();
    }
    
    /**
     * Create the form components.
     */
    private void createComponents() {
        firstName = new JTextField();
        m_okButton = new JButton();
        m_cancelButton = new JButton();
    }
    
    /**
     * Layout the form components.
     */
    private void createLayout() {
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(this);
        setLayout(layout);

        // the first two rows contains a label and a text field each
        layout.row().label("First Name").add(firstName);
        layout.row().add(m_okButton).add(m_cancelButton);
    }
}
