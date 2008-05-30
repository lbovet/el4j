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
package ch.elca.el4j.services.gui.richclient.executors;

import ch.elca.el4j.services.gui.richclient.dialogs.BeanConfirmationDialog;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;

/**
 * Abstract executor used to confirm actions on beans.
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
public abstract class AbstractConfirmBeanExecutor 
    extends AbstractDisplayableBeanExecutor {
    /**
     * {@inheritDoc}
     */
    public void execute() {
        Object[] beans = getBeanPresenter().getSelectedBeans();
        if (beans == null || beans.length == 0) {
            return;
        }
        
        ExecutorDisplayable displayable = getDisplayable();
        if (!displayable.isConfigured()) {
            displayable.configure(this);
        }
        displayable.showDisplayable();
    }

    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(getBeanPresenter().getSelectedBeans() != null);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Default displayable is <code>BeanConfirmationDialog</code>.
     * 
     * @see BeanConfirmationDialog
     */
    protected ExecutorDisplayable getDefaultDisplayable() {
        return new BeanConfirmationDialog();
    }
}
