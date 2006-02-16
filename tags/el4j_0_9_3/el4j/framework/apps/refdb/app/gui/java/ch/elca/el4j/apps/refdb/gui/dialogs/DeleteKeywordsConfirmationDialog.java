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
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanConfirmationDialog;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
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
        Object[] objects = getBeanPresenter().getSelectedBeans();
        Reject.ifNull(objects);
        m_keywords = new KeywordDto[objects.length];
        System.arraycopy(objects, 0, m_keywords, 0, m_keywords.length);
        
        int [] keys = new int[m_keywords.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = m_keywords[i].getKey();
        }
        
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        referenceService.removeKeywords(keys);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.removeBeans(m_keywords);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void onFinishException(Exception e) {
        String errorCode = null;
        boolean closeDialog = true;

        BeanPresenter beanPresenter = getBeanPresenter();
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        
        if (e instanceof JdbcUpdateAffectedIncorrectNumberOfRowsException) {
            errorCode = "JdbcUpdateAffectedIncorrectNumberOfRowsException";
        } else if (e instanceof DataAccessException) {
            errorCode = "DataAccessException";
        }
        
        if (errorCode != null) {
            KeywordDto[] actualizedKeywords = new KeywordDto[m_keywords.length];
            for (int i = 0; i < m_keywords.length; i++) {
                KeywordDto oldKeyword = m_keywords[i];
                try {
                    KeywordDto newKeyword 
                        = referenceService.getKeywordByKey(oldKeyword.getKey());
                    beanPresenter.replaceBean(oldKeyword, newKeyword);
                    actualizedKeywords[i] = newKeyword;
                } catch (DataAccessException ex) {
                    beanPresenter.removeBean(oldKeyword);
                }
            }
            
            // Selecting beans must be done in a seperate step, when no bean 
            // will be added to or removed from data list. Otherwise already 
            // made selections will be lost!
            beanPresenter.clearSelection();
            for (int i = 0; i < actualizedKeywords.length; i++) {
                KeywordDto newKeyword = actualizedKeywords[i];
                beanPresenter.selectBeanAdditionally(newKeyword);
            }
            
            String messageCodePrefix = m_keywords.length == 1 
                ? "singlebean" : "multiplebeans";
            
            String title = MessageUtils.getMessage(getPropertiesId(), 
                "keyword", errorCode + "." + messageCodePrefix + ".title");
            String message = MessageUtils.getMessage(getPropertiesId(), 
                "keyword", errorCode + "." + messageCodePrefix + ".message");
            
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
