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
package ch.elca.el4j.apps.refdb.gui.dialogs;

import javax.swing.JOptionPane;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.gui.views.AbstractRefdbView;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanTitledPageApplicationDialog;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;

/**
 * Dialog to save changes made on a keyword dto.
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
public class KeywordPropertiesApplicationDialog 
    extends AbstractBeanTitledPageApplicationDialog {

    /**
     * {@inheritDoc}
     * 
     * Save the received keyword dto and update gui components.
     */
    protected boolean onFinishAfterCommit(Object currentBean) {
        KeywordDto currentKeyword = (KeywordDto) currentBean;
        AbstractRefdbView view = (AbstractRefdbView) getBeanView();
        KeywordDto returnValue = view.getReferenceService().saveKeyword(
                currentKeyword);
        view.replaceBean(currentKeyword, returnValue);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onFinishException(Exception e) {
        String errorCode = null;
        boolean closeDialog = false;

        KeywordDto currentKeyword = (KeywordDto) getCurrentBean();
        AbstractRefdbView view = (AbstractRefdbView) getBeanView();
        ReferenceService service 
            = view.getReferenceService();
        
        if (e instanceof DataIntegrityViolationException) {
            KeywordDto oldKeyword
                = service.getKeywordByKey(currentKeyword.getKey());
            view.replaceBean(currentKeyword, oldKeyword);
            getRootFormModel().setFormObject(oldKeyword);
            closeDialog = false;
            errorCode = "dialogDuplicatedKeyword";
        } else if (e instanceof OptimisticLockingFailureException) {
            // Get keyword from database.
            KeywordDto modifiedKeyword;
            try {
                modifiedKeyword
                    = service.getKeywordByKey(currentKeyword.getKey());
            } catch (Exception ex) {
                modifiedKeyword = null;
            }
            
            if (modifiedKeyword != null) {
                // Keyword has been modificated. Update the bean and left 
                // dialog open.
                view.replaceBean(currentKeyword, modifiedKeyword);
                getRootFormModel().setFormObject(modifiedKeyword);
                closeDialog = false;
                errorCode = "dialogKeywordModified";
            } else {
                // Remove the current bean and close the dialog.
                view.removeBean(currentKeyword);
                closeDialog = true;
                errorCode = "dialogKeywordDeleted";
            }
        } else if (e instanceof DataAccessException) {
            closeDialog = false;
            errorCode = "dialogDataAccessProblem";
        }
        
        if (errorCode != null) {
            String title = MessageUtils.getMessage(getPropertiesId(), 
                errorCode + ".title");
            String message = MessageUtils.getMessage(getPropertiesId(), 
                errorCode + ".message");
            
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
