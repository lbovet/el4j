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
package ch.elca.el4j.services.gui.richclient.executors;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract bean executor with a dialog reference name.
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
public abstract class AbstractBeanDialogExecutor 
    extends AbstractBeanExecutor {
    
    /**
     * Is the bean name of the application dialog.
     */
    private String m_dialogBeanName;

    /**
     * @return Returns the dialogBeanName.
     */
    public final String getDialogBeanName() {
        return m_dialogBeanName;
    }

    /**
     * @param dialogBeanName The dialogBeanName to set.
     */
    public final void setDialogBeanName(
        String dialogBeanName) {
        m_dialogBeanName = dialogBeanName;
    }
    
    /**
     * @return Returns an instance of a dialog.
     */
    protected final Object createDialog() {
        return getApplicationContext().getBean(
            m_dialogBeanName);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getDialogBeanName(), "dialogBeanName", this);
    }
}