/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.richclient.executors.convenience;

import ch.elca.el4j.services.gui.richclient.executors.AbstractWizardBeanExecutor;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;

/**
 * Abstract executor to create new beans.
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
public abstract class AbstractBeanNewExecutor
    extends AbstractWizardBeanExecutor {
    /**
     * {@inheritDoc}
     */
    public boolean onFinishAfterCommit(Object currentBean) {
        PrimaryKeyObject givenBean = (PrimaryKeyObject) currentBean;
        PrimaryKeyObject newBean = saveBean(givenBean);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.addBean(newBean);
        beanPresenter.focusBean(newBean);
        return true;
    }

    /**
     * @param newBean Is the bean to save.
     * @return Returns the saved bean.
     */
    protected abstract PrimaryKeyObject saveBean(PrimaryKeyObject newBean);
    
    /**
     * Creates a new keyword dto.
     * 
     * {@inheritDoc}
     */
    protected abstract Object createNewBean();
}
