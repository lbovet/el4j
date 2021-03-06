/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.gui.forms;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.widgets.IntegerField;
import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.model.tablemodel.TableSorter;
import ch.elca.el4j.services.gui.swing.GUIApplication;

import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * Demonstrates master/detail view.
 *
 * This demo extends the concepts used in {@link BindingDemoForm}.
 *
 * The table can be sorted by clicking onto the column headers. This feature is
 * provided by the {@link TableSorter} class, which is adapted to beans
 * binding. Specialized tables like JXTable by swinglabs.org don't work
 * properly!
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@Lazy
@Component("masterDetailDemoForm")
@Form(autoBind = true)
public class MasterDetailDemoForm extends JPanel {
	// fields must have the same name as the property in the model to get bound
	private JTextField firstName;
	private JTextField lastName;
	private JCheckBox smart;
	private JTextField age;
	
	private JTable children;
	
	private JButton createButton;
	private JButton deleteButton;

	/**
	 * The binder instance variable.
	 */
	private final Binder binder = BinderManager.getBinder(this);

	/**
	 * The model to bind to this form.
	 */
	@ModelObject(isDefault = true)
	private Person person;

	@Autowired
	public MasterDetailDemoForm(GUIApplication application) {
		createComponents();
		createLayout();
		
		// assign actions
		createButton.setAction(application.getAction(this, "create"));
		deleteButton.setAction(application.getAction(this, "delete"));
		
		addData();
		createDataBinding();
		
		
		binder.bindAll();
		
		// add sorting functionality to table.
		// this has to be done after bindAll(), otherwise tableModel gets overwritten again
		TableSorter sorter = new TableSorter(
			children.getModel(), children.getTableHeader());
		children.setModel(sorter);
	}
	
	/**
	 * Create a new table entry.
	 */
	@Action
	public void create() {
		if (children.getCellEditor() != null) {
			children.getCellEditor().cancelCellEditing();
		}
		Person newChild = new DefaultPerson();
		newChild = PropertyChangeListenerMixin.addPropertyChangeMixin(newChild);
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
			int selectedRow = children.getSelectedRow();
			if (children.getCellEditor() != null) {
				children.getCellEditor().cancelCellEditing();
			}
			Object o = children.getValueAt(selectedRow, 0);
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
		age = new IntegerField();
		
		children = new JTable();
		
		createButton = new JButton();
		deleteButton = new JButton();
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
		layout.row().grid().add(new JLabel("First Name")).add(firstName, 2).add(new JLabel("Smart")).add(smart, 2);
		layout.row().grid().add(new JLabel("Last Name")).add(lastName, 2).add(new JLabel("Age")).add(age, 2);
		layout.row().grid().add(createButton).add(deleteButton);
	}
	
	/**
	 * Bind the model to the table.
	 */
	@SuppressWarnings("unchecked")
	private void createDataBinding() {

		// prepare table bindings
		String[] propertyNames = new String[] {
			"firstName", "lastName", "smart", "age"};
		String[] columnLabels = new String[] {
			"First Name", "Last Name", "Smart", "Age"};
		Class[] columnClasses = new Class[] {
			String.class, String.class, Boolean.class, Integer.class};

		// Add table binding
		binder.addAutoBinding(this, children,
			new TableBinding(propertyNames, columnLabels, columnClasses), true);

		// bind the variable "person" to "this"
		// this interprets the @ModelObject annotation (see above)
		binder.addAutoBinding(this);

		// bind selected item to related editing component

		// bind first name
		Property selectedFirstNameP = BeanProperty
				.create("selectedElement.firstName");
		Property textP = BeanProperty.create("text");

		binder.addManualBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, children, selectedFirstNameP,
				firstName, textP));
		
		// bind last name
		Property selectedLastNameP = BeanProperty
			.create("selectedElement.lastName");
		binder.addManualBinding(Bindings.createAutoBinding(
			UpdateStrategy.READ_WRITE,
			children, selectedLastNameP, lastName, textP));
		
		// bind smart property
		Property selectedSmartP = BeanProperty.create("selectedElement.smart");
		Property selectedP = BeanProperty.create("selected");
		
		binder.addManualBinding(Bindings.createAutoBinding(
			UpdateStrategy.READ_WRITE,
			children, selectedSmartP, smart, selectedP));
		
		// bind age property
		Property selectedAgeP = BeanProperty.create("selectedElement.age");
		binder.addManualBinding(Bindings.createAutoBinding(
			UpdateStrategy.READ_WRITE,
			children, selectedAgeP, age, textP));
	}
	
	/**
	 * Add sample data to the model.
	 */
	private void addData() {
		// creating model entirely programmatically:
		Person personCreatedProgrammatically = new DefaultPerson();
		personCreatedProgrammatically = PropertyChangeListenerMixin
				.addPropertyChangeMixin(personCreatedProgrammatically);
		person = personCreatedProgrammatically;
		
		person.setFirstName("James");
		person.setLastName("Bond");
		person.setAge(60);
		person.setSmart(true);
		
		// add a child
		Person child1 = new DefaultPerson();
		child1 = PropertyChangeListenerMixin.addPropertyChangeMixin(child1);
		
		// Checkstyle: MagicNumber off
		child1.setFirstName("FirstName of Child1");
		child1.setLastName("LastName of Child1");
		child1.setAge(5);
		person.getChildren().add(child1);
		
		// add another child to the table
		Person child2 = new DefaultPerson();
		child2 = PropertyChangeListenerMixin.addPropertyChangeMixin(child2);
		child2.setFirstName("FirstName of Child2");
		child2.setLastName("LastName of Child2");
		child2.setAge(3);
		person.getChildren().add(child2);
		// Checkstyle: MagicNumber on
	}

}