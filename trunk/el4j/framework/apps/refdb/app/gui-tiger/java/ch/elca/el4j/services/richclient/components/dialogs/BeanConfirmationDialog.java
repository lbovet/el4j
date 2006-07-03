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
package ch.elca.el4j.services.richclient.components.dialogs;

import org.springframework.richclient.application.PageComponent;
import org.springframework.util.Assert;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.lightrefdb.dom.Keyword;
import ch.elca.el4j.apps.lightrefdb.dom.Reference;
import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.AbstractConfirmBeanExecutor;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.richclient.naming.Naming;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;

/**
 * BeanConfirmationDialog with new message lookup.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class BeanConfirmationDialog extends
    ch.elca.el4j.services.gui.richclient.dialogs.BeanConfirmationDialog {

    /** {@inheritDoc} */
    @ImplementationAssumption(
        "Super types of entity types are not entity types themselves.")
    // duplicated from superclass, intended to be merged if the new system
    // is adopted (or discarded otherwise. There is not enough justification
    // for two message lookup systems.)
    @Override
    public void configure(AbstractBeanExecutor executor) {
        
        Assert.isInstanceOf(AbstractConfirmBeanExecutor.class, executor);
        
        AbstractConfirmBeanExecutor confirmBeanExecutor
            = (AbstractConfirmBeanExecutor) executor;
        
        BeanPresenter beanPresenter = confirmBeanExecutor.getBeanPresenter();
        Object[] beans = beanPresenter.getSelectedBeans();
        Reject.ifNull(beans, 
            "Can not configure dialog without any selected bean!");
        
        // Sets the executor action.
        setExecutorAction(confirmBeanExecutor);
        
        // Set the parent component.
        if (beanPresenter instanceof PageComponent) {
            PageComponent pageComponent = (PageComponent) beanPresenter;
            setParent(pageComponent.getContext().getWindow().getControl());
        }
        
        // Kludge to account for non-generic persistence logic
        Class<?> clazz = beans[0].getClass().equals(KeywordDto.class)
                       ? Keyword.class
                       : Reference.class;
        
        // Sets the title and confirmation message on this dialog.        
        Naming.Fetcher msgs = Naming.instance().forConfirmation(
            confirmBeanExecutor.getId(),
            EntityType.get(clazz),
            beans.length
        );
        setConfirmationMessage(msgs.get("message"));
        setTitle(msgs.get("title"));
    }
}
