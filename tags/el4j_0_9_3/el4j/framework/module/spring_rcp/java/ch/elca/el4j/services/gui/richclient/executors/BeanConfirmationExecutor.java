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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.richclient.application.PageComponent;

import ch.elca.el4j.services.gui.richclient.dialogs.AbstractBeanConfirmationDialog;
import ch.elca.el4j.services.gui.richclient.utils.MessageUtils;


/**
 * Executor to do something with selected beans that needs confirmation.
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
public class BeanConfirmationExecutor extends AbstractBeanDialogExecutor {
    /**
     * {@inheritDoc}
     */
    public void execute() {
        Object[] beans = getBeanPresenter().getSelectedBeans();
        if (beans == null || beans.length == 0) {
            return;
        }
        
        AbstractBeanConfirmationDialog dialog
            = (AbstractBeanConfirmationDialog) getApplicationContext().getBean(
                getDialogBeanName());
        PageComponent pageComponent = (PageComponent) getBeanPresenter();
        dialog.setParent(pageComponent.getContext().getWindow().getControl());
        dialog.setBeanPresenter(getBeanPresenter());
        
        String messageCodePrefix = beans.length == 1 
            ? "singlebean" : "multiplebeans";
        String confirmationMessage = MessageUtils.getMessage(
            dialog.getPropertiesId(), messageCodePrefix + ".message");
        String title = MessageUtils.getMessage(
            dialog.getPropertiesId(), messageCodePrefix + ".title");
        
        
        dialog.setConfirmationMessage(confirmationMessage);
        dialog.setTitle(title);

        dialog.showDialog();
    }

    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(getBeanPresenter().getSelectedBeans() != null);
    }
}
