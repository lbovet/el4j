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
package ch.elca.el4j.demos.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import ch.elca.el4j.demos.model.DefaultPerson;
import ch.elca.el4j.demos.model.Person;
import ch.elca.el4j.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;

import cookxml.cookswing.CookSwing;

import zappini.designgridlayout.DesignGridLayout;


/**
 * Demonstrates how to use cookSwing.
 * 
 * The most important command is <code>cookSwing.render("test.xml");</code>. It
 * processes the whole XML file and initializes bound local variables (e.g.
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
public class XMLDemoForm extends JPanel implements Bindable {
    private JTextField m_firstName;
    private JTextField m_lastName;
    private Person m_person;
    private List<Person> m_persons;
    private final Binder m_binder = BinderManager.getBinder(this);
    
    public XMLDemoForm() {
        createData();
        
        CookSwing cookSwing = new CookSwing(this);
        cookSwing.render("gui/xmlDemoForm.xml");
        
        m_binder.bindAll();
    }

    private void createData() {
        m_persons = new ArrayList<Person>();
        
        Person person1 = new DefaultPerson("Test", "Last", 4);
        person1 = PropertyChangeListenerMixin.addPropertyChangeMixin(person1);
        
        Person person2 = new DefaultPerson("Test2", "Last2", 88);
        person2 = PropertyChangeListenerMixin.addPropertyChangeMixin(person2);
        
        m_persons.add(person1);
        m_persons.add(person2);
        
        m_person = person1;
        m_person.getChildren().add(person2);
    }
    
    private void setGridPanelLayout(JPanel formPanel) {
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(formPanel);
        formPanel.setLayout(layout);

        // the first two rows contains a label and a text field each
        
        layout.row().add("First Name").add(m_firstName);
        layout.row().add("Last Name").add(m_lastName);
        //layout.row().add("Incomplete").add(m_incomplete);
        //layout.row().add(m_createButton).add(m_deleteButton);
        // Hint: spacers can be inserted using add(Row.EMPTY)

        // Checkstyle: MagicNumber off
        layout.row().height(10);
        // Checkstyle: MagicNumber on
    }
    
    public Binder getBinder() {
        return m_binder;
    }
    
    @Action
    public void create() {
        System.out.println("create");
    }
}
