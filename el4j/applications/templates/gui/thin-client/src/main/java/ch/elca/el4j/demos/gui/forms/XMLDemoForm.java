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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.TableCellRenderers.JodaTimeTableCellRenderer;
import ch.elca.el4j.demos.gui.widgets.JodaDateTimePicker;
import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.swing.cookswing.binding.Bindable;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.binding.BindingFactory;

import cookxml.cookswing.CookSwing;


/**
 * Demonstrates how to use cookSwing. CookSwing allows to define a form in XML.
 *
 * The most important command is <code>cookSwing.render("test.xml");</code>. It
 * processes the whole XML file and initializes corresponing local variables (e.g.
 * <code>firstName</code>).
 *
 * As this class implements {@link Bindable}, cookSwing is able to set up all
 * bindings declared in the XML file.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Scope("prototype")
@Component("xmlDemoForm")
public class XMLDemoForm extends JPanel implements Bindable {
	
	
	
	private JTextField firstName;
	private JodaDateTimePicker bornInYear;
	private JTextField lastName;
	private JLabel statusLabel;
	private Person person;
	private List<Person> persons;
	private JTable table;
	private final Binder binder = BinderManager.getBinder(this);
	
	public XMLDemoForm() {
		// Create or load data. This has to be done before creating and binding the GUI components.
		createData();
		BindingFactory.getDefaultProperties().register(JodaDateTimePicker.class, "jodaDateTime");
		// Create the GUI components from an XML description.
		// When a component has a property cx:var the created component is assigned to the corresponding variable.
		// As we implement Bindable, all specified bindings are registered in binder.
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/xmlDemoForm.xml");

		
		// Bind all bindings created during cookSwing.render()
		binder.bindAll();
		
		// sets our own renderer for DateTime objects
		table.setDefaultRenderer(org.joda.time.DateTime.class, new JodaTimeTableCellRenderer());

		
	}

	private void createData() {
		persons = new ArrayList<Person>();
		
		Person person1 = new DefaultPerson("Test", "Last", 4, new DateTime());
		// all objects that should be bound to a GUI component must have a property change capability to work corretly.
		person1 = PropertyChangeListenerMixin.addPropertyChangeMixin(person1);
		
		Person person2 = new DefaultPerson("Test2", "Last2", 88, new DateTime());
		person2 = PropertyChangeListenerMixin.addPropertyChangeMixin(person2);
		
		persons.add(person1);
		persons.add(person2);
		
		person = person1;
		person.getChildren().add(person2);
	}
	
	/**
	 * Creation method for the JodaDateTimePicker.
	 * 
	 * @return a nicely configured JodaDateTimePicker instance.
	 */
	
	public JodaDateTimePicker getJodaDateTimePicker() {
		JodaDateTimePicker picker = new JodaDateTimePicker(new DateTime());
		picker.setShowSecondsInTimeField(false);
		return picker;
	}
	
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		// this method is called during cookSwing.render() to register the bindings specified in the XML
		return binder;
	}
	
	/**
	 * An action that prints some information. See action attribute in XML.
	 */
	@Action
	public void info() {
		statusLabel.setText("Person: " + firstName.getText() + " " + lastName.getText());
	}
}
