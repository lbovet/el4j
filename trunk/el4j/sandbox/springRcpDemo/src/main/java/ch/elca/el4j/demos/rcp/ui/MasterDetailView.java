/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.demos.rcp.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.util.env.EnvPropertiesUtils;


/**
 * This class provides View for the master/detail example. It provides a table
 * showing the domain objects and a quick filter field to narrow down the list
 * of visible domain object. Several commands are tied to the selection of the
 * master table
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
 * @param <T> Class type of Domain Object we want to display
 * @author David Stefan (DST)
 */
public class MasterDetailView<T> extends AbstractView 
    implements ApplicationListener {

    /**
     * Handler for the New Entity action.
     */
    private ActionCommandExecutor m_newEntityExecutor 
        = new NewBookExecutor();

    /**
     * Handler for the "Delete" action.
     */
    private GuardedActionCommandExecutor m_deleteExecutor 
        = new DeleteExecutor();
    
    /**
     * The MasterDetail form.
     */
    private MasterDetailForm<T> m_form;

    /**
     * Domain Class of this Master/Detail View.
     */
    private Class<T> m_domainType;
    

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
            "masterDetailFilter.label");
        filterPanel.add(filterLabel, BorderLayout.WEST);
        String tip = getMessage("masterDetailFilter.caption");
        JTextField filterField = getComponentFactory().createTextField();
        filterField.setToolTipText(tip);
        filterPanel.add(filterField, BorderLayout.CENTER);
        // Checkstyle: MagicNumber off
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Checkstyle: MagicNumber on
        
        
        // Create Master/Detail form
        DaoRegistry registry 
            = (DaoRegistry) getApplicationContext().getBean("daoRegistry");
        GenericDao<T> dao = registry.getFor(m_domainType);
        Properties pros = EnvPropertiesUtils.getEnvProperties();
        String sortProperty = pros.getProperty("masterDetail.sortProperty");    
        HierarchicalFormModel model = FormModelHelper.createFormModel(dao);
        m_form 
            = new MasterDetailForm<T>(model, "all", m_domainType, sortProperty);
        
        String filterProperty 
            = pros.getProperty("masterDetail.filterProperties");    
        StringTokenizer tokenizer = new StringTokenizer(filterProperty, ",");
        List<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        TextFilterator filterator = GlazedLists
                .textFilterator(tokens.toArray(new String[0]));
        MatcherEditor editor 
            = new TextComponentMatcherEditor(filterField, filterator);
        m_form.setFilterMatcherEditor(editor);
        
        // Puth things together
        JPanel view = getComponentFactory().createPanel(new BorderLayout());
        //new JPanel(new BorderLayout());
        JScrollPane sp = getComponentFactory().createScrollPane(
            m_form.getControl());      
        view.add(filterPanel, BorderLayout.NORTH);
        view.add(sp, BorderLayout.CENTER);
        return view;
    }

    /**
     * Register the local command executors to be associated with named
     * commands. This is called by the platform prior to making the view
     * visible.
     * 
     * @param context The PageComponentContext
     */
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("newBookCommand", m_newEntityExecutor);
        context.register(GlobalCommandIds.DELETE, m_deleteExecutor);
    }

    /**
     * Private inner class to create a new contact.
     */
    private class NewBookExecutor implements ActionCommandExecutor {
        /**
         * 
         * {@inheritDoc}
         */
        public void execute() {
            m_form.getNewFormObjectCommand().execute();
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
            String title = getMessage("MasterDetailForm.confirmDelete.title");
            String message 
                = getMessage("MasterDetailForm.confirmDelete.message");
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
     * @param domainType Class of domain object to set
     */
    public void setDomainType(Class<T> domainType) {
        m_domainType = domainType;
    }
}