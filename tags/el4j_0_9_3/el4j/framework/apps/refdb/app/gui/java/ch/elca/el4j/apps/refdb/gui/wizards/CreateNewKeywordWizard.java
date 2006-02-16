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
package ch.elca.el4j.apps.refdb.gui.wizards;

import javax.swing.JOptionPane;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;
import ch.elca.el4j.services.gui.richclient.wizards.AbstractBeanWizard;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;

/**
 * Wizard to create a new keyword.
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
public class CreateNewKeywordWizard extends AbstractBeanWizard {
    /**
     * {@inheritDoc}
     */
    protected boolean onFinishAfterCommit(Object currentBean) {
        KeywordDto currentKeyword = (KeywordDto) currentBean;
        
        ReferenceService referenceService = ServiceBroker.getReferenceService();
        KeywordDto newKeyword = referenceService.saveKeyword(currentKeyword);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.addBean(newKeyword);
        beanPresenter.focusBean(newKeyword);
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean onFinishException(RuntimeException re) {
        String errorCode = null;
        boolean closeDialog = false;
        
        if (re instanceof DataIntegrityViolationException) {
            errorCode = "DataIntegrityViolationException";
        } else if (re instanceof InsertionFailureException) {
            errorCode = "InsertionFailureException";
        } else if (re instanceof DataAccessException) {
            errorCode = "DataAccessException";
        } else {
            throw re;
        }
        
        String title = MessageUtils.getMessage(getPropertiesId(), 
            "keyword", errorCode + ".title");
        String message = MessageUtils.getMessage(getPropertiesId(), 
            "keyword", errorCode + ".message");
        
        JOptionPane.showMessageDialog(getWizardDialog().getDialog(), 
            message, title, JOptionPane.ERROR_MESSAGE);
        
        return closeDialog;
    }
}
