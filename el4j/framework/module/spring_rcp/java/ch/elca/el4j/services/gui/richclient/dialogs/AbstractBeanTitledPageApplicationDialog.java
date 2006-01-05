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
package ch.elca.el4j.services.gui.richclient.dialogs;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;

import ch.elca.el4j.services.gui.richclient.Constants;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanView;

/**
 * Abstract application dialog with title used for beans.
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
public abstract class AbstractBeanTitledPageApplicationDialog
    extends TitledPageApplicationDialog 
    implements InitializingBean, BeanNameAware {
    
    /**
     * Is the root form model for this dialog.
     */
    private FormModel m_rootFormModel;

    /**
     * Are the bean properties forms.
     */
    private BeanPropertiesForm[] m_propertiesForms;

    /**
     * Reference to the bean view.
     */
    private AbstractBeanView m_beanView;

    /**
     * Is the id used to get properties like labels for the created form.
     */
    private String m_propertiesId = Constants.DEFAULT_DIALOG_PROPERTIES_ID;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;
    
    

    
    /**
     * {@inheritDoc}
     */
    protected void onAboutToShow() {
        m_propertiesForms[0].focusFirstComponent();
        setEnabled(getDialogPage().isPageComplete());
    }

    /**
     * @return Returns current bean. This is the bean the dialog is made for.
     */
    protected Object getCurrentBean() {
        return m_rootFormModel != null ? m_rootFormModel.getFormObject() : null;
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean onFinish() {
        if (m_rootFormModel.isDirty()) {
            m_rootFormModel.commit();
            return onFinishAfterCommit(getCurrentBean());
        } else {
            return true;
        }
    }
    
    /**
     * Will be invoked if data changed and so data has been written into current
     * bean.
     * 
     * @param currentBean Is the bean this dialog is made for.
     * @return Returns <code>true</code> if action completed successfully.
     */
    protected abstract boolean onFinishAfterCommit(Object currentBean);

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
    public final FormModel getRootFormModel() {
        return m_rootFormModel;
    }

    /**
     * @param rootFormModel The rootFormModel to set.
     */
    public final void setRootFormModel(FormModel rootFormModel) {
        m_rootFormModel = rootFormModel;
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
    public void setDialogPage(DialogPage dialogPage) {
        super.setDialogPage(dialogPage);
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
    public void afterPropertiesSet() throws Exception { }
}
