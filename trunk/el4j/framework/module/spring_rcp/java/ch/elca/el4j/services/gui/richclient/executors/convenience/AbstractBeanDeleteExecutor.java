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
package ch.elca.el4j.services.gui.richclient.executors.convenience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.executors.AbstractConfirmBeanExecutor;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Abstract executor to delete beans.
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
public abstract class AbstractBeanDeleteExecutor<T> 
    extends AbstractConfirmBeanExecutor {
    
    /** Constructor. */
    public AbstractBeanDeleteExecutor() {
        // subclasses may override this by calling setId as well.
        setId("delete");
    }
    
    /**
     * Are the beans that should be deleted.
     */
    private List<T> m_beans;

    /**
     * @return Returns the beans that should be deleted.
     */
    protected List<T> getBeans() {
        return m_beans;
    }
    
    /**
     * @param beans Are the beans to set.
     */
    public void setBeans(List<T> beans) {
        m_beans = beans;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean onFinishOrConfirm() {
        Object[] objects = getBeanPresenter().getSelectedBeans();
        Reject.ifNull(objects);
        
        // two casts because the compiler is stubborn
        List<T> beans = (List<T>) (List<?>) Arrays.asList(objects);
        
        setBeans(beans);
        deleteBeans(beans);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.removeBeans(objects);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirmException(Exception e) {
        BeanPresenter beanPresenter = getBeanPresenter();
        
        List<T> beans = getBeans();
        List<T> updatedBeans = new ArrayList<T>();
        for (T oldBean : beans) {
            try {
                T newBean = reloadBean(oldBean);
                beanPresenter.replaceBean(oldBean, newBean);
                updatedBeans.add(newBean);
            } catch (Exception ex) {
                beanPresenter.removeBean(oldBean);
            }
        }
        
        // Selecting beans must be done in a seperate step, when no bean 
        // will be added to or removed from data list. Otherwise already 
        // made selections will be lost!
        beanPresenter.clearSelection();
        for (T newBean : updatedBeans) {
            beanPresenter.selectBeanAdditionally(newBean);
        }
        
        // Show error message dialog.
        int multiplicity = beans.size();
        DialogUtils.showErrorMessageDialog(getId(), getSchema(), e, 
            multiplicity, getDisplayable().getMainComponent());
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandId() {
        String commandId = super.getCommandId();
        if (!StringUtils.hasText(commandId)) {
            commandId = GlobalCommandIds.DELETE;
            setCommandId(commandId);
        }
        return commandId;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getSchema() {
        return "delete";
    }

    /**
     * Deletes beans with the given keys.
     * 
     * @param beans Are the beans to delete.
     */
    protected abstract void deleteBeans(List<T> beans);
    
    /**
     * @return Returns the most current version of this entity.
     * @throws Exception If bean could not be fetched.
     */
    protected abstract T reloadBean(T entity) throws Exception;
}
