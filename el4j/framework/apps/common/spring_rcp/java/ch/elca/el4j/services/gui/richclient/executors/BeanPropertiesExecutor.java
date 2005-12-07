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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanTitledPageApplicationDialog;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanView;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

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
public class BeanPropertiesExecutor extends AbstractActionCommandExecutor
    implements InitializingBean, BeanNameAware, ApplicationContextAware {
    /**
     * Is the view where this executor is used.
     */
    private AbstractBeanView m_beanView;
    
    /**
     * Are the property forms bean names for this executor.
     */
    private String[] m_propertiesFormBeanNames;

    /**
     * Is the bean name of the application dialog.
     */
    private String m_applicationDialogBeanName;

    /**
     * Is the id used to get properties like labels for the created form.
     */
    private String m_propertiesId;
    
    
    /**
     * Name of this bean.
     */
    private String m_beanName;

    /**
     * Is the application context this bean was created with.
     */
    private ApplicationContext m_applicationContext;
    
    /**
     * {@inheritDoc}
     */
    public void execute() {
        // Get bean to edit.
        Object bean = m_beanView.getSelectedBean();
        
        // Do nothing if no bean is selected.
        if (bean == null) {
            return;
        }
        
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
            rootDialogPage = new FormBackedDialogPage(m_propertiesId , pf);
        } else {
            // Create a root form model for multiple properties forms.
            HierarchicalFormModel hierarchicalFormModel 
                = FormModelHelper.createCompoundFormModel(bean);
            rootFormModel = hierarchicalFormModel;
            
            // Create dialog page for multiple properties forms.
            TabbedDialogPage tabbedDialogPage 
                = new TabbedDialogPage(m_propertiesId);
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
        
        // Get an instance of the application dialog.
        AbstractBeanTitledPageApplicationDialog appDialog 
            = createApplicationDialog(propertiesForms, rootFormModel, 
                rootDialogPage);

        // Show the dialog.
        appDialog.showDialog();
    }

    /**
     * Create properties forms.
     * 
     * @return Created properties forms.
     */
    protected BeanPropertiesForm[] createPropertiesForms() {
        BeanPropertiesForm[] propertiesForms 
            = new BeanPropertiesForm[m_propertiesFormBeanNames.length];
        for (int i = 0; i < propertiesForms.length; i++) {
            String beanName = m_propertiesFormBeanNames[i];
            propertiesForms[i] 
                = (BeanPropertiesForm) m_applicationContext.getBean(beanName);
        }
        return propertiesForms;
    }

    /**
     * Creates an application dialog.
     * 
     * @param propertiesForms
     *            Are the properties forms.
     * @param rootFormModel
     *            Is the root form model for this dialog.
     * @param rootDialogPage
     *            Is the root dialog page for this dialog.
     * @return Returns the created application dialog.
     */
    protected AbstractBeanTitledPageApplicationDialog createApplicationDialog(
        BeanPropertiesForm[] propertiesForms, FormModel rootFormModel, 
        AbstractDialogPage rootDialogPage) {
        
        AbstractBeanTitledPageApplicationDialog appDialog 
            = (AbstractBeanTitledPageApplicationDialog) 
                m_applicationContext.getBean(m_applicationDialogBeanName);
        appDialog.setDialogPage(rootDialogPage);
        appDialog.setTitle(rootDialogPage.getTitle());
        appDialog.setParent(m_beanView.getContext().getWindow().getControl());
        appDialog.setBeanView(m_beanView);
        appDialog.setRootFormModel(rootFormModel);
        appDialog.setPropertiesForms(propertiesForms);
        return appDialog;
    }

    /**
     * @return Returns the applicationDialogBeanName.
     */
    public final String getApplicationDialogBeanName() {
        return m_applicationDialogBeanName;
    }

    /**
     * @param applicationDialogBeanName The applicationDialogBeanName to set.
     */
    public final void setApplicationDialogBeanName(
        String applicationDialogBeanName) {
        m_applicationDialogBeanName = applicationDialogBeanName;
    }

    /**
     * @return Returns the beanView.
     */
    public final AbstractBeanView getBeanView() {
        return m_beanView;
    }

    /**
     * @param beanView The beanView to set.
     */
    public final void setBeanView(AbstractBeanView beanView) {
        m_beanView = beanView;
    }

    /**
     * @return Returns the propertiesFormBeanNames.
     */
    public final String[] getPropertiesFormBeanNames() {
        return m_propertiesFormBeanNames;
    }

    /**
     * @param propertiesFormBeanNames The propertiesFormBeanNames to set.
     */
    public final void setPropertiesFormBeanNames(
        String[] propertiesFormBeanNames) {
        m_propertiesFormBeanNames = propertiesFormBeanNames;
    }
    
    /**
     * @return Returns the propertiesId.
     */
    public final String getPropertiesId() {
        return m_propertiesId;
    }

    /**
     * @param propertiesId The propertiesId to set.
     */
    public final void setPropertiesId(String propertiesId) {
        m_propertiesId = propertiesId;
    }

    
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_propertiesFormBeanNames, "propertiesFormBeanNames", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_applicationDialogBeanName, "applicationDialogBeanName", this);
        
        if (!StringUtils.hasText(m_propertiesId)) {
            m_propertiesId = m_beanName;
        }
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_propertiesId, "propertiesId", this);
    }
    
    /**
     * @return Returns the name of this bean.
     */
    public String getBeanName() {
        return m_beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException {
        m_applicationContext = applicationContext;
    }
}
