package ch.elca.el4j.demos.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.help.SeparatorAction;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingx.JXTable;
import org.springframework.context.ApplicationContext;

import zappini.designgridlayout.DesignGridLayout;
import zappini.designgridlayout.Row;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ch.elca.el4j.demos.gui.model.DefaultPerson;
import ch.elca.el4j.demos.gui.model.MyNumber;
import ch.elca.el4j.demos.gui.model.Person;
import ch.elca.el4j.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.gui.model.mixin.SaveRestoreCapability;
import ch.elca.el4j.gui.model.mixin.SaveRestoreMixin;
import ch.elca.el4j.gui.swing.GUIApplication;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.ListBinding;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

/**
 * Demonstrates how to use the automatic bean binding.
 * 
 * @author SWI
 * 
 */
@Form(autoBind = true)
public class ExampleForm extends JPanel {
    private JTextField companyField;
    private JTextField test;

    // fields must have the same name as the property in the model to get bound
    private JTextField age;
    private JTextField firstName;
    private JTextField lastName;

    private JList numbers;
    private JXTable children;

    // setup a final binder instance variable
    private final Binder binder = BinderManager.getBinder(this);

    // bind this model to this form
    @ModelObject(isDefault = true)
    private Person person;

    public ExampleForm(GUIApplication app) {
        createComponents();
        
        JPanel formPanel = new JPanel();

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        
        // add the table to the center
        children.setColumnControlVisible(true);
        JScrollPane scrollPane = new JScrollPane(children);

        add(scrollPane, BorderLayout.CENTER);
        
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(formPanel);
        formPanel.setLayout(layout);

        // the first two rows contains a label and a text field each
        layout.row().label("Company").add(companyField);
        layout.row().label("Age").add(age);

        // add a vertical spacer
        layout.row().height(20);

        // the text fields are twice as wide as the labels
        layout.row().label("First Name").add(firstName, 2).add("Info A").add(
                test, 2);
        layout.row().label("Last Name").add(lastName, 2).add("Info B").add(
                numbers, 2);
        // Hint: spacers can be inserted using add(Row.EMPTY)

        layout.row().height(20);



        ApplicationContext springContext = app.getSpringContext();

        // creating model entirely programmatically:
        Person personCreatedProgrammatically = new DefaultPerson();
        personCreatedProgrammatically = (Person) PropertyChangeListenerMixin
                .addPropertyChangeMixin(personCreatedProgrammatically);
        person = personCreatedProgrammatically;

        // initialize model
        person.setFirstName("Nobody");
        person.setLastName("Last");
        person.setSmart(true);

        // add numbers
        person.getNumbers().add(new MyNumber(3));
        person.getNumbers().add(new MyNumber(2));

        // add a child, shows how to get a model instance via Spring
        Person child1 = (Person) springContext.getBean("person");

        child1.setFirstName("FirstName of Child1");
        child1.setLastName("LastName of Child1");
        child1.setAge(5);
        person.getChildren().add(child1);

        // prepare table bindings
        String[] propertyNames = new String[] { "firstName", "lastName",
                "smart" };
        String[] columnLabels = new String[] { "First Name", "Last Name", "Age" };
        Class[] columnClasses = new Class[] { String.class, String.class,
                Boolean.class };

        // Register the property names, column names and their type to that
        // specific table
        binder.registerBinding(children, new TableBinding(propertyNames,
                columnLabels, columnClasses));

        // show the "value" property in the JList
        binder.registerBinding(numbers, new ListBinding("value"));

        // bind the variable "person" to "this"
        // this interprets the @ModelObject annotation (see above)
        BindingGroup group = binder.getAutoBinding(this);
        group.bind();

        Property selectedLastP = BeanProperty
                .create("selectedElement.lastName");
        Property textP = BeanProperty.create("text");
        Binding selBinding = Bindings.createAutoBinding(
                UpdateStrategy.READ_WRITE, children, selectedLastP,
                companyField, textP);
        selBinding.bind();

        // ///////////////////////////////////////////////////////////////////////////
        // now we can modify the model (person) and the GUI is automatically
        // updated!

        // the first name is updated in the model (person) and then
        // via our binding propagated to its associated text field)
        person.setFirstName("Bindings work");

        // add a number to the list
        person.getNumbers().add(new MyNumber(9));

        // add another child to the table
        Person child2 = (Person) springContext.getBean("person");
        child2.setFirstName("FirstName of Child2");
        child2.setLastName("LastName of Child2");
        child2.setAge(3);
        person.getChildren().add(child2);

        /*
         * // replace the whole numbers-List // not surprisingly this doesn't
         * work yet (how could he know of this?) //(a manual
         * EventTableModel-Creation + setModel is required) List<MyNumber>
         * newList = new ArrayList<MyNumber>(); newList.add(new MyNumber(4));
         * newList.add(new MyNumber(5)); person.setNumbers(newList);
         */

        person.getNumbers().remove(1);
        person.getChildren().remove(0);
    }

    private void createComponents() {
        companyField = new JTextField();
        age = new JTextField();
        firstName = new JTextField(10);
        lastName = new JTextField(8);
        test = new JTextField();

        numbers = new JList();
        children = new JXTable();
    }

    private javax.swing.Action getAction(String actionName) {
        org.jdesktop.application.ApplicationContext ac = Application
                .getInstance().getContext();
        return ac.getActionMap(this).get(actionName);
    }
}
