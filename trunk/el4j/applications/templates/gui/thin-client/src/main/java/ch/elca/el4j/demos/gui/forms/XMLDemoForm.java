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
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.swing.cookswing.binding.Bindable;

import cookxml.cookswing.CookSwing;


/**
 * Demonstrates how to use cookSwing. CookSwing allows to define a form in XML.
 *
 * The most important command is <code>cookSwing.render("test.xml");</code>. It
 * processes the whole XML file and initializes corresponing local variables (e.g.
 * <code>m_firstName</code>).
 *
 * As this class implements {@link Bindable}, cookSwing is able to set up all
 * bindings declared in the XML file.
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
@LazyInit
@Scope("prototype")
@Component("xmlDemoForm")
public class XMLDemoForm extends JPanel implements Bindable {
	private JTextField m_firstName;
	private JTextField m_lastName;
	private JLabel m_statusLabel;
	private Person m_person;
	private List<Person> m_persons;
	private final Binder m_binder = BinderManager.getBinder(this);
	
	public XMLDemoForm() {
		// Create or load data. This has to be done before creating and binding the GUI components.
		createData();
		
		// Create the GUI components from an XML description.
		// When a component has a property cx:var the created component is assigned to the corresponding variable.
		// As we implement Bindable, all specified bindings are registered in m_binder.
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/xmlDemoForm.xml");
		
		// Bind all bindings created during cookSwing.render()
		m_binder.bindAll();
	}

	private void createData() {
		m_persons = new ArrayList<Person>();
		
		Person person1 = new DefaultPerson("Test", "Last", 4);
		// all objects that should be bound to a GUI component must have a property change capability to work corretly.
		person1 = PropertyChangeListenerMixin.addPropertyChangeMixin(person1);
		
		Person person2 = new DefaultPerson("Test2", "Last2", 88);
		person2 = PropertyChangeListenerMixin.addPropertyChangeMixin(person2);
		
		m_persons.add(person1);
		m_persons.add(person2);
		
		m_person = person1;
		m_person.getChildren().add(person2);
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		// this method is called during cookSwing.render() to register the bindings specified in the XML
		return m_binder;
	}
	
	/**
	 * An action that prints some information. See action attribute in XML.
	 */
	@Action
	public void info() {
		m_statusLabel.setText("Person: " + m_firstName.getText() + " " + m_lastName.getText());
	}
}
