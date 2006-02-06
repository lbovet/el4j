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
package ch.elca.el4j.apps.refdb.gui.dialogs;

import javax.swing.JOptionPane;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanTitledPageApplicationDialog;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;

/**
 * Dialog to save changes made on a reference dto.
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
public class ReferencePropertiesApplicationDialog 
    extends AbstractBeanTitledPageApplicationDialog {

    /**
     * {@inheritDoc}
     * 
     * Save the received keyword dto and update gui components.
     */
    protected boolean onFinishAfterCommit(Object currentBean) {
        ReferenceDto currentReference = (ReferenceDto) currentBean;
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        ReferenceDto returnValue 
            = referenceService.saveReference(currentReference);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.replaceBean(currentReference, returnValue);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onFinishException(Exception e) {
        String errorCode = null;
        boolean closeDialog = false;

        ReferenceDto currentReference = (ReferenceDto) getCurrentBean();
        BeanPresenter beanPresenter = getBeanPresenter();
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        
        if (e instanceof OptimisticLockingFailureException) {
            // Get reference from database.
            ReferenceDto modifiedReference;
            try {
                modifiedReference = referenceService.getReferenceByKey(
                    currentReference.getKey());
            } catch (Exception ex) {
                modifiedReference = null;
            }
            
            if (modifiedReference != null) {
                // Reference has been modificated. Update the bean and left 
                // dialog open.
                beanPresenter.replaceBean(currentReference, modifiedReference);
                getRootFormModel().setFormObject(modifiedReference);
                closeDialog = false;
                errorCode = "OptimisticLockingFailureException.modified";
            } else {
                // Remove the current bean and close the dialog.
                beanPresenter.removeBean(currentReference);
                closeDialog = true;
                errorCode = "OptimisticLockingFailureException.deleted";
            }
        } else if (e instanceof DataAccessException) {
            closeDialog = false;
            errorCode = "DataAccessException";
        }
        
        if (errorCode != null) {
            String title = MessageUtils.getMessage(getPropertiesId(), 
                "reference", errorCode + ".title");
            String message = MessageUtils.getMessage(getPropertiesId(), 
                "reference", errorCode + ".message");
            
            JOptionPane.showMessageDialog(getDialog(), message, 
                title, JOptionPane.ERROR_MESSAGE);
            
            if (closeDialog) {
                dispose();
            }
        } else {
            super.onFinishException(e);
        }
    }
}
