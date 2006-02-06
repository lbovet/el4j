/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanTitledPageApplicationDialog;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;

/**
 * Executor for bean properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class BeanPropertiesExecutor extends AbstractBeanDialogFormExecutor {
    /**
     * {@inheritDoc}
     */
    public void execute() {
        // Get bean to edit.
        Object bean = getBeanPresenter().getSelectedBean();
        
        // Do nothing if no bean is selected.
        if (bean == null) {
            return;
        }
        
        // Create an application dialog.
        AbstractBeanTitledPageApplicationDialog appDialog 
            = (AbstractBeanTitledPageApplicationDialog)
              createDialog();
        String parentId = appDialog.getPropertiesId();
        
        // Create properties forms by application context. 
        BeanPropertiesForm[] propertiesForms = createPropertiesForms();
        
        // Create a root form model and a root dialog page that contains the
        // root form model.
        FormModel rootFormModel;
        AbstractDialogPage rootDialogPage;
        if (propertiesForms.length == 1) {
            // Only one properties form exists. No need to tab.
            BeanPropertiesForm pf = propertiesForms[0];
            
            // Back a validatable form model for the current bean and remember
            // this object as root form model, because it is the only one.
            ValidatingFormModel validatingFormModel 
                = FormModelHelper.createFormModel(bean);
            rootFormModel = validatingFormModel;
            
            // Give the created validating form model to the properties form. 
            pf.setValidatingFormModel(validatingFormModel);
            
            // Create a dialog page out of the properties form.
            rootDialogPage = new FormBackedDialogPage(parentId, pf);
        } else {
            // Create a root form model for multiple properties forms.
            HierarchicalFormModel hierarchicalFormModel 
                = FormModelHelper.createCompoundFormModel(bean);
            rootFormModel = hierarchicalFormModel;
            
            // Create dialog page for multiple properties forms.
            TabbedDialogPage tabbedDialogPage 
                = new TabbedDialogPage(parentId);
            rootDialogPage = tabbedDialogPage;
            
            // Go through all properties forms.
            for (int i = 0; i < propertiesForms.length; i++) {
                BeanPropertiesForm pf = propertiesForms[i];
                
                // Create a validating form model for each properties form and
                // give the created validating form model to the properties 
                // form. 
                ValidatingFormModel validatingFormModel 
                    = FormModelHelper.createChildPageFormModel(
                        hierarchicalFormModel, null);
                pf.setValidatingFormModel(validatingFormModel);
                
                // Add properties form to the tabbed dialog page.
                tabbedDialogPage.addForm(pf);
            }
        }
        
        // Initializes the application dialog.
        initializeBeanTitledPageApplicationDialog(appDialog, propertiesForms, 
                rootFormModel, rootDialogPage);

        // Show the dialog.
        appDialog.showDialog();
    }

    /**
     * Initializes the given bean titled page application dialog.
     * 
     * @param appDialog
     *            Is the application dialog to initialize.
     * @param propertiesForms
     *            Are the properties forms.
     * @param rootFormModel
     *            Is the root form model for this dialog.
     * @param rootDialogPage
     *            Is the root dialog page for this dialog.
     */
    protected void initializeBeanTitledPageApplicationDialog(
        AbstractBeanTitledPageApplicationDialog appDialog,
        BeanPropertiesForm[] propertiesForms, FormModel rootFormModel, 
        AbstractDialogPage rootDialogPage) {
        
        appDialog.setDialogPage(rootDialogPage);
        PageComponent pageComponent = (PageComponent) getBeanPresenter();
        appDialog.setParent(
            pageComponent.getContext().getWindow().getControl());
        appDialog.setBeanPresenter(getBeanPresenter());
        appDialog.setRootFormModel(rootFormModel);
        appDialog.setPropertiesForms(propertiesForms);

        // Set the dialog title.
        String parentId = appDialog.getPropertiesId();
        String title = MessageUtils.getMessage(parentId, "title");
        if (!StringUtils.hasText(title)) {
            title = rootDialogPage.getTitle();
        }
        appDialog.setTitle(title);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandId() {
        String commandId = super.getCommandId();
        if (!StringUtils.hasText(commandId)) {
            commandId = GlobalCommandIds.PROPERTIES;
            setCommandId(commandId);
        }
        return commandId;
    }

    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(getBeanPresenter().getSelectedBean() != null);
    }
}
