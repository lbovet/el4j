/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.elca.el4j.addressbook.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.command.GuardedActionCommandExecutor;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.form.FormModelHelper;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import ch.elca.el4j.addressbook.dao.ContactDao;
import ch.elca.el4j.addressbook.dom.Addressbook;
import ch.elca.el4j.addressbook.dom.Contact;

/**
 * This class provides the main view of the contacts. It provides a table
 * showing the contact objects and a quick filter field to narrow down the list
 * of visible contacts. Several commands are tied to the selection of the
 * contacts table
 * <p>
 * By implementing special tag interfaces, this component will be automatically
 * wired in to certain events of interest.
 * <ul>
 * <li><b>ApplicationListener</b> - This component will be automatically
 * registered as a listener for application events.</li>
 * </ul>
 * 
 * <script type="text/javascript">printFileStatus
*   ("$URL$",
    *    "$Revision$",
    *    "$Date$",
    *    "$Author$"
    * );</script>
 * 
 * @author David Stefan (DST)
 */
public class AddressbookView extends AbstractView 
    implements ApplicationListener {

    /**
     * Handler for the "New Contact" action.
     */
    private ActionCommandExecutor m_newContactExecutor 
        = new NewContactExecutor();

    /**
     * Handler for the "Properties" action.
     */
    private GuardedActionCommandExecutor m_propertiesExecutor 
        = new PropertiesExecutor();

    /**
     * Handler for the "Delete" action.
     */
    private GuardedActionCommandExecutor m_deleteExecutor 
        = new DeleteExecutor();

    /**
     * The text field allowing the user to filter the contents of the contact
     * table.
     */
    private JTextField m_filterField;
    
    /**
     * My Addressbook.
     */
    private Addressbook m_myAddressBook;
    
    /**
     * Addressbook form.
     */
    private AddressbookForm m_form;
    
    

    /**
     * Create the control for this view. This method is called by the platform
     * in order to obtain the control to add to the surrounding window and page.
     * 
     * @return component holding this view
     */
    protected JComponent createControl() {
        // Create filter panel
        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel filterLabel = getComponentFactory().createLabel(
            "nameAddressFilter.label");
        filterPanel.add(filterLabel, BorderLayout.WEST);
        String tip = getMessage("nameAddressFilter.caption");
        m_filterField = getComponentFactory().createTextField();
        m_filterField.setToolTipText(tip);
        filterPanel.add(m_filterField, BorderLayout.CENTER);
        // Checkstyle: MagicNumber off
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Checkstyle: MagicNumber on

        //Create Master/Detail form    
        HierarchicalFormModel model 
            = FormModelHelper.createFormModel(m_myAddressBook);
        m_form = new AddressbookForm(model, "myContacts", Contact.class);
        TextFilterator filterator = GlazedLists
                .textFilterator(new String[] {"lastName", "address"});
        MatcherEditor editor 
            = new TextComponentMatcherEditor(m_filterField, filterator);
        m_form.setFilterMatcherEditor(editor);

        // Puth things togeterh
        JPanel view = new JPanel(new BorderLayout());
        JScrollPane sp = getComponentFactory().createScrollPane(
            m_form.getControl());
        view.add(filterPanel, BorderLayout.NORTH);
        view.add(sp, BorderLayout.CENTER);

        // Need two lines to create TabbedPane
        JTabbedPane pane = new JTabbedPane();
        JTextField infoText = new JTextField("Here will be the explanation");
        infoText.setEditable(false);
        pane.add("Info", infoText);
        pane.add("Addressbook", view);
        pane.setSelectedIndex(1);
        return pane;
    }

    /**
     * Register the local command executors to be associated with named
     * commands. This is called by the platform prior to making the view
     * visible.
     * 
     * @param context The PageComponentContext
     */
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("newContactCommand", m_newContactExecutor);
        context.register(GlobalCommandIds.PROPERTIES, m_propertiesExecutor);
        context.register(GlobalCommandIds.DELETE, m_deleteExecutor);
    }

    /**
     * Private inner class to create a new contact.
     */
    private class NewContactExecutor implements ActionCommandExecutor {
        /**
         * 
         * {@inheritDoc}
         */
        public void execute() {
            
            Contact con = new Contact();
            con.setFirstName("Foo1");
            con.setLastName("Bar1");
            con.setAddress("11001011");
            con.setCity("LA1");
            
            ContactDao dao = (ContactDao) m_myAddressBook.getDaoRegistry().getFor(Contact.class);
            Contact con2 = dao.saveOrUpdate(con);
            System.out.println(con2.getFirstName() + " " + con2.getKey());
            
//             m_form.getNewFormObjectCommand().execute();
        }
    }

    /**
     * Private inner class to handle the properties form display.
     */
    private class PropertiesExecutor extends AbstractActionCommandExecutor {
        /**
         * 
         * {@inheritDoc}
         */
        public void execute() {
            
        }
    }

    /**
     * Private class to handle the delete command. Note that due to the
     * configuration above, this executor is only enabled when exactly one
     * contact is selected in the table. Thus, we don't have to protect against
     * being executed with an incorrect state.
     */
    private class DeleteExecutor extends AbstractActionCommandExecutor {
        /**
         * 
         * {@inheritDoc}
         */
        public void execute() {
            // We know exactly one contact will be selected at this time because
            // of the guards put in place in prepareTable.
            // final Contact contact = contactTable.getSelectedContact();
            // Query the user to be sure they want to do this
            String title = getMessage("contact.confirmDelete.title");
            String message = getMessage("contact.confirmDelete.message");
            ConfirmationDialog dlg = new ConfirmationDialog(title, message) {
                protected void onConfirm() {
                // Delete the object from the persistent store.
                // getContactDataStore().delete(contact);
                // And notify the rest of the application of the change
                // getApplicationContext().publishEvent(
                // new
                // LifecycleApplicationEvent(LifecycleApplicationEvent.DELETED,
                // contact));
                }
            };
            dlg.showDialog();
        }
    }

    /**
     * Handle an application event. This will notify us of object adds, deletes,
     * and modifications. Forward to our object table for handling.
     * 
     * @param e
     *            event to process
     */
    public void onApplicationEvent(ApplicationEvent e) {

    }

    /**
     * @return My Addressbook
     */
    public Addressbook getMyAddressBook() {
        return m_myAddressBook;
    }

    /**
     * @param myAddressBook Set My Addressbook
     */
    public void setMyAddressBook(Addressbook myAddressBook) {
        this.m_myAddressBook = myAddressBook;
    }
}