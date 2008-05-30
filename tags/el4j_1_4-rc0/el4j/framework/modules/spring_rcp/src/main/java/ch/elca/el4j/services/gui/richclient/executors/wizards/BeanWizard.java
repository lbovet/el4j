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
package ch.elca.el4j.services.gui.richclient.executors.wizards;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.FormBackedWizardPage;

import ch.elca.el4j.services.gui.richclient.executors.AbstractWizardBeanExecutor;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Wizard class for beans.
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
public class BeanWizard extends AbstractWizard {
    /**
     * Is the wizard executor to use in this wizard.
     */
    protected final AbstractWizardBeanExecutor m_executor;

    /**
     * Constructor.
     * 
     * @param executor Is the wizard executor to use in this wizard.
     */
    public BeanWizard(AbstractWizardBeanExecutor executor) {
        m_executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        FormModel formModel = m_executor.getFormModel();
        Reject.ifNull(formModel);
        Reject.ifNotAssignableTo(formModel, HierarchicalFormModel.class,
            "Form model must be of type " 
            + HierarchicalFormModel.class.getName() 
            + ". Current form model is of type " 
            + formModel.getClass().getName() + ".");
        BeanPropertiesForm[] beanPropertiesForms 
            = m_executor.getBeanPropertiesForms();
        Reject.ifNull(beanPropertiesForms);
        
        for (int i = 0; i < beanPropertiesForms.length; i++) {
            BeanPropertiesForm pf = beanPropertiesForms[i];
            
            ValidatingFormModel childFormModel 
                = FormModelHelper.createChildPageFormModel(
                    (HierarchicalFormModel) formModel, null);
            pf.setValidatingFormModel(childFormModel);
            
            FormBackedWizardPage wizardPage
                = new FormBackedWizardPage(m_executor.getId(), pf);
            addPage(wizardPage);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected boolean onFinish() {
        boolean actionCompleted = false;
        try {
            actionCompleted = m_executor.onFinishOrConfirm();
        } catch (Exception e) {
            actionCompleted 
                = m_executor.onFinishOrConfirmException(e);
        }
        return actionCompleted;
    }
}