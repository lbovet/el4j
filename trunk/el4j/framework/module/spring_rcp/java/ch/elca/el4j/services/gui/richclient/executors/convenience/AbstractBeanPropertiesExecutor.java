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
package ch.elca.el4j.services.gui.richclient.executors.convenience;

import org.springframework.binding.form.FormModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.gui.richclient.executors.AbstractPropertiesBeanExecutor;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;

/**
 * Abstract executor to edit beans.
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
public abstract class AbstractBeanPropertiesExecutor 
    extends AbstractPropertiesBeanExecutor {
    /**
     * {@inheritDoc}
     */
    public boolean onFinishAfterCommit(Object currentBean) throws Exception {
        PrimaryKeyObject givenBean = (PrimaryKeyObject) currentBean;
        PrimaryKeyObject savedBean = saveBean(givenBean);
        getBeanPresenter().replaceBean(givenBean, savedBean);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirmException(Exception e) {
        String appendix = null;
        boolean actionCompleted = false;

        PrimaryKeyObject currentBean = (PrimaryKeyObject) getCurrentBean();
        Object key = currentBean.getKeyAsObject();
        BeanPresenter beanPresenter = getBeanPresenter();
        FormModel formModel = getFormModel();
        
        if (e instanceof DataIntegrityViolationException) {
            PrimaryKeyObject oldBean;
            try {
                oldBean = getBeanByKey(key);
                beanPresenter.replaceBean(currentBean, oldBean);
                formModel.setFormObject(oldBean);
            } catch (Exception ex) {
                if (ex instanceof DataIntegrityViolationException) {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "Endless loop detected!", ex);
                }
                onFinishOrConfirmException(ex);
            }
        } else if (e instanceof OptimisticLockingFailureException) {
            // Get bean from database.
            PrimaryKeyObject modifiedBean;
            try {
                modifiedBean = getBeanByKey(key);
            } catch (Exception ex) {
                modifiedBean = null;
            }
            if (modifiedBean != null) {
                // Bean has been modificated. Update the bean.
                beanPresenter.replaceBean(currentBean, modifiedBean);
                formModel.setFormObject(modifiedBean);
                appendix = "modified";
            } else {
                // Remove the current bean and close the gui element.
                beanPresenter.removeBean(currentBean);
                appendix = "deleted";
                actionCompleted = true;
            }
        }
        
        DialogUtils.showErrorMessageDialog(getId(), getSchema(), e, appendix, 
            getDisplayable().getMainComponent());
        
        return actionCompleted;
    }

    /**
     * @param givenBean Is the bean to save
     * @return Returns the saved bean.
     */
    protected abstract PrimaryKeyObject saveBean(PrimaryKeyObject givenBean);

    /**
     * @param key Used to lookup the bean.
     * @return Returns the bean with given key.
     * @throws Exception If bean could not be fetched.
     */
    protected abstract PrimaryKeyObject getBeanByKey(Object key) 
        throws Exception;
}
