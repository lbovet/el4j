package ch.elca.el4j.demos.gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.BindingGroup;

import zappini.designgridlayout.DesignGridLayout;
import ch.elca.el4j.demos.gui.model.DefaultPerson;
import ch.elca.el4j.demos.gui.model.Person;
import ch.elca.el4j.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.gui.model.mixin.SaveRestoreCapability;
import ch.elca.el4j.gui.swing.GUIApplication;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;

@Form(autoBind = true)
public class CancelableForm extends JPanel {
    private JTextField firstName;
    private JButton okButton, cancelButton;
    
    @ModelObject(isDefault = true)
    private Person person;
    
    // setup a final binder instance variable
    private final Binder binder = BinderManager.getBinder(this);
    
    public CancelableForm(GUIApplication app) {
        createComponents();
        createLayout();
        
        // assign actions
        okButton.setAction(app.getAction(this, "applyChanges"));
        cancelButton.setAction(app.getAction(this, "discardChanges"));
        
        
        // creating model entirely programmatically:
        person = new DefaultPerson();
        person = (Person) PropertyChangeListenerMixin
                .addPropertyChangeMixin(person);

        // initialize model
        person.setFirstName("Nobody");
        
        // save properties
        ((SaveRestoreCapability) person).save();
        
        // bind the variable "person" to "this"
        // this interprets the @ModelObject annotation (see above)
        BindingGroup group = binder.getAutoBinding(this);
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
    
    private void createComponents() {
        firstName = new JTextField();
        okButton = new JButton();
        cancelButton = new JButton();
    }
    
    private void createLayout() {
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(this);
        setLayout(layout);

        // the first two rows contains a label and a text field each
        layout.row().label("First Name").add(firstName);
        layout.row().add(okButton).add(cancelButton);
    }
}
