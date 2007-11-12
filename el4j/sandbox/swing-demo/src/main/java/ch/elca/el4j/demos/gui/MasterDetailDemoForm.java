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
package ch.elca.el4j.demos.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.jdesktop.swingx.JXTable;
import org.springframework.context.ApplicationContext;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.widgets.IntegerField;

import zappini.designgridlayout.DesignGridLayout;

/**
 * Demonstrates master/detail view.
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
public class MasterDetailDemoForm extends JPanel {
    // fields must have the same name as the property in the model to get bound
    private JTextField firstName;
    private JTextField lastName;
    private JCheckBox smart;
    private JTextField age;
    
    private JXTable children;
    
    private JButton m_createButton;
    private JButton m_deleteButton;

    /**
     * The binder instance variable.
     */
    private final Binder binder = BinderManager.getBinder(this);
    
    /**
     * The current Spring context.
     */
    private ApplicationContext m_springContext;

    /**
     * The model to bind to this form.
     */
    @ModelObject(isDefault = true)
    private Person person;

    public MasterDetailDemoForm(GUIApplication app) {
        m_springContext = app.getSpringContext();
        
        createComponents();
        createLayout();
        
        // assign actions
        m_createButton.setAction(app.getAction(this, "create"));
        m_deleteButton.setAction(app.getAction(this, "delete"));
        
        createDataBinding();
        addData();
    }
    
    /**
     * Create a new table entry.
     */
    @Action
    public void create() {
        Person newChild = (Person) m_springContext.getBean("person");
        newChild.setFirstName(firstName.getText());
        newChild.setLastName(lastName.getText());
        newChild.setSmart(smart.isSelected());
        newChild.setAge(Integer.parseInt(age.getText()));
        person.getChildren().add(newChild);
    }
    
    /**
     * Delete selected table entry.
     */
    @Action
    public void delete() {
        if (children.getSelectedRow() >= 0) {
            if (children.getCellEditor() != null) {
                children.getCellEditor().cancelCellEditing();
            }
            Object o = children.getValueAt(children.getSelectedRow(), 0);
            if (o != null) {
                Person selectedPerson = (Person) ((ValidatedProperty) o)
                    .getParent();
                person.getChildren().remove(selectedPerson);
            }
        }
    }
    
    /**
     * Create the form components.
     */
    private void createComponents() {
        firstName = new JTextField();
        lastName = new JTextField();
        smart = new JCheckBox();
        // Checkstyle: MagicNumber off
        age = new IntegerField(new Color(255, 128, 128));
        // Checkstyle: MagicNumber on        
        
        children = new JXTable();
        
        m_createButton = new JButton();
        m_deleteButton = new JButton();
    }
    
    /**
     * Layout the form components.
     */
    private void createLayout() {
        JPanel formPanel = new JPanel();

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        
        // add the table to the center
        //children.setColumnControlVisible(true);
        JScrollPane scrollPane = new JScrollPane(children);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(formPanel);
        formPanel.setLayout(layout);

        // the first two rows contains a label and a text field each

        // the text fields are twice as wide as the labels
        layout.row().add("First Name").add(firstName, 2).add("Smart").add(
                smart, 2);
        layout.row().add("Last Name").add(lastName, 2).add("Age").add(
                age, 2);
        layout.row().add(m_createButton).add(m_deleteButton);
        // Hint: spacers can be inserted using add(Row.EMPTY)

        // Checkstyle: MagicNumber off
        layout.row().height(10);
        // Checkstyle: MagicNumber on
    }
    
    /**
     * Bind the model to the table.
     */
    @SuppressWarnings("unchecked")
    private void createDataBinding() {
        
        // creating model entirely programmatically:
        Person personCreatedProgrammatically = new DefaultPerson();
        personCreatedProgrammatically = PropertyChangeListenerMixin
                .addPropertyChangeMixin(personCreatedProgrammatically);
        person = personCreatedProgrammatically;

        // prepare table bindings
        String[] propertyNames = new String[] {
            "firstName", "lastName", "smart", "age"};
        String[] columnLabels = new String[] {
            "First Name", "Last Name", "Smart", "Age"};
        Class[] columnClasses = new Class[] {
            String.class, String.class, Boolean.class, Integer.class};

        // Register the property names, column names and their type to that
        // specific table
        binder.registerBinding(children, new TableBinding(propertyNames,
                columnLabels, columnClasses));

        // bind the variable "person" to "this"
        // this interprets the @ModelObject annotation (see above)
        BindingGroup group = binder.getAutoBinding(this);
        group.bind();

        // bind selected item to related editing component
        BindingGroup masterDetail = new BindingGroup();
        Binding tmpBinding;
        
        // bind first name
        Property selectedFirstNameP = BeanProperty
                .create("selectedElement.firstName");
        Property textP = BeanProperty.create("text");

        tmpBinding = Bindings.createAutoBinding(
                UpdateStrategy.READ_WRITE, children, selectedFirstNameP,
                firstName, textP);
        masterDetail.addBinding(tmpBinding);
        
        // bind last name
        Property selectedLastNameP = BeanProperty
            .create("selectedElement.lastName");
        tmpBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            children, selectedLastNameP, lastName, textP);
        masterDetail.addBinding(tmpBinding);
        
        // bind smart property
        Property selectedSmartP = BeanProperty.create("selectedElement.smart");
        Property selectedP = BeanProperty.create("selected");
        
        tmpBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            children, selectedSmartP, smart, selectedP);
        masterDetail.addBinding(tmpBinding);
        
        // bind age property
        Property selectedAgeP = BeanProperty.create("selectedElement.age");
        tmpBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            children, selectedAgeP, age, textP);
        masterDetail.addBinding(tmpBinding);
        
        masterDetail.bind();
    }
    
    /**
     * Add sample data to the model.
     */
    private void addData() {
        // add a child, shows how to get a model instance via Spring
        Person child1 = (Person) m_springContext.getBean("person");

        // Checkstyle: MagicNumber off
        child1.setFirstName("FirstName of Child1");
        child1.setLastName("LastName of Child1");
        child1.setAge(5);
        person.getChildren().add(child1);
        
        // add another child to the table
        Person child2 = (Person) m_springContext.getBean("person");
        child2.setFirstName("FirstName of Child2");
        child2.setLastName("LastName of Child2");
        child2.setAge(3);
        person.getChildren().add(child2);
        // Checkstyle: MagicNumber on
    }

}