/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.WizardDialog;

import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.wizards.AbstractBeanWizard;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Executor for bean wizards.
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
public class BeanWizardExecutor extends AbstractBeanDialogFormExecutor {
    /**
     * Is the class of the bean the wizard is for.
     */
    private Class m_beanClass;
    
    /**
     * {@inheritDoc}
     */
    public void execute() {
        // Create a new bean for the wizard to fill.
        Object newBean = getNewBean();
        
        // Create properties forms by application context. 
        BeanPropertiesForm[] propertiesForms = createPropertiesForms();

        // Create form model for the wizard.
        HierarchicalFormModel formModel 
            = FormModelHelper.createCompoundFormModel(newBean);
        
        // Create a wizard by using the given dialog bean name.
        AbstractBeanWizard wizard 
            = createBeanWizard(formModel, propertiesForms);
        
        // Get the wizard dialog from wizard.
        WizardDialog wizardDialog = wizard.getWizardDialog();
        wizardDialog.setParent(
            getBeanView().getContext().getWindow().getControl());
        
        // Show the wizard dialog.
        wizardDialog.showDialog();
    }
    
    /**
     * @return Returns a new bean instance.
     */
    protected Object getNewBean() {
        Object newBean = null;
        if (m_beanClass != null) {
            try {
                newBean = m_beanClass.newInstance();
            } catch (Exception e) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Could not made an instance by using default constructor "
                    + "of class '" + m_beanClass.getName() + "'.", e);
            }
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "No bean class given.");
        }
        return newBean;
    }
    
    /**
     * Creates a bean wizard.
     * 
     * @param rootFormModel Is the root form model.
     * @param propertiesForms Are the properties forms.
     * @return Returns the created wizard.
     */
    protected AbstractBeanWizard createBeanWizard(
        HierarchicalFormModel rootFormModel,
        BeanPropertiesForm[] propertiesForms) {
        AbstractBeanWizard wizard 
            = (AbstractBeanWizard) createDialog();
        wizard.setBeanView(getBeanView());
        wizard.setRootFormModel(rootFormModel);
        wizard.setPropertiesForms(propertiesForms);
        return wizard;
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(true);
    }

    /**
     * @return Returns the beanClass.
     */
    public final Class getBeanClass() {
        return m_beanClass;
    }

    /**
     * @param beanClass The beanClass to set.
     */
    public final void setBeanClass(Class beanClass) {
        m_beanClass = beanClass;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getBeanClass(), "beanClass", this);
    }
}
