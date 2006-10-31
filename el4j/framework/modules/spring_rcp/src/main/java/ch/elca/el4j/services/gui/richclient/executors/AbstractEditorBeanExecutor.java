/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.dialogs.BeanTitledPageApplicationDialog;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.views.descriptors.impl.DialogPageViewDescriptor;

/**
 * Abstract executor to edit bean properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractEditorBeanExecutor 
    extends AbstractFinishBeanExecutor {
    
    /**
     * Is the used dialog page.
     */
    private DialogPage m_dialogPage;
    
    /**
     * Flag to indicate if the displayable should be embedded in page or be a 
     * separate dialog. Default is not embedded.
     */
    private boolean m_embedded = false;
    
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
        
        FormModel formModel = getFormModel();
        DialogPage dialogPage = getDialogPage();
        if (formModel == null || dialogPage == null) {
            initializeFormModelAndDialogPage(bean);
        } else {
            formModel.setFormObject(bean);
        }
        
        ExecutorDisplayable displayable = getDisplayable();
        if (!displayable.isConfigured()) {
            displayable.configure(this);
        }
        displayable.showDisplayable();
    }

    /**
     * Method to initialize the form model and the dialog page.
     * 
     * @param bean Is the bean to create a form model for.
     */
    protected void initializeFormModelAndDialogPage(Object bean) {
        BeanPropertiesForm[] propertiesForms = getBeanPropertiesForms();
        
        // Create a root form model and a root dialog page that contains the
        // root form model.
        if (propertiesForms.length == 1) {
            // Only one properties form exists. No need to tab.
            BeanPropertiesForm pf = propertiesForms[0];
            
            // Back a validatable form model for the current bean and remember
            // this object as root form model, because it is the only one.
            ValidatingFormModel validatingFormModel 
                = FormModelHelper.createFormModel(bean);
            setFormModel(validatingFormModel);
            
            // Give the created validating form model to the properties form. 
            pf.setValidatingFormModel(validatingFormModel);
            
            // Create a dialog page out of the properties form.
            setDialogPage(new FormBackedDialogPage(getId(), pf));
        } else {
            // Create a root form model for multiple properties forms.
            HierarchicalFormModel hierarchicalFormModel 
                = FormModelHelper.createCompoundFormModel(bean);
            setFormModel(hierarchicalFormModel);
            
            // Create dialog page for multiple properties forms.
            TabbedDialogPage tabbedDialogPage 
                = new TabbedDialogPage(getId());
            setDialogPage(tabbedDialogPage);
            
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
    }

    /**
     * @return Returns the dialogPage.
     */
    public final DialogPage getDialogPage() {
        return m_dialogPage;
    }

    /**
     * @param dialogPage Is the dialogPage to set.
     */
    public final void setDialogPage(DialogPage dialogPage) {
        m_dialogPage = dialogPage;
    }
    
    /**
     * @return Returns the embedded.
     */
    public final boolean isEmbedded() {
        return m_embedded;
    }

    /**
     * Flag to indicate if the displayable should be embedded in page or be a 
     * separate dialog. Default is not embedded.
     * 
     * @param embedded Is the embedded to set.
     */
    public final void setEmbedded(boolean embedded) {
        m_embedded = embedded;
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

    /**
     * {@inheritDoc}
     * 
     * If displayable should be embedded the default 
     * <code>DialogPageViewDescriptor</code> else it is 
     * <code>BeanTitledPageApplicationDialog</code>.
     * 
     * @see DialogPageViewDescriptor
     * @see BeanTitledPageApplicationDialog
     */
    protected ExecutorDisplayable getDefaultDisplayable() {
        if (isEmbedded()) {
            return new DialogPageViewDescriptor();
        } else {
            return new BeanTitledPageApplicationDialog();
        }
    }
}
