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

import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract executor for actions that edit or create beans.
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
public abstract class AbstractFinishBeanExecutor
    extends AbstractDisplayableBeanExecutor {
    /**
     * Is the used form model.
     */
    private FormModel m_formModel;
    
    /**
     * Are the forms for the bean properties.
     */
    private BeanPropertiesForm[] m_beanPropertiesForms;
    
    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirm()
        throws Exception {
        FormModel formModel = getFormModel();
        if (formModel.isDirty()) {
            formModel.commit();
            return onFinishAfterCommit(getCurrentBean());
        } else {
            return true;
        }
    }

    /**
     * Will be invoked if data changed and so data has been written into current
     * bean.
     * 
     * @param currentBean
     *            Is the bean this action is made for.
     * @return Returns <code>true</code> if the finish action could be
     *         completed successfully and corresponding gui elements can be
     *         closed.
     * @throws Exception
     *             On any exception.
     */
    public boolean onFinishAfterCommit(Object currentBean) throws Exception {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirmException(Exception e) {
        DialogUtils.showErrorMessageDialog(getId(), getSchema(), e, 
            getDisplayable().getMainComponent());
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void onRevertOrCancel() {
        FormModel formModel = getFormModel();
        formModel.revert();
    }
    
    /**
     * {@inheritDoc}
     */
    public void onAboutToShow() {
        BeanPropertiesForm[] beanPropertiesForms = getBeanPropertiesForms();
        if (beanPropertiesForms != null && beanPropertiesForms.length > 0) {
            beanPropertiesForms[0].focusFirstComponent();
        }
    }
    
    /**
     * @return Returns current bean.
     */
    public Object getCurrentBean() {
        FormModel formModel = getFormModel();
        return formModel != null ? formModel.getFormObject() : null;
    }
    
    /**
     * @return Returns the formModel.
     */
    public FormModel getFormModel() {
        return m_formModel;
    }

    /**
     * @param formModel Is the formModel to set.
     */
    public void setFormModel(FormModel formModel) {
        m_formModel = formModel;
    }

    /**
     * @return Returns the beanPropertiesForms.
     */
    public final BeanPropertiesForm[] getBeanPropertiesForms() {
        return m_beanPropertiesForms;
    }

    /**
     * @param beanPropertiesForms Is the beanPropertiesForms to set.
     */
    public final void setBeanPropertiesForms(
        BeanPropertiesForm[] beanPropertiesForms) {
        m_beanPropertiesForms = beanPropertiesForms;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        BeanPropertiesForm[] beanPropertiesForms = getBeanPropertiesForms();
        if (beanPropertiesForms == null || beanPropertiesForms.length == 0) {
            CoreNotificationHelper.notifyMisconfiguration("Property "
                + "'beanPropertiesForms' of bean with name '" 
                + getBeanName() + "' must have at minimum one item.");
        }
    }
}
