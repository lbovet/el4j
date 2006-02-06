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
package ch.elca.el4j.services.gui.richclient.wizards;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.FormBackedWizardPage;
import org.springframework.richclient.wizard.WizardDialog;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.Constants;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;

/**
 * Abstract wizard for beans.
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
public abstract class AbstractBeanWizard extends AbstractWizard
    implements InitializingBean, BeanNameAware {

    /**
     * Is the dialog for this wizard.
     */
    private WizardDialog m_wizardDialog;
    
    /**
     * Is the root form model for this dialog.
     */
    private HierarchicalFormModel m_rootFormModel;

    /**
     * Are the bean properties forms.
     */
    private BeanPropertiesForm[] m_propertiesForms;

    /**
     * Reference to the bean presenter.
     */
    private BeanPresenter m_beanPresenter;

    /**
     * Is the id used to get properties like labels for the created form.
     */
    private String m_propertiesId = Constants.DEFAULT_DIALOG_PROPERTIES_ID;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * @return Returns current bean. This is the bean the dialog is made for.
     */
    protected Object getCurrentBean() {
        return m_rootFormModel != null ? m_rootFormModel.getFormObject() : null;
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public void addPages() {
        for (int i = 0; i < m_propertiesForms.length; i++) {
            BeanPropertiesForm pf = m_propertiesForms[i];
            
            ValidatingFormModel childFormModel 
                = FormModelHelper.createChildPageFormModel(
                    getRootFormModel(), null);
            pf.setValidatingFormModel(childFormModel);
            
            FormBackedWizardPage wizardPage = new FormBackedWizardPage(pf);
            addPage(wizardPage);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean onFinish() {
        boolean success = true;
        if (m_rootFormModel.isDirty()) {
            m_rootFormModel.commit();
            try {
                success = onFinishAfterCommit(getCurrentBean());
            } catch (RuntimeException re) {
                success = onFinishException(re);
            }
        }
        return success;
    }
    
    /**
     * Will be invoked if data changed and so data has been written into current
     * bean.
     * 
     * @param currentBean Is the bean this dialog is made for.
     * @return Returns <code>true</code> if action completed successfully and
     *         the dialog should be closed.
     */
    protected abstract boolean onFinishAfterCommit(Object currentBean);

    /**
     * Will be invoked if a runtime exception occurred while finishing wizard.
     * 
     * @param re Is the thrown runtime exception.
     * @return Returns <code>false</code> if the wizard should not close the 
     *         dialog.
     */
    protected abstract boolean onFinishException(RuntimeException re);
    
    /**
     * @return Returns the wizardDialog.
     */
    public final WizardDialog getWizardDialog() {
        if (m_wizardDialog == null) {
            m_wizardDialog = new WizardDialog(this) {
                protected void onAboutToShow() {
                    super.onAboutToShow();
                    m_propertiesForms[0].focusFirstComponent();
                }
            };
        }
        return m_wizardDialog;
    }

    /**
     * @param wizardDialog The wizardDialog to set.
     */
    public final void setWizardDialog(WizardDialog wizardDialog) {
        m_wizardDialog = wizardDialog;
    }

    /**
     * @return Returns the propertiesForms.
     */
    public final BeanPropertiesForm[] getPropertiesForms() {
        return m_propertiesForms;
    }

    /**
     * @param propertiesForms The propertiesForms to set.
     */
    public final void setPropertiesForms(BeanPropertiesForm[] propertiesForms) {
        m_propertiesForms = propertiesForms;
    }

    /**
     * @return Returns the rootFormModel.
     */
    public final HierarchicalFormModel getRootFormModel() {
        return m_rootFormModel;
    }

    /**
     * @param rootFormModel The rootFormModel to set.
     */
    public final void setRootFormModel(HierarchicalFormModel rootFormModel) {
        m_rootFormModel = rootFormModel;
    }

    /**
     * @return Returns the beanPresenter.
     */
    public final BeanPresenter getBeanPresenter() {
        return m_beanPresenter;
    }

    /**
     * @param beanPresenter The beanPresenter to set.
     */
    public final void setBeanPresenter(BeanPresenter beanPresenter) {
        m_beanPresenter = beanPresenter;
    }

    /**
     * {@inheritDoc}
     * 
     * Method overridden to tell wizard the correct id for fetching messages.
     */
    public String getId() {
        return m_propertiesId;
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
     * @return Returns the name of this bean.
     */
    public final String getBeanName() {
        return m_beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        // Set wizard dialog title.
        String title = MessageUtils.getMessage(getPropertiesId(), "title");
        if (StringUtils.hasText(title)) {
            setTitle(title);
        }
    }
}
