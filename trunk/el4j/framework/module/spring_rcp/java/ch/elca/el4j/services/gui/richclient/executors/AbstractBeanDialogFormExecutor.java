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

import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Abstract bean executor with a dialog reference name and form reference names.
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
public abstract class AbstractBeanDialogFormExecutor 
    extends AbstractBeanDialogExecutor {
    /**
     * Are the property forms bean names for this executor.
     */
    private String[] m_propertiesFormBeanNames;

    /**
     * Create properties forms.
     * 
     * @return Created properties forms.
     */
    protected BeanPropertiesForm[] createPropertiesForms() {
        BeanPropertiesForm[] propertiesForms 
            = new BeanPropertiesForm[m_propertiesFormBeanNames.length];
        for (int i = 0; i < propertiesForms.length; i++) {
            String beanName = m_propertiesFormBeanNames[i];
            propertiesForms[i] 
                = (BeanPropertiesForm) getApplicationContext().getBean(
                    beanName);
        }
        return propertiesForms;
    }

    /**
     * @return Returns the propertiesFormBeanNames.
     */
    public final String[] getPropertiesFormBeanNames() {
        return m_propertiesFormBeanNames;
    }

    /**
     * @param propertiesFormBeanNames The propertiesFormBeanNames to set.
     */
    public final void setPropertiesFormBeanNames(
        String[] propertiesFormBeanNames) {
        m_propertiesFormBeanNames = propertiesFormBeanNames;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getPropertiesFormBeanNames(), "propertiesFormBeanNames", this);
    }
}
