package ch.elca.el4j.demos.gui;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.swingx.JXTable;
import org.springframework.context.ApplicationContext;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ch.elca.el4j.demos.gui.model.DefaultPerson;
import ch.elca.el4j.demos.gui.model.MyNumber;
import ch.elca.el4j.demos.gui.model.Person;
import ch.elca.el4j.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.gui.swing.GUIApplication;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;

/**
 * Demonstrates how to use the automatic bean binding.
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
	
	/* ATTENTION Beans Binding doesn't work properly with lists/tables */
	private JList numbersList;	
	private JXTable childrenTable;

	
	// setup a final binder instance variable
    private final Binder binder = BinderManager.getBinder(this);
    
    // bind this model to this form
    @ModelObject(isDefault = true)
    private Person person;

    
	public ExampleForm(GUIApplication app) {
		createComponents();
		
		// see http://www.jgoodies.com/freeware/forms/
		//  allows easier layout of GUIs
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, pref, 7dlu, right:pref, 3dlu, pref", // cols
                "p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, 100dlu");      // rows
        
        // Specify that columns 1 & 5 as well as 3 & 7 have equal widths.       
        layout.setColumnGroups(new int[][]{{1, 5}, {3, 7}});
        
        // Create a builder that assists in adding components to the container. 
        // Wrap the panel with a standardized border.
        PanelBuilder builder = new PanelBuilder(layout, this);
        builder.setDefaultDialogBorder();

        // Obtain a reusable constraints object to place components in the grid.
        CellConstraints cc = new CellConstraints();

        // Fill the grid with components; the builder offers to create
        // frequently used components, e.g. separators and labels.
        
        // Add a titled separator to cell (1, 1) that spans 7 columns.
        builder.addSeparator("General", cc.xyw(1,  1, 7));
        builder.addLabel("Company",     cc.xy (1,  3));
        builder.add(companyField,       cc.xyw(3,  3, 5));
        builder.addLabel("Age",         cc.xy (1,  5));
        builder.add(age,                cc.xyw(3,  5, 5));

        builder.addSeparator("Information", cc.xyw(1,  7, 7));
        builder.addLabel("First Name",   	cc.xy(1,  9));
        builder.add(firstName,      		cc.xy(3,  9));
        builder.addLabel("Last Name",   	cc.xy(1, 11));
        builder.add(lastName,     			cc.xy(3, 11));
        builder.addLabel("Info A",   		cc.xy(5,  9));
        builder.add(test,        			cc.xy(7,  9));
        builder.addLabel("Info D",   		cc.xy(5, 11));
        builder.add(numbersList,   			cc.xy(7, 11));
        
        childrenTable.setColumnControlVisible(true);
        JScrollPane scrollPane = new JScrollPane(childrenTable);
        
        builder.addSeparator("Table", cc.xyw(1, 13, 7));
        builder.add(scrollPane,       cc.xyw(1, 15, 7));

        ApplicationContext springContext = app.getSpringContext(); 
        
        // creating model entirely programmatically:
        Person personCreatedProgrammatically = new DefaultPerson();
        personCreatedProgrammatically = 
        	(Person) PropertyChangeListenerMixin.addPropertyChangeMixin(personCreatedProgrammatically);
        person = personCreatedProgrammatically;
        	
        
        
        // initialize model
        person.setFirstName("Nobody");
        person.setLastName("Last");

        // add numbers
        person.getNumbers().add(new MyNumber(3));
        person.getNumbers().add(new MyNumber(2));
        
        // add a child, shows how to get a model instance via Spring
        Person child1 = (Person)springContext.getBean("person");
        
        child1.setFirstName("FirstName of Child1");
        child1.setLastName("LastName of Child1");
        child1.setAge(5);
        person.getChildren().add(child1);
        
        
        // prepare table bindings
        String[] propertyNames = new String[] {"firstName", "lastName", "age"};
        String[] columnLabels = new String[] {"First Name", "Last Name", "Age"};

        /* CAVEAT: Beans Binding doesn't work yet properly with lists/tables 
         *  Use rather glazed lists for now, see this example below.*/
        /* 
        // table:
        binder.registerBinding(children, new TableBinding(propertyNames, columnLabels));
        // list:
        binder.registerBinding(numbers, new ListBinding("value"));
        */
        
        // the same thing with beans binding:
        boolean[] editable = new boolean[] {true, true, true};
        TableFormat tf = GlazedLists.tableFormat(Person.class, propertyNames, columnLabels, editable);
        EventTableModel tableModel = new EventTableModel((EventList)person.getChildren(), tf);
        childrenTable.setModel(tableModel);
        
        EventListModel listModel = new EventListModel((EventList)person.getNumbers());
        numbersList.setModel(listModel);
        
        // bind the variable "person" to "this"
        //  this interprets the @ModelObject annotation (see above) 
        BindingGroup group = binder.getAutoBinding(this);
        group.bind();
        
        // CAVEAT: Beans Binding doesn't work properly (yet) with lists/tables.
        /*// this is how it would look like with beans binding 
        Property selectedLastP = BeanProperty.create("selectedElement.lastName");
        Property textP = BeanProperty.create("text");
        Binding selBinding =
            Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, children, selectedLastP,
                    companyField, textP);
        selBinding.bind();
        */
        

        /////////////////////////////////////////////////////////////////////////////
        // now we can modify the model (person) and the GUI is automatically updated!
        
        // the first name is updated in the model (person) and then
        //  via our binding propagated to its associated text field)
        person.setFirstName("Bindings work");
        
        // add a number to the list
        person.getNumbers().add(new MyNumber(9));
        
        // add another child to the table
        Person child2 = (Person)springContext.getBean("person");
        child2.setFirstName("FirstName of Child2");
        child2.setLastName("LastName of Child2");
        child2.setAge(3);
        person.getChildren().add(child2);
        
        /*
        // replace the whole numbers-List
        // not surprisingly this doesn't work yet (how could he know of this?)
        //(a manual EventTableModel-Creation + setModel is required)
        List<MyNumber> newList = new ArrayList<MyNumber>();
        newList.add(new MyNumber(4));
        newList.add(new MyNumber(5));
        person.setNumbers(newList); */
	}


    
    private void createComponents() {
        companyField = new JTextField();
        age = new JTextField();
        firstName = new JTextField(10);
        lastName = new JTextField(8);
        test = new JTextField();
        
        numbersList = new JList();      
        childrenTable = new JXTable();
    }
	
    private javax.swing.Action getAction(String actionName) {
        org.jdesktop.application.ApplicationContext ac = Application.getInstance().getContext();
        return ac.getActionMap(this).get(actionName);
    }
}
