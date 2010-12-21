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

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.ListBinding;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.validation.CustomValidationResponder;
import ch.elca.el4j.demos.gui.widgets.IntegerField;
import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.MyNumber;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;

import net.java.dev.designgridlayout.DesignGridLayout;


/**
 * Demonstrates how to use the automatic bean binding.
 *
 * This demo shows how form components that have the same name as properties of
 * the model (like <code>firstName</code>) get automatically bound. Binding
 * is done using <code>binder.addAutoBinding(this)</code>.
 *
 * Non-trivial bindings like lists or tables need special information. In this
 * demo the <code>JList</code> should show the <code>value</code> property of
 * each list element. Therefore, it is necessary to specify that by
 * (<code>binder.addManualBinding(... new ListBinding("value") ...)</code>).
 *
 * We also shown how to manually bind components: the textfield
 * <code>curListSelection</code> is set to always show the selected list item
 * value.
 *
 * Another aspect shown here is the ability to provide a custom validation
 * responder (a class that knows how to react to (in)valid values). Our
 * <code>CustomValidationResponder</code> shows the validation messages on a
 * label on the form (see {@link CustomValidationResponder}).
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@Lazy
@Component("bindingDemoForm")
@SuppressWarnings("serial")
@Form(autoBind = true)
public class BindingDemoForm extends JPanel {
	// fields must have the same name as the property in the model to get bound (prefix "" gets removed)
	
	/**
	 * The textfield for firstName. Bound to person.firstName (person because of @ModelObject annotation)
	 */
	private JTextField firstName;
	
	/**
	 * The textfield for lastName. Bound to person.lastName
	 */
	private JTextField lastName;
	
	/**
	 * The textfield for age. Bound to person.age
	 */
	private JTextField age;

	private JTextField curListSelection;
	private JList numbers;
	
	private JLabel validationMessage;

	/**
	 * The binder instance variable.
	 */
	private final Binder binder = BinderManager.getBinder(this);

	/**
	 * The model to bind to this form.
	 */
	@ModelObject(isDefault = true)
	private Person person;

	public BindingDemoForm() {
		createComponents();
		createLayout();

		createData();
		createDataBinding();
		binder.bindAll();
		
		// now we can modify the model (person) and the GUI is automatically
		// updated!

		addData();
	}
	
	/**
	 * Create the form components.
	 */
	private void createComponents() {
		firstName = new JTextField();
		lastName = new JTextField();
		age = new IntegerField();
		curListSelection = new JTextField();

		numbers = new JList();
		
		validationMessage = new JLabel();
	}
	
	/**
	 * Layout the form components.
	 */
	private void createLayout() {
		JPanel formPanel = new JPanel();

		setLayout(new BorderLayout());
		add(formPanel, BorderLayout.NORTH);
		
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(formPanel);
		formPanel.setLayout(layout);

		// the text fields are twice as wide as the labels
		// Checkstyle: MagicNumber off
		layout.row().left().add(new JLabel("These fields are bound to the underlying Person model."));
		layout.row().left().add(new JLabel("Validation Info: Firstname must be longer than 3 charcters."));

		layout.row().grid().add(new JLabel("First Name")).add(firstName, 2)
			.add(new JLabel("Last Name")).add(lastName, 2);
		layout.row().grid().add(new JLabel("Age")).add(age, 2).empty(3);
		layout.row().grid().add(new JLabel("Current Validation Message:")).add(validationMessage, 3);
		
		// add a vertical spacer
		layout.emptyRow();
		
		layout.row().left().add(new JLabel("Edit list items. Negative numbers are invalid."));
		layout.row().grid().add(new JLabel("List Selection")).add(curListSelection, 2)
			.add(new JLabel("The list")).add(numbers, 2);
		
		// Checkstyle: MagicNumber on
	}
	
	/**
	 * Create data.
	 */
	private void createData() {
		// creating model entirely programmatically:
		Person personCreatedProgrammatically = new DefaultPerson();
		personCreatedProgrammatically = PropertyChangeListenerMixin
				.addPropertyChangeMixin(personCreatedProgrammatically);
		person = personCreatedProgrammatically;

		// initialize model
		person.setFirstName("???");
		person.setLastName("???");

		// add initial numbers
		// Checkstyle: MagicNumber off
		MyNumber aNumber = new MyNumber();
		aNumber = PropertyChangeListenerMixin.addPropertyChangeMixin(aNumber);
		aNumber.setValue(3);
		person.getNumbers().add(aNumber);
		
		aNumber = new MyNumber();
		aNumber = PropertyChangeListenerMixin.addPropertyChangeMixin(aNumber);
		aNumber.setValue(2);
		person.getNumbers().add(aNumber);
		// Checkstyle: MagicNumber on
	}
	
	/**
	 * Bind the model to the table.
	 */
	@SuppressWarnings("unchecked")
	private void createDataBinding() {
		// show the "value" property in the JList
		binder.addManualBinding(person, "numbers", numbers,
			new ListBinding("value"), true);
		
		ValidationResponder responder
			= new CustomValidationResponder(validationMessage);
		
		// bind the variable "person" to "this"
		// this interprets the @ModelObject annotation (see above)
		BindingGroup bindings = binder.addAutoBinding(this);
		binder.addValidationResponder(bindings, responder);

		// bind value of selected item in the list to a textField
		// there is no explicit validation (invalid values are silently dropped)
		Property selectedlistP = BeanProperty.create("selectedElement.value");
		Property textP = BeanProperty.create("text");
		binder.addManualBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, numbers, selectedlistP,
				curListSelection, textP));
	}
	
	/**
	 * Add more data to the model.
	 */
	private void addData() {
		// the name is updated in the model (person) and then
		// via our binding propagated to its associated text field)
		person.setFirstName("Beans");
		person.setLastName("Binding");

		// add a validated number to the list
		MyNumber aNumber = new MyNumber();
		aNumber = PropertyChangeListenerMixin.addPropertyChangeMixin(aNumber);
		// Checkstyle: MagicNumber off
		aNumber.setValue(-7);
		// Checkstyle: MagicNumber on
		person.getNumbers().add(aNumber);

		// replace the whole numbers-List
		// not surprisingly this doesn't work (how could he know of this?)
		//(a manual EventTableModel-Creation + setModel is required)
		/*List<MyNumber> newList = new ArrayList<MyNumber>();
		newList.add(new MyNumber(4));
		newList.add(new MyNumber(5));
		person.setNumbers(newList);*/

		person.getNumbers().remove(1);
		//person.getChildren().remove(0);
	}
}
