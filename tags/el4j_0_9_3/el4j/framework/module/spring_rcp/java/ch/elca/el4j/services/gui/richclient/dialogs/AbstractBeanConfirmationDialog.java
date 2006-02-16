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
package ch.elca.el4j.services.gui.richclient.dialogs;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.dialog.ConfirmationDialog;

import ch.elca.el4j.services.gui.richclient.Constants;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;

/**
 * Abstract confirmation dialog for beans.
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
public abstract class AbstractBeanConfirmationDialog extends ConfirmationDialog 
    implements InitializingBean, BeanNameAware {
    /**
     * Reference to the bean presenter.
     */
    private BeanPresenter m_beanPresenter;

    /**
     * Is the id used to get properties like labels for the created form.
     */
    private String m_propertiesId = Constants.DEFAULT_DIALOG_PROPERTIES_ID;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;
    
    /**
     * Flag to indicate if the yes button should be used as default. Otherwise
     * the no button is set as default.
     */
    private boolean m_yesDefault = true;

    /**
     * {@inheritDoc}
     * 
     * If property <code>yesDefault</code> is <code>true</code>, the yes button
     * will be used as default, if <code>false</code> the no button.
     */
    protected void registerDefaultCommand() {
        if (isControlCreated()) {
            if (isYesDefault()) {
                getFinishCommand().setDefaultButtonIn(getDialog());
            } else {
                getCancelCommand().setDefaultButtonIn(getDialog());
            }
        }
    }
    
    /**
     * @return Returns the beanPresenter.
     */
    public final BeanPresenter getBeanPresenter() {
        return m_beanPresenter;
    }

    /**
     * @param beanPresenter The beanPresenter to set.
     */
    public final void setBeanPresenter(BeanPresenter beanPresenter) {
        m_beanPresenter = beanPresenter;
    }

    /**
     * @return Returns the propertiesId.
     */
    public final String getPropertiesId() {
        return m_propertiesId;
    }

    /**
     * @param propertiesId The propertiesId to set.
     */
    public final void setPropertiesId(String propertiesId) {
        m_propertiesId = propertiesId;
    }

    /**
     * @return Returns the name of this bean.
     */
    public final String getBeanName() {
        return m_beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * @return Returns the yesDefault.
     */
    public final boolean isYesDefault() {
        return m_yesDefault;
    }

    /**
     * @param yesDefault The yesDefault to set.
     */
    public final void setYesDefault(boolean yesDefault) {
        m_yesDefault = yesDefault;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception { }
}
