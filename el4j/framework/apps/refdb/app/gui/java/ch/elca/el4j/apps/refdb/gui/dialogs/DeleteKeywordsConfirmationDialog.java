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
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.gui.views.AbstractRefdbView;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanConfirmationDialog;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Confirmation dialog to delete selected keywords.
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
public class DeleteKeywordsConfirmationDialog extends
    AbstractBeanConfirmationDialog {
    
    /**
     * Are the keywords that should be deleted.
     */
    private KeywordDto[] m_keywords;

    /**
     * {@inheritDoc}
     */
    protected void onConfirm() {
        Object[] objects = getBeanView().getSelectedBeans();
        m_keywords = new KeywordDto[objects.length];
        for (int i = 0; i < objects.length; i++) {
            m_keywords[i] = (KeywordDto) objects[i];
        }
        Reject.ifNull(m_keywords);
        int [] keys = new int[m_keywords.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = m_keywords[i].getKey();
        }
        
        AbstractRefdbView view = (AbstractRefdbView) getBeanView();
        ReferenceService service = view.getReferenceService();
        service.removeKeywords(keys);
        view.removeBeans(m_keywords);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onFinishException(Exception e) {
        String errorCode = null;
        boolean closeDialog = true;

        AbstractRefdbView view = (AbstractRefdbView) getBeanView();
        ReferenceService service = view.getReferenceService();
        
        if (e instanceof JdbcUpdateAffectedIncorrectNumberOfRowsException) {
            errorCode = "dialogKeywordDeletingProblem";
        } else if (e instanceof DataAccessException) {
            errorCode = "dialogDataAccessProblem";
        }
        
        if (errorCode != null) {
            KeywordDto[] actualizedKeywords = new KeywordDto[m_keywords.length];
            for (int i = 0; i < m_keywords.length; i++) {
                KeywordDto oldKeyword = m_keywords[i];
                try {
                    KeywordDto newKeyword 
                        = service.getKeywordByKey(oldKeyword.getKey());
                    view.replaceBean(oldKeyword, newKeyword);
                    actualizedKeywords[i] = newKeyword;
                } catch (DataAccessException ex) {
                    view.removeBean(oldKeyword);
                }
            }
            
            // Selecting beans must be done in a seperate step, when no bean 
            // will be added to or removed from data list. Otherwise already 
            // made selections will be lost!
            view.clearSelection();
            for (int i = 0; i < actualizedKeywords.length; i++) {
                KeywordDto newKeyword = actualizedKeywords[i];
                view.selectBeanAdditionally(newKeyword);
            }
            
            String messageCodePrefix = m_keywords.length == 1 
                ? "singleBean" : "multipleBeans";
            
            String title = MessageUtils.getMessage(getPropertiesId(), 
                errorCode + "." + messageCodePrefix + "Title");
            String message = MessageUtils.getMessage(getPropertiesId(), 
                errorCode + "." + messageCodePrefix + "Message");
            
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
