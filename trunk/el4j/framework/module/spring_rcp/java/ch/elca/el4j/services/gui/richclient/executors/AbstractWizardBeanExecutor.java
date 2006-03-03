/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.Wizard;

import ch.elca.el4j.services.gui.richclient.dialogs.BeanWizardDialog;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.executors.wizards.BeanWizard;

/**
 * Abstract executor to create new beans.
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
public abstract class AbstractWizardBeanExecutor extends
    AbstractFinishBeanExecutor {
    /**
     * Is the wizard for this executor.
     */
    private Wizard m_wizard;
    
    /**
     * {@inheritDoc}
     */
    public void execute() {
        // Create a new bean for the wizard to fill.
        Object newBean = createNewBean();
        
        FormModel formModel = getFormModel();
        Wizard wizard = getWizard();
        if (formModel == null || wizard == null) {
            initializeFormModelAndWizard(newBean);
        } else {
            formModel.setFormObject(newBean);
        }
        
        ExecutorDisplayable displayable = getDisplayable();
        if (!displayable.isConfigured()) {
            displayable.configure(this);
        }
        displayable.showDisplayable();
    }

    /**
     * @return Returns the wizard.
     */
    public final Wizard getWizard() {
        return m_wizard;
    }

    /**
     * @param wizard Is the wizard to set.
     */
    public final void setWizard(Wizard wizard) {
        m_wizard = wizard;
    }

    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(true);
    }
    
    /**
     * @return Returns a new bean for this wizard.
     */
    protected abstract Object createNewBean();
    
    /**
     * {@inheritDoc}
     * 
     * Default displayable is <code>BeanWizardDialog</code>.
     * 
     * @see BeanWizardDialog
     */
    protected ExecutorDisplayable getDefaultDisplayable() {
        return new BeanWizardDialog();
    }
    
    /**
     * Initializes the form model with the given new bean and the wizard.
     * 
     * @param newBean Is the bean to use in form model.
     */
    protected void initializeFormModelAndWizard(Object newBean) {
        // Create form model for the wizard.
        HierarchicalFormModel formModel 
            = FormModelHelper.createCompoundFormModel(newBean);
        setFormModel(formModel);
        
        /**
         * Create and set the wizard.
         */
        setWizard(new BeanWizard(this));
    }
}
